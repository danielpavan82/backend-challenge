package br.com.ztech.backendchallenge.exception;

public class DuplicatedMovieException extends RuntimeException {

    public DuplicatedMovieException(final String name) {
        super("The movie with name '"+ name +"' already exists.");
    }

}
