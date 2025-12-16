package aegiscrypt.secrets.service;

import com.fasterxml.jackson.core.type.TypeReference;
import cryptocore.api.CryptoAlgorithm;

import cryptocore.api.CryptoService;
import cryptocore.rsa.RSAKeyPair;
import aegiscrypt.secrets.domain.SecretAlgorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SecretCryptoService {

    private final CryptoService cryptoService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final SecureRandom RND = new SecureRandom();

    // RSA 4096 — как ты решил
    private static final int RSA_BITS = 4096;

    public CryptoResult encrypt(String plaintext, SecretAlgorithm algorithm) {
        byte[] data = plaintext.getBytes();

        return switch (algorithm) {
            case KUZNECHIK -> encryptKuznechik(data);
            case RSA_TEXTBOOK -> encryptRsa(data);
            case HYBRID_RSA_KUZNECHIK -> encryptHybrid(data);
        };
    }

    /* ---------------- implementations ---------------- */

    private CryptoResult encryptKuznechik(byte[] data) {
        byte[] key = new byte[32];
        RND.nextBytes(key);

        byte[] cipher = cryptoService.encryptSymmetric(
                CryptoAlgorithm.KUZNECHIK_ECB_PKCS7, key, data
        );

        return new CryptoResult(
                Base64.getEncoder().encodeToString(cipher),
                toJson(Map.of(
                        "keyBase64", Base64.getEncoder().encodeToString(key)
                ))
        );
    }

    private CryptoResult encryptRsa(byte[] data) {
        RSAKeyPair kp = cryptoService.generateRsaKeyPair(RSA_BITS, true);

        // RSA ограничение
        if (data.length > 400) {
            throw new IllegalArgumentException("RSA_TEXTBOOK supports only short plaintexts");
        }

        byte[] cipher = cryptoService.encryptAsymmetric(
                CryptoAlgorithm.RSA, kp, data
        );

        return new CryptoResult(
                Base64.getEncoder().encodeToString(cipher),
                toJson(Map.of(
                        "rsaPublicN", kp.getN().toString(),
                        "rsaPublicE", kp.getE().toString(),
                        "rsaPrivateD", kp.getD().toString()
                ))
        );
    }

    private CryptoResult encryptHybrid(byte[] data) {
        // 1) Kuznechik key
        byte[] key = new byte[32];
        RND.nextBytes(key);

        byte[] encryptedData = cryptoService.encryptSymmetric(
                CryptoAlgorithm.KUZNECHIK_ECB_PKCS7, key, data
        );

        // 2) RSA wraps key
        RSAKeyPair kp = cryptoService.generateRsaKeyPair(RSA_BITS, true);
        byte[] wrappedKey = cryptoService.encryptAsymmetric(
                CryptoAlgorithm.RSA, kp, key
        );

        return new CryptoResult(
                Base64.getEncoder().encodeToString(encryptedData),
                toJson(Map.of(
                        "wrappedKeyBase64", Base64.getEncoder().encodeToString(wrappedKey),
                        "rsaPublicN", kp.getN().toString(),
                        "rsaPublicE", kp.getE().toString(),
                        "rsaPrivateD", kp.getD().toString()
                ))
        );
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public record CryptoResult(String ciphertextBase64, String metaJson) {
    }

    public String decrypt(String ciphertextBase64, SecretAlgorithm algorithm, String metaJson) {
        byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);
        Map<String, Object> meta = parseJson(metaJson);

        return switch (algorithm) {
            case KUZNECHIK -> decryptKuznechik(ciphertext, meta);
            case RSA_TEXTBOOK -> decryptRsa(ciphertext, meta);
            case HYBRID_RSA_KUZNECHIK -> decryptHybrid(ciphertext, meta);
        };
    }

    private String decryptKuznechik(byte[] ciphertext, Map<String, Object> meta) {
        String keyB64 = requireString(meta, "keyBase64");
        byte[] key = Base64.getDecoder().decode(keyB64);

        byte[] pt = cryptoService.decryptSymmetric(CryptoAlgorithm.KUZNECHIK_ECB_PKCS7, key, ciphertext);
        return new String(pt);
    }

    private String decryptRsa(byte[] ciphertext, Map<String, Object> meta) {
        RSAKeyPair kp = rsaKeyPairFromMeta(meta);

        byte[] pt = cryptoService.decryptAsymmetric(CryptoAlgorithm.RSA, kp, ciphertext);
        return new String(pt);
    }

    private String decryptHybrid(byte[] ciphertext, Map<String, Object> meta) {
        RSAKeyPair kp = rsaKeyPairFromMeta(meta);

        String wrappedKeyB64 = requireString(meta, "wrappedKeyBase64");
        byte[] wrappedKey = Base64.getDecoder().decode(wrappedKeyB64);

        // 1) unwrap symmetric key via RSA
        byte[] key = cryptoService.decryptAsymmetric(CryptoAlgorithm.RSA, kp, wrappedKey);

        // 2) decrypt data via Kuznechik
        byte[] pt = cryptoService.decryptSymmetric(CryptoAlgorithm.KUZNECHIK_ECB_PKCS7, key, ciphertext);
        return new String(pt);
    }

    private RSAKeyPair rsaKeyPairFromMeta(Map<String, Object> meta) {
        // meta хранит BigInteger в виде строк
        BigInteger n = new BigInteger(requireString(meta, "rsaPublicN"));
        BigInteger e = new BigInteger(requireString(meta, "rsaPublicE"));
        BigInteger d = new BigInteger(requireString(meta, "rsaPrivateD"));
        return new RSAKeyPair(n, e, d);
    }

    private Map<String, Object> parseJson(String json) {
        try {
            if (json == null || json.isBlank()) return Map.of();
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse metaJson", e);
        }
    }

    private static String requireString(Map<String, Object> meta, String key) {
        Object v = meta.get(key);
        if (v == null) throw new IllegalStateException("metaJson missing key: " + key);
        return String.valueOf(v);
    }
}
