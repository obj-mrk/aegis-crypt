package mrk.cryptocore.kuznechik;

/**
 * Реализация блочного шифра "Кузнечик" (ГОСТ Р 34.12-2015).
 */
public class KuznechikEngine implements BlockCipher {

    public static final int BLOCK_SIZE = 16; // 128 бит
    public static final int KEY_SIZE   = 32; // 256 бит
    public static final int NUM_ROUNDS = 10; // K1..K10

    private boolean forEncryption;

    /**
     * Раундовые ключи для шифрования (и для расшифрования тоже, см. decryptBlock).
     * encRoundKeys[i] — 16-байтный блок Ki, i=0..9.
     */
    private byte[][] roundKeysEnc;

    /**
     * Раундовые ключи для расшифрования (опционально, здесь не используем напрямую).
     * Оставлены на случай дальнейших оптимизаций.
     */
    @SuppressWarnings("unused")
    private byte[][] roundKeysDec;

    public KuznechikEngine() {
    }

    @Override
    public int getBlockSize() {
        return BLOCK_SIZE;
    }

    @Override
    public int getKeySize() {
        return KEY_SIZE;
    }

    @Override
    public void init(boolean forEncryption, byte[] key) {
        if (key == null || key.length != KEY_SIZE) {
            throw new IllegalArgumentException("Invalid Kuznechik key size: " +
                    (key == null ? "null" : key.length));
        }
        this.forEncryption = forEncryption;

        KuznechikKeySchedule.RoundKeys keys =
                KuznechikKeySchedule.generateRoundKeys(key);

        this.roundKeysEnc = keys.getEncRoundKeys();
        this.roundKeysDec = keys.getDecRoundKeys();
    }

    @Override
    public void encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (!forEncryption) {
            throw new IllegalStateException("KuznechikEngine not initialized for encryption");
        }
        checkBuffer(in, inOff);
        checkBuffer(out, outOff);

        byte[] state = new byte[BLOCK_SIZE];
        System.arraycopy(in, inOff, state, 0, BLOCK_SIZE);

        // 9 раундов LSX с ключами K1..K9
        for (int i = 0; i < NUM_ROUNDS - 1; i++) { // i = 0..8 => K1..K9
            xor16(state, roundKeysEnc[i]);        // X[Ki](state) = state XOR Ki
            KuznechikSBox.applyS(state);          // S(state)
            KuznechikLTransform.applyL(state);    // L(state)
        }

        // Финальный шаг: X[K10]
        xor16(state, roundKeysEnc[NUM_ROUNDS - 1]); // K10 (индекс 9)

        System.arraycopy(state, 0, out, outOff, BLOCK_SIZE);
    }

    @Override
    public void decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (forEncryption) {
            throw new IllegalStateException("KuznechikEngine not initialized for decryption");
        }
        checkBuffer(in, inOff);
        checkBuffer(out, outOff);

        byte[] state = new byte[BLOCK_SIZE];
        System.arraycopy(in, inOff, state, 0, BLOCK_SIZE);

        // Шаг 1: снимаем X[K10]
        xor16(state, roundKeysEnc[NUM_ROUNDS - 1]); // K10

        // Шаг 2: 9 обратных раундов для K9..K1
        for (int i = NUM_ROUNDS - 2; i >= 0; i--) { // i = 8..0 => K9..K1
            KuznechikLTransform.applyInvL(state);   // L^{-1}
            KuznechikSBox.applyInvS(state);         // S^{-1}
            xor16(state, roundKeysEnc[i]);         // X[Ki]
        }

        System.arraycopy(state, 0, out, outOff, BLOCK_SIZE);
    }

    // --- Вспомогательные методы ---

    private static void xor16(byte[] state, byte[] key) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            state[i] ^= key[i];
        }
    }

    private static void checkBuffer(byte[] buf, int off) {
        if (buf == null) {
            throw new IllegalArgumentException("Buffer is null");
        }
        if (off < 0 || off + BLOCK_SIZE > buf.length) {
            throw new IllegalArgumentException(
                    "Buffer too short for Kuznechik block at offset " + off +
                            ", length = " + buf.length
            );
        }
    }

    @Override
    public String toString() {
        return "Kuznechik-128/256";
    }
}
