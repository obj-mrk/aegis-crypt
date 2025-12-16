package cryptocore.api;

public enum CryptoAlgorithm {
    // симметричное шифрование (лабораторный MVP)
    KUZNECHIK_ECB_PKCS7,

    // хеш
    STREEBOG_256,
    STREEBOG_512,

    // асимметрия
    RSA
}
