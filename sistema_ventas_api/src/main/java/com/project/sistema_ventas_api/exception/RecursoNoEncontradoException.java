package com.project.sistema_ventas_api.exception;

public class RecursoNoEncontradoException extends RuntimeException{

    public RecursoNoEncontradoException(String mensaje)
    {
        super(mensaje); //Con super le mandamos el mensaje a la clase padre que es el gestor de excepciones
    }
}
