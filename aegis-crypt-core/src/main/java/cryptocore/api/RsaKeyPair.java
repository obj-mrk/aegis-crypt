package cryptocore.api;

import java.math.BigInteger;

public record RsaKeyPair(BigInteger n, BigInteger e, BigInteger d) { }
