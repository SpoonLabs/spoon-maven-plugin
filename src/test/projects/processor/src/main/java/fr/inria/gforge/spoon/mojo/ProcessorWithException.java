package fr.inria.gforge.spoon.mojo;


import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;

/**
 * Created by urli on 05/10/2017.
 */
public class ProcessorWithException extends AbstractProcessor {

    @Override
    public void process(CtElement element) {
        throw new SpoonException("Try to throw spoonException");
    }
}
