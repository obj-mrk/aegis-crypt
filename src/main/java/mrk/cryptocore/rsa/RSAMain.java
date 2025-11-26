package mrk.cryptocore.rsa;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class RSAMain {
    public static void main(String[] args) {
        int bits = 32768;

        String text = "RSA is win";

        // 1 — знак (положительное число), чтобы BigInteger не стал отрицательным
        BigInteger message = new BigInteger(1, text.getBytes(StandardCharsets.UTF_8));

        // Генерируем ключ с CRT-параметрами
        long startTime = System.currentTimeMillis();
        RSAKeyPair keys = new RSAKeyPair(bits, true);
        long endTime = System.currentTimeMillis();

        long durationMs = endTime - startTime;
        displayTime(durationMs);

        BigInteger encryptedMessage = RSA.encrypt(message, keys);
        BigInteger decryptedMessage = RSA.decrypt(encryptedMessage, keys);

        String decryptedText = new String(decryptedMessage.toByteArray(), StandardCharsets.UTF_8);

        System.out.println("Original text:   " + text);
        System.out.println("Decrypted text:  " + decryptedText);
        System.out.println("Equal:           " + text.equals(decryptedText));
    }

    private static void displayTime(long durationMs) {
        long seconds = durationMs / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        long remainingMs = durationMs % 1000;

        if (minutes > 0) {
            System.out.println("Генерация ключа заняла: " + minutes + " мин " +
                    remainingSeconds + " сек " + remainingMs + " мс");
        } else if (seconds > 0) {
            System.out.println("Генерация ключа заняла: " + seconds + " сек " +
                    remainingMs + " мс");
        } else {
            System.out.println("Генерация ключа заняла: " + durationMs + " мс");
        }

        // Альтернативный вывод в секундах с дробной частью
        double secondsExact = durationMs / 1000.0;
        System.out.println("Точное время: " + String.format("%.3f", secondsExact) + " секунд");
    }
}