package org.zunr1.backend.exception;

public class TokenErrorException extends RuntimeException {
    public TokenErrorException(String message) {
        super(message);
    }
}
