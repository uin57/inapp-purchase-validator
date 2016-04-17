package android;

import com.fasterxml.jackson.annotation.JsonProperty;
import core.AbstractReceiptDto;

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

    @JsonProperty("signature")
    private String signature;
}
