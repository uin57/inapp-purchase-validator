package android;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.AbstractReceiptDto;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class AndroidReceiptDto extends AbstractReceiptDto{

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("packageName")
    private String packageName;

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("purchaseTime")
    private long purchaseTime;

    @JsonProperty("purchaseState")
    private int purchaseState;

    @JsonProperty("purchaseToken")
    private String purchaseToken;

    @Setter
    @JsonProperty("signature")
    private String signature;
}
