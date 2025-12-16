package cryptocore.kuznechik;

/**
 * Операции в поле GF(2^8) для Кузнечика.
 * Поле задаётся неприводимым многочленом p(x) = x^8 + x^7 + x^6 + x + 1.
 */
final class Gf256 {

    // Полином редукции без x^8: x^7 + x^6 + x + 1 -> 1100 0011b -> 0xC3.
    private static final int REDUCTION_POLY = 0xC3;

    private Gf256() {
    }

    /**
     * Умножение двух элементов GF(2^8) (байты интерпретируются как элементы поля).
     *
     * @param a множимое
     * @param b множитель
     * @return произведение в GF(2^8)
     */
    static byte mul(byte a, byte b) {
        int aa = a & 0xFF;
        int bb = b & 0xFF;
        int res = 0;

        for (int i = 0; i < 8; i++) {
            if ((bb & 1) != 0) {
                res ^= aa;
            }
            boolean hiBitSet = (aa & 0x80) != 0;
            aa <<= 1;
            aa &= 0xFF; // оставляем только 8 младших бит
            if (hiBitSet) {
                aa ^= REDUCTION_POLY;
            }
            bb >>>= 1;
        }

        return (byte) res;
    }
}
