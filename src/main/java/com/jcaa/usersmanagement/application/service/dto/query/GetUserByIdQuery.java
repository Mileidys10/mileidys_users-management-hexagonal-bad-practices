package com.jcaa.usersmanagement.application.service.dto.query;

import jakarta.validation.constraints.NotBlank;
// VIOLACIÓN Regla 3: se mezcla @Builder de Lombok con un record.
// Los records ya tienen constructor canónico; usar @Builder es redundante e innecesario en este caso.
import lombok.Builder;

@Builder
public record GetUserByIdQuery(

    @NotBlank String id) {

}
