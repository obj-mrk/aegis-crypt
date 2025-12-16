package cryptocore.kuznechik;

import java.util.Arrays;

/**
 * Развёртка ключей Кузнечика (ГОСТ Р 34.12-2015).
 */
final class KuznechikKeySchedule {

    private KuznechikKeySchedule() {}

    private static final int KEY_BLOCK_SIZE = 16; // 128 бит
    private static final int NUM_ROUND_KEYS = 10; // K1..K10

    /**
     * Хранение раундовых ключей для шифрования и расшифрования.
     */
    static final class RoundKeys {
        private final byte[][] enc;
        private final byte[][] dec;

        RoundKeys(byte[][] enc, byte[][] dec) {
            this.enc = enc;
            this.dec = dec;
        }

        byte[][] getEncRoundKeys() {
            return enc;
        }

        byte[][] getDecRoundKeys() {
            return dec;
        }
    }

    /**
     * Генерация раундовых ключей согласно ГОСТ.
     */
    static RoundKeys generateRoundKeys(byte[] masterKey) {
        if (masterKey.length != 32) {
            throw new IllegalArgumentException("Master key must be 256 bits");
        }

        // 1) Разбиваем на K1, K2
        byte[] K1 = Arrays.copyOfRange(masterKey, 0, 16);
        byte[] K2 = Arrays.copyOfRange(masterKey, 16, 32);

        // Сразу положим K1,K2 в массив раундовых ключей
        byte[][] enc = new byte[NUM_ROUND_KEYS][16];
        enc[0] = K1;
        enc[1] = K2;

        // 2) Генерируем 32 константы Ci
        byte[][] C = generateConstants();

        // 3) Генерируем оставшиеся пары ключей
        int idx = 2; // следующий незаполненный раундовый ключ

        byte[] A = K1;
        byte[] B = K2;

        for (int j = 0; j < 4; j++) { // 4 группы по 8 шагов
            for (int i = 0; i < 8; i++) {
                byte[] Ci = C[j * 8 + i];
                byte[][] tmp = f(A, B, Ci);
                A = tmp[0];
                B = tmp[1];
            }
            enc[idx++] = A;
            enc[idx++] = B;
        }

        // 4) Строим ключи для расшифрования
        byte[][] dec = generateDecryptKeys(enc);

        return new RoundKeys(enc, dec);
    }

    /**
     * Построение массива из 32 констант Ci = L(0..0 || i).
     */
    private static byte[][] generateConstants() {
        byte[][] C = new byte[32][16];

        for (int i = 0; i < 32; i++) {
            byte[] v = new byte[16];
            v[15] = (byte) (i + 1); // последний байт = номер константы

            KuznechikLTransform.applyL(v);
            C[i] = v;
        }

        return C;
    }

    /**
     * Преобразование F(X, Y, C) = ( Y ⊕ L(S(X ⊕ C)), X )
     *
     * @return массив из двух блоков: newX,newY
     */
    private static byte[][] f(byte[] X, byte[] Y, byte[] C) {
        byte[] tmp = X.clone();

        // tmp = X ⊕ C
        for (int i = 0; i < 16; i++) {
            tmp[i] ^= C[i];
        }

        // tmp = S(tmp)
        KuznechikSBox.applyS(tmp);

        // tmp = L(tmp)
        KuznechikLTransform.applyL(tmp);

        // newX = Y ⊕ tmp
        byte[] newX = new byte[16];
        for (int i = 0; i < 16; i++) {
            newX[i] = (byte) (Y[i] ^ tmp[i]);
        }

        // newY = X
        byte[] newY = X.clone();

        return new byte[][] { newX, newY };
    }

    /**
     * Генерация раундовых ключей для расшифрования:
     * K1dec = K1enc,
     * K10dec = K10enc,
     * промежуточные = InvL(Ki).
     */
    private static byte[][] generateDecryptKeys(byte[][] enc) {

        byte[][] dec = new byte[NUM_ROUND_KEYS][16];

        // Первый и последний ключи одинаковы
        dec[0] = enc[0].clone();
        dec[9] = enc[9].clone();

        // Для ключей K2..K9 применяем L^{-1}
        for (int i = 1; i < 9; i++) {
            byte[] t = enc[i].clone();
            KuznechikLTransform.applyInvL(t);
            dec[i] = t;
        }

        return dec;
    }
}