package org.example.common.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
@ConfigurationProperties(prefix ="mail")
@Data
public class EmailProperties {
    private Map<String, SenderConfig> senders;
    @Data
    public static class SenderConfig {
        private String host;
        private String port;
        private String username;
        private String password;//授权码
        private Boolean starttlsEnable;
        private Boolean sslEnable;
    }
}
