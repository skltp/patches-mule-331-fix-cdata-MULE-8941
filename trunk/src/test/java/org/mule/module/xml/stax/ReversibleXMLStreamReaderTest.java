package org.mule.module.xml.stax;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.AbstractServiceAndFlowTestCase;

public class ReversibleXMLStreamReaderTest extends AbstractServiceAndFlowTestCase
{
    public ReversibleXMLStreamReaderTest(ConfigVariant variant, String configResources) {
        super(variant, configResources);
    }
    
    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "src/test/app/cdatatest-service.xml"},
        });
    }

   @Test
   public void testProxyCDATA() throws Exception
   {
        String msg="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:add=\"http://www.w3.org/2005/08/addressing\" xmlns:urn=\"urn:skl:tjanst1:rivtabp20\">"+
                    "<soapenv:Header>"+
                    "<add:To>?</add:To>"+
                    "</soapenv:Header>"+
                    "<soapenv:Body>"+
                    "<urn:getProductDetailElem>"+
                    "<productId><![CDATA[<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header/><soapenv:Body><int:test/></soapenv:Body></soapenv:Envelope>]]></productId>"+
                    "</urn:getProductDetailElem>"+
                    "</soapenv:Body>"+
                    "</soapenv:Envelope>";

        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("http://localhost:8080/cdatatest", msg, null);
        assertNotNull(result);
        assertTrue(result.getPayloadAsString().contains("![CDATA["));
   }
}
