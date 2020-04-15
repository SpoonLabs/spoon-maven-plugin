package fr.inria.gforge.spoon.mojo;

import org.codehaus.plexus.util.StringUtils;
import spoon.processing.AbstractProcessor;
import spoon.processing.Property;
import spoon.reflect.declaration.CtClass;

import java.util.List;
import java.util.Map;

public class ProcessorWithProperty extends AbstractProcessor<CtClass> {
    @Property
    String methods;

    @Property
    String oldClassName;

    @Property
    String newClassName;

    @Property
    List<String> comments;

    @Property
    List<Integer> other;

    @Property
    Map<String, Object> mapTest;

    @Override
    public void process(CtClass element) {
        System.out.println(methods);
        if (!(comments != null && comments.size() == 2 && comments.get(0).equals("hello") && comments.get(1).equals("hello, world"))) {
            element.addComment(this.getFactory().createInlineComment("Content comments: "+ StringUtils.join(comments.iterator(),",")));
            return;
        }

        if (other != null && other.size() == 3) {
            for (int i = 0; i < 3; i++) {
                if (! (other.get(i) == i*2)) {
                    element.addComment(this.getFactory().createInlineComment("Content other: "+ StringUtils.join(other.iterator(),",")));
                    return;
                }
            }
        } else {
            return;
        }

        if (mapTest != null && mapTest.keySet().size() == 3) {
            for (String s : mapTest.keySet()) {
                if (s.equals("un")) {
                    if (!mapTest.get(s).equals(1)) {
                        element.addComment(this.getFactory().createInlineComment("Error un : Content mapTest keys: "+ StringUtils.join(mapTest.keySet().iterator(),":")+" and value: "+StringUtils.join(mapTest.values().iterator(),":")));
                        return;
                    }
                } else if (s.equals("deux")) {
                    if (!mapTest.get(s).equals("two")) {
                        element.addComment(this.getFactory().createInlineComment("Error deux : Content mapTest keys: "+ StringUtils.join(mapTest.keySet().iterator(),":")+" and value: "+StringUtils.join(mapTest.values().iterator(),":")));
                        return;
                    }
                }else if (s.equals("trois")) {
                    if (!mapTest.get(s).equals("3,1")) {
                        element.addComment(this.getFactory().createInlineComment("Error trois : Content mapTest keys: "+ StringUtils.join(mapTest.keySet().iterator(),":")+" and value: "+StringUtils.join(mapTest.values().iterator(),":")));
                        return;
                    }
                } else {
                    element.addComment(this.getFactory().createInlineComment("Wrong key: Content mapTest keys: "+ StringUtils.join(mapTest.keySet().iterator(),":")+" and value: "+StringUtils.join(mapTest.values().iterator(),":")));
                    return;
                }
            }
        }

        if (element.getSimpleName().equals(this.oldClassName)) {
            element.setSimpleName(this.newClassName);
        }
    }
}
