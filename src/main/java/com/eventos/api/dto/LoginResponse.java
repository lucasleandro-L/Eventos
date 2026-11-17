package com.eventos.api.dto;

public record LoginResponse(String token, String tipo, long expiraEmSegundos, String perfil) {

}