package com.dooapp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * TODO write documentation<br>
 * <br>
 * Created at 18/11/2013 17:33.<br>
 *
 * @author Christophe DUFOUR
 */
public class XMLLoader {
    public static SpoonModel load(InputStream inputStream) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(SpoonModel.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (SpoonModel) jaxbUnmarshaller.unmarshal(inputStream);
    }
}
