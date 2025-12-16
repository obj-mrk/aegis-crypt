package aegiscrypt.config;

import cryptocore.api.CryptoService;
import cryptocore.impl.DefaultCryptoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoCoreConfig {

    @Bean
    public CryptoService cryptoService() {
        return new DefaultCryptoService();
    }
}

