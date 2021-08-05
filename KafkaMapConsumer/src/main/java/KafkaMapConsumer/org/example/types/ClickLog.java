
package KafkaMapConsumer.org.example.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "bidId",
    "chargeAmount",
    "timestamp"
})
public class ClickLog {

    @JsonProperty("bidId")
    private String bidId;
    @JsonProperty("chargeAmount")
    private Double chargeAmount;
    @JsonProperty("timestamp")
    private String timestamp;

    @JsonProperty("bidId")
    public String getBidId() {
        return bidId;
    }

    @JsonProperty("bidId")
    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public ClickLog withBidId(String bidId) {
        this.bidId = bidId;
        return this;
    }

    @JsonProperty("chargeAmount")
    public Double getChargeAmount() {
        return chargeAmount;
    }

    @JsonProperty("chargeAmount")
    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public ClickLog withChargeAmount(Double chargeAmount) {
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

    public ClickLog withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("bidId", bidId).append("chargeAmount", chargeAmount).append("timestamp", timestamp).toString();
    }

}
