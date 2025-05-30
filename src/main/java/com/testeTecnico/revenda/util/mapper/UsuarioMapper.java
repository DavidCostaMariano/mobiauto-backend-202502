package com.testeTecnico.revenda.util.mapper;

import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.UsuarioEntity;
import com.testeTecnico.revenda.model.request.UsuarioRequestDTO;
import com.testeTecnico.revenda.model.response.UsuarioResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    @Mapping(source = "entity.revenda.nome_social", target = "revenda_nome")

    UsuarioResponseDTO toResponseDto(String message, UsuarioEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "entity", target = "revenda")
    UsuarioEntity toEntityFromRequestDto(UsuarioRequestDTO requestDTO, RevendaEntity entity);

}
