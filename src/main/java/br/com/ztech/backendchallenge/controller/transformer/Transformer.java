package br.com.ztech.backendchallenge.controller.transformer;

@FunctionalInterface
public interface Transformer<F, T> {
    T transform(F f);
}