package itunes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ItunesV1ReceiptDto {
    @JsonProperty("original_purchase_date_pst")
    private LocalDateTime originalPurchaseDatePst;

    @JsonProperty("purchase_date_ms")
    private LocalDateTime purchaseDateMs;

    @JsonProperty("unique_identifier")
    private String uniqueIdentifier;

    @JsonProperty("original_transaction_id")
    private long originalTransactionId;

    @JsonProperty("bvrs")
    private int bvrs;

    @JsonProperty("transaction_id")
    private long transactionId;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("unique_vendor_identifier")
    private String uniqueVendorIdentifier;

    @JsonProperty("item_id")
    private long itemId;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("purchase_date")
    private LocalDateTime purchaseDate;

    @JsonProperty("original_purchase_date")
    private LocalDateTime originalPurchaseDate;

    @JsonProperty("purchase_date_pst")
    private LocalDateTime purchaseDatePst;

    @JsonProperty("bid")
    private String bid;

    @JsonProperty("original_purchase_date_ms")
    private long originalPurchaseDateMs;
}
