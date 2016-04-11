package urbandictionary.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
public class Definition
{
    @JsonProperty("defid")
    private long id;
    private String word;
    private String author;
    private String permalink;
    private String definition;
    private String example;
    @JsonProperty("thumbs_up")
    private int thumbsUp;
    @JsonProperty("thumbs_down")
    private int thumbsDown;
    @JsonProperty("current_vote")
    private String currentVote;

}