package com.testeTecnico.revenda.model.response;

public record AuthResponse(
        String message,
        long usuario_id,
        String cargo,
        String token) {}