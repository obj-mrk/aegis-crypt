package cryptocore.streebog;

/**
 * Константы ГОСТ Р 34.11-2012 (Стрибог) согласно RFC 6986.
 */
final class StreebogConstants {

    private StreebogConstants() {}

    /**
     * S-блок Pi (тот же, что и в "Кузнечике").
     * RFC 6986, раздел 6.2. Nonlinear bijection (Pi'). :contentReference[oaicite:4]{index=4}
     */
    static final short[] SBOX = {
            252, 238, 221,  17, 207, 110,  49,  22,
            251, 196, 250, 218,  35, 197,   4,  77,
            233, 119, 240, 219, 147,  46, 153, 186,
            23,  54, 241, 187,  20, 205,  95, 193,
            249,  24, 101,  90, 226,  92, 239,  33,
            129,  28,  60,  66, 139,   1, 142,  79,
            5, 132,   2, 174, 227, 106, 143, 160,
            6,  11, 237, 152, 127, 212, 211,  31,
            235,  52,  44,  81, 234, 200,  72, 171,
            242,  42, 104, 162, 253,  58, 206, 204,
            181, 112,  14,  86,   8,  12, 118,  18,
            191, 114,  19,  71, 156, 183,  93, 135,
            21, 161, 150,  41,  16, 123, 154, 199,
            243, 145, 120, 111, 157, 158, 178, 177,
            50, 117,  25,  61, 255,  53, 138, 126,
            109,  84, 198, 128, 195, 189,  13,  87,
            223, 245,  36, 169,  62, 168,  67, 201,
            215, 121, 214, 246, 124,  34, 185,   3,
            224,  15, 236, 222, 122, 148, 176, 188,
            220, 232,  40,  80,  78,  51,  10,  74,
            167, 151,  96, 115,  30,   0,  98,  68,
            26, 184,  56, 130, 100, 159,  38,  65,
            173,  69,  70, 146,  39,  94,  85,  47,
            140, 163, 165, 125, 105, 213, 149,  59,
            7,  88, 179,  64, 134, 172,  29, 247,
            48,  55, 107, 228, 136, 217, 231, 137,
            225,  27, 131,  73,  76,  63, 248, 254,
            141,  83, 170, 144, 202, 216, 133,  97,
            32, 113, 103, 164,  45,  43,   9,  91,
            203, 155,  37, 208, 190, 229, 108,  82,
            89, 166, 116, 210, 230, 244, 180, 192,
            209, 102, 175, 194,  57,  75,  99, 182
    };

    /**
     * Перестановка Tau для P-преобразования.
     * RFC 6986, раздел 6.3. :contentReference[oaicite:5]{index=5}
     */
    static final byte[] TAU = {
            0,  8, 16, 24, 32, 40, 48, 56,
            1,  9, 17, 25, 33, 41, 49, 57,
            2, 10, 18, 26, 34, 42, 50, 58,
            3, 11, 19, 27, 35, 43, 51, 59,
            4, 12, 20, 28, 36, 44, 52, 60,
            5, 13, 21, 29, 37, 45, 53, 61,
            6, 14, 22, 30, 38, 46, 54, 62,
            7, 15, 23, 31, 39, 47, 55, 63
    };

    /**
     * Матрица A для линейного преобразования l: V_64 -> V_64.
     * Хранится построчно, каждая строка — 64-битное слово,
     * порядок и значения как в RFC 6986, раздел 6.4. :contentReference[oaicite:6]{index=6}
     */
    static final long[] A = {
            0x8e20faa72ba0b470L, 0x47107ddd9b505a38L,
            0xad08b0e0c3282d1cL, 0xd8045870ef14980eL,
            0x6c022c38f90a4c07L, 0x3601161cf205268dL,
            0x1b8e0b0e798c13c8L, 0x83478b07b2468764L,
            0xa011d380818e8f40L, 0x5086e740ce47c920L,
            0x2843fd2067adea10L, 0x14aff010bdd87508L,
            0x0ad97808d06cb404L, 0x05e23c0468365a02L,
            0x8c711e02341b2d01L, 0x46b60f011a83988eL,
            0x90dab52a387ae76fL, 0x486dd4151c3dfdb9L,
            0x24b86a840e90f0d2L, 0x125c354207487869L,
            0x092e94218d243cbaL, 0x8a174a9ec8121e5dL,
            0x4585254f64090fa0L, 0xaccc9ca9328a8950L,
            0x9d4df05d5f661451L, 0xc0a878a0a1330aa6L,
            0x60543c50de970553L, 0x302a1e286fc58ca7L,
            0x18150f14b9ec46ddL, 0x0c84890ad27623e0L,
            0x0642ca05693b9f70L, 0x0321658cba93c138L,
            0x86275df09ce8aaa8L, 0x439da0784e745554L,
            0xafc0503c273aa42aL, 0xd960281e9d1d5215L,
            0xe230140fc0802984L, 0x71180a8960409a42L,
            0xb60c05ca30204d21L, 0x5b068c651810a89eL,
            0x456c34887a3805b9L, 0xac361a443d1c8cd2L,
            0x561b0d22900e4669L, 0x2b838811480723baL,
            0x9bcf4486248d9f5dL, 0xc3e9224312c8c1a0L,
            0xeffa11af0964ee50L, 0xf97d86d98a327728L,
            0xe4fa2054a80b329cL, 0x727d102a548b194eL,
            0x39b008152acb8227L, 0x9258048415eb419dL,
            0x492c024284fbaec0L, 0xaa16012142f35760L,
            0x550b8e9e21f7a530L, 0xa48b474f9ef5dc18L,
            0x70a6a56e2440598eL, 0x3853dc371220a247L,
            0x1ca76e95091051adL, 0x0edd37c48a08a6d8L,
            0x07e095624504536cL, 0x8d70c431ac02a736L,
            0xc83862965601dd1bL, 0x641c314b2b8ee083L
    };

