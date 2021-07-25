
package KafkaConsumer.org.example.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "bidId",
    "adAccountId",
    "creativeId",
    "gender",
    "age",
    "tagId",
    "chargeAmount",
    "timestamp",
    "mapped"
})
public class WinLog {

    @JsonProperty("bidId")
    private String bidId;
    @JsonProperty("adAccountId")
    private Integer adAccountId;
    @JsonProperty("creativeId")
    private Integer creativeId;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("age")
    private Integer age;
    @JsonProperty("tagId")
    private String tagId;
    @JsonProperty("chargeAmount")
    private Double chargeAmount;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("mapped")
    private Boolean mapped;

    @JsonProperty("bidId")
    public String getBidId() {
        return bidId;
    }

    @JsonProperty("bidId")
    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public WinLog withBidId(String bidId) {
        this.bidId = bidId;
        return this;
    }

    @JsonProperty("adAccountId")
    public Integer getAdAccountId() {
        return adAccountId;
    }

    @JsonProperty("adAccountId")
    public void setAdAccountId(Integer adAccountId) {
        this.adAccountId = adAccountId;
    }

    public WinLog withAdAccountId(Integer adAccountId) {
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

    public WinLog withCreativeId(Integer creativeId) {
        this.creativeId = creativeId;
        return this;
    }

    @JsonProperty("gender")
    public String getGender() {
        return gender;
    }

    @JsonProperty("gender")
    public void setGender(String gender) {
        this.gender = gender;
    }

    public WinLog withGender(String gender) {
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

    public WinLog withAge(Integer age) {
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

    public WinLog withTagId(String tagId) {
        this.tagId = tagId;
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

    public WinLog withChargeAmount(Double chargeAmount) {
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

    public WinLog withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @JsonProperty("mapped")
    public Boolean getMapped() {
        return mapped;
    }

    @JsonProperty("mapped")
    public void setMapped(Boolean mapped) {
        this.mapped = mapped;
    }

    public WinLog withMapped(Boolean mapped) {
        this.mapped = mapped;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("bidId", bidId).append("adAccountId", adAccountId).append("creativeId", creativeId).append("gender", gender).append("age", age).append("tagId", tagId).append("chargeAmount", chargeAmount).append("timestamp", timestamp).append("mapped", mapped).toString();
    }

}
