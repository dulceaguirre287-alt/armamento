package com.uniremington.armamento.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(RecursoNoEncontradoException exception, Model model, HttpServletRequest request) {
        model.addAttribute("mensaje", exception.getMessage());
        model.addAttribute("ruta", request.getRequestURI());
        return "error/404";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String solicitudInvalida(IllegalArgumentException exception, Model model, HttpServletRequest request) {
        model.addAttribute("mensaje", exception.getMessage());
        model.addAttribute("ruta", request.getRequestURI());
        return "error/400";
    }
}
