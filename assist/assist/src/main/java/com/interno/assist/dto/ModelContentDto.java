package com.interno.assist.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ModelContentDto {
    List<ModelContentPartDto> parts;
}
