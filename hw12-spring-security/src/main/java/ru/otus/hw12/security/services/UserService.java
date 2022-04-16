package ru.otus.hw12.security.services;


import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.hw12.security.domain.Role;
import ru.otus.hw12.security.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val userEntity = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь " + username + " не найден"));
        return new User(userEntity.getName(),
                userEntity.getPassword(),
                userEntity.isEnabled(),
                !userEntity.isExpired(),
                true,
                !userEntity.isLocked(),
                getGrantedAuthorities(userEntity.getRoles())
        );
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<Role> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());
    }
}
