package cryptocore.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAKeyPair {

    private static final BigInteger DEFAULT_PUBLIC_EXPONENT = BigInteger.valueOf(65537L);

    private final BigInteger n;   // modulus
    private final BigInteger e;   // public exponent
    private final BigInteger d;   // private exponent
    private final BigInteger p;   // prime p
    private final BigInteger q;   // prime q
    private final BigInteger dP;  // d mod (p-1)
    private final BigInteger dQ;  // d mod (q-1)
    private final BigInteger qInv;// q^-1 mod p

    public RSAKeyPair(int bitLength, boolean useCRT) {
        SecureRandom random = new SecureRandom();

        BigInteger pLocal;
        BigInteger qLocal;
        BigInteger nLocal;
        BigInteger phi;

        // Генерируем p, q до тех пор, пока gcd(e, phi) == 1
        while (true) {
            pLocal = generatePrime(bitLength / 2, random);
            qLocal = generatePrime(bitLength / 2, random);

            if (pLocal.equals(qLocal)) {
                continue; // p и q должны быть различными
            }

            nLocal = pLocal.multiply(qLocal);
            phi = pLocal.subtract(BigInteger.ONE)
                    .multiply(qLocal.subtract(BigInteger.ONE));

            // Проверка простоты чисел
            if (DEFAULT_PUBLIC_EXPONENT.gcd(phi).equals(BigInteger.ONE)) {
                break;
            }
            // иначе повторяем генерацию
        }

        // Присваиваем значение переменных после подбора
        this.n = nLocal;
        this.e = DEFAULT_PUBLIC_EXPONENT;
        this.d = e.modInverse(phi);

        if (useCRT) {
            this.p   = pLocal;
            this.q   = qLocal;
            this.dP  = d.mod(pLocal.subtract(BigInteger.ONE));
            this.dQ  = d.mod(qLocal.subtract(BigInteger.ONE));
            this.qInv = qLocal.modInverse(pLocal);
        } else {
            this.p   = null;
            this.q   = null;
            this.dP  = null;
            this.dQ  = null;
            this.qInv = null;
        }
    }

    public RSAKeyPair(BigInteger n, BigInteger e, BigInteger d) {
        if (n == null || e == null || d == null) {
            throw new IllegalArgumentException("n/e/d must not be null");
        }
        this.n = n;
        this.e = e;
        this.d = d;

        // CRT параметры неизвестны при восстановлении
        this.p = null;
        this.q = null;
        this.dP = null;
        this.dQ = null;
        this.qInv = null;
    }


    // Генерация случайного простого числа заданной битовой длины
    private static BigInteger generatePrime(int bitLength, SecureRandom random) {
        return BigInteger.probablePrime(bitLength, random);
    }

    public BigInteger getN()   { return n; }
    public BigInteger getE()   { return e; }
    public BigInteger getD()   { return d; }
    public BigInteger getP()   { return p; }
    public BigInteger getQ()   { return q; }
    public BigInteger getDP()  { return dP; }
    public BigInteger getDQ()  { return dQ; }
    public BigInteger getQInv(){ return qInv; }

    // Доступны ли CRT-параметры для ускоренной расшифровки.
    public boolean isCrtAvailable() {
        return p != null && q != null && dP != null && dQ != null && qInv != null;
    }
}
