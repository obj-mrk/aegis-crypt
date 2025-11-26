package mrk.cryptocore.kuznechik;

import java.util.Arrays;

public class KuznechikMain {
    public static void main(String[] args) {
        // 1. Ключ 256 бит (здесь просто пример, в реальном коде — SecureRandom)
        byte[] key = new byte[32];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) i; // 00 01 02 ... 1F
        }

        // 2. Открытый текст 128 бит
        byte[] plaintext = new byte[16];
        for (int i = 0; i < plaintext.length; i++) {
            plaintext[i] = (byte) (0xFF - i); // просто для примера
        }

        byte[] ciphertext = new byte[16];
        byte[] decrypted  = new byte[16];

        KuznechikEngine engine = new KuznechikEngine();

        // --- ШИФРОВАНИЕ ---
        engine.init(true, key);
        engine.encryptBlock(plaintext, 0, ciphertext, 0);

        // --- РАСШИФРОВАНИЕ ---
        engine.init(false, key);
        engine.decryptBlock(ciphertext, 0, decrypted, 0);

        System.out.println("Plain : " + bytesToHex(plaintext));
        System.out.println("Cipher: " + bytesToHex(ciphertext));
        System.out.println("Decr  : " + bytesToHex(decrypted));
        System.out.println("Equal : " + Arrays.equals(plaintext, decrypted));
    }

    private static String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
