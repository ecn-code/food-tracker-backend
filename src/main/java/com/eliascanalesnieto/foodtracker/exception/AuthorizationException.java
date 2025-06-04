package com.eliascanalesnieto.foodtracker.exception;

public class AuthorizationException extends Exception {

    public AuthorizationException() {
        super("Error while authorizing");
    }

}