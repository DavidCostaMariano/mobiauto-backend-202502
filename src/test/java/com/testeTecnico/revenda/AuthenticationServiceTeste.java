package com.testeTecnico.revenda;

import com.testeTecnico.revenda.model.UsuarioEntity;
import com.testeTecnico.revenda.repository.*;
import com.testeTecnico.revenda.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
public class AuthenticationServiceTeste {
    @InjectMocks
    private AuthorizationService authorizationService;
    @Mock private UsuarioRepository usuarioRepository;



    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void deveRetornarUsuarioQuandoEmailExiste() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setEmail("teste@email.com");

        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(usuario);

        UserDetails result = authorizationService.loadUserByUsername("teste@email.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("teste@email.com");
    }

}
