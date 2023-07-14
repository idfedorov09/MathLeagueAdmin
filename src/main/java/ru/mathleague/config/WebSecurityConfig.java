package ru.mathleague.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.mathleague.repository.UserRepository;
import ru.mathleague.service.UserService;
import ru.mathleague.util.UpdateLastRequestFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UpdateLastRequestFilter updateLastRequestFilter;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/registration**").permitAll()
                        .requestMatchers("/css/unauth/**").permitAll()
                        .requestMatchers("/js/unauth/**").permitAll()
                        .requestMatchers("/images/unauth/**", "/favicon.ico").permitAll()
                        //.requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll())
                .sessionManagement(sessionManagement ->
                        sessionManagement
                                .maximumSessions(1)
                                .sessionRegistry(sessionRegistry)
                )
                .addFilterAfter(updateLastRequestFilter, BasicAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .invalidSessionUrl("/")
                );
        return http.build();
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }
}
