package aegiscrypt.webui.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class ApiClientConfig {

    @Bean
    @RequestScope
    public RestClient restClient(HttpServletRequest req) {
        String baseUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
