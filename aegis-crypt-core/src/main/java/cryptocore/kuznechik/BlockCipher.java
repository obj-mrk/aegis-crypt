package cryptocore.kuznechik;

public interface BlockCipher {

    /**
     * Размер блока в байтах.
     */
    int getBlockSize();

    /**
     * Размер ключа в байтах.
     */
    int getKeySize();

    /**
     * Инициализация шифра.
     *
     * @param forEncryption true, если шифрование; false, если расшифрование
     * @param key           ключ длины getKeySize()
     */
    void init(boolean forEncryption, byte[] key);

    /**
     * Зашифровать один блок.
     *
     * @param in     входной массив
     * @param inOff  смещение во входном массиве
     * @param out    выходной массив
     * @param outOff смещение в выходном массиве
     */
    void encryptBlock(byte[] in, int inOff, byte[] out, int outOff);

    /**
     * Расшифровать один блок.
     */
    void decryptBlock(byte[] in, int inOff, byte[] out, int outOff);
}
