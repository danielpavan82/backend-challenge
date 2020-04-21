package br.com.ztech.backendchallenge.exception;

public class CouldNotFindMovieException extends RuntimeException {

    public CouldNotFindMovieException(final String message, Throwable cause) {
        super(message, cause);
    }

}
