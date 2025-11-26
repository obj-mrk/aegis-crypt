package mrk.cryptocore.kuznechik;

/**
 * Таблицы S-бокса и обратного S-бокса для Кузнечика (ГОСТ Р 34.12-2015 / RFC 7801).
 */
final class KuznechikSBox {

    private KuznechikSBox() {
        // utility class
    }

    /**
     * Прямой S-бокс Pi' из стандарта (значения 0..255).
     */
    static final byte[] SBOX = new byte[] {
            (byte)252, (byte)238, (byte)221, (byte) 17, (byte)207, (byte)110, (byte) 49, (byte) 22,
            (byte)251, (byte)196, (byte)250, (byte)218, (byte) 35, (byte)197, (byte)  4, (byte) 77,
            (byte)233, (byte)119, (byte)240, (byte)219, (byte)147, (byte) 46, (byte)153, (byte)186,
            (byte) 23, (byte) 54, (byte)241, (byte)187, (byte) 20, (byte)205, (byte) 95, (byte)193,
            (byte)249, (byte) 24, (byte)101, (byte) 90, (byte)226, (byte) 92, (byte)239, (byte) 33,
            (byte)129, (byte) 28, (byte) 60, (byte) 66, (byte)139, (byte)  1, (byte)142, (byte) 79,
            (byte)  5, (byte)132, (byte)  2, (byte)174, (byte)227, (byte)106, (byte)143, (byte)160,
            (byte)  6, (byte) 11, (byte)237, (byte)152, (byte)127, (byte)212, (byte)211, (byte) 31,
            (byte)235, (byte) 52, (byte) 44, (byte) 81, (byte)234, (byte)200, (byte) 72, (byte)171,
            (byte)242, (byte) 42, (byte)104, (byte)162, (byte)253, (byte) 58, (byte)206, (byte)204,
            (byte)181, (byte)112, (byte) 14, (byte) 86, (byte)  8, (byte) 12, (byte)118, (byte) 18,
            (byte)191, (byte)114, (byte) 19, (byte) 71, (byte)156, (byte)183, (byte) 93, (byte)135,
            (byte) 21, (byte)161, (byte)150, (byte) 41, (byte) 16, (byte)123, (byte)154, (byte)199,
            (byte)243, (byte)145, (byte)120, (byte)111, (byte)157, (byte)158, (byte)178, (byte)177,
            (byte) 50, (byte)117, (byte) 25, (byte) 61, (byte)255, (byte) 53, (byte)138, (byte)126,
            (byte)109, (byte) 84, (byte)198, (byte)128, (byte)195, (byte)189, (byte) 13, (byte) 87,
            (byte)223, (byte)245, (byte) 36, (byte)169, (byte) 62, (byte)168, (byte) 67, (byte)201,
            (byte)215, (byte)121, (byte)214, (byte)246, (byte)124, (byte) 34, (byte)185, (byte)  3,
            (byte)224, (byte) 15, (byte)236, (byte)222, (byte)122, (byte)148, (byte)176, (byte)188,
            (byte)220, (byte)232, (byte) 40, (byte) 80, (byte) 78, (byte) 51, (byte) 10, (byte) 74,
            (byte)167, (byte)151, (byte) 96, (byte)115, (byte) 30, (byte)  0, (byte) 98, (byte) 68,
            (byte) 26, (byte)184, (byte) 56, (byte)130, (byte)100, (byte)159, (byte) 38, (byte) 65,
            (byte)173, (byte) 69, (byte) 70, (byte)146, (byte) 39, (byte) 94, (byte) 85, (byte) 47,
            (byte)140, (byte)163, (byte)165, (byte)125, (byte)105, (byte)213, (byte)149, (byte) 59,
            (byte)  7, (byte) 88, (byte)179, (byte) 64, (byte)134, (byte)172, (byte) 29, (byte)247,
            (byte) 48, (byte) 55, (byte)107, (byte)228, (byte)136, (byte)217, (byte)231, (byte)137,
            (byte)225, (byte) 27, (byte)131, (byte) 73, (byte) 76, (byte) 63, (byte)248, (byte)254,
            (byte)141, (byte) 83, (byte)170, (byte)144, (byte)202, (byte)216, (byte)133, (byte) 97,
            (byte) 32, (byte)113, (byte)103, (byte)164, (byte) 45, (byte) 43, (byte)  9, (byte) 91,
            (byte)203, (byte)155, (byte) 37, (byte)208, (byte)190, (byte)229, (byte)108, (byte) 82,
            (byte) 89, (byte)166, (byte)116, (byte)210, (byte)230, (byte)244, (byte)180, (byte)192,
            (byte)209, (byte)102, (byte)175, (byte)194, (byte) 57, (byte) 75, (byte) 99, (byte)182
    };

