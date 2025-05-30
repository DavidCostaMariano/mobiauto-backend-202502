package com.testeTecnico.revenda.util.mapper;

import com.testeTecnico.revenda.model.RevendaEntity;
import com.testeTecnico.revenda.model.request.RevendaRequestDTO;
import com.testeTecnico.revenda.model.response.RevendaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RevendaMapper {

    RevendaMapper INSTANCE = Mappers.getMapper(RevendaMapper.class);

    RevendaResponseDTO toResponseDto(String message, RevendaEntity entity);

    RevendaEntity toEntityFromRequestDto(RevendaRequestDTO requestDTO);
}
