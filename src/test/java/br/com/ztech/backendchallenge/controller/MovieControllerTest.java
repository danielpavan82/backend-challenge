package br.com.ztech.backendchallenge.controller;

import br.com.ztech.backendchallenge.BaseTest;
import br.com.ztech.backendchallenge.controller.request.MovieRequest;
import br.com.ztech.backendchallenge.controller.response.MovieResponse;
import br.com.ztech.backendchallenge.controller.transformer.MovieRequestTransformer;
import br.com.ztech.backendchallenge.controller.transformer.MovieResponseTransformer;
import br.com.ztech.backendchallenge.exception.CouldNotFindMovieException;
import br.com.ztech.backendchallenge.exception.DuplicatedMovieException;
import br.com.ztech.backendchallenge.model.CensorshipLevel;
import br.com.ztech.backendchallenge.model.Movie;
import br.com.ztech.backendchallenge.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;

import static br.com.six2six.fixturefactory.Fixture.from;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@FixMethodOrder(NAME_ASCENDING)
@WebMvcTest(MovieController.class)
@RunWith(SpringRunner.class)
public class MovieControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @MockBean
    private MovieRequestTransformer movieRequestTransformer;

    @MockBean
    private MovieResponseTransformer movieResponseTransformer;

    private static final String URL = "/movies";


    @Test
    public void createMovieMustWorks() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("candidateSemCensura");
        final Movie movie = from(Movie.class).gimme("candidateSemCensura");
        final Movie persisted = from(Movie.class).gimme("persistedSemCensura");
        final MovieResponse movieResponse = from(MovieResponse.class).gimme("persistedSemCensura");

        when(this.movieRequestTransformer.transform(movieRequest))
                .thenReturn(movie);

        when(this.movieService.create(movie))
                .thenReturn(persisted);

        when(this.movieResponseTransformer.transform(persisted))
                .thenReturn(movieResponse);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.id").value(persisted.getId()));
    }

    @Test
    public void createMovieMustReturnErrorWhenDuplicatedMovie() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("candidateSemCensura");
        final Movie movie = from(Movie.class).gimme("candidateSemCensura");
        final Exception exception = new DuplicatedMovieException(movieRequest.getName());

        when(this.movieRequestTransformer.transform(movieRequest))
                .thenReturn(movie);

        when(this.movieService.create(movie))
                .thenThrow(exception);

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String error = mvcResult.getResponse().getErrorMessage();

        assertThat(error)
                .isEqualTo("The movie with name '"+ movieRequest.getName() +"' already exists.");

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWhenCreateMovie() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("candidateSemCensura");
        final Movie movie = from(Movie.class).gimme("candidateSemCensura");
        final Exception exception = new RuntimeException("Unexpected Exception");

        when(this.movieRequestTransformer.transform(movieRequest))
                .thenReturn(movie);

        when(this.movieService.create(movie))
                .thenThrow(exception);

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();
        final String errorMessage = mvcResult.getResponse().getErrorMessage();

        assertThat(errorMessage)
                .startsWith("Error creating a movie: ");

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWithInvalidCast() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("invalidCast");

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                 )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String errorMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertThat(errorMessage)
                .contains("Cast must have at least 1 and at most 10 names");

        verify(this.movieRequestTransformer, never())
                .transform(any(MovieRequest.class));

        verify(this.movieService, never())
                .create(any(Movie.class));

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWithNullCensorshipLevel() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("nullCensorshipLevel");

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String error = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertThat(error)
                .contains("Censorship level is required. Allowed values: 'CENSURADO' and 'SEM_CENSURA'");

        verify(this.movieRequestTransformer, never())
                .transform(any(MovieRequest.class));

        verify(this.movieService, never())
                .create(any(Movie.class));

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWithEmptyCast() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("emptyCast");

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                 )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String error = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertThat(error)
                .contains("Cast must have at least 1 and at most 10 names");

        verify(this.movieRequestTransformer, never())
                .transform(any(MovieRequest.class));

        verify(this.movieService, never())
                .create(any(Movie.class));

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWithNullCast() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("nullCast");

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String error = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertThat(error)
                .contains("Cast must have at least 1 and at most 10 names");

        verify(this.movieRequestTransformer, never())
                .transform(any(MovieRequest.class));

        verify(this.movieService, never())
                .create(any(Movie.class));

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWithBlankName() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("blankName");

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String error = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertThat(error)
                .contains("Name cannot be blank");

        verify(this.movieRequestTransformer, never())
                .transform(any(MovieRequest.class));

        verify(this.movieService, never())
                .create(any(Movie.class));

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWithNullName() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("nullName");

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String error = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertThat(error)
                .contains("Name is required");

        verify(this.movieRequestTransformer, never())
                .transform(any(MovieRequest.class));

        verify(this.movieService, never())
                .create(any(Movie.class));

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWithNullReleaseDate() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("nullReleaseDate");

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String error = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertThat(error)
                .contains("Release date is required");

        verify(this.movieRequestTransformer, never())
                .transform(any(MovieRequest.class));

        verify(this.movieService, never())
                .create(any(Movie.class));

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWithBlankDirector() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("blankDirector");

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String error = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertThat(error)
                .contains("Director cannot be blank");

        verify(this.movieRequestTransformer, never())
                .transform(any(MovieRequest.class));

        verify(this.movieService, never())
                .create(any(Movie.class));

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void createMovieMustReturnErrorWithNullDirector() throws Exception {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("nullDirector");

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post(URL)
                        .content(objectMapper.writeValueAsString(movieRequest))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String error = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertThat(error)
                .contains("Director is required");

        verify(this.movieRequestTransformer, never())
                .transform(any(MovieRequest.class));

        verify(this.movieService, never())
                .create(any(Movie.class));

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnUnPaged() throws Exception {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final List<Movie> movies = from(Movie.class).gimme(2, "persistedSemCensura");
        final MovieResponse movieResponse = from(MovieResponse.class).gimme("persistedSemCensura");
        final Pageable pageable = Pageable.unpaged();

        when(this.movieService.findAllByCensorshipLevel(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies, pageable, 2));

        when(this.movieResponseTransformer.transform(any(Movie.class)))
                .thenReturn(movieResponse);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .param("censorshipLevel", censorshipLevel.toString())
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content").value(hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnEmptyList() throws Exception {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final List<Movie> movies = emptyList();
        final Pageable pageable = Pageable.unpaged();

        when(this.movieService.findAllByCensorshipLevel(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies, pageable, 0));

        when(this.movieResponseTransformer.transform(any(Movie.class)))
                .thenReturn(null);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .param("censorshipLevel", censorshipLevel.toString())
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content").value(hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(0))
                .andExpect(jsonPath("$.numberOfElements").value(0))
                .andExpect(jsonPath("$.empty").value(true))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnErrorWhenExceptionOccurs() throws Exception {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final Pageable pageable = PageRequest.of(0, 10);
        final Exception exception = new CouldNotFindMovieException("Exception", null);

        when(this.movieService.findAllByCensorshipLevel(censorshipLevel, pageable))
                .thenThrow(exception);

        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .param("censorshipLevel", censorshipLevel.toString())
                        .param("page", "1")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andReturn();

        final String errorMessage = mvcResult.getResponse().getErrorMessage();

        assertThat(errorMessage)
                .startsWith("Error finding all movies by Censorship Level");

        verify(this.movieResponseTransformer, never())
                .transform(any(Movie.class));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnErrorWhenCensorshipLevelIsNull() throws Exception {
        final MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .param("censorshipLevel", "")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        final String errorMessage = mvcResult.getResponse().getErrorMessage();

        assertThat(errorMessage)
                .startsWith("Censorship level cannot be blank. Allowed values: 'CENSURADO' and 'SEM_CENSURA'");

        verify(this.movieService, never())
                .findAllByCensorshipLevel(any(CensorshipLevel.class), any(Pageable.class));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnMoviesUnPagedWithPageZero() throws Exception {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final List<Movie> movies = from(Movie.class).gimme(2, "persistedSemCensura");
        final MovieResponse movieResponse = from(MovieResponse.class).gimme("persistedSemCensura");
        final Pageable pageable = Pageable.unpaged();

        when(this.movieService.findAllByCensorshipLevel(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies, pageable, 2));

        when(this.movieResponseTransformer.transform(any(Movie.class)))
                .thenReturn(movieResponse);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .param("censorshipLevel", censorshipLevel.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content").value(hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnMoviesUnPagedWithSizeZero() throws Exception {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final List<Movie> movies = from(Movie.class).gimme(2, "persistedSemCensura");
        final MovieResponse movieResponse = from(MovieResponse.class).gimme("persistedSemCensura");
        final Pageable pageable = Pageable.unpaged();

        when(this.movieService.findAllByCensorshipLevel(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies, pageable, 2));

        when(this.movieResponseTransformer.transform(any(Movie.class)))
                .thenReturn(movieResponse);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .param("censorshipLevel", censorshipLevel.toString())
                        .param("page", "1")
                        .param("size", "0")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content").value(hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnMoviesFirstPageOfOnePage() throws Exception {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final Pageable pageable = PageRequest.of(0, 2);
        final List<Movie> movies = from(Movie.class).gimme(2, "persistedSemCensura");
        final MovieResponse movieResponse = from(MovieResponse.class).gimme("persistedSemCensura");

        when(this.movieService.findAllByCensorshipLevel(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies, pageable, 2));

        when(this.movieResponseTransformer.transform(any(Movie.class)))
                .thenReturn(movieResponse);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .param("censorshipLevel", censorshipLevel.toString())
                        .param("page", "1")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content").value(hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnMoviesFirstPageOfTwoPages() throws Exception {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final Pageable pageable = PageRequest.of(0, 1);
        final List<Movie> movies = from(Movie.class).gimme(1, "persistedSemCensura");
        final MovieResponse movieResponse = from(MovieResponse.class).gimme("persistedSemCensura");

        when(this.movieService.findAllByCensorshipLevel(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies, pageable, 2));

        when(this.movieResponseTransformer.transform(any(Movie.class)))
                .thenReturn(movieResponse);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .param("censorshipLevel", censorshipLevel.toString())
                        .param("page", "1")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content").value(hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    public void findAllByCensorshipLevelMustReturnMoviesSecondPageOfTwoPages() throws Exception {
        final CensorshipLevel censorshipLevel = CensorshipLevel.SEM_CENSURA;
        final Pageable pageable = PageRequest.of(1, 1);
        final List<Movie> movies = from(Movie.class).gimme(1, "persistedSemCensura");
        final MovieResponse movieResponse = from(MovieResponse.class).gimme("persistedSemCensura");

        when(this.movieService.findAllByCensorshipLevel(censorshipLevel, pageable))
                .thenReturn(new PageImpl<>(movies, pageable, 2));

        when(this.movieResponseTransformer.transform(any(Movie.class)))
                .thenReturn(movieResponse);

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URL)
                        .param("censorshipLevel", censorshipLevel.toString())
                        .param("page", "2")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content").value(hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

}
