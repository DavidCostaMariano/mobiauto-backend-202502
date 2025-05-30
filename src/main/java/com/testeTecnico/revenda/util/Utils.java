package com.testeTecnico.revenda.util;

import com.testeTecnico.revenda.model.ClienteEntity;
import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.UsuarioEntity;
import com.testeTecnico.revenda.model.VeiculoEntity;
import com.testeTecnico.revenda.repository.ClienteRepository;
import com.testeTecnico.revenda.repository.RevendaRepository;
import com.testeTecnico.revenda.repository.UsuarioRepository;
import com.testeTecnico.revenda.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Utils {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RevendaRepository revendaRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;
    public String getUserRole(Authentication authentication){
        return authentication.getAuthorities().iterator().next().getAuthority();
    }

    public Map<String, Object> recuperarInformacoesJwtToken(Authentication auth){
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        return details;
    }

    public ClienteEntity clienteValido(long id) throws Exception {
        ClienteEntity cliente = clienteRepository.findById(id).orElse(null);
        if(cliente == null){
            throw new Exception("Cliente inexistente");
        }
        return cliente;
    }

    public VeiculoEntity veiculoValido(long id) throws Exception {
        VeiculoEntity veiculo = veiculoRepository.findById(id).orElse(null);
        if(veiculo == null){
            throw new Exception("Veiculo inexistente");
        }
        return veiculo;
    }

    public RevendaEntity revendaValido(long id) throws Exception {
        RevendaEntity revenda = revendaRepository.findById(id).orElse(null);
        if(revenda == null){
            throw new Exception("Revenda inexistente");
        }
        return revenda;
    }

    public UsuarioEntity usuarioValido(long id) throws Exception {
        UsuarioEntity usuario = usuarioRepository.findById(id).orElse(null);
        if(usuario == null){
            throw new Exception("Usuario inexistente");
        }
        return usuario;
    }

}
