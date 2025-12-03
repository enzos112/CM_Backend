package com.medico.backend.exception;

import java.time.LocalDateTime;

public record CustomErrorResponse(
        LocalDateTime fecha,
        String mensaje,
        String detalles
) {}