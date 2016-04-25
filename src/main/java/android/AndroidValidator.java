package android;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.media.jfxmedia.logging.Logger;
import core.AbstractValidator;
import core.Channel;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.AbstractMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class AndroidValidator extends AbstractValidator{
    private String refreshToken;
    private static final String urlRefreshToken="https://accounts.google.com/o/oauth2/token";
    private static final String url="https://buy.itunes.apple.com/verifyReceipt";
    private String clientId;
    private String clientSecret;
    private String keyPlayStore;
    private String signature;
    private AndroidReceiptDto receiptDto;
    private PublicKey publicKey;

    private static final String FORMATTED_BODY = "grant_type=refresh_token&client_id=%s&client_secret=%s&refresh_token=%s";
    private static final AbstractMap.SimpleEntry<String, String> HEADER = new AbstractMap.SimpleEntry<>("Content-Type", "application/x-www-form-urlencoded");



    public AndroidValidator(String receipt, String signature, String keyPlayStore){
        channel = Channel.ANDROID;
        this.receipt = receipt;
        this.signature = signature;

        byte[] publicKeyBase64 = Base64.decodeBase64(keyPlayStore.getBytes());

        try {
            this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBase64));
        }catch (NoSuchAlgorithmException e){
            Logger.logMsg(Logger.ERROR, "NoSuchAlgorithmException encountered during the build of the public key from the playstore key");
        }catch (InvalidKeySpecException e){
            Logger.logMsg(Logger.ERROR, "InvalidKeySpecException encountered during the build of the public key from the playstore key");
        }
    }

    public AndroidValidator(String receipt, String signature, String refreshToken, String clientId, String clientSecret, String keyPlayStore) {
        this(receipt, signature, keyPlayStore);
        this.refreshToken = refreshToken;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.keyPlayStore = keyPlayStore;
    }

    public AndroidReceiptDto buildDto() throws IOException {
        if(receiptDto == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                receiptDto = mapper.readValue(receipt, AndroidReceiptDto.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            receiptDto.setReceipt(receipt);
            receiptDto.setSignature(signature);
        }
        return receiptDto;
    }

    public boolean validate(){
        try {
            buildDto();
        }catch (IOException e){
            Logger.logMsg(Logger.ERROR, "error encountered during the generation of the receiptDto");
        }
        return offlineValidation() && onlineValidation();
    }

    public boolean offlineValidation() {
        byte[] signatureBase64 = Base64.decodeBase64(StringEscapeUtils.unescapeJava(signature).getBytes());

        try {
            Signature signatureObj = Signature.getInstance("SHA1withRSA");
            signatureObj.initVerify(publicKey);
            signatureObj.update(receipt.getBytes());

            if (signatureObj.verify(signatureBase64)) {
                Logger.logMsg(Logger.DEBUG, "the receipt is validated");
                return true;
            } else
                Logger.logMsg(Logger.ERROR, "the receipt is invalid");
        } catch (SignatureException e) {
            Logger.logMsg(Logger.ERROR, "the receipt and/or the signature is invalid");
        } catch (Exception e) {
            Logger.logMsg(Logger.ERROR, "unexpected error encountered during the validation of the receipt");
        }
        return false;
    }

    public boolean refreshToken() {
        String context = null;

        boolean hasLock = mLock.tryLock();
        if (hasLock) {
            CloseableHttpResponse response = null;
            try {

                HttpPost post = new HttpPost(urlRefreshToken);
                HttpEntity entity = new StringEntity(String.format(FORMATTED_BODY, clientId, clientSecret, refreshToken));
                post.setEntity(entity);
                post.setHeader(HEADER.getKey(), HEADER.getValue());

                response = httpClient.execute(post);

                int code = response.getStatusLine().getStatusCode();
                entity = response.getEntity();

                String playstoreResponse = EntityUtils.toString(entity);

                if (code >= 200 && code < 300) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(playstoreResponse);

                    accessToken = jsonNode.path("access_token").textValue();
                    tokenType = jsonNode.path("token_type").textValue();
                    expiresIn = jsonNode.path("expires_in").longValue();
                    accessTokenInitializationTime = System.currentTimeMillis();
                    return true;
                } else {
                    Logger.logMsg(Logger.ERROR, "unable to refresh the token");
                }
            } catch (UnsupportedEncodingException e) {
                Logger.logMsg(Logger.ERROR, "error while building the entity from the Playstore refresh token server");
            } catch (NullPointerException e) {
                Logger.logMsg(Logger.ERROR, "null response gets from Playstore refresh token server");
            } catch (IOException e) {
                Logger.logMsg(Logger.ERROR, "I/O error encountered while sending request to the Playstore refresh token server");
            } catch (Exception e) {
                Logger.logMsg(Logger.ERROR, "unexpected error encountered during the refresh of the token");
            } finally {
                close(response);
                mLock.unlock();
            }
            return false;
        } else {
            try {
                mLock.lock();
            } finally {
                mLock.unlock();
            }
            return true;
        }
    }

    private volatile String accessToken = null;
    private volatile String tokenType = null;
    private volatile long accessTokenInitializationTime = 0;
    private volatile long expiresIn = 0;
    private static final int DELTA = 300;
    private final Lock mLock = new ReentrantLock();

    public boolean onlineValidation() {

        String packageName = receiptDto.getPackageName();
        String productId = receiptDto.getProductId();
        String purchaseToken = receiptDto.getPurchaseToken();

        CloseableHttpResponse response = null;
        CloseableHttpClient client = buildHttpClient();

        try {
            if (System.currentTimeMillis() + DELTA * 1000 - accessTokenInitializationTime > expiresIn * 1000) {
                refreshToken();
            }
            HttpGet get = new HttpGet(String.format(url, packageName, productId, purchaseToken));
            get.addHeader("Authorization", tokenType + " " + accessToken);
            response = client.execute(get);
            int code = response.getStatusLine().getStatusCode();

            if (code == 401) {
                refreshToken();
                get.addHeader("Authorization", tokenType + " " + accessToken);
                response = client.execute(get);
                code = response.getStatusLine().getStatusCode();
            }

            HttpEntity entity = response.getEntity();

            String playstoreResponse = EntityUtils.toString(entity);
            ObjectMapper mapper = new ObjectMapper();

            if (code >= 200 && code < 300) {
                JsonNode jsonNode = mapper.readTree(playstoreResponse);
                int consumptionState = jsonNode.path("consumptionState").intValue();
                if (consumptionState == 0) {
                    Logger.logMsg(Logger.DEBUG, "the receipt is validated and notyet consumed");
                } else if (consumptionState == 1) {
                    Logger.logMsg(Logger.WARNING, "the receipt is validated, but already consumed");
                } else {
                    Logger.logMsg(Logger.WARNING, "the receipt is validated, but the consumption state is invalid");
                }
                return true;
            } else
                Logger.logMsg(Logger.ERROR, "the receipt is invalid");
        } catch (NullPointerException e) {
            Logger.logMsg(Logger.ERROR, "null response gets from playstore validation server");
        } catch (IOException e) {
            Logger.logMsg(Logger.ERROR, "I/O error encountered while sending request to the Playstore validation server");
        } catch (Exception e) {
            Logger.logMsg(Logger.ERROR, "Unexpected error encountered while sending request to the Playstore validation server");
        } finally {
            close(response);
        }
        return false;
    }
}
