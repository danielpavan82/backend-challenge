package br.com.ztech.backendchallenge.templates;

import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.ztech.backendchallenge.controller.request.CastRequest;
import br.com.ztech.backendchallenge.controller.request.MovieRequest;
import br.com.ztech.backendchallenge.model.CensorshipLevel;

import java.time.LocalDate;

import static br.com.six2six.fixturefactory.Fixture.of;
import static java.util.Collections.emptyList;

public class MovieRequestTemplate implements TemplateLoader {

    private static final String NAME = "name";
    private static final String RELEASE_DATE = "releaseDate";
    private static final String CENSORSHIP_LEVEL = "censorshipLevel";
    private static final String DIRECTOR = "director";
    private static final String CAST = "cast";

    @Override
    public void load() {
        of(MovieRequest.class).addTemplate("candidateSemCensura", new Rule() {{
            add(NAME, "Velozes e Furiosos");
            add(RELEASE_DATE, LocalDate.now().minusYears(19));
            add(CENSORSHIP_LEVEL, CensorshipLevel.SEM_CENSURA);
            add(DIRECTOR, "Rob Cohen");
            add(CAST, has(4).of(CastRequest.class, "vin", "paul", "michelle", "jordana"));
        }});

        of(MovieRequest.class).addTemplate("castNull").inherits("candidateSemCensura", new Rule() {{
            add(CAST, null);
        }});

        of(MovieRequest.class).addTemplate("blankName").inherits("candidateSemCensura", new Rule() {{
            add("name", "");
        }});

        of(MovieRequest.class).addTemplate("nullName").inherits("candidateSemCensura", new Rule() {{
            add("name", null);
        }});

        of(MovieRequest.class).addTemplate("nullReleaseDate").inherits("candidateSemCensura", new Rule() {{
            add("releaseDate", null);
        }});

        of(MovieRequest.class).addTemplate("nullCensorshipLevel").inherits("candidateSemCensura", new Rule() {{
            add(CENSORSHIP_LEVEL, null);
        }});

        of(MovieRequest.class).addTemplate("blankDirector").inherits("candidateSemCensura", new Rule() {{
            add("director", "");
        }});

        of(MovieRequest.class).addTemplate("nullDirector").inherits("candidateSemCensura", new Rule() {{
            add("director", null);
        }});

        of(MovieRequest.class).addTemplate("emptyCast").inherits("candidateSemCensura", new Rule() {{
            add(CAST, emptyList());
        }});

        of(MovieRequest.class).addTemplate("nullCast").inherits("candidateSemCensura", new Rule() {{
            add(CAST, null);
        }});
//
        of(MovieRequest.class).addTemplate("invalidCast").inherits("candidateSemCensura", new Rule() {{
            add(CAST, has(13).of(CastRequest.class, "vin", "paul", "michelle", "jordana", "mel", "joanne", "david", "steve", "roger", "tom", "cameron", "will", "scarlett"));
        }});

    }

}
