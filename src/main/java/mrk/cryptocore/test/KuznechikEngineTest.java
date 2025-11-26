package mrk.cryptocore.test;

import mrk.cryptocore.kuznechik.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class KuznechikEngineTest {

    @Test
    void testGOSTVector() {
        // данные из RFC 7801, раздел 5.4–5.5
        byte[] key = Hex.fromHex(
                "8899aabbccddeeff0011223344556677" +
                        "fedcba98765432100123456789abcdef"
        );

        byte[] plaintext  = Hex.fromHex("1122334455667700ffeeddccbbaa9988");
        byte[] ciphertext = Hex.fromHex("7f679d90bebc24305a468d42b9d4edcd");

        byte[] outEnc = new byte[16];
        byte[] outDec = new byte[16];

        KuznechikEngine engine = new KuznechikEngine();

        // Шифрование
        engine.init(true, key);
        engine.encryptBlock(plaintext, 0, outEnc, 0);
        assertArrayEquals(ciphertext, outEnc, "Ciphertext must match RFC 7801");

        // Расшифрование
        engine.init(false, key);
        engine.decryptBlock(ciphertext, 0, outDec, 0);
        assertArrayEquals(plaintext, outDec, "Plaintext must be recovered");
    }
}