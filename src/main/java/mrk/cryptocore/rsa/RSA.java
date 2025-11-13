package mrk.cryptocore.rsa;

import java.math.BigInteger;

public final class RSA {

    // Шифрование сообщения открытым ключом (e, n), содержащимся в RSAKeyPair.
    public static BigInteger encrypt(BigInteger message, RSAKeyPair keys) {
        BigInteger n = keys.getN();
        validateLessThanModulus(message, n, "Message");
        return message.modPow(keys.getE(), n);
    }

    /**
     * Расшифровка шифротекста закрытым ключом.
     * Если доступны CRT-параметры, используется ускоренный вариант.
     */
    public static BigInteger decrypt(BigInteger cipher, RSAKeyPair keys) {
        BigInteger n = keys.getN();
        validateLessThanModulus(cipher, n, "Ciphertext");

        // Если параметры CRT не заданы, используем обычное modPow
        if (!keys.isCrtAvailable()) {
            return cipher.modPow(keys.getD(), n);
        }

        BigInteger p = keys.getP();
        BigInteger q = keys.getQ();
        BigInteger dP = keys.getDP();
        BigInteger dQ = keys.getDQ();
        BigInteger qInv = keys.getQInv();

        // Расшифровка по модулю p и q
        BigInteger mP = cipher.modPow(dP, p);
        BigInteger mQ = cipher.modPow(dQ, q);

        // Сборка по китайской теореме об остатках
        BigInteger h = mP.subtract(mQ)
                .multiply(qInv)
                .mod(p);
        BigInteger m = mQ.add(h.multiply(q));

        return m.mod(n);
    }

    // Валидация условий RSA
    private static void validateLessThanModulus(BigInteger value,
                                                BigInteger modulus,
                                                String what) {
        if (value.signum() < 0) {
            throw new IllegalArgumentException(what + " must be non-negative");
        }
        if (value.compareTo(modulus) >= 0) {
            throw new IllegalArgumentException(what + " is too large for this RSA modulus");
        }
    }
}
