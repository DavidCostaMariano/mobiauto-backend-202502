package com.testeTecnico.revenda.util.mapper;

import com.testeTecnico.revenda.model.ClienteEntity;
import com.testeTecnico.revenda.model.VeiculoEntity;
import com.testeTecnico.revenda.model.request.ClienteRequestDTO;
import com.testeTecnico.revenda.model.request.VeiculoRequestDTO;
import com.testeTecnico.revenda.model.response.ClienteResponseDTO;
import com.testeTecnico.revenda.model.response.VeiculoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    ClienteResponseDTO toResponseDto(String message, ClienteEntity entity);

    @Mapping(target = "id", ignore = true)
    ClienteEntity toEntityFromRequestDto(ClienteRequestDTO requestDTO);

}
