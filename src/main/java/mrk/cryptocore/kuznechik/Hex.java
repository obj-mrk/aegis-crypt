package mrk.cryptocore.kuznechik;

public final class Hex {

    private Hex() {}

    public static byte[] fromHex(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string length must be even");
        }
        int len = hex.length() / 2;
        byte[] out = new byte[len];
        for (int i = 0; i < len; i++) {
            int hi = Character.digit(hex.charAt(2 * i), 16);
            int lo = Character.digit(hex.charAt(2 * i + 1), 16);
            if (hi == -1 || lo == -1) {
                throw new IllegalArgumentException("Invalid hex char in: " + hex);
            }
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }
}

