package oauth2.security;

import lombok.RequiredArgsConstructor;
import oauth2.entity.Role;
import oauth2.entity.User;
import oauth2.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class OAuth2SecurityConfig {

    private final UserRepository userRepository;
    /**
     * <p>This first filter chain basically filter the authentication requests</p>
     *
     * <p>The order matters where, so the authentication filter must run first</p>
     *
     * <p>The method just trigger the auth system to auto generate the whole auth configuration, including the endpoints
     * (No controller needed to these methods</p>
     *
     * <p>Once is all set up and the endpoints are up, the filter checks if the user is authenticated. Once isn't, the user is
     * redirected to the user login endpoint</p>
     *
     *<p>This filter enables the Open ID, that makes possible the consumer to verify the user (and security config) infos without pass by the resource server</p>
     *
     * @param http (HttpSecurity object)
     * @return SecurityFilterChain object (makes the security system pass through filters)
     * @throws Exception
     */
    @Bean
    @Order (1)
    public SecurityFilterChain authenticationFilterChain (HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http.exceptionHandling(ex ->
                ex.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

        return http.build();
    }

    /**
     * <p>This is the second filter, that is responsible to the rest of the application access.</p>
     *
     * <p>The method allows the CSRF makes the sessions operation safe (ignoring session only in the register endpoint)</p>
     *
     * <p>This filter allows anyone to access the register endpoint without authenticate, but restricts others endpoints</p>
     *
     * <p>The spring will use the http basic form to authorize the client to authenticate the user, using it client identifier and 'password' (secret key)</p>
     *
     * <p>The form login permits the spring use the basic form login (as default)</p>
     *
     * @param http (HttpSecurity object)
     * @return SecurityFilterChain object
     * @throws Exception
     */
    @Bean
    @Order (2)
    public SecurityFilterChain defaultFilterChain (HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/v1/auth/"));

        http.authorizeHttpRequests(auth ->
                auth.requestMatchers("/api/v1/auth/").permitAll()
                .anyRequest().authenticated());

        http.httpBasic(Customizer.withDefaults());

        http.formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> customizeToken () {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {

                Authentication principal = context.getPrincipal();

                if (principal.getPrincipal() instanceof UserDetails userDetails) {

                    User user = userRepository.findByCpf(userDetails.getUsername())
                            .orElseThrow(() -> new NoSuchElementException("User not found"));// username = user cpf

                    List<String> roles = user.getRoles().stream()
                            .map(Role::getAuthority)
                            .toList();

                    context.getClaims().claim("role", roles);
                    context.getClaims().subject(String.valueOf(user.getId()));
                }
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
