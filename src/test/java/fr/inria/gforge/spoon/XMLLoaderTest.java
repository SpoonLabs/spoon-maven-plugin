package fr.inria.gforge.spoon;

import fr.inria.gforge.spoon.util.XMLLoader;
import org.junit.Test;

/**
 * Created at 19/11/2013 10:14.<br>
 *
 * @author Christophe DUFOUR
 */
public class XMLLoaderTest {
    @Test
    public void simpleTest() throws Exception {
        SpoonModel model = XMLLoader
				.load(XMLLoaderTest.class.getResourceAsStream("spoon.xml"));
    }
}
