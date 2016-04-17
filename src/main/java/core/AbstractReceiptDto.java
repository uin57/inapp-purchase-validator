package core;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractReceiptDto {
    @JsonProperty("receipt")
    private String receipt;
}
