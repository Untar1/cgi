package com.example.demo.exceptions;

public class PythonServiceException extends RuntimeException {
    public PythonServiceException(String message) {super(message);}

    public PythonServiceException(String message, Exception e) {
        super(message, e);
    }
}
