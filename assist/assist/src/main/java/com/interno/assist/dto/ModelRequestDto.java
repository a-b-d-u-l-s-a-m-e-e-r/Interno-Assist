package com.interno.assist.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ModelRequestDto {
    List<ModelContentDto> contents;
}
