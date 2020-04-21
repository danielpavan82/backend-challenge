package br.com.ztech.backendchallenge.service;

import br.com.ztech.backendchallenge.model.CensorshipLevel;
import br.com.ztech.backendchallenge.model.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieService {

    Movie create(final Movie movie);

    boolean existsByName(final String name);

    Page<Movie> findAllByCensorshipLevel(final CensorshipLevel censorshipLevel, final Pageable pageRequest);

}
