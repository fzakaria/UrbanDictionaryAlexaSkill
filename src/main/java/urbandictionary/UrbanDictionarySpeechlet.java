package urbandictionary;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import urbandictionary.api.Definition;
import urbandictionary.api.Term;
import urbandictionary.api.UrbanDictionary;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class UrbanDictionarySpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(UrbanDictionarySpeechlet.class);

    /**
     * A word can have many definitions. This is the intent to paginate through.
     */
    private static final String CONTINUE_INTENT = "ContinueIntent";

    /**
     * The name of the intent when begining a new session to defin a specific word
     */
    private static final String DEFINE_INTENT = "DefineIntent";

    /**
     * The slot name for the word we need to define
     */
    private static final String TERM_SLOT = "Term";

    /**
     * Session attribute to store the current index of the definition to read of.
     */
    private static final String SESSION_DEFINE_INDEX = "defineIndex";

    /**
     * Session attribute to store the word we are currently defining
     */
    private static final String SESSION_DEFINE_WORD = "defineWord";


    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        session.setAttribute(SESSION_DEFINE_INDEX, 0);
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = intent.getName();
        if (Objects.equals(DEFINE_INTENT,intentName)) {
            final Slot nameSlot = intent.getSlot(TERM_SLOT);
            return getDefinitionResponse(nameSlot.getValue(), session);
        } else if (Objects.equals(CONTINUE_INTENT, intentName)) {
            final String word = (String) session.getAttribute(SESSION_DEFINE_WORD);
            return getDefinitionResponse(word, session);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else if ("AMAZON.StopIntent".equals(intentName)) {
            return getGoodbyeResponse();
        } else if ("AMAZON.CancelIntent".equals(intentName)) {
            return getGoodbyeResponse();
        } else {
            throw new SpeechletException(String.format("Unknown Intent: %s", intentName));
        }
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
    }

    SpeechletResponse getDefinitionResponse(final String word, final Session session) {
        Term term = UrbanDictionary.INSTANCE.define(word);
        if (term.getDefinitions().isEmpty()) {
            String speechText = String.format("Sorry. I could not find any definitions for %s",word);
            // Create the plain text output.
            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(speechText);
            return SpeechletResponse.newTellResponse(speech);
        } else {
            //Descending list in terms of thumbs up
            List<Definition> sortedDefinitions = term.getDefinitions().stream().sorted(
                    (Definition d1, Definition d2) -> Integer.compare(d2.getThumbsUp(), d1.getThumbsUp()))
                    .collect(Collectors.toList());

            Integer index = (Integer) session.getAttribute(SESSION_DEFINE_INDEX);
            if (index > sortedDefinitions.size()) {
                String speechText = String.format("There are no more definitions for %s",word);
                // Create the plain text output.
                PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
                speech.setText(speechText);
                return SpeechletResponse.newTellResponse(speech);
            }

            Definition definition = sortedDefinitions.get(index);
            // Create the Simple card content.
            SimpleCard card = new SimpleCard();
            card.setTitle(word);
            card.setContent(definition.getDefinition());
            // Create the plain text output.
            PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
            speech.setText(definition.getDefinition());
            if (index == sortedDefinitions.size() -1) {
                return SpeechletResponse.newTellResponse(speech, card);
            } else {
                session.setAttribute(SESSION_DEFINE_WORD, word);
                session.setAttribute(SESSION_DEFINE_INDEX, index+1);
                //Reprompt for next definition
                PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
                repromptSpeech.setText("There are more definitions available. " +
                                         "Would you like another ? You can tell me to continue.");
                Reprompt reprompt = new Reprompt();
                reprompt.setOutputSpeech(repromptSpeech);
                return SpeechletResponse.newAskResponse(speech, reprompt, card);
            }
        }
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
     SpeechletResponse getHelpResponse() {
         String speechText = "You can give me a word to lookup on urban dictionary dot com";

         // Create the plain text output.
         PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
         speech.setText(speechText);

         // Create reprompt
         Reprompt reprompt = new Reprompt();
         reprompt.setOutputSpeech(speech);

         return SpeechletResponse.newAskResponse(speech, reprompt);
     }


    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    SpeechletResponse getWelcomeResponse() {
        String speechText = "Hello, you can give me a word to define through urban dictionary dot com.";

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a goodbye message;
     */
    SpeechletResponse getGoodbyeResponse() {
        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("");
        return SpeechletResponse.newTellResponse(speech);
    }

}