    /**
     * Обратный S-бокс Pi^{-1}' из стандарта.
     */
    static final byte[] INV_SBOX = new byte[] {
            (byte)165, (byte) 45, (byte) 50, (byte)143, (byte) 14, (byte) 48, (byte) 56, (byte)192,
            (byte) 84, (byte)230, (byte)158, (byte) 57, (byte) 85, (byte)126, (byte) 82, (byte)145,
            (byte)100, (byte)  3, (byte) 87, (byte) 90, (byte) 28, (byte) 96, (byte)  7, (byte) 24,
            (byte) 33, (byte)114, (byte)168, (byte)209, (byte) 41, (byte)198, (byte)164, (byte) 63,
            (byte)224, (byte) 39, (byte)141, (byte) 12, (byte)130, (byte)234, (byte)174, (byte)180,
            (byte)154, (byte) 99, (byte) 73, (byte)229, (byte) 66, (byte)228, (byte) 21, (byte)183,
            (byte)200, (byte)  6, (byte)112, (byte)157, (byte) 65, (byte)117, (byte) 25, (byte)201,
            (byte)170, (byte)252, (byte) 77, (byte)191, (byte) 42, (byte)115, (byte)132, (byte)213,
            (byte)195, (byte)175, (byte) 43, (byte)134, (byte)167, (byte)177, (byte)178, (byte) 91,
            (byte) 70, (byte)211, (byte)159, (byte)253, (byte)212, (byte) 15, (byte)156, (byte) 47,
            (byte)155, (byte) 67, (byte)239, (byte)217, (byte)121, (byte)182, (byte) 83, (byte)127,
            (byte)193, (byte)240, (byte) 35, (byte)231, (byte) 37, (byte) 94, (byte)181, (byte) 30,
            (byte)162, (byte)223, (byte)166, (byte)254, (byte)172, (byte) 34, (byte)249, (byte)226,
            (byte) 74, (byte)188, (byte) 53, (byte)202, (byte)238, (byte)120, (byte)  5, (byte)107,
            (byte) 81, (byte)225, (byte) 89, (byte)163, (byte)242, (byte)113, (byte) 86, (byte) 17,
            (byte)106, (byte)137, (byte)148, (byte)101, (byte)140, (byte)187, (byte)119, (byte) 60,
            (byte)123, (byte) 40, (byte)171, (byte)210, (byte) 49, (byte)222, (byte)196, (byte) 95,
            (byte)204, (byte)207, (byte)118, (byte) 44, (byte)184, (byte)216, (byte) 46, (byte) 54,
            (byte)219, (byte)105, (byte)179, (byte) 20, (byte)149, (byte)190, (byte) 98, (byte)161,
            (byte) 59, (byte) 22, (byte)102, (byte)233, (byte) 92, (byte)108, (byte)109, (byte)173,
            (byte) 55, (byte) 97, (byte) 75, (byte)185, (byte)227, (byte)186, (byte)241, (byte)160,
            (byte)133, (byte)131, (byte)218, (byte) 71, (byte)197, (byte)176, (byte) 51, (byte)250,
            (byte)150, (byte)111, (byte)110, (byte)194, (byte)246, (byte) 80, (byte)255, (byte) 93,
            (byte)169, (byte)142, (byte) 23, (byte) 27, (byte)151, (byte)125, (byte)236, (byte) 88,
            (byte)247, (byte) 31, (byte)251, (byte)124, (byte)  9, (byte) 13, (byte)122, (byte)103,
            (byte) 69, (byte)135, (byte)220, (byte)232, (byte) 79, (byte) 29, (byte) 78, (byte)  4,
            (byte)235, (byte)248, (byte)243, (byte) 62, (byte) 61, (byte)189, (byte)138, (byte)136,
            (byte)221, (byte)205, (byte) 11, (byte) 19, (byte)152, (byte)  2, (byte)147, (byte)128,
            (byte)144, (byte)208, (byte) 36, (byte) 52, (byte)203, (byte)237, (byte)244, (byte)206,
            (byte)153, (byte) 16, (byte) 68, (byte) 64, (byte)146, (byte) 58, (byte)  1, (byte) 38,
            (byte) 18, (byte) 26, (byte) 72, (byte)104, (byte)245, (byte)129, (byte)139, (byte)199,
            (byte)214, (byte) 32, (byte) 10, (byte)  8, (byte)  0, (byte) 76, (byte)215, (byte)116
    };

    /**
     * Применить S к блоку из 16 байт (in-place).
     */
    static void applyS(byte[] state) {
        if (state.length != 16) {
            throw new IllegalArgumentException("State must be 16 bytes");
        }
        for (int i = 0; i < 16; i++) {
            state[i] = SBOX[state[i] & 0xFF];
        }
    }

    /**
     * Применить S^{-1} к блоку из 16 байт (in-place).
     */
    static void applyInvS(byte[] state) {
        if (state.length != 16) {
            throw new IllegalArgumentException("State must be 16 bytes");
        }
        for (int i = 0; i < 16; i++) {
            state[i] = INV_SBOX[state[i] & 0xFF];
        }
    }
}
