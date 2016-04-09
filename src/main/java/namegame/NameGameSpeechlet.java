package namegame;

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
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class NameGameSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(NameGameSpeechlet.class);
    private static final String FIRST_NAME_SLOT = "FirstName";
    private static final List<Character> CONSONANTS =
            ImmutableList.of('b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm'
            , 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z');

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
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
        if (StringUtils.equals("NameGameIntent",intentName)) {
            final Slot nameSlot = intent.getSlot(FIRST_NAME_SLOT);
            return getNameGameResponse(nameSlot.getValue());
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
    }

    SpeechletResponse getNameGameResponse(final String name) {
        //We want to remove all consonants
        String nameWithoutConsonants = name;
        while(CONSONANTS.contains(nameWithoutConsonants.charAt(0))) {
            nameWithoutConsonants = nameWithoutConsonants.substring(1, nameWithoutConsonants.length()-1);
        }

        String speechText = "";
        if (name.startsWith("b")) {
            speechText += String.format("%s, %s, boo-%s",name, name, nameWithoutConsonants);
        } else {
            speechText += String.format("%s, %s, boo-b%s",name, name, nameWithoutConsonants);
        }
        if (name.startsWith("f")) {
            speechText += String.format("Banana-fana fo-f%s",nameWithoutConsonants);
        } else {
            speechText += String.format("Banana-fana fo-%s ",nameWithoutConsonants);
        }
        if (name.startsWith("m")) {
            speechText += String.format("Fee-fy-mo-m%s",nameWithoutConsonants);
        } else {
            speechText += String.format("Fee-fy-mo-%s",nameWithoutConsonants);
        }
        speechText += String.format("%s!", name);


        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Name Game");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
     SpeechletResponse getHelpResponse() {
         String speechText = "You can give me a name to rhyme!";

         // Create the Simple card content.
         SimpleCard card = new SimpleCard();
         card.setTitle("Name Game");
         card.setContent(speechText);

         // Create the plain text output.
         PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
         speech.setText(speechText);

         // Create reprompt
         Reprompt reprompt = new Reprompt();
         reprompt.setOutputSpeech(speech);

         return SpeechletResponse.newAskResponse(speech, reprompt, card);
     }


        /**
         * Creates and returns a {@code SpeechletResponse} with a welcome message.
         *
         * @return SpeechletResponse spoken and visual response for the given intent
         */
    SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome to the Name Game, you can give it your name to start.";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Name Game");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
}
