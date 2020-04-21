package br.com.ztech.backendchallenge.controller;

import br.com.ztech.backendchallenge.controller.request.MovieRequest;
import br.com.ztech.backendchallenge.controller.response.MovieResponse;
import br.com.ztech.backendchallenge.controller.transformer.MovieRequestTransformer;
import br.com.ztech.backendchallenge.controller.transformer.MovieResponseTransformer;
import br.com.ztech.backendchallenge.exception.CensorshipLevelException;
import br.com.ztech.backendchallenge.exception.DuplicatedMovieException;
import br.com.ztech.backendchallenge.model.CensorshipLevel;
import br.com.ztech.backendchallenge.model.Movie;
import br.com.ztech.backendchallenge.service.MovieService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Api(value = "/movies", protocols = "http", tags = "movies")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/movies")
@RestController
public class MovieController {

    private final MovieService movieService;
    private final MovieRequestTransformer movieRequestTransformer;
    private final MovieResponseTransformer movieResponseTransformer;


    @ApiOperation(value = "Create a movie")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MovieResponse createMovie(final @RequestBody @Valid MovieRequest movieRequest) {
        log.info("Creating a movie: {}", movieRequest);
        try {
            log.info("Transform MovieRequest {} to Movie", movieRequest);
            final Movie movieTransform = this.movieRequestTransformer.transform(movieRequest);
            final Movie movie = this.movieService.create(movieTransform);
            log.info("Transform Movie {} to MovieResponse", movie);
            final MovieResponse movieResponse = this.movieResponseTransformer.transform(movie);
            log.info("The {} movie was successfully created with ID {}", movie.getName(), movie.getId());
            return movieResponse;
        } catch(final DuplicatedMovieException e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch(final Exception e) {
            final String errorMessage = "Error creating a movie: "+ movieRequest.toString();
            log.error(errorMessage, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, e);
        }
    }

    @ApiOperation(value = "Finding all movies by Censorship Level")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Page<MovieResponse> findAllByCensorshipLevel(final @RequestParam CensorshipLevel censorshipLevel,
                                                        final @RequestParam(required = false, defaultValue = "0") int page,
                                                        final @RequestParam(required = false, defaultValue = "0") int size) {
        log.info("Finding all movies by Censorship Level {} and page {} and size {}", censorshipLevel, page, size);
        try {
            if (censorshipLevel == null){
                throw new CensorshipLevelException("Censorship level cannot be blank. Allowed values: 'CENSURADO' and 'SEM_CENSURA'");
            }

            final Pageable pageRequest = page == 0 || size == 0
                    ? Pageable.unpaged()
                    : PageRequest.of(page - 1, size);

            final Page<Movie> moviesPage = this.movieService.findAllByCensorshipLevel(censorshipLevel, pageRequest);

            log.info("The movies by Censorship Level {} were found and returned", censorshipLevel);

            return moviesPage.map(this.movieResponseTransformer::transform);
        } catch(final CensorshipLevelException e) {
            log.error(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch(final Exception e) {
            final String error = "Error finding all movies by Censorship Level "+ censorshipLevel +" and page "+ page +" and size "+ size;
            log.error(error, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error, e);
        }
    }

}
