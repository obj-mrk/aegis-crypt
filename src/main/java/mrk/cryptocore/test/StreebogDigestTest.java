package mrk.cryptocore.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import mrk.cryptocore.streebog.StreebogDigest;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;

public class StreebogDigestTest {

    @Test
    void testEmptyString_512() {
        StreebogDigest d = new StreebogDigest(StreebogDigest.DIGEST_SIZE_512);
        byte[] hash = d.digest();

        byte[] expected = hex(
                "8e945da209aa869f0455928529bcae46" +
                        "79e9873ab707b55315f56ceb98bef0a7" +
                        "362f715528356ee83cda5f2aac4c6ad2" +
                        "ba3a715c1bcd81cb8e9f90bf4c1c1a8a"
        );
        assertArrayEquals(expected, hash);
    }

    @Test
    void testEmptyString_256() {
        StreebogDigest d = new StreebogDigest(StreebogDigest.DIGEST_SIZE_256);
        byte[] hash = d.digest();

        byte[] expected = hex(
                "3f539a213e97c802cc229d474c6aa32a" +
                        "825a360b2a933a949fd925208d9ce1bb"
        );
        assertArrayEquals(expected, hash);
    }

    @Test
    void testQuickBrownFox_256() {
        StreebogDigest d = new StreebogDigest(StreebogDigest.DIGEST_SIZE_256);
        d.update("The quick brown fox jumps over the lazy dog".getBytes(StandardCharsets.UTF_8));
        byte[] hash = d.digest();

        byte[] expected = hex(
                "3e7dea7f2384b6c5a3d0e24aaa29c05e" +
                        "89ddd762145030ec22c71a6db8b2c1f4"
        );
        assertArrayEquals(expected, hash);
    }

    @Test
    void testQuickBrownFox_512() {
        StreebogDigest d = new StreebogDigest(StreebogDigest.DIGEST_SIZE_512);
        d.update("The quick brown fox jumps over the lazy dog".getBytes(StandardCharsets.UTF_8));
        byte[] hash = d.digest();

        byte[] expected = hex(
                "d2b793a0bb6cb5904828b5b6dcfb443b" +
                        "b8f33efc06ad09368878ae4cdc8245b9" +
                        "7e60802469bed1e7c21a64ff0b179a6a" +
                        "1e0bb74d92965450a0adab69162c00fe"
        );
        assertArrayEquals(expected, hash);
    }

    private static byte[] hex(String s) {
        int len = s.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) Integer.parseInt(s.substring(i, i + 2), 16);
        }
        return out;
    }
}
