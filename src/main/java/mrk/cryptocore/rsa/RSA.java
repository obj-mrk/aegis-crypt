package mrk.cryptocore.rsa;

import java.math.BigInteger;

public class RSA {
    public static BigInteger encrypt(BigInteger msg, BigInteger e, BigInteger n) {
        if (msg.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Message too large for this RSA modulus");
        }
        return msg.modPow(e, n);
    }

    public static BigInteger decrypt(BigInteger cipher, BigInteger e, BigInteger n) {
        return cipher.modPow(n, n);
    }
}
