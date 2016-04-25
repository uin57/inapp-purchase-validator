package itunes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.media.jfxmedia.logging.Logger;
import core.AbstractReceiptDto;
import core.AbstractValidator;
import core.Channel;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public final class ItunesValidator extends AbstractValidator {
    public ItunesV1ReceiptDto itunesV1ReceiptDto;
    public ItunesV2ReceiptDto itunesV2ReceiptDto;
    public String cipheredReceipt;

    public ItunesValidator(String cipheredReceipt){
        channel = Channel.ITUNES;
        this.cipheredReceipt = cipheredReceipt;
    }

    public AbstractReceiptDto buildDto() throws IOException {
        if(itunesV1ReceiptDto != null)
            return itunesV1ReceiptDto;
        if(itunesV2ReceiptDto != null)
            return itunesV2ReceiptDto;
        ObjectMapper mapper = new ObjectMapper();
        try {
            itunesV1ReceiptDto = mapper.readValue(receipt, ItunesV1ReceiptDto.class);
            itunesV1ReceiptDto.setReceipt(receipt);
            return itunesV1ReceiptDto;
        } catch (IOException e1) {
            try {
                itunesV2ReceiptDto = mapper.readValue(receipt, ItunesV2ReceiptDto.class);
                itunesV2ReceiptDto.setReceipt(receipt);
                return itunesV2ReceiptDto;
            }catch (IOException e2) {
                throw e2;
            }
        }
    }

    @Override
    public boolean validate() {

        try {
            String urlItunes = "https://buy.itunes.apple.com/verifyReceipt";
            int code = -1;
            for (int i = 0; i <= 1; i++) {
                String itunesResponse = postRequest(urlItunes, receipt);
                code = extractStatusCode(itunesResponse);

                switch (code) {
                    case 0:
                        Logger.logMsg(Logger.DEBUG, "the receipt is validated");
                        return true;
                    case 21000:
                        Logger.logMsg(Logger.ERROR, "the cipheredReceipt is unreadable by Itunes");
                        return false;
                    case 21002:
                        Logger.logMsg(Logger.ERROR, "the cipheredReceipt is missing or malformed for Itunes");
                        return false;
                    case 21004:
                        Logger.logMsg(Logger.ERROR, "the provided shared secret does not match the shared secret on file for this account");
                        return false;
                    case 21005:
                        Logger.logMsg(Logger.ERROR, "the Itunes validation server is not currently available");
                        return false;
                    case 21008:
                        Logger.logMsg(Logger.ERROR, "the validation server answers that it should be sent to the prod server, but it is the case");
                        return false;
                    case 21007:
                        if (i == 0) {
                            Logger.logMsg(Logger.DEBUG, "the ciphered receipt is a test receipt");
                            urlItunes = "https://sandbox.itunes.apple.com/verifyReceipt";
                        }
                        break;
                    case 21003:
                        Logger.logMsg(Logger.ERROR, "Itunes answers that the cipheredReceipt could not be authenticated");
                    case 21006:
                        Logger.logMsg(Logger.ERROR, "Itunes answers that the cipheredReceipt is valid but the subscription has expired");
                        return false;
                    default:
                        Logger.logMsg(Logger.ERROR, "Unknown code gets from Itunes");
                        return false;
                }
            }
            Logger.logMsg(Logger.ERROR, "Itunes answers that it should be sent to the test server, but it is the case");
            return false;
        } catch (Exception e) {
            Logger.logMsg(Logger.ERROR, "Unexpected error encountered during the validation of the receipt");
            return false;
        }
    }

    private Integer extractStatusCode(String itunesResponse) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode jsonNode = mapper.readTree(itunesResponse);
            String codeString = jsonNode.path("status").asText();
            if (codeString == null) {
                Logger.logMsg(Logger.ERROR, "unable to locate the field 'status' from the response gets from itunes");
                return null;
            }
            return Integer.parseInt(codeString);
        } catch (NullPointerException | IOException e) {
            Logger.logMsg(Logger.ERROR, "unable to parse the response gets from itunes");
            return null;
        } catch (NumberFormatException e) {
            Logger.logMsg(Logger.ERROR, "the format of the status code from the field 'status' is invalid");
            return null;
        } catch (Exception e) {
            Logger.logMsg(Logger.ERROR, "unexpected error encountered during the extraction of the code from the itunes response");
            return null;
        }
    }

    public String postRequest(String url, String cipheredReceipt) {

        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(url);
            String param = "{\"receipt-data\":\"" + cipheredReceipt + "\"}";

            HttpEntity entity = new ByteArrayEntity(param.getBytes());
            post.setEntity(entity);

            response = super.buildHttpClient().execute(post);

            int code = response.getStatusLine().getStatusCode();
            entity = response.getEntity();

            String itunesResponse = EntityUtils.toString(entity);

            if(code >= 200 && code < 300){
                return itunesResponse;
            }
            Logger.logMsg(Logger.ERROR, "wring status code gets from Itunes");
            return null;
        } catch (NullPointerException e) {
            Logger.logMsg(Logger.ERROR, "null response gets from Itunes");
            return null;
        } catch (Exception e) {
            Logger.logMsg(Logger.ERROR, "unexpected error code gets from Itunes");
            return null;
        } finally {
            close(response);
        }
    }
}
