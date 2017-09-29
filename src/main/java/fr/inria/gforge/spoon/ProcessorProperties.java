package fr.inria.gforge.spoon;

import java.util.Properties;

/**
 * Created by urli on 21/09/2017.
 */
public class ProcessorProperties {
    private String name;
    private Properties properties;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
