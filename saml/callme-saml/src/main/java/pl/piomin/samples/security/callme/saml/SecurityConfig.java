package pl.piomin.samples.security.callme.saml;

import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.saml2.core.Saml2ResponseValidatorResult;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        OpenSaml4AuthenticationProvider provider = new OpenSaml4AuthenticationProvider();
        provider.setResponseValidator((responseToken) -> {
            Saml2ResponseValidatorResult result = OpenSaml4AuthenticationProvider
                    .createDefaultResponseValidator()
                    .convert(responseToken);
            if (!result.getErrors().isEmpty()) {
                String inResponseTo = responseToken.getResponse().getInResponseTo();
//                throw new CustomSaml2AuthenticationException(result, inResponseTo);
            }
            LOG.info("!!! SAML2 RESPONSE: " + responseToken.getToken().getSaml2Response());
            return result;
        });

        provider.setResponseAuthenticationConverter(token -> {
            var auth = OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter().convert(token);
            LOG.info("AUTHORITIES: " + auth.getAuthorities());
            token.getResponse().getAssertions().forEach(it -> it.getAttributeStatements()
                    .forEach(st -> st.getAttributes().forEach(attr -> LOG.info(attr.getName() + "=" + ((XSStringImpl) attr.getAttributeValues().get(0)).getValue()))));
            return auth;
        });

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest()
                        .authenticated())
                .saml2Login(saml2 -> saml2
                        .authenticationManager(new ProviderManager(provider))
                )
                .saml2Metadata(withDefaults())
//                .saml2Logout(withDefaults())
                ;
        return http.build();
    }

}
