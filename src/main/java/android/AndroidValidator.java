package android;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.AbstractReceiptDto;
import core.AbstractValidator;
import core.Channel;

import java.io.IOException;

public final class AndroidValidator extends AbstractValidator{

    private String signature;

    public AndroidValidator(String receipt, String signature){
        channel = Channel.ANDROID;
        this.receipt = receipt;
        this.signature = signature;
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
            return mapper.readValue(receipt, AndroidReceiptDto.class);
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
