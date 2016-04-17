package core;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@Getter
@NoArgsConstructor
@Builder
public abstract class AbstractValidator {
    private Channel channel;
    private String receipt;
    private AbstractReceiptDto abstractReceiptDto;

    public abstract boolean onlineValidator();
    public abstract boolean offlineValidator();
    public abstract AbstractReceiptDto buildDto();
}