    /**
     * Итерационные константы C[1..12] для ключевого расписания
     * во внутреннем шифре E(K, m).
     * RFC 6986, раздел 6.5. :contentReference[oaicite:7]{index=7}
     */
    static final byte[][] C = new byte[12][];

    static {
        C[0]  = hexToBytes(
                "b1085bda1ecadae9ebcb2f81c0657c1f" +
                        "2f6a76432e45d016714eb88d7585c4fc" +
                        "4b7ce09192676901a2422a08a460d315" +
                        "05767436cc744d23dd806559f2a64507");
        C[1]  = hexToBytes(
                "6fa3b58aa99d2f1a4fe39d460f70b5d7" +
                        "f3feea720a232b9861d55e0f16b50131" +
                        "9ab5176b12d699585cb561c2db0aa7ca" +
                        "55dda21bd7cbcd56e679047021b19bb7");
        C[2]  = hexToBytes(
                "f574dcac2bce2fc70a39fc286a3d8435" +
                        "06f15e5f529c1f8bf2ea7514b1297b7b" +
                        "d3e20fe490359eb1c1c93a376062db09" +
                        "c2b6f443867adb31991e96f50aba0ab2");
        C[3]  = hexToBytes(
                "ef1fdfb3e81566d2f948e1a05d71e4dd" +
                        "488e857e335c3c7d9d721cad685e353f" +
                        "a9d72c82ed03d675d8b71333935203be" +
                        "3453eaa193e837f1220cbebc84e3d12e");
        C[4]  = hexToBytes(
                "4bea6bacad4747999a3f410c6ca92363" +
                        "7f151c1f1686104a359e35d7800fffbd" +
                        "bfcd1747253af5a3dfff00b723271a16" +
                        "7a56a27ea9ea63f5601758fd7c6cfe57");
        C[5]  = hexToBytes(
                "ae4faeae1d3ad3d96fa4c33b7a3039c0" +
                        "2d66c4f95142a46c187f9ab49af08ec6" +
                        "cffaa6b71c9ab7b40af21f66c2bec6b6" +
                        "bf71c57236904f35fa68407a46647d6e");
        C[6]  = hexToBytes(
                "f4c70e16eeaac5ec51ac86febf240954" +
                        "399ec6c7e6bf87c9d3473e33197a93c9" +
                        "0992abc52d822c3706476983284a0504" +
                        "3517454ca23c4af38886564d3a14d493");
        C[7]  = hexToBytes(
                "9b1f5b424d93c9a703e7aa020c6e4141" +
                        "4eb7f8719c36de1e89b4443b4ddbc49a" +
                        "f4892bcb929b069069d18d2bd1a5c42f" +
                        "36acc2355951a8d9a47f0dd4bf02e71e");
        C[8]  = hexToBytes(
                "378f5a541631229b944c9ad8ec165fde" +
                        "3a7d3a1b258942243cd955b7e00d0984" +
                        "800a440bdbb2ceb17b2b8a9aa6079c54" +
                        "0e38dc92cb1f2a607261445183235adb");
        C[9]  = hexToBytes(
                "abbedea680056f52382ae548b2e4f3f3" +
                        "8941e71cff8a78db1fffe18a1b336103" +
                        "9fe76702af69334b7a1e6c303b7652f4" +
                        "3698fad1153bb6c374b4c7fb98459ced");
        C[10] = hexToBytes(
                "7bcd9ed0efc889fb3002c6cd635afe94" +
                        "d8fa6bbbebab07612001802114846679" +
                        "8a1d71efea48b9caefbacd1d7d476e98" +
                        "dea2594ac06fd85d6bcaa4cd81f32d1b");
        C[11] = hexToBytes(
                "378ee767f11631bad21380b00449b17a" +
                        "cda43c32bcdf1d77f82012d430219f9b" +
                        "5d80ef9d1891cc86e71da4aa88e12852" +
                        "faf417d5d9b21b9948bc924af11bd720");
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < out.length; i++) {
            int hi = Character.digit(hex.charAt(2 * i), 16);
            int lo = Character.digit(hex.charAt(2 * i + 1), 16);
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }
}
