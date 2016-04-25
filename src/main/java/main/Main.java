package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.media.jfxmedia.logging.Logger;
import core.AbstractReceiptDto;
import windows.WindowsValidator;

import java.io.IOException;

public class Main {
    public static void main(String[] args){
        String receipt = "";

        Logger.setLevel(Logger.DEBUG);

        WindowsValidator validator = new WindowsValidator(receipt);


        try {
            AbstractReceiptDto receiptDto = validator.buildDto();
            ObjectMapper mapper = new ObjectMapper();
            String s = mapper.writeValueAsString(receiptDto);
            System.out.println(s);
            System.out.println(validator.validate());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
