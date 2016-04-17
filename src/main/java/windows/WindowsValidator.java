package windows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import core.AbstractReceiptDto;
import core.AbstractValidator;
import core.Channel;

import java.io.IOException;

public final class WindowsValidator extends AbstractValidator {

    public WindowsValidator(String receipt){
        channel = Channel.WINDOWS;
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
        ObjectMapper mapper = new XmlMapper();
        try {
            return mapper.readValue(receipt, WindowsReceiptDto.class);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
