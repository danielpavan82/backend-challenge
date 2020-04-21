package br.com.ztech.backendchallenge.service.impl;

import br.com.ztech.backendchallenge.BaseTest;
import br.com.ztech.backendchallenge.exception.CouldNotCreateMovieException;
import br.com.ztech.backendchallenge.exception.CouldNotFindMovieException;
import br.com.ztech.backendchallenge.exception.DuplicatedMovieException;
import br.com.ztech.backendchallenge.model.CensorshipLevel;
import br.com.ztech.backendchallenge.model.Movie;
import br.com.ztech.backendchallenge.repository.MovieRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static br.com.six2six.fixturefactory.Fixture.from;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MovieServiceImplTest extends BaseTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl target;


    @Test
    public void createMustWorks() {
        final Movie movie = from(Movie.class).gimme("candidateSemCensura");
        final Movie persistedMovie = from(Movie.class).gimme("persistedSemCensura");

        when(this.target.existsByName(movie.getName()))
                .thenReturn(Boolean.FALSE);

        when(this.movieRepository.save(movie))
                .thenReturn(persistedMovie);

        final Movie result = this.target.create(movie);

        assertThat(result.getId()).isNotNull();
    }

    @Test(expected = CouldNotCreateMovieException.class)
    public void createMustThrowExceptionWhenExistsByNameReturnException() {
        final Movie movieCandidate = from(Movie.class).gimme("candidateSemCensura");
        final Exception exception = new RuntimeException("Exception");

        when(this.target.existsByName(movieCandidate.getName()))
                .thenThrow(exception);

        this.target.create(movieCandidate);
    }

    @Test(expected = DuplicatedMovieException.class)
    public void createMustThrowExceptionWhenMovieNameExists() {
        final Movie movie = from(Movie.class).gimme("candidateSemCensura");

        when(this.target.existsByName(movie.getName()))
                .thenReturn(Boolean.TRUE);

        this.target.create(movie);
    }


    @Test(expected = CouldNotCreateMovieException.class)
    public void createMustThrowExceptionWhenSaveReturnException() {
        final Movie movie = from(Movie.class).gimme("candidateSemCensura");
        final Exception exception = new RuntimeException("Exception");

        when(this.target.existsByName(movie.getName()))
                .thenReturn(Boolean.FALSE);

        when(this.movieRepository.save(movie))
                .thenThrow(exception);

        this.target.create(movie);
    }

    @Test
    public void findAllByCensorshipLevelMustReturnResultsUnPaged() {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final List<Movie> movies = from(Movie.class).gimme(2, "persistedSemCensura", "persistedSemCensura");
        final Pageable pageable = Pageable.unpaged();

        when(this.movieRepository.findAllByCensorshipLevelOrderByNameAsc(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies));

        final Page<Movie> result = this.target.findAllByCensorshipLevel(censorshipLevel, pageable);

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsAll(movies);
    }

    @Test
    public void findAllByCensorshipLevelMustReturnResultsFromTheFirstPageOfOnePages() {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final List<Movie> movies = from(Movie.class).gimme(1, "persistedSemCensura");
        final Pageable pageable = PageRequest.of(0, 1);

        when(this.movieRepository.findAllByCensorshipLevelOrderByNameAsc(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies));

        final Page<Movie> result = this.target.findAllByCensorshipLevel(censorshipLevel, pageable);

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsAll(movies);
    }

    @Test
    public void findAllByCensorshipLevelMustReturnFirstPageResultsFromTwoPages() {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final List<Movie> movies = from(Movie.class).gimme(1, "persistedSemCensura");
        final Pageable pageable = PageRequest.of(0, 1);

        when(this.movieRepository.findAllByCensorshipLevelOrderByNameAsc(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies, pageable, 2));

        final Page<Movie> result = this.target.findAllByCensorshipLevel(censorshipLevel, pageable);

        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(movies.get(0));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnResultsFromTheLastPageOfTwoPages() {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final List<Movie> movies = from(Movie.class).gimme(1, "persistedSemCensura");
        final Pageable pageable = PageRequest.of(1, 1);

        when(this.movieRepository.findAllByCensorshipLevelOrderByNameAsc(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies, pageable, 2));

        final Page<Movie> result = this.target.findAllByCensorshipLevel(censorshipLevel, pageable);

        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(movies.get(0));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnLastEmptyPage() {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final List<Movie> movies = emptyList();
        final Pageable pageable = PageRequest.of(0, 10);

        when(this.movieRepository.findAllByCensorshipLevelOrderByNameAsc(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies));

        final Page<Movie> result = this.target.findAllByCensorshipLevel(censorshipLevel, pageable);

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent()).hasSize(0);
    }

    @Test(expected = CouldNotFindMovieException.class)
    public void findAllByCensorshipLevelMustThrowExceptionWhenAnErrorOccurs() {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final Pageable pageable = PageRequest.of(0, 10);
        final Exception exception = new RuntimeException("Exception");

        when(this.movieRepository.findAllByCensorshipLevelOrderByNameAsc(censorshipLevel, pageable))
                .thenThrow(exception);

        this.target.findAllByCensorshipLevel(censorshipLevel, pageable);
    }

    @Test(expected = CouldNotFindMovieException.class)
    public void existsByNameMustThrowExceptionWhenAnErrorOccurs() {
        final String movieName = "Velozes e Furiosos";
        final Exception exception = new RuntimeException("Exception");

        when(this.movieRepository.existsByNameIgnoreCase(movieName))
                .thenThrow(exception);

        this.target.existsByName(movieName);
    }

    @Test
    public void existsByNameMustReturnTrueWhenMovieNameExists() {
        final String movieName = "Velozes e Furiosos";

        when(this.movieRepository.existsByNameIgnoreCase(movieName))
                .thenReturn(Boolean.TRUE);

        final boolean result = this.target.existsByName(movieName);

        assertThat(result).isTrue();
    }

    @Test
    public void existsByNameMustReturnFalseWhenMovieNameNotExists() {
        final String movieName = "TESTE1234";

        when(this.movieRepository.existsByNameIgnoreCase(movieName))
                .thenReturn(Boolean.FALSE);

        final boolean result = this.target.existsByName(movieName);

        assertThat(result).isFalse();
    }

}
