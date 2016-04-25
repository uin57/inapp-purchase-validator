package windows.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import windows.serializer.WindowsLocalDateTimeDeserializer;
import windows.serializer.WindowsLocalDateTimeSerializer;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductReceipt {
    @JacksonXmlProperty(localName = "PurchasePrice", isAttribute = true)
    private String purchasePrice;

    @JacksonXmlProperty(localName = "PurchaseDate", isAttribute = true)
    @JsonDeserialize(using = WindowsLocalDateTimeDeserializer.class)
    @JsonSerialize(using = WindowsLocalDateTimeSerializer.class)
    private LocalDateTime purchaseDate;

    @JacksonXmlProperty(localName = "Id", isAttribute = true)
    private String id;

    @JacksonXmlProperty(localName = "AppId", isAttribute = true)
    private String appId;

    @JacksonXmlProperty(localName = "ProductId", isAttribute = true)
    private String productId;

    @JacksonXmlProperty(localName = "ProductType", isAttribute = true)
    private String productType;

    @JacksonXmlProperty(localName = "PublisherUserId", isAttribute = true)
    private String publisherUserId;

    @JacksonXmlProperty(localName = "PublisherDeviceId", isAttribute = true)
    private String publisherDeviceId;

    @JacksonXmlProperty(localName = "MicrosoftProductId", isAttribute = true)
    private String microsoftProductId;

    @JacksonXmlProperty(localName = "MicrosoftAppId", isAttribute = true)
    private String microsoftAppId;
}
