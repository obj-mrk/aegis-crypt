package mrk.cryptocore.rsa;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        int bits = 4096;
        RSAKeyPair keys = new RSAKeyPair(bits);

        BigInteger msg = new BigInteger("123456789");

        BigInteger cipher = RSA.encrypt(msg, keys.getE(), keys.getN());
        BigInteger decrypted = RSA.decrypt(cipher, keys.getD(), keys.getN());

        System.out.println("Original message: " + msg);
        System.out.println("Encrypted: " + cipher);
        System.out.println("Decrypted: " + decrypted);
    }
}
