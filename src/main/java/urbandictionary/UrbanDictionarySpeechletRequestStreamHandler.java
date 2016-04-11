package urbandictionary;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

/**
 * This class could be the handler for an AWS Lambda function powering an Alexa Skills Kit
 * experience. To do this, simply set the handler field in the AWS Lambda console to
 * "namegame.UrbanDictionarySpeechletRequestStreamHandler".
 */
public final class UrbanDictionarySpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>();

    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
        supportedApplicationIds.add("amzn1.echo-sdk-ams.app.e9e69e2e-a5e8-4aac-9872-f675298e44df");
    }

    public UrbanDictionarySpeechletRequestStreamHandler() {
        super(new UrbanDictionarySpeechlet(), supportedApplicationIds);
    }
}