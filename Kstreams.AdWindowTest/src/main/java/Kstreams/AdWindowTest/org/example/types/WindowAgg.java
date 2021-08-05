
package Kstreams.AdWindowTest.org.example.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "chargeAmount",
    "win",
    "click"
})
public class WindowAgg {

    public WindowAgg() {}
    public WindowAgg(Double charge, Integer win, Integer click) {
        this.win = win;
        this.click = click;
        this.chargeAmount = charge;
    }

    @JsonProperty("chargeAmount")
    private Double chargeAmount;
    @JsonProperty("win")
    private Integer win;
    @JsonProperty("click")
    private Integer click;

    @JsonProperty("chargeAmount")
    public Double getChargeAmount() {
        return chargeAmount;
    }

    @JsonProperty("chargeAmount")
    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public WindowAgg withChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
        return this;
    }

    @JsonProperty("win")
    public Integer getWin() {
        return win;
    }

    @JsonProperty("win")
    public void setWin(Integer win) {
        this.win = win;
    }

    public WindowAgg withWin(Integer win) {
        this.win = win;
        return this;
    }

    @JsonProperty("click")
    public Integer getClick() {
        return click;
    }

    @JsonProperty("click")
    public void setClick(Integer click) {
        this.click = click;
    }

    public WindowAgg withClick(Integer click) {
        this.click = click;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("chargeAmount", chargeAmount).append("win", win).append("click", click).toString();
    }

}
