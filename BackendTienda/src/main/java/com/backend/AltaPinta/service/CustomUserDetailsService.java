package com.backend.AltaPinta.service;

import com.backend.AltaPinta.model.Cliente;
import com.backend.AltaPinta.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        Cliente c = clienteRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado"));

        return User.builder()
                .username(c.getCorreo())
                .password(c.getPassword())
                .authorities("ROLE_" + c.getRol().name())
                .build();
    }
}
