package windows.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SignedInfo {
    @JacksonXmlProperty(localName = "CanonicalizationMethod")
    public CanonicalizationMethod canonicalizationMethod;

    @JacksonXmlProperty(localName = "SignatureMethod")
    public SignatureMethod signatureMethod;

    @JacksonXmlProperty(localName = "Reference")
    public Reference reference;
}
