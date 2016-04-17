package itunes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import core.AbstractReceiptDto;
import itunes.serializer.ItunesLocalDateTimeDeserializer;
import itunes.serializer.ItunesLocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItunesV2ReceiptDto extends AbstractReceiptDto{

    @JsonProperty("receipt_type")
    private String receiptType;

    @JsonProperty("adam_id")
    private int adamId;

    @JsonProperty("app_item_id")
    private int appItemId;

    @JsonProperty("bundle_id")
    private String bundleId;

    @JsonProperty("application_version")
    private String applicationVersion;

    @JsonProperty("download_id")
    private int downloadId;

    @JsonProperty("version_external_identifier")
    private int versionExternalIdentifier;

    @JsonProperty("receipt_creation_date")
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime receiptCreationDate;

    @JsonProperty("receipt_creation_date_ms")
    private String receiptCreationDateMs;

    @JsonProperty("receipt_creation_date_pst")
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime receiptCreationDatePst;

    @JsonProperty("request_date")
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime requestDate;

    @JsonProperty("request_date_ms")
    private String requestDateMs;

    @JsonProperty("request_date_pst")
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime requestDatePst;

    @JsonProperty("original_purchase_date")
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime originalPurchaseDate;

    @JsonProperty("original_purchase_date_ms")
    private String originalPurchaseDateMs;

    @JsonProperty("original_purchase_date_pst")
    @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
    @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
    private LocalDateTime originalPurchaseDatePst;

    @JsonProperty("original_application_version")
    private String originalApplicationVersion;

    @JsonProperty("in_app")
    private List<OrderLine> inApp;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLine {
        @JsonProperty("quantity")
        private String quantity;

        @JsonProperty("product_id")
        private String productId;

        @JsonProperty("transaction_id")
        private String transactionId;

        @JsonProperty("original_transaction_id")
        private String originalTransactionId;

        @JsonProperty("purchase_date")
        @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
        @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
        private LocalDateTime purchaseDate;

        @JsonProperty("purchase_date_ms")
        private String purchaseDateMs;

        @JsonProperty("purchase_date_pst")
        @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
        @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
        private LocalDateTime purchaseDatePst;

        @JsonProperty("original_purchase_date")
        @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
        @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
        private LocalDateTime originalPurchaseDate;

        @JsonProperty("original_purchase_date_ms")
        private String originalPurchaseDateMs;

        @JsonProperty("original_purchase_date_pst")
        @JsonDeserialize(using = ItunesLocalDateTimeDeserializer.class)
        @JsonSerialize(using = ItunesLocalDateTimeSerializer.class)
        private LocalDateTime originalPurchaseDatePst;

        @JsonProperty("is_trial_period")
        private String isTrialPeriod;
    }
}
