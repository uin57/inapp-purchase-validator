package core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import java.io.IOException;

@Getter
@NoArgsConstructor
public abstract class AbstractValidator {
    protected Channel channel;
    protected String receipt;

    public abstract boolean onlineValidator();
    public abstract boolean offlineValidator();
    public abstract AbstractReceiptDto buildDto() throws IOException;
}
