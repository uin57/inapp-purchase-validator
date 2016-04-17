package android;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AndroidReceiptDto {

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
