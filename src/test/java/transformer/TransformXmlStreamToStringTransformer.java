package transformer;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.xml.stax.ReversibleXMLStreamReader;

import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransformXmlStreamToStringTransformer extends AbstractMessageTransformer {

    private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory
            .newInstance();

    private static final Logger log = LoggerFactory
            .getLogger(TransformXmlStreamToStringTransformer.class);

    public TransformXmlStreamToStringTransformer() {
        super();
    }

    @Override
    public void setMuleContext(MuleContext muleContext) {
        log.debug("setMuleContext { muleContext: {} }", muleContext);
        super.setMuleContext(muleContext);
    }

    public MuleContext getMuleContext() {
        return super.muleContext;
    }

    public Object transformMessage(MuleMessage message, String outputEncoding)
            throws TransformerException {
        
        String payloadAsString = null;
        
        payloadAsString = getPayloadAsString(message.getPayload());

        System.out.println(payloadAsString);

        return message;
    }

    public String getPayloadAsString(Object payload) {
        if (payload == null) {
            return null;
        }
        return convertReversibleXMLStreamReaderToString(
                (ReversibleXMLStreamReader) payload, "UTF-8");
    }

    public static String convertReversibleXMLStreamReaderToString(
            ReversibleXMLStreamReader reader, String encoding) {

        boolean wasTrackingEnabled = reader.isTracking();

        // Turn on tracking if not already enabled
        if (!wasTrackingEnabled) {
            reader.setTracking(true);
        }

        // Now transform the the stream to a string
        String xml = convertXMLStreamReaderToString(reader, encoding);

        // Turn off tracking if it was not already enabled
        if (!wasTrackingEnabled) {
            reader.setTracking(false);
        }

        // Rest the stream so other consumers can read it
        reader.reset();

        return xml;
    }

    public static String convertXMLStreamReaderToString(XMLStreamReader reader,
            String encoding) {
        try {

            ByteArrayOutputStream os = new ByteArrayOutputStream(2048);

            XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(os,
                    encoding);

            try {
                writer.writeStartDocument();
              
                org.mule.module.xml.util.XMLUtils.copy(reader, writer);
                
                writer.writeEndDocument();

                String result = os.toString();
                
                return result;

            } finally {
                writer.close();
                os.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}