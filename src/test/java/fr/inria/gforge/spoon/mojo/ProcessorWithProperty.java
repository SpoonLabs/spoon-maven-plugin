package fr.inria.gforge.spoon.mojo;

import spoon.processing.AbstractProcessor;
import spoon.processing.Property;
import spoon.reflect.declaration.CtClass;

/**
 * Created by urli on 21/09/2017.
 */
public class ProcessorWithProperty extends AbstractProcessor<CtClass> {

    @Property
    String oldClassName;

    @Property
    String newClassName;

    @Override
    public void process(CtClass element) {
        if (element.getSimpleName().equals(this.oldClassName)) {
            element.setSimpleName(this.newClassName);
        }
    }
}
