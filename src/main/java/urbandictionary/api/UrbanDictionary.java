package urbandictionary.api;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;

public interface UrbanDictionary {

    String API_VERSION = "v0";
    String API_ENDPOINT = String.format("http://api.urbandictionary.com/%s",API_VERSION);

    @RequestLine("GET /define?term={term}")
    Term define(@Param("term") String term);

    UrbanDictionary INSTANCE = Feign.builder()
            .logger(new Slf4jLogger())
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(UrbanDictionary.class, API_ENDPOINT);
}
