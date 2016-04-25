package windows.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Reference {
    @JacksonXmlProperty(localName = "URI", isAttribute = true)
    public String uri;

    @JacksonXmlProperty(localName = "Transforms")
    public List<Transform> transforms;

    @JacksonXmlProperty(localName = "DigestMethod")
    public DigestMethod digestMethod;

    @JacksonXmlProperty(localName = "DigestValue")
    public String digestValue;
}
