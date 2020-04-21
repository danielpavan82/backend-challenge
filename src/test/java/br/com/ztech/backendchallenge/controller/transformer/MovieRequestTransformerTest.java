package br.com.ztech.backendchallenge.controller.transformer;

import br.com.ztech.backendchallenge.BaseTest;
import br.com.ztech.backendchallenge.controller.request.MovieRequest;
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
public class MovieRequestTransformerTest extends BaseTest {

    @InjectMocks
    private MovieRequestTransformer target;

    @Test
    public void transformMustWorks() {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("candidateSemCensura");
        final Movie movie = from(Movie.class).gimme("candidateSemCensura");

        final Movie result = target.transform(movieRequest);

        assertThat(result).isEqualToComparingFieldByField(movie);
    }

    @Test
    public void transformMustWorksReturnEmptyObjectWithNullCast() {
        final MovieRequest movieRequest = from(MovieRequest.class).gimme("castNull");
        final Movie movie = from(Movie.class).gimme("castNull");

        final Movie result = target.transform(movieRequest);

        assertThat(result).isNotNull();
        assertThat(result.getCast()).isNullOrEmpty();
        assertThat(result).isEqualToComparingFieldByField(movie);
    }

    @Test(expected = CouldNotTransformException.class)
    public void transformMustThrowExceptionWithNullParameter() {
        target.transform(null);
    }

}
