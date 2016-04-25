package core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractReceiptDto {
    @JsonProperty("receipt")
    private String receipt;
}
