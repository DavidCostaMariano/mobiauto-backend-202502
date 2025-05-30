package com.testeTecnico.revenda.util.mapper;

import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.UsuarioEntity;
import com.testeTecnico.revenda.model.VeiculoEntity;
import com.testeTecnico.revenda.model.request.UsuarioRequestDTO;
import com.testeTecnico.revenda.model.request.VeiculoRequestDTO;
import com.testeTecnico.revenda.model.response.UsuarioResponseDTO;
import com.testeTecnico.revenda.model.response.VeiculoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VeiculoMapper {

    VeiculoMapper INSTANCE = Mappers.getMapper(VeiculoMapper.class);

    VeiculoResponseDTO toResponseDto(String message, VeiculoEntity entity);

    @Mapping(target = "id", ignore = true)
    VeiculoEntity toEntityFromRequestDto(VeiculoRequestDTO requestDTO);

}
