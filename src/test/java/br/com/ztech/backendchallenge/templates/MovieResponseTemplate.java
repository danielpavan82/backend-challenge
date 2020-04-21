package br.com.ztech.backendchallenge.templates;

import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.ztech.backendchallenge.controller.response.CastResponse;
import br.com.ztech.backendchallenge.controller.response.MovieResponse;
import br.com.ztech.backendchallenge.model.Cast;
import br.com.ztech.backendchallenge.model.CensorshipLevel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static br.com.six2six.fixturefactory.Fixture.of;

public class MovieResponseTemplate implements TemplateLoader {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String RELEASE_DATE = "releaseDate";
    private static final String CENSORSHIP_LEVEL = "censorshipLevel";
    private static final String DIRECTOR = "director";
    private static final String CAST = "cast";
    private static final String CREATED_DATE = "createdDate";

    @Override
    public void load() {

        of(MovieResponse.class).addTemplate("persistedSemCensura", new Rule() {{
            add(ID, "5e9ae3bb4945c71b05dc7a5a");
            add(NAME, "Velozes e Furiosos");
            add(RELEASE_DATE, LocalDate.now().minusYears(19));
            add(CENSORSHIP_LEVEL, CensorshipLevel.SEM_CENSURA);
            add(DIRECTOR, "Rob Cohen");
            add(CAST, has(4).of(CastResponse.class, "vin", "paul", "michelle", "jordana"));
            add(CREATED_DATE, LocalDateTime.of(LocalDate.now(), LocalTime.of(8,53,12)));
        }});

        of(MovieResponse.class).addTemplate("persistedSemCensuraCastNull").inherits("persistedSemCensura", new Rule() {{
            add(CAST, null);
        }});

        of(MovieResponse.class).addTemplate("persistedCensurado", new Rule() {{
            add(ID, "5e9ae3bb4945c71b05dc7a5a");
            add(NAME, "Mad Max");
            add(RELEASE_DATE, LocalDate.now().minusYears(30));
            add(CENSORSHIP_LEVEL, CensorshipLevel.CENSURADO);
            add(DIRECTOR, "George Miller");
            add(CAST, has(5).of(Cast.class, "mel", "joanne", "david", "steve", "roger"));
            add(CREATED_DATE, LocalDateTime.of(LocalDate.now(), LocalTime.of(8,53,12)));
        }});



    }

}
