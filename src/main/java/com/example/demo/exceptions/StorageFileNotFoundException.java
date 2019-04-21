package com.example.demo.exceptions;

public class StorageFileNotFoundException extends StorageException {
    public StorageFileNotFoundException(String message) {super(message);}

    public StorageFileNotFoundException(String message, Exception e) {
        super(message, e);
    }
}
