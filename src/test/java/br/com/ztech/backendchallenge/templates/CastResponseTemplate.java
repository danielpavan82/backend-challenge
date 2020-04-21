package br.com.ztech.backendchallenge.templates;

import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.ztech.backendchallenge.controller.response.CastResponse;

import static br.com.six2six.fixturefactory.Fixture.of;

public class CastResponseTemplate implements TemplateLoader {

    private static final String NAME = "name";

    @Override
    public void load() {
        of(CastResponse.class).addTemplate("vin", new Rule() {{
            add(NAME, "Vin Diesel");
        }});

        of(CastResponse.class).addTemplate("paul", new Rule() {{
            add(NAME, "Paul Walker");
        }});

        of(CastResponse.class).addTemplate("michelle", new Rule() {{
            add(NAME, "Michelle Rodriguez");
        }});

        of(CastResponse.class).addTemplate("jordana", new Rule() {{
            add(NAME, "Jordana Brewster");
        }});

        of(CastResponse.class).addTemplate("mel", new Rule() {{
            add(NAME, "Mel Gibson");
        }});

        of(CastResponse.class).addTemplate("joanne", new Rule() {{
            add(NAME, "Joanne Samuel");
        }});

        of(CastResponse.class).addTemplate("david", new Rule() {{
            add(NAME, "David Bracks");
        }});

        of(CastResponse.class).addTemplate("steve", new Rule() {{
            add(NAME, "Steve Bisley");
        }});

        of(CastResponse.class).addTemplate("roger", new Rule() {{
            add(NAME, "Roger Ward");
        }});
    }

}
