package cryptocore.impl;

import cryptocore.api.CryptoAlgorithm;
import cryptocore.api.CryptoService;
import cryptocore.kuznechik.KuznechikEngine;
import cryptocore.rsa.RSA;
import cryptocore.rsa.RSAKeyPair;
import cryptocore.streebog.StreebogDigest;

import java.math.BigInteger;
import java.util.Arrays;

public final class DefaultCryptoService implements CryptoService {

    // ----------------- symmetric -----------------

    @Override
    public byte[] encryptSymmetric(CryptoAlgorithm alg, byte[] key, byte[] plaintext) {
        return switch (alg) {
            case KUZNECHIK_ECB_PKCS7 -> kuznechikEcbEncryptPkcs7(key, plaintext);
            default -> throw new IllegalArgumentException("Unsupported symmetric algorithm: " + alg);
        };
    }

    @Override
    public byte[] decryptSymmetric(CryptoAlgorithm alg, byte[] key, byte[] ciphertext) {
        return switch (alg) {
            case KUZNECHIK_ECB_PKCS7 -> kuznechikEcbDecryptPkcs7(key, ciphertext);
            default -> throw new IllegalArgumentException("Unsupported symmetric algorithm: " + alg);
        };
    }

    // ----------------- hash -----------------

    @Override
    public byte[] hash(CryptoAlgorithm alg, byte[] data) {
        if (data == null) throw new IllegalArgumentException("data is null");

        return switch (alg) {
            case STREEBOG_256 -> streebogDigest(StreebogDigest.DIGEST_SIZE_256, data);
            case STREEBOG_512 -> streebogDigest(StreebogDigest.DIGEST_SIZE_512, data);
            default -> throw new IllegalArgumentException("Unsupported hash algorithm: " + alg);
        };
    }

    // ----------------- asymmetric -----------------

    @Override
    public RSAKeyPair generateRsaKeyPair(int bits, boolean useCrt) {
        if (bits < 1024) {
            throw new IllegalArgumentException("RSA bits too small: " + bits);
        }
        return new RSAKeyPair(bits, useCrt);
    }

    @Override
    public byte[] encryptAsymmetric(CryptoAlgorithm alg, RSAKeyPair keyPair, byte[] plaintext) {
        if (alg != CryptoAlgorithm.RSA) {
            throw new IllegalArgumentException("Unsupported asymmetric algorithm: " + alg);
        }
        if (keyPair == null) throw new IllegalArgumentException("keyPair is null");
        if (plaintext == null) throw new IllegalArgumentException("plaintext is null");

        BigInteger m = toUnsignedBigInteger(plaintext);

        // ВАЖНО: в твоей RSA-реализации есть проверка "message < n" (и это правильно)
        // Поэтому для больших данных RSA напрямую нельзя использовать.
        BigInteger c = RSA.encrypt(m, keyPair);

        return toUnsignedByteArray(c);
    }

    @Override
    public byte[] decryptAsymmetric(CryptoAlgorithm alg, RSAKeyPair keyPair, byte[] ciphertext) {
        if (alg != CryptoAlgorithm.RSA) {
            throw new IllegalArgumentException("Unsupported asymmetric algorithm: " + alg);
        }
        if (keyPair == null) throw new IllegalArgumentException("keyPair is null");
        if (ciphertext == null) throw new IllegalArgumentException("ciphertext is null");

        BigInteger c = toUnsignedBigInteger(ciphertext);
        BigInteger m = RSA.decrypt(c, keyPair);

        return toUnsignedByteArray(m);
    }

    // ================== internals ==================

    private static byte[] streebogDigest(int digestSize, byte[] data) {
        StreebogDigest d = new StreebogDigest(digestSize);
        d.update(data);
        return d.digest();
    }

    private static byte[] kuznechikEcbEncryptPkcs7(byte[] key, byte[] plaintext) {
        if (key == null || key.length != 32) {
            throw new IllegalArgumentException("Kuznechik key must be 32 bytes (256-bit)");
        }
        if (plaintext == null) throw new IllegalArgumentException("plaintext is null");

        byte[] padded = pkcs7Pad(plaintext, 16);

        KuznechikEngine engine = new KuznechikEngine();
        engine.init(true, key);

        byte[] out = new byte[padded.length];
        for (int off = 0; off < padded.length; off += 16) {
            engine.encryptBlock(padded, off, out, off);
        }
        return out;
    }

    private static byte[] kuznechikEcbDecryptPkcs7(byte[] key, byte[] ciphertext) {
        if (key == null || key.length != 32) {
            throw new IllegalArgumentException("Kuznechik key must be 32 bytes (256-bit)");
        }
        if (ciphertext == null) throw new IllegalArgumentException("ciphertext is null");
        if (ciphertext.length == 0 || ciphertext.length % 16 != 0) {
            throw new IllegalArgumentException("Ciphertext must be non-empty multiple of 16 bytes");
        }

        KuznechikEngine engine = new KuznechikEngine();
        engine.init(false, key);

        byte[] tmp = new byte[ciphertext.length];
        for (int off = 0; off < ciphertext.length; off += 16) {
            engine.decryptBlock(ciphertext, off, tmp, off);
        }
        return pkcs7Unpad(tmp, 16);
    }

    private static byte[] pkcs7Pad(byte[] data, int blockSize) {
        int padLen = blockSize - (data.length % blockSize);
        byte[] out = Arrays.copyOf(data, data.length + padLen);
        Arrays.fill(out, data.length, out.length, (byte) padLen);
        return out;
    }

    private static byte[] pkcs7Unpad(byte[] data, int blockSize) {
        if (data.length == 0 || data.length % blockSize != 0) {
            throw new IllegalArgumentException("Invalid padded data length");
        }
        int padLen = data[data.length - 1] & 0xFF;
        if (padLen < 1 || padLen > blockSize) {
            throw new IllegalArgumentException("Invalid PKCS7 padding");
        }
        for (int i = data.length - padLen; i < data.length; i++) {
            if ((data[i] & 0xFF) != padLen) {
                throw new IllegalArgumentException("Invalid PKCS7 padding");
            }
        }
        return Arrays.copyOf(data, data.length - padLen);
    }

    /**
     * BigInteger(byte[]) воспринимает массив как signed two's complement.
     * Нам нужно однозначное "беззнаковое" преобразование.
     */
    private static BigInteger toUnsignedBigInteger(byte[] bytes) {
        return new BigInteger(1, bytes);
    }

    /**
     * BigInteger.toByteArray() может добавить ведущий 0x00 для знака.
     * Убираем его, чтобы представление было компактным.
     */
    private static byte[] toUnsignedByteArray(BigInteger value) {
        byte[] raw = value.toByteArray();
        if (raw.length > 1 && raw[0] == 0x00) {
            return Arrays.copyOfRange(raw, 1, raw.length);
        }
        return raw;
    }
}
