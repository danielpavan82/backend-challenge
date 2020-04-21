package br.com.ztech.backendchallenge.templates;

import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.ztech.backendchallenge.model.Cast;

import static br.com.six2six.fixturefactory.Fixture.of;

public class CastTemplate implements TemplateLoader {

    private static final String NAME = "name";

    @Override
    public void load() {
        of(Cast.class).addTemplate("vin", new Rule() {{
            add(NAME, "Vin Diesel");
        }});

        of(Cast.class).addTemplate("paul", new Rule() {{
            add(NAME, "Paul Walker");
        }});

        of(Cast.class).addTemplate("michelle", new Rule() {{
            add(NAME, "Michelle Rodriguez");
        }});

        of(Cast.class).addTemplate("jordana", new Rule() {{
            add(NAME, "Jordana Brewster");
        }});

        of(Cast.class).addTemplate("mel", new Rule() {{
            add(NAME, "Mel Gibson");
        }});

        of(Cast.class).addTemplate("joanne", new Rule() {{
            add(NAME, "Joanne Samuel");
        }});

        of(Cast.class).addTemplate("david", new Rule() {{
            add(NAME, "David Bracks");
        }});

        of(Cast.class).addTemplate("steve", new Rule() {{
            add(NAME, "Steve Bisley");
        }});

        of(Cast.class).addTemplate("roger", new Rule() {{
            add(NAME, "Roger Ward");
        }});

    }

}
