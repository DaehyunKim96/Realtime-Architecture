
package Kstreams.JavaObjectTest.org.example.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gender",
    "age",
    "tagId"
})
public class GAS {

    @JsonProperty("gender")
    private String gender;
    @JsonProperty("age")
    private Integer age;
    @JsonProperty("tagId")
    private String tagId;

    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    public GAS withGender(String gender) {
        this.gender = gender;
        return this;
    }

    @JsonProperty("age")
    public Integer getAge() {
        return age;
    }

    @JsonProperty("age")
    public void setAge(Integer age) {
        this.age = age;
    }

    public GAS withAge(Integer age) {
        this.age = age;
        return this;
    }

    @JsonProperty("tagId")
    public String getTagId() {
        return tagId;
    }

    @JsonProperty("tagId")
    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public GAS withTagId(String tagId) {
        this.tagId = tagId;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("gender", gender).append("age", age).append("tagId", tagId).toString();
    }

}
