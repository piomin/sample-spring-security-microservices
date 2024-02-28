package pl.piomin.samples.security.gateway.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(auth -> auth.anyExchange().authenticated())
                .oauth2Login(withDefaults())
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }

//	@Bean
//	public ReactiveJwtDecoder jwtDecoder() {
//		return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
//	}

//	@Bean
//	WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
//		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
//				new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
//		return WebClient.builder()
//				.filter(oauth2Client)
//				.build();
//	}

//	@Bean
//	public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authorizationCodeAccessTokenResponseClient() {
//		DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient =
//				new DefaultAuthorizationCodeTokenResponseClient();
//		accessTokenResponseClient.setRestOperations(restTemplate());
//
//		return accessTokenResponseClient;
//	}

//	@Bean
//	public RestTemplate restTemplate() {
//		return new RestTemplateBuilder().build();
//	}
}
