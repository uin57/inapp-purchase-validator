package windows.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Signature {
    @JacksonXmlProperty(localName = "SignedInfo")
    public SignedInfo signedInfo;

    @JacksonXmlProperty(localName = "SignatureValue")
    public String signatureValue;
}
