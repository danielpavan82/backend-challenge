package br.com.ztech.backendchallenge.service.impl;

import br.com.ztech.backendchallenge.exception.CouldNotCreateMovieException;
import br.com.ztech.backendchallenge.exception.CouldNotFindMovieException;
import br.com.ztech.backendchallenge.exception.DuplicatedMovieException;
import br.com.ztech.backendchallenge.model.CensorshipLevel;
import br.com.ztech.backendchallenge.model.Movie;
import br.com.ztech.backendchallenge.repository.MovieRepository;
import br.com.ztech.backendchallenge.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;


    @Override
    public Movie create(final Movie movie) {
        log.info("Create a movie '{}'", movie.getName());
        try {
            if (this.existsByName(movie.getName())) {
                throw new DuplicatedMovieException(movie.getName());
            }
            return this.movieRepository.save(movie);
        } catch (final DuplicatedMovieException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (final Exception e) {
            log.error("Unexpected error creating a movie", e);
            throw new CouldNotCreateMovieException(e.getMessage(), e);
        }
    }

    @Override
    public Page<Movie> findAllByCensorshipLevel(final CensorshipLevel censorshipLevel, final Pageable pageRequest) {
        log.info("Find all movies by Censorship Level '{}'", censorshipLevel);
        try {
            return this.movieRepository.findAllByCensorshipLevelOrderByNameAsc(censorshipLevel, pageRequest);
        } catch (final Exception e) {
            log.error("Unexpected error finding movies by Censorship Level: "+ censorshipLevel, e);
            throw new CouldNotFindMovieException(e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByName(final String name) {
        log.info("Check if the movie name exists '{}'", name);
        try {
            return this.movieRepository.existsByNameIgnoreCase(name.trim());
        } catch (final Exception e) {
            log.error("Unexpected error when checking if the movie exists by name: "+ name, e);
            throw new CouldNotFindMovieException(e.getMessage(), e);
        }
    }

}
