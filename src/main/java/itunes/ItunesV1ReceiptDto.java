package itunes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import core.AbstractReceiptDto;
import itunes.serializer.ItunesLocalDateTimeDeserializer;
import itunes.serializer.ItunesLocalDateTimeSerializer;

import java.time.LocalDateTime;

public final class ItunesV1ReceiptDto extends AbstractReceiptDto{

    @JsonProperty("original_purchase_date_pst")
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime originalPurchaseDatePst;

    @JsonProperty("purchase_date_ms")
    private long purchaseDateMs;

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
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime purchaseDate;

    @JsonProperty("original_purchase_date")
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime originalPurchaseDate;

    @JsonProperty("purchase_date_pst")
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime purchaseDatePst;

    @JsonProperty("bid")
    private String bid;

    @JsonProperty("original_purchase_date_ms")
    private long originalPurchaseDateMs;
}

