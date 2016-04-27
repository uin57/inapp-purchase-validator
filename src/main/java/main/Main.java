package main;

import android.AndroidValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.media.jfxmedia.logging.Logger;
import core.AbstractReceiptDto;
import core.AbstractValidator;
import core.Channel;
import itunes.ItunesValidator;
import windows.WindowsValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        AbstractReceiptDto receiptDto;
        AbstractValidator validator;
        if(args.length == 0)
            throw new RuntimeException("Required parameters are missing");
        Channel channel = Channel.valueOf(args[0].toUpperCase());
        try {
            switch (channel) {
                case WINDOWS:
                    if (args.length != 2) {
                        Logger.logMsg(Logger.ERROR, "the parameters in input are invalid ; required : WINDOWS 'path/to/receipt'");
                        throw new RuntimeException(String.format("Incorrect number of parameters (expected %s, but %s found)", 2, args.length));
                    }
                    String receipt = readFile(args[1]);
                    validator = new WindowsValidator(receipt);
                    break;
                case ITUNES:
                    if (args.length != 2){
                        Logger.logMsg(Logger.ERROR, "the parameters in input are invalid ; required : ITUNES 'path/to/receipt'");
                        throw new RuntimeException(String.format("Incorrect number of parameters (expected %s, but %s found)", 2, args.length));
                    }
                    receipt = readFile(args[1]);
                    validator = new ItunesValidator(receipt);
                    break;
                case ANDROID:
                    if(args.length != 4 && args.length != 7) {
                        Logger.logMsg(Logger.ERROR, "the parameters in input are invalid ; required : ANDROID 'path/to/receipt' 'path/to/signature' 'path/to/keyPlaystore' [ 'path/to/clientId' 'path/to/clientSecret' 'path/to/refreshToken' ]");
                        throw new RuntimeException(String.format("Incorrect number of parameters (expected %s or %s, but %s found)", 3, 7, args.length));
                    }
                    receipt = readFile(args[1]);
                    String signature = readFile(args[2]);
                    String keyplaystore = readFile(args[3]);
                    if(args.length == 7){
                        String clientId = readFile(args[4]);
                        String clientSecret = readFile(args[5]);
                        String refreshToken = readFile(args[6]);
                        validator = new AndroidValidator(receipt, signature, keyplaystore, clientId, clientSecret, refreshToken);
                        break;
                    }
                    validator = new AndroidValidator(receipt, signature, keyplaystore);
                    break;
                default:
                    throw new RuntimeException(String.format("unknown channel %s", channel.name()));
            }
        }catch (Exception e){
            Logger.logMsg(Logger.ERROR, "error encountered during the parsing of the parameters and the build of the validator");
            throw new RuntimeException("error encountered during the initialization");
        }

        Logger.setLevel(Logger.DEBUG);

        Logger.logMsg(Logger.DEBUG, "The validator is successfully initialized");
        try {
            if (validator.validate()) {
                receiptDto = validator.buildDto();
                ObjectMapper mapper = new ObjectMapper();
                System.out.println(mapper.writeValueAsString(receiptDto));
            } else {
                throw new RuntimeException("unable to validate the receipt");
            }
        }catch (IOException e){
            throw new RuntimeException("error encountered during the validation of the receipt");
        }
    }

    public static String readFile(String path) {
        try {
            String file = "";
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine())
                file += scanner.nextLine();
            return file;
        }catch (FileNotFoundException e){
            Logger.logMsg(Logger.ERROR, "unable to read the file at path "+path);
            throw new RuntimeException("unable to read the file");
        }
    }
}
