package htmlcompiler.example.spring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.boot.Banner.Mode.OFF;

@SpringBootApplication
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class Main {

    public static void main(final String... args) {
        new SpringApplicationBuilder(Main.class)
            .bannerMode(OFF).logStartupInfo(false)
            .build().run(args);
    }

    @Bean
    public SecurityFilterChain securityChain(final HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests((requests) -> requests.anyRequest().permitAll())
            .build();
    }

}
