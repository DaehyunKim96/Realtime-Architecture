
package Kstreams.JavaObject.org.example.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "chargeAmount",
    "timestamp",
    "mapped"
})
public class SimpleValue {

    @JsonProperty("chargeAmount")
    private Double chargeAmount;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("mapped")
    private Integer mapped;

    @JsonProperty("chargeAmount")
    public Double getChargeAmount() {
        return chargeAmount;
    }

    @JsonProperty("chargeAmount")
    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public SimpleValue withChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
        return this;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public SimpleValue withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @JsonProperty("mapped")
    public Integer getMapped() {
        return mapped;
    }

    @JsonProperty("mapped")
    public void setMapped(Integer mapped) {
        this.mapped = mapped;
    }

    public SimpleValue withMapped(Integer mapped) {
        this.mapped = mapped;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("chargeAmount", chargeAmount).append("timestamp", timestamp).append("mapped", mapped).toString();
    }

}
