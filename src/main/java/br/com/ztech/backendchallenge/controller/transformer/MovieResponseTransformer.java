package br.com.ztech.backendchallenge.controller.transformer;

import br.com.ztech.backendchallenge.controller.response.CastResponse;
import br.com.ztech.backendchallenge.controller.response.MovieResponse;
import br.com.ztech.backendchallenge.exception.CouldNotTransformException;
import br.com.ztech.backendchallenge.model.Cast;
import br.com.ztech.backendchallenge.model.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MovieResponseTransformer implements Transformer<Movie, MovieResponse> {

    @Override
    public MovieResponse transform(final Movie movie) {
        try {
            return MovieResponse.builder()
                    .id(movie.getId())
                    .name(movie.getName())
                    .censorshipLevel(movie.getCensorshipLevel())
                    .releaseDate(movie.getReleaseDate())
                    .director(movie.getDirector())
                    .cast(getCast(movie.getCast()))
                    .createdDate(movie.getCreatedDate())
                    .build();
        } catch(final Exception e) {
            throw new CouldNotTransformException("Could not transform Movie to MovieResponse", e);
        }
    }

    private List<CastResponse> getCast(final List<Cast> cast) {
        return CollectionUtils.isEmpty(cast) ? null : cast.stream()
                .map(e -> CastResponse.builder()
                        .name(e.getName())
                        .build())
                .collect(Collectors.toList());
    }

}
