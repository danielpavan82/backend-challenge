package br.com.ztech.backendchallenge.templates;

import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import br.com.ztech.backendchallenge.controller.request.CastRequest;

import static br.com.six2six.fixturefactory.Fixture.of;

public class CastRequestTemplate implements TemplateLoader {

    private static final String NAME = "name";

    @Override
    public void load() {
        of(CastRequest.class).addTemplate("vin", new Rule() {{
            add(NAME, "Vin Diesel");
        }});

        of(CastRequest.class).addTemplate("paul", new Rule() {{
            add(NAME, "Paul Walker");
        }});

        of(CastRequest.class).addTemplate("michelle", new Rule() {{
            add(NAME, "Michelle Rodriguez");
        }});

        of(CastRequest.class).addTemplate("jordana", new Rule() {{
            add(NAME, "Jordana Brewster");
        }});

        of(CastRequest.class).addTemplate("mel", new Rule() {{
            add(NAME, "Mel Gibson");
        }});

        of(CastRequest.class).addTemplate("joanne", new Rule() {{
            add(NAME, "Joanne Samuel");
        }});

        of(CastRequest.class).addTemplate("david", new Rule() {{
            add(NAME, "David Bracks");
        }});

        of(CastRequest.class).addTemplate("steve", new Rule() {{
            add(NAME, "Steve Bisley");
        }});

        of(CastRequest.class).addTemplate("roger", new Rule() {{
            add(NAME, "Roger Ward");
        }});

        of(CastRequest.class).addTemplate("tom", new Rule() {{
            add(NAME, "Tom Cruise");
        }});

        of(CastRequest.class).addTemplate("cameron", new Rule() {{
            add(NAME, "Cameron Diaz");
        }});

        of(CastRequest.class).addTemplate("will", new Rule() {{
            add(NAME, "Will Smith");
        }});

        of(CastRequest.class).addTemplate("scarlett", new Rule() {{
            add(NAME, "Scarlett Johansson");
        }});

    }

}
