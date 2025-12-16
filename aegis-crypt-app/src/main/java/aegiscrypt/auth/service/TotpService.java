package aegiscrypt.auth.service;

import aegiscrypt.auth.totp.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;

public final class TotpService {

    private static final SecureRandom RND = new SecureRandom();

    private TotpService() {}

    public static String generateBase32Secret() {
        byte[] secret = new byte[20]; // 160-bit, стандартный размер
        RND.nextBytes(secret);
        return Base32.encode(secret);
    }

    public static boolean verify(String base32Secret, String totp, int stepSeconds, int digits, int window) {
        long now = Instant.now().getEpochSecond();
        long t = now / stepSeconds;

        for (long i = -window; i <= window; i++) {
            String candidate = generate(base32Secret, t + i, digits);
            if (candidate.equals(totp)) return true;
        }
        return false;
    }

    public static String otpauthUri(String issuer, String accountName, String base32Secret, int digits, int periodSeconds) {
        // Формат совместим с Google Authenticator/и др.
        // secret должен быть Base32
        String label = issuer + ":" + accountName;
        return "otpauth://totp/"
                + url(label)
                + "?secret=" + url(base32Secret)
                + "&issuer=" + url(issuer)
                + "&digits=" + digits
                + "&period=" + periodSeconds;
    }

    private static String generate(String base32Secret, long counter, int digits) {
        try {
            byte[] key = Base32.decode(base32Secret);
            byte[] msg = ByteBuffer.allocate(8).putLong(counter).array();

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(msg);

            int offset = hash[hash.length - 1] & 0x0F;
            int bin = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int mod = (int) Math.pow(10, digits);
            int code = bin % mod;

            return String.format("%0" + digits + "d", code);
        } catch (Exception e) {
            throw new IllegalStateException("TOTP generation failed", e);
        }
    }

    private static String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
