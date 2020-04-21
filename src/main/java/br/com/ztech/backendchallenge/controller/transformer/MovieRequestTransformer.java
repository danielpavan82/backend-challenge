package br.com.ztech.backendchallenge.controller.transformer;

import br.com.ztech.backendchallenge.controller.request.CastRequest;
import br.com.ztech.backendchallenge.controller.request.MovieRequest;
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
public class MovieRequestTransformer implements Transformer<MovieRequest, Movie> {

    @Override
    public Movie transform(final MovieRequest movieRequest) {
        try {
            return Movie.builder()
                    .name(movieRequest.getName())
                    .censorshipLevel(movieRequest.getCensorshipLevel())
                    .releaseDate(movieRequest.getReleaseDate())
                    .director(movieRequest.getDirector())
                    .cast(getCast(movieRequest.getCast()))
                    .build();
        } catch(final Exception e) {
            throw new CouldNotTransformException("Could not transform MovieRequest to Movie", e);
        }
    }

    private List<Cast> getCast(final List<CastRequest> cast) {
        return CollectionUtils.isEmpty(cast) ? null : cast.stream()
                .map(e -> Cast.builder()
                        .name(e.getName())
                        .build())
                .collect(Collectors.toList());
    }

}
