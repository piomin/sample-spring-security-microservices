package pl.piomin.samples.security.callme.saml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CallmeSamlApplication {

    public static void main(String[] args) {
        SpringApplication.run(CallmeSamlApplication.class, args);
    }

//    @Bean
//    Saml2AuthenticationRequestResolver authenticationRequestResolver(RelyingPartyRegistrationRepository registrations) {
//        RelyingPartyRegistrationResolver registrationResolver =
//                new DefaultRelyingPartyRegistrationResolver(registrations);
//        OpenSaml4AuthenticationRequestResolver authenticationRequestResolver =
//                new OpenSaml4AuthenticationRequestResolver(registrationResolver);
//        authenticationRequestResolver.setAuthnRequestCustomizer((context) -> context
//                .getAuthnRequest().setForceAuthn(true));
//        return authenticationRequestResolver;
//    }
}
