package mrk.cryptocore.rsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        int bits = 32768;

        String text = "RSA is win";

        // 1 — знак (положительное число), чтобы BigInteger не стал отрицательным
        BigInteger message = new BigInteger(1, text.getBytes(StandardCharsets.UTF_8));

        // Генерируем ключ с CRT-параметрами
        RSAKeyPair keys = new RSAKeyPair(bits, true);

        BigInteger encryptedMessage = RSA.encrypt(message, keys);
        BigInteger decryptedMessage = RSA.decrypt(encryptedMessage, keys);

        String decryptedText = new String(decryptedMessage.toByteArray(), StandardCharsets.UTF_8);

        System.out.println("Original text:   " + text);
        System.out.println("Decrypted text:  " + decryptedText);
        System.out.println("Equal:           " + text.equals(decryptedText));
    }
}
