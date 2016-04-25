package windows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.sun.media.jfxmedia.logging.Logger;
import core.AbstractValidator;
import core.Channel;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import windows.dto.WindowsReceiptDto;

import org.apache.http.client.methods.CloseableHttpResponse;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.*;
import java.util.List;

public final class WindowsValidator extends AbstractValidator {
    private static final String windows_url = "https://lic.apps.microsoft.com/licensing/certificateserver/";
    private static final DocumentBuilderFactory documentBuilderFactory;
    private static XMLSignatureFactory xmlSignatureFactory;

    static {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();

        String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
        Provider provider;
        try {
            provider = (Provider) Class.forName(providerName).newInstance();
            xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM", provider);
        } catch (Exception e) {
            Logger.logMsg(Logger.ERROR, "Unable to initialize the xml signature factory");
            throw new RuntimeException();
        }
    }

    public WindowsValidator(String receipt){
        channel = Channel.WINDOWS;
        this.receipt = receipt;
    }

    @Override
    public boolean validate(){
        try {
            WindowsReceiptDto receiptDto = buildDto();
            return validReceipt(receiptDto);
        }catch (IOException e){
            Logger.logMsg(Logger.ERROR, "Unable to parse the receipt given in input");
            return false;
        }
    }

    public WindowsReceiptDto buildDto() throws IOException {
        ObjectMapper mapper = new XmlMapper();
        WindowsReceiptDto receiptDto = mapper.readValue(receipt, WindowsReceiptDto.class);
        receiptDto.setReceipt(receipt);
        return receiptDto;
    }

    private boolean validReceipt(WindowsReceiptDto receiptDto){
        try {
            String receipt = receiptDto.getReceipt().replaceAll(">\\s+<", "><");
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receipt.getBytes());
            Document document = documentBuilder.parse(byteArrayInputStream);

            NodeList nl = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0) {
                Logger.logMsg(Logger.ERROR, "Unable to extract the signature from the receipt");
                return false;
            }

            String certificateId = document.getDocumentElement().getAttribute("CertificateId");

            if (certificateId == null || certificateId.equals("")){
                Logger.logMsg(Logger.ERROR, "The certificate id from the receipt is empty or invalid");
                return false;
            }

            PublicKey publicKey = getPublicKey(certificateId);
            if (publicKey == null) {
                Logger.logMsg(Logger.ERROR, "null key built from the receipt");
                return false;
            }

            DOMValidateContext validateContext = new DOMValidateContext(publicKey, nl.item(0));
            XMLSignature xmlSignature = xmlSignatureFactory.unmarshalXMLSignature(validateContext); // MarshalException

            if (!xmlSignature.validate(validateContext)) {
                Logger.logMsg(Logger.ERROR, "The validation has failed");
                return false;
            }

            Logger.logMsg(Logger.INFO, "The receipt is validated");
            return true;
        } catch (SAXException | IOException e) {
            Logger.logMsg(Logger.INFO, "Error encountered during the parsing of the receipt");
            return false;
        } catch (ParserConfigurationException e) {
            Logger.logMsg(Logger.INFO, "A documentBuilder which satisfies the requested configuration cannot be created");
            return false;
        } catch (MarshalException e) {
            Logger.logMsg(Logger.INFO, "Unable to extract the signature from the receipt");
            return false;
        } catch (XMLSignatureException e) {
            Logger.logMsg(Logger.INFO, "The type of the generated public key seems to be invalid");
            return false;
        } catch (Exception e) {
            Logger.logMsg(Logger.INFO, "Unexpected error encountered during the validation of the receipt");
            return false;
        }
    }

    public PublicKey getPublicKey(String certificateId) {

        CloseableHttpResponse response = null;

        try {
            List<BasicNameValuePair> entities = Lists.newArrayList(new BasicNameValuePair("cid", certificateId));
            String params = URLEncodedUtils.format(entities, Charsets.UTF_8);
            String url = windows_url + "?" + params;
            HttpGet get = new HttpGet(url);
            response = httpClient.execute(get);


            int code = response.getStatusLine().getStatusCode();

            if (code != 200) {
                Logger.logMsg(Logger.ERROR, "the certificate id is invalid or an unexpected error has been encountered by the validation server");
                return null;
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                Logger.logMsg(Logger.ERROR, "the certificate gets from the validation server is null");
                return null;
            }
            String stringCertificate = EntityUtils.toString(entity);
            stringCertificate = stringCertificate
                    .replaceAll("-----BEGIN CERTIFICATE-----", "")
                    .replaceAll("-----END CERTIFICATE-----", "")
                    .replaceAll("\\s+", "");
            byte data[] = Base64.decodeBase64(stringCertificate.getBytes());
            CertificateFactory f = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) f.generateCertificate(new ByteArrayInputStream(data));
            certificate.checkValidity();

            PublicKey pubkey = certificate.getPublicKey();
            Logger.logMsg(Logger.DEBUG, "A public key is has successfully been generated");
            return pubkey;
        } catch (IOException e) {
            if (response == null)
                Logger.logMsg(Logger.DEBUG, "Error encountered while sending request to the validation server");
            else
                Logger.logMsg(Logger.DEBUG, "Error encountered while extracting data from the response gets from the validation server");
            return null;
        } catch (CertificateExpiredException e) {
            Logger.logMsg(Logger.DEBUG, "The certificate gets from the validation server is expired");
            return null;
        } catch (CertificateNotYetValidException e) {
            Logger.logMsg(Logger.DEBUG, "The certificate gets from the validation server is not yet valid");
            return null;
        } catch (CertificateException e) {
            Logger.logMsg(Logger.DEBUG, "Unable to parse the certificate (no provider supports a CertificateFactorySpi implementation) gets from the windows certificate server");
            return null;
        } catch (Exception e) {
            Logger.logMsg(Logger.DEBUG, "Unexpected error encountered during the generation of the public key");
            return null;
        } finally {
            close(response);
        }
    }
}
