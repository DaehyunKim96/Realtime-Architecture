
package Kstreams.AdWindow.org.example.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "adAccountId",
    "creativeId"
})
public class AdAccount {

    @JsonProperty("adAccountId")
    private Integer adAccountId;
    @JsonProperty("creativeId")
    private Integer creativeId;

    @JsonProperty("adAccountId")
    public Integer getAdAccountId() {
        return adAccountId;
    }

    @JsonProperty("adAccountId")
    public void setAdAccountId(Integer adAccountId) {
        this.adAccountId = adAccountId;
    }

    public AdAccount withAdAccountId(Integer adAccountId) {
        this.adAccountId = adAccountId;
        return this;
    }

    @JsonProperty("creativeId")
    public Integer getCreativeId() {
        return creativeId;
    }

    @JsonProperty("creativeId")
    public void setCreativeId(Integer creativeId) {
        this.creativeId = creativeId;
    }

    public AdAccount withCreativeId(Integer creativeId) {
        this.creativeId = creativeId;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("adAccountId", adAccountId).append("creativeId", creativeId).toString();
    }

}
