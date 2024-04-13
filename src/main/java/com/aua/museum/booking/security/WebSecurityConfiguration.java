package com.aua.museum.booking.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@PropertySource("classpath:values.properties")
public class WebSecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @Value("${security.secret.key}")
    private String SECRET_KEY;

    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<HttpSessionEventPublisher>(new HttpSessionEventPublisher());
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService);
//    }


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/i18n/**", "/title", "/footer-info", "/js/**", "/css/**", "/images/**", "/", "/register", "/login", "/forgot-password/**", "/webjars/**").permitAll()
                                .requestMatchers("/calendar/**", "/event/", "/edit-profile/", "*/download-events/**", "/notifications/**").hasAnyRole("ADMIN_ROLE", "USER_ROLE",  "SUPER_ADMIN_ROLE")
                                .requestMatchers("/users/**", "/waiting-list/**", "/events/**").hasAnyAuthority("ADMIN_ROLE",  "SUPER_ADMIN_ROLE")
                                .requestMatchers("/homepage/update-content/**").hasAnyAuthority("ADMIN_ROLE", "SUPER_ADMIN_ROLE")
                                .anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .formLogin(auth ->
                        auth.loginPage("/login")
                                .defaultSuccessUrl("/homepage", true)
                                .failureHandler(customAuthenticationFailureHandler)
                                .permitAll())
                .logout(auth -> auth.logoutRequestMatcher(new AntPathRequestMatcher("/logoutCustom"))
                        .logoutSuccessUrl("/").deleteCookies("JSESSIONID", "remember-me")
                        .invalidateHttpSession(true))
                .rememberMe(auth -> auth.tokenValiditySeconds(365 * 24 * 3600).key(SECRET_KEY))
                .sessionManagement(auth -> auth.maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                        .expiredUrl("/login"))
                .build();
    }

}
