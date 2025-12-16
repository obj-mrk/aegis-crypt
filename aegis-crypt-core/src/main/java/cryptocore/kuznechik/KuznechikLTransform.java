package cryptocore.kuznechik;

/**
 * Линейное преобразование L и его обратное для Кузнечика.
 *
 * Представление блока: state[0] = a15, ..., state[15] = a0.
 */
final class KuznechikLTransform {

    private KuznechikLTransform() {
    }

    /**
     * Вектор коэффициентов линейного преобразования L (α15..α0).
     * Значения взяты из определения функции l(a15,...,a0) в ГОСТ / RFC.
     */
    static final byte[] L_VECTOR = new byte[] {
            (byte)148, (byte) 32, (byte)133, (byte) 16,
            (byte)194, (byte)192, (byte)  1, (byte)251,
            (byte)  1, (byte)192, (byte)194, (byte) 16,
            (byte)133, (byte) 32, (byte)148, (byte)  1
    };

    /**
     * Одношаговое преобразование R: R(a15||...||a0) = l(a)*||a15||...||a1.
     *
     * @param state массив длины 16 (in-place)
     */
    private static void applyR(byte[] state) {
        if (state.length != 16) {
            throw new IllegalArgumentException("State must be 16 bytes");
        }

        byte x = l(state);
        // сдвиг вправо: [a15, ..., a0] -> [x, a15, ..., a1]
        for (int i = 15; i > 0; i--) {
            state[i] = state[i - 1];
        }
        state[0] = x;
    }

    /**
     * Обратное одношаговое преобразование R^{-1}: R^{-1}(a15||...||a0) =
     * a14||a13||...||a0||l(a14,a13,...,a0,a15).
     */
    private static void applyInvR(byte[] state) {
        if (state.length != 16) {
            throw new IllegalArgumentException("State must be 16 bytes");
        }

        // Сконструируем временный вектор (a14, ..., a0, a15)
        byte[] tmp = new byte[16];
        // a14..a1
        for (int i = 0; i < 14; i++) {
            tmp[i] = state[i + 1]; // state[1]=a14, ..., state[14]=a1
        }
        tmp[14] = state[15]; // a0
        tmp[15] = state[0];  // a15

        byte x = l(tmp); // l(a14,...,a0,a15)

        // результат: a14||a13||...||a0||x
        for (int i = 0; i < 15; i++) {
            state[i] = state[i + 1]; // a14..a0
        }
        state[15] = x;
    }

    /**
     * Функция l(a15,...,a0) = Σ α_i * a_i в GF(2^8).
     *
     * @param state массив длины 16: state[0]=a15,...,state[15]=a0
     * @return один байт результата
     */
    private static byte l(byte[] state) {
        int acc = 0;
        for (int i = 0; i < 16; i++) {
            acc ^= (Gf256.mul(state[i], L_VECTOR[i]) & 0xFF);
        }
        return (byte) acc;
    }

    /**
     * Применить полное линейное преобразование L = R^16 к блоку (in-place).
     */
    static void applyL(byte[] state) {
        if (state.length != 16) {
            throw new IllegalArgumentException("State must be 16 bytes");
        }
        for (int i = 0; i < 16; i++) {
            applyR(state);
        }
    }

    /**
     * Применить обратное линейное преобразование L^{-1} = (R^{-1})^16 к блоку (in-place).
     */
    static void applyInvL(byte[] state) {
        if (state.length != 16) {
            throw new IllegalArgumentException("State must be 16 bytes");
        }
        for (int i = 0; i < 16; i++) {
            applyInvR(state);
        }
    }

    static void applyROnceForTest(byte[] state) {
        applyR(state);
    }
}

