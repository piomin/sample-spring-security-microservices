package pl.piomin.samples.security.callme.saml;

import org.opensaml.core.xml.schema.impl.XSStringImpl;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.core.Saml2ResponseValidatorResult;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

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
            if (result == null || result.hasErrors()) {
                String inResponseTo = responseToken.getResponse().getInResponseTo();
                throw new CustomSamlValidationException(inResponseTo);
            }
            LOG.info("SAML2 RESPONSE: {}", responseToken.getToken().getSaml2Response());
            return result;
        });

        provider.setResponseAuthenticationConverter(token -> {
            var auth = OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter().convert(token);
            LOG.info("AUTHORITIES: {}", auth.getAuthorities());

            var attrValues = token.getResponse().getAssertions().stream()
                    .flatMap(as -> as.getAttributeStatements().stream())
                    .flatMap(attrs -> attrs.getAttributes().stream())
                        .filter(attrs -> attrs.getName().equals("member"))
                        .findFirst().orElseThrow().getAttributeValues();

            if (!attrValues.isEmpty()) {
                var member = ((XSStringImpl) attrValues.getFirst()).getValue();
                LOG.info("MEMBER: {}", member);
                List<GrantedAuthority> authoritiesList = List.of(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("ROLE_" + member.toUpperCase().replaceFirst("/", ""))
                );

                LOG.info("NEW AUTHORITIES: {}", authoritiesList);
                return new Saml2Authentication((AuthenticatedPrincipal) auth.getPrincipal(), auth.getSaml2Response(), authoritiesList);
            } else return auth;
        });

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest()
                        .authenticated())
//                .saml2Login(withDefaults())
                .saml2Login(saml2 -> saml2
                        .authenticationManager(new ProviderManager(provider))
                )
                .saml2Metadata(withDefaults());
        return http.build();
    }

}
