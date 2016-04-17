package windows;

import java.time.LocalDateTime;
import java.util.List;

public class WindowsReceiptDto {

    private Receipt receipt;


    public class Receipt {
        private String xmlns;
        private float version;
        private String certificateId;

        private ProductReceipt productReceipt;
        private Signature signature;
    }

    public class ProductReceipt {
        private String purchasePrice;
        private LocalDateTime purchaseDate;
        private String id;
        private String appId;
        private String productId;
        private String productType;
        private String publisherUserId;
        private String publisherDeviceId;
        private String microsoftProductId;
        private String microsoftAppId;
    }

    public class Signature {
        private SignedInfo signedInfo;
        private String signatureValue;
    }

    public class SignedInfo {
        private CanonicalizationMethod canonicalizationMethod;
        private SignatureMethod signatureMethod;
        private Reference reference;
    }

    public class CanonicalizationMethod {
        private String algorithm;
    }

    public class SignatureMethod {
        private String algorithm;
    }

    public class Reference {
        private String uri;
        private List<Transform> transforms;
        private DigestMethod digestMethod;
        private String digestValue;
    }

    public class Transform {
        private String algorithm;
    }

    public class DigestMethod {
        private String algorithm;
    }
}
