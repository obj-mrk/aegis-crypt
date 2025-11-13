package mrk.cryptocore.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAKeyPair {
    private final BigInteger n;  // modulus
    private final BigInteger e;  // public exponent
    private final BigInteger d;  // private exponent

    public RSAKeyPair(int bitLength) {
        SecureRandom random = new SecureRandom();

        // 1. Generate p and q
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);

        // 2. Compute n = p * q
        this.n = p.multiply(q);

        // 3. Compute phi(n) = (p-1)(q-1)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // 4. Choose e
        this.e = BigInteger.valueOf(65537); // стандартный выбор

        // 5. Compute d = e^{-1} mod phi
        this.d = e.modInverse(phi);
    }

    public BigInteger getN() { return n; }
    public BigInteger getE() { return e; }
    public BigInteger getD() { return d; }
}
