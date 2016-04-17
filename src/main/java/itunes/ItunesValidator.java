package itunes;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.AbstractReceiptDto;
import core.AbstractValidator;
import core.Channel;

import java.io.IOException;

public final class ItunesValidator extends AbstractValidator {

    public ItunesValidator(String receipt){
        channel = Channel.ITUNES;
        this.receipt = receipt;
    }

    @Override
    public boolean onlineValidator() {
        return false;
    }

    @Override
    public boolean offlineValidator() {
        return false;
    }

    @Override
    public AbstractReceiptDto buildDto() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(receipt, ItunesV1ReceiptDto.class);
        } catch (IOException e1) {
            try {
                return mapper.readValue(receipt, ItunesV2ReceiptDto.class);
            }catch (IOException e2) {
                throw e2;
            }
        }
    }
}
