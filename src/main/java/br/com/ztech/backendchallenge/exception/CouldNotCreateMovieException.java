package br.com.ztech.backendchallenge.exception;

public class CouldNotCreateMovieException extends RuntimeException {

    public CouldNotCreateMovieException(final String message, Throwable cause) {
        super(message, cause);
    }

}
