package com.eliascanalesnieto.foodtracker.exception;

public class EntityNotFoundException extends Exception {

    public EntityNotFoundException() {
        super("Entity not found");
    }

}