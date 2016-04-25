package windows.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import core.AbstractReceiptDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "Receipt")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public final class WindowsReceiptDto extends AbstractReceiptDto {

    @JacksonXmlProperty(localName = "Version", isAttribute = true)
    public String Version;

    @JacksonXmlProperty(localName = "CertificateId", isAttribute = true)
    public String certificateId;

    @JacksonXmlProperty(localName = "ProductReceipt")
    public ProductReceipt productReceipt;

    @JacksonXmlProperty(localName = "Signature")
    public Signature signature;
}