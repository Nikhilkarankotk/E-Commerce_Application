package com.nkk.gatewayServer.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    //  interface available inside the spring framework.
    //  whenever you want to convey the role's information or privileged information,
    //  we need to make sure we are sending those details with these interface formats only.
    public Collection<GrantedAuthority> convert(Jwt source) {
//        purpose of getClaims() is it will give you access to the payload data
//        available inside your access token. (getClaims() is of type Map)
        Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");
        if(realmAccess == null || realmAccess.isEmpty()) {
            return new ArrayList<>();
        }
        // from these realm_access, i'm going to invoke the get method with the key as roles.
        // Inside this realme_access we have a map there is a key with the name roles
        Collection<GrantedAuthority> returnValue = ((List<String>) realmAccess.get("roles"))
                .stream().map(roleName -> "ROLE_" +roleName)
                // spring security expects the roles to be of type object of SimpleGrantedAuthority
                //  so we need to map the role name to SimpleGrantedAuthority
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return returnValue;
    }
}
