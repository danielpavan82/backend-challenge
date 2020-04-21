package br.com.ztech.backendchallenge.repository;

import br.com.ztech.backendchallenge.BaseTest;
import br.com.ztech.backendchallenge.model.CensorshipLevel;
import br.com.ztech.backendchallenge.model.Movie;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

@DataMongoTest
@FixMethodOrder(NAME_ASCENDING)
@EnableMongoAuditing
@RunWith(SpringRunner.class)
public class MovieRepositoryTest extends BaseTest {

    @Autowired
    private MovieRepository movieRepository;

    @Before
    public void before() throws Exception {
        this.movieRepository.deleteAll();
    }

    @Test(expected = DuplicateKeyException.class)
    public void saveMustReturnDuplicateKeyExceptionWhenInsertSameMovieName() {
        final Movie movie1 = from(Movie.class).gimme("candidateSemCensura");
        final Movie movie2 = from(Movie.class).gimme("candidateSemCensura");

        this.movieRepository.save(movie1);
        this.movieRepository.save(movie2);
    }

    @Test
    public void findAllByCensorshipLevelMustReturnValidMovieCensurado() {
        final List<Movie> movies = from(Movie.class).gimme(2, "candidateSemCensura", "candidateCensurado");
        final Pageable pageRequest = Pageable.unpaged();

        final List<Movie> persistedMovies = this.movieRepository.saveAll(movies);

        final Page<Movie> result = this.movieRepository.findAllByCensorshipLevelOrderByNameAsc(CensorshipLevel.CENSURADO, pageRequest);

        assertThat(persistedMovies).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    public void findAllByCensorshipLevelMustReturnValidMovieSemCensura() {
        final List<Movie> movies = from(Movie.class).gimme(2, "candidateSemCensura", "candidateCensurado");
        final Pageable pageRequest = Pageable.unpaged();

        final List<Movie> persistedMovies = this.movieRepository.saveAll(movies);

        final Page<Movie> result = this.movieRepository.findAllByCensorshipLevelOrderByNameAsc(CensorshipLevel.SEM_CENSURA, pageRequest);

        assertThat(persistedMovies).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    public void existsByNameMustReturnTrue() {
        final List<Movie> movies = from(Movie.class).gimme(2, "candidateSemCensura", "candidateCensurado");

        this.movieRepository.saveAll(movies);

        final boolean exists = this.movieRepository.existsByNameIgnoreCase("Mad Max");

        assertThat(exists).isTrue();
    }

    @Test
    public void existsByNameMustReturnFalseWhenCensorshipLevelIsSemCensura() {
        final List<Movie> movies = from(Movie.class).gimme(2, "candidateSemCensura", "candidateCensurado");

        this.movieRepository.saveAll(movies);

        final boolean exists = this.movieRepository.existsByNameIgnoreCase("Monstros SA");

        assertThat(exists).isFalse();
    }

}
