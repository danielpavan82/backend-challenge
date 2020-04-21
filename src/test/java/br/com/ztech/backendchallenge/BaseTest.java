package br.com.ztech.backendchallenge;

import org.junit.BeforeClass;

import static br.com.six2six.fixturefactory.loader.FixtureFactoryLoader.loadTemplates;
import static br.com.ztech.backendchallenge.templates.Templates.BASE_PACKAGE;

public abstract class BaseTest {

    @BeforeClass
    public static void setUpClass() {
        loadTemplates(BASE_PACKAGE);
    }

}
