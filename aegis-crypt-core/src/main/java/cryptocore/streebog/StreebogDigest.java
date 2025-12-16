package cryptocore.streebog;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Реализация хеш-функции ГОСТ Р 34.11-2012 (Стрибог) на 256/512 бит.
 *
 * Реализовано строго по RFC 6986:
 *  - обработка сообщения идёт с конца к началу (M = M' || m, обрабатываем m – последний блок);
 *  - N – счётчик длины сообщения в битах (V_512);
 *  - Σ – сумма всех блоков сообщения (V_512);
 *  - раундовая функция g_N(h, m) = E(LPS(h xor N), m) xor h xor m.
 *
 */
public final class StreebogDigest {

    public static final int DIGEST_SIZE_512 = 64;
    public static final int DIGEST_SIZE_256 = 32;

    private final int digestSize; // 64 или 32 (байт)
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public StreebogDigest(int digestSizeBytes) {
        if (digestSizeBytes != DIGEST_SIZE_512 && digestSizeBytes != DIGEST_SIZE_256) {
            throw new IllegalArgumentException("Streebog digest size must be 32 or 64 bytes");
        }
        this.digestSize = digestSizeBytes;
    }

    public void reset() {
        buffer.reset();
    }

    public void update(byte input) {
        buffer.write(input);
    }

    public void update(byte[] input) {
        update(input, 0, input.length);
    }

    public void update(byte[] input, int off, int len) {
        if (input == null || len == 0) {
            return;
        }
        buffer.write(input, off, len);
    }

    /**
     * Упрощённый метод: вернуть новый массив с хешем.
     */
    public byte[] digest() {
        byte[] out = new byte[digestSize];
        doFinal(out, 0);
        return out;
    }

    /**
     * Основной метод завершения вычислений.
     * Возвращает количество записанных байт (32 или 64).
     */
    public int doFinal(byte[] out, int outOff) {
        // Собираем все данные из буфера
        byte[] msg = buffer.toByteArray();

        // ---------------- ИНИЦИАЛИЗАЦИЯ ----------------
        // h_0:
        //  - для 512 бит: IV = 0^512
        //  - для 256 бит: IV = (0x01)^64
        byte[] h = new byte[64];
        if (digestSize == DIGEST_SIZE_256) {
            Arrays.fill(h, (byte) 0x01);
        } else {
            Arrays.fill(h, (byte) 0x00);
        }

        // N — счётчик длины в битах (512-битное число)
        byte[] N = new byte[64];
        // Σ — сумма всех блоков (512-битное число)
        byte[] Sigma = new byte[64];

        // Вспомогательные буферы, как в BouncyCastle
        byte[] tmp = new byte[64];   // временный буфер для входного блока
        byte[] block = new byte[64]; // блок, который реально идёт в g_N
        int bOff = 64;               // смещение свободной позиции в block (заполняем справа налево)

        int inOff = 0;
        int len = msg.length;

        // -------- Эмуляция update(byte) / update(byte[],...) --------
        // 1) Если в block уже что-то было —
        //    сначала добиваем его посимвольно.
        while (bOff != 64 && len > 0) {
            block[--bOff] = msg[inOff++];
            if (bOff == 0) {
                h = gN(h, N, block);
                addIntTo512(N, 512);
                add512(Sigma, block);
                bOff = 64;
            }
            len--;
        }

        // 2) Полные 64-байтные блоки: копируем, разворачиваем (reverse) и сразу в g_N
        while (len >= 64) {
            System.arraycopy(msg, inOff, tmp, 0, 64);
            reverse(tmp, block);     // как в BouncyCastle: block[i] = tmp[63-i]

            h = gN(h, N, block);
            addIntTo512(N, 512);
            add512(Sigma, block);

            len   -= 64;
            inOff += 64;
        }

        // 3) Оставшиеся (неполные) байты — снова посимвольно справа налево в block
        while (len > 0) {
            block[--bOff] = msg[inOff++];
            if (bOff == 0) {
                h = gN(h, N, block);
                addIntTo512(N, 512);
                add512(Sigma, block);
                bOff = 64;
            }
            len--;
        }

        // ---------------- ДОПОЛНЕНИЕ ПО ГОСТ ----------------
        // Сейчас в block (справа) лежит хвост сообщения (lenM байт), либо block пуст.
        int lenM = 64 - bOff; // число байт "хвоста"

        byte[] m = new byte[64];
        // m[0 .. 64-lenM-1] = 0
        for (int i = 0; i < 64 - lenM; i++) {
            m[i] = 0;
        }

        // Один бит "1" ставим перед хвостом (по байтам — это байт 0x01):
        // m = 0...0 01 <данные хвоста>
        m[63 - lenM] = 1;

        if (bOff != 64) {
            // переносим хвост сообщения в конец блока m
            System.arraycopy(block, bOff, m, 64 - lenM, lenM);
        }

        // Обработка последнего (дополненного) блока
        h = gN(h, N, m);
        addIntTo512(N, (long) lenM * 8L); // N += длина хвоста в битах
        add512(Sigma, m);                 // Σ += m

        // ---------------- ЗАВЕРШАЮЩИЕ ДВА ВЫЗОВА g_N ----------------
        byte[] zero = new byte[64]; // 0^512
        h = gN(h, zero, N);
        h = gN(h, zero, Sigma);

        // ---------------- ФОРМИРОВАНИЕ ВЫХОДА ----------------
        // ГОСТ определён в "обратном" порядке байт, поэтому перед выдачей делаем reverse(h)
        byte[] tmpOut = new byte[64];
        reverse(h, tmpOut);

        if (digestSize == DIGEST_SIZE_512) {
            // 512 бит — все 64 байта
            System.arraycopy(tmpOut, 0, out, outOff, 64);
        } else {
            // 256 бит — младшие 256 бит результата (последние 32 байта)
            System.arraycopy(tmpOut, 32, out, outOff, 32);
        }

        reset();
        return digestSize;
    }

    // ======================== ВНУТРЕННЯЯ МЕХАНИКА ========================

    private static final byte[] ZERO_512 = new byte[64];

    /**
     * g_N(h, m) = E(LPS(h xor N), m) xor h xor m
     */
    private static byte[] gN(byte[] h, byte[] N, byte[] m) {
        byte[] k = xor512(h, N);
        lps(k); // k = LPS(k)

        byte[] e = encrypt(k, m); // E(k, m)
        byte[] res = new byte[64];

        for (int i = 0; i < 64; i++) {
            res[i] = (byte) (e[i] ^ h[i] ^ m[i]);
        }
        return res;
    }

    /**
     * E(K, m) по RFC:
     *  K[1] = K
     *  K[i] = LPS(K[i-1] xor C[i-1]), i=2..13
     *  E(K,m) = X[K[13]] LPS X[K[12]] ... LPS X[K[1]](m)
     */
    private static byte[] encrypt(byte[] K, byte[] m) {
        byte[][] keys = new byte[13][64];

        // K[1]
        System.arraycopy(K, 0, keys[0], 0, 64);

        // K[2..13]
        for (int i = 1; i < 13; i++) {
            byte[] tmp = xor512(keys[i - 1], StreebogConstants.C[i - 1]);
            lps(tmp);
            keys[i] = tmp;
        }

        byte[] state = Arrays.copyOf(m, 64);

        // X[K[1]] -> LPS -> ... -> X[K[12]] -> LPS -> X[K[13]]
        for (int i = 0; i < 12; i++) {
            xorInto(state, keys[i]);
            lps(state);
        }

        xorInto(state, keys[12]); // X[K[13]]
        return state;
    }

    /**
     * Последовательное применение S, затем P, затем L.
     */
    private static void lps(byte[] state) {
        // S
        for (int i = 0; i < 64; i++) {
            state[i] = (byte) StreebogConstants.SBOX[state[i] & 0xFF];
        }

        // P
        byte[] tmp = new byte[64];
        // P(a) = a_{Tau(63)} || ... || a_{Tau(0)}, где a = a_63 || ... || a_0
        // В нашем представлении state[0] = a_63, state[63] = a_0.
        for (int j = 0; j < 64; j++) {
            int tauIndex = StreebogConstants.TAU[63 - j]; // Tau(63-j)
            int srcIndex = 63 - tauIndex;                 // индекс соответствующего a_{Tau(63-j)}
            tmp[j] = state[srcIndex];
        }
        System.arraycopy(tmp, 0, state, 0, 64);

        // L
        L(state);
    }

    /**
     * L(a) = l(a_7) || ... || l(a_0), где a делится на 8 блоков по 64 бита.
     * Здесь каждый блок – 8 байт, интерпретируем как 64-битное число big-endian.
     */
    private static void L(byte[] state) {
        for (int block = 0; block < 8; block++) {
            int offset = block * 8;

            long x = 0L;
            for (int i = 0; i < 8; i++) {
                x = (x << 8) | (state[offset + i] & 0xFFL);
            }

            long y = 0L;
            // матрица A[0..63] из ГОСТ, храним как 64-битные строки
            for (int bit = 0; bit < 64; bit++) {
                if (((x >>> (63 - bit)) & 1L) != 0) {
                    y ^= StreebogConstants.A[bit];
                }
            }

            for (int i = 7; i >= 0; i--) {
                state[offset + i] = (byte) (y & 0xFFL);
                y >>>= 8;
            }
        }
    }

    // ======================== ВСПОМОГАТЕЛЬНЫЕ ОПЕРАЦИИ ========================

    private static byte[] xor512(byte[] a, byte[] b) {
        byte[] res = new byte[64];
        for (int i = 0; i < 64; i++) {
            res[i] = (byte) (a[i] ^ b[i]);
        }
        return res;
    }

    private static void xorInto(byte[] target, byte[] mask) {
        for (int i = 0; i < 64; i++) {
            target[i] ^= mask[i];
        }
    }

    /**
     * Σ := Σ + m (512-битное сложение по модулю 2^512, big-endian).
     */
    private static void add512(byte[] acc, byte[] block) {
        int carry = 0;
        for (int i = 63; i >= 0; i--) {
            int sum = (acc[i] & 0xFF) + (block[i] & 0xFF) + carry;
            acc[i] = (byte) sum;
            carry = sum >>> 8;
        }
    }

    /**
     * N := N + value (value – число бит, до 2^32 достаточно long).
     */
    private static void addIntTo512(byte[] acc, long value) {
        int carry = 0;

        int v0 = (int) (value & 0xFFL);
        int v1 = (int) ((value >>> 8) & 0xFFL);
        int v2 = (int) ((value >>> 16) & 0xFFL);
        int v3 = (int) ((value >>> 24) & 0xFFL);

        int i = 63;
        int sum = (acc[i] & 0xFF) + v0;
        acc[i] = (byte) sum;
        carry = sum >>> 8;

        i--;
        sum = (acc[i] & 0xFF) + v1 + carry;
        acc[i] = (byte) sum;
        carry = sum >>> 8;

        i--;
        sum = (acc[i] & 0xFF) + v2 + carry;
        acc[i] = (byte) sum;
        carry = sum >>> 8;

        i--;
        sum = (acc[i] & 0xFF) + v3 + carry;
        acc[i] = (byte) sum;
        carry = sum >>> 8;

        for (i = 59; i >= 0 && carry != 0; i--) {
            sum = (acc[i] & 0xFF) + carry;
            acc[i] = (byte) sum;
            carry = sum >>> 8;
        }
    }

    /**
     * Разворот 64-байтного вектора: out[i] = in[63-i].
     */
    private static void reverse(byte[] in, byte[] out) {
        if (in.length != 64 || out.length != 64) {
            throw new IllegalArgumentException("reverse: ожидаются массивы длины 64 байта");
        }
        for (int i = 0; i < 64; i++) {
            out[i] = in[63 - i];
        }
    }
}
