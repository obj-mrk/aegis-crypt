package aegiscrypt.auth.totp;

import java.util.Arrays;

public final class Base32 {

    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();

    // reverse lookup: ASCII -> value
    private static final int[] LOOKUP = new int[128];
    static {
        Arrays.fill(LOOKUP, -1);
        for (int i = 0; i < ALPHABET.length; i++) {
            LOOKUP[ALPHABET[i]] = i;
        }
        // allow lowercase
        for (int i = 0; i < ALPHABET.length; i++) {
            char c = Character.toLowerCase(ALPHABET[i]);
            LOOKUP[c] = i;
        }
    }

    private Base32() {}

    public static String encode(byte[] data) {
        if (data == null || data.length == 0) return "";

        StringBuilder out = new StringBuilder((data.length * 8 + 4) / 5);

        int buffer = 0;
        int bitsLeft = 0;

        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;

            while (bitsLeft >= 5) {
                int idx = (buffer >> (bitsLeft - 5)) & 0x1F;
                bitsLeft -= 5;
                out.append(ALPHABET[idx]);
            }
        }

        if (bitsLeft > 0) {
            int idx = (buffer << (5 - bitsLeft)) & 0x1F;
            out.append(ALPHABET[idx]);
        }

        return out.toString(); // без '=' padding, как обычно в otpauth
    }

    public static byte[] decode(String base32) {
        if (base32 == null) throw new IllegalArgumentException("base32 is null");

        // допускаем пробелы/дефисы (некоторые UI так показывают)
        String s = base32.replace(" ", "").replace("-", "");

        if (s.isEmpty()) return new byte[0];

        int buffer = 0;
        int bitsLeft = 0;

        byte[] tmp = new byte[(s.length() * 5 + 7) / 8];
        int outPos = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= LOOKUP.length || LOOKUP[c] < 0) {
                throw new IllegalArgumentException("Invalid Base32 char: " + c);
            }
            buffer = (buffer << 5) | LOOKUP[c];
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                tmp[outPos++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }

        return Arrays.copyOf(tmp, outPos);
    }
}
