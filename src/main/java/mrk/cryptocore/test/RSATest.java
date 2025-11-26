package mrk.cryptocore.test;

import mrk.cryptocore.rsa.RSA;
import mrk.cryptocore.rsa.RSAKeyPair;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RSATest {
    @Test
    void testRSAEncryptionDecryption() {
        // Arrange - подготавливаем данные
        int bits = 2048; // Используем меньший размер для быстрых тестов
        String originalText = "RSA is win";
        BigInteger message = new BigInteger(1, originalText.getBytes(StandardCharsets.UTF_8));

        // Act - выполняем действия
        RSAKeyPair keys = new RSAKeyPair(bits, true);
        BigInteger encrypted = RSA.encrypt(message, keys);
        BigInteger decrypted = RSA.decrypt(encrypted, keys);
        String decryptedText = new String(decrypted.toByteArray(), StandardCharsets.UTF_8);

        // Assert - проверяем результаты
        assertEquals(originalText, decryptedText);
        assertNotEquals(message, encrypted);
    }
}
