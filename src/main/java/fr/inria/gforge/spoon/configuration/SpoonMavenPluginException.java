package fr.inria.gforge.spoon.configuration;

public class SpoonMavenPluginException extends Exception {
    SpoonMavenPluginException(String msg) {
        super(msg);
    }

    SpoonMavenPluginException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
