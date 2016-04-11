package urbandictionary.api;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UrbanDictionaryTest {

    @Test
    public void integrationTest() {
        final String word = "bitch";
        Term term = UrbanDictionary.INSTANCE.define(word);
        assertThat(term , not(nullValue()));
    }
}
