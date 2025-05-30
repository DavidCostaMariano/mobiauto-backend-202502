package com.testeTecnico.revenda.util.mapper;


import com.testeTecnico.revenda.model.*;
import com.testeTecnico.revenda.model.request.ModificaOportunidadeRequestDTO;
import com.testeTecnico.revenda.model.request.OportunidadeRequestDTO;
import com.testeTecnico.revenda.model.response.OportunidadeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface OportunidadeMapper {

    OportunidadeMapper INSTANCE = Mappers.getMapper(OportunidadeMapper.class);

    @Mapping(source = "entity.usuario.nome", target = "responsavel_nome")
    @Mapping(source = "entity.revenda.nome_social", target = "revenda_nome")
    OportunidadeResponseDTO toResponseDto(String message, OportunidadeEntity entity);


    @Mapping(target = "id", ignore = true)
    @Mapping(source = "usuarioEntity", target = "usuario")
    OportunidadeEntity toEntityFromRequestDto(OportunidadeRequestDTO oportunidadeRequestDTO, ClienteEntity cliente, VeiculoEntity veiculo, RevendaEntity revenda, UsuarioEntity usuarioEntity);

    OportunidadeRequestDTO toOportunidadeRequestFromModificaOportunidadeRequest(ModificaOportunidadeRequestDTO modificaOportunidadeRequestDTO);

}
