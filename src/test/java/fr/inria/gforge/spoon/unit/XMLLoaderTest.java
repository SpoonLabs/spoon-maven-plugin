package fr.inria.gforge.spoon.unit;

import fr.inria.gforge.spoon.SpoonModel;
import fr.inria.gforge.spoon.util.XMLLoader;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created at 19/11/2013 10:14.<br>
 *
 * @author Christophe DUFOUR
 */
public class XMLLoaderTest {
    @Test
    public void simpleTest() throws Exception {
        final SpoonModel model = XMLLoader.load(XMLLoaderTest.class.getResourceAsStream("spoon.xml"));

        assertThat(model.getProcessors().size()).isEqualTo(9);
        assertThat(model.getTemplates().size()).isEqualTo(16);
    }
}
