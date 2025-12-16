package cryptocore.api;

import cryptocore.rsa.RSAKeyPair;

public interface CryptoService {

    // --- symmetric ---
    byte[] encryptSymmetric(CryptoAlgorithm alg, byte[] key, byte[] plaintext);

    byte[] decryptSymmetric(CryptoAlgorithm alg, byte[] key, byte[] ciphertext);

    // --- hash ---
    byte[] hash(CryptoAlgorithm alg, byte[] data);

    // --- asymmetric (textbook RSA, для учебных целей) ---
    RSAKeyPair generateRsaKeyPair(int bits, boolean useCrt);

    byte[] encryptAsymmetric(CryptoAlgorithm alg, RSAKeyPair keyPair, byte[] plaintext);

    byte[] decryptAsymmetric(CryptoAlgorithm alg, RSAKeyPair keyPair, byte[] ciphertext);
}
