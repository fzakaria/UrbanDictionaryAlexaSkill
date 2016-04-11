package urbandictionary.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
public class Term {

    private static final String NO_RESULT_TYPE = "no_results";

    @Singular
    private List<String> tags = Lists.newArrayList();

    @JsonProperty("result_type")
    private String resultType;

    @JsonProperty("list")
    private List<Definition> definitions = Lists.newArrayList();

    @Singular
    private List<String> sounds = Lists.newArrayList();

}
