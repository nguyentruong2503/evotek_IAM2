package com.example.iam2.security;

import com.example.iam2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class KeycloakAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Autowired
    private UserService userService;

    @Value("${iam.security.keycloak-enabled:false}")
    private boolean keycloakEnabled;

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>(defaultConverter.convert(jwt));

        if (keycloakEnabled) {
            String username = jwt.getClaimAsString("preferred_username");
            if (username != null) {
                try {
                    authorities.addAll(userService.getAuthoritiesByUsername(username));
                    System.out.println("Quyền :" +authorities);
                } catch (Exception e) {
                    System.out.println("KHÔNG ĐƯỢC: " + e.getMessage());
                }
            }
        } else {
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));

            List<String> permissions = jwt.getClaimAsStringList("permissions");
            if (permissions != null) permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        }

        return authorities;
    }
}
