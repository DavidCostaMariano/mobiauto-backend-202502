package com.testeTecnico.revenda.util.mapper;

import com.testeTecnico.revenda.model.ClienteEntity;
import com.testeTecnico.revenda.model.request.ClienteRequestDTO;
import com.testeTecnico.revenda.model.response.AuthResponse;
import com.testeTecnico.revenda.model.response.ClienteResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    AuthResponse toResponseDto(String message, long usuario_id, String cargo, String token);


}