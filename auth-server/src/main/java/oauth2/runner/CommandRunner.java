package oauth2.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Configuration
public class CommandRunner {

    @Bean
    public CommandLineRunner initClient (RegisteredClientRepository clientRepository) {
        return args -> {
            if (clientRepository.findByClientId("postman-client") == null) {

                RegisteredClient postmanClient = RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("postman-client")
                        .clientSecret("{noop}postman-secret")
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("https://oauth.pstmn.io/v1/callback")
                        .scope(OidcScopes.OPENID)
                        .scope(OidcScopes.PROFILE)
                        .scope("api.read")
                        .scope("api.write")
                        .clientSettings(ClientSettings.builder()
                                .requireAuthorizationConsent(true)
                                .build())
                        .tokenSettings(TokenSettings.builder()
                                .accessTokenTimeToLive(Duration.of(1, ChronoUnit.HOURS))
                                .refreshTokenTimeToLive(Duration.of(7, ChronoUnit.DAYS))
                                .reuseRefreshTokens(false)
                                .build())
                        .build();

                clientRepository.save(postmanClient);
            }
        };
    }
}
