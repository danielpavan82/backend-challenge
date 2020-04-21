package br.com.ztech.backendchallenge.controller.transformer;

import br.com.ztech.backendchallenge.BaseTest;
import br.com.ztech.backendchallenge.controller.response.MovieResponse;
import br.com.ztech.backendchallenge.exception.CouldNotTransformException;
import br.com.ztech.backendchallenge.model.Movie;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

@FixMethodOrder(NAME_ASCENDING)
@RunWith(MockitoJUnitRunner.class)
public class MovieResponseTransformerTest extends BaseTest {

    @InjectMocks
    private MovieResponseTransformer target;

    @Test
    public void transformMustWorks() {
        final MovieResponse movieResponse = from(MovieResponse.class).gimme("persistedSemCensura");
        final Movie movie = from(Movie.class).gimme("persistedSemCensura");

        final MovieResponse result = target.transform(movie);

        assertThat(result).isEqualToComparingFieldByField(movieResponse);
    }

    @Test
    public void transformMustWorksReturnEmptyObjectWithNullCast() {
        final MovieResponse movieResponse = from(MovieResponse.class).gimme("persistedSemCensuraCastNull");
        final Movie movie = from(Movie.class).gimme("persistedSemCensuraCastNull");

        final MovieResponse result = target.transform(movie);

        assertThat(result).isNotNull();
        assertThat(result.getCast()).isNullOrEmpty();
        assertThat(result).isEqualToComparingFieldByField(movie);
    }

    @Test(expected = CouldNotTransformException.class)
    public void transformMustThrowExceptionWithNullParameter() {
        target.transform(null);
    }

}
