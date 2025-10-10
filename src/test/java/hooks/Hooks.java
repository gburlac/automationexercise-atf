package hooks;

import io.cucumber.java.Before;
import support.PropertyReader;

public class Hooks {
    @Before(order = 0)
    public void loadProperties() {
        PropertyReader.loadProperties("config/test.properties");
    }
}

