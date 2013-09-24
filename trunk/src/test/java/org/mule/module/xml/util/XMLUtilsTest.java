package org.mule.module.xml.util;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Test;

public class XMLUtilsTest {

    @Test
    public void testCopyCDATA() throws Exception {
        
        final URL resource = Thread.currentThread().getContextClassLoader().getResource("cdataRequest.xml");
        final XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(resource.openStream());

        ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(os);
        writer.writeStartDocument();

        XMLUtils.copy(reader, writer);
        writer.writeEndDocument();

        String actualXml = os.toString();
        assertTrue(actualXml.contains("![CDATA["));
    }

}
