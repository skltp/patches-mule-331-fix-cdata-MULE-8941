MuleSoft casenr: 00008941

In the case when the request contains CData elements there is an error indicating problems in ReversibleXMLStreamReader.

##############################
The request containing CData
##############################

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:add="http://www.w3.org/2005/08/addressing" xmlns:urn="urn:skl:tjanst1:rivtabp20">
   <soapenv:Header>
      <add:To>?</add:To>
   </soapenv:Header>
   <soapenv:Body>
      <urn:getProductDetailElem>
         <!--Optional:-->
         <productId><![CDATA[<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"><soapenv:Header/><soapenv:Body><int:test/></soapenv:Body></soapenv:Envelope>]]></productId>
      </urn:getProductDetailElem>
   </soapenv:Body>
</soapenv:Envelope>

########################
The error occuring in log
########################

INFO  2013-09-16 13:46:02,896 [[cdatatest-reversiblexmlstreamreader].connector.http.mule.default.receiver.02] org.mule.component.simple.LogComponent: 
********************************************************************************
* Message received in service: cdatatest-flow. Content is: '[Message could     *
* not be converted to string]'                                                 *
********************************************************************************
WARN  2013-09-16 13:46:02,974 [[cdatatest-reversiblexmlstreamreader].connector.http.mule.default.receiver.02] org.apache.cxf.phase.PhaseInterceptorChain: Interceptor for {urn:skl:tjanst1:rivtabp20}Tjanst1Service has thrown exception, unwinding now
java.lang.ClassCastException: javanet.staxutils.events.CDataEvent cannot be cast to javanet.staxutils.events.CharactersEvent
	at org.mule.module.xml.stax.ReversibleXMLStreamReader.getText(ReversibleXMLStreamReader.java:583)
	at org.apache.cxf.staxutils.StaxUtils.copy(StaxUtils.java:541)
	at org.apache.cxf.staxutils.StaxUtils.copy(StaxUtils.java:513)
	at org.mule.module.cxf.support.OutputPayloadInterceptor$1.write(OutputPayloadInterceptor.java:83)
	at org.apache.cxf.databinding.stax.StaxDataBinding$XMLStreamDataWriter.write(StaxDataBinding.java:127)
	at org.apache.cxf.databinding.stax.StaxDataBinding$XMLStreamDataWriter.write(StaxDataBinding.java:117)
	at org.apache.cxf.databinding.stax.StaxDataBinding$XMLStreamDataWriter.write(StaxDataBinding.java:113)
	at org.apache.cxf.interceptor.AbstractOutDatabindingInterceptor.writeParts(AbstractOutDatabindingInterceptor.java:119)
	at org.apache.cxf.interceptor.BareOutInterceptor.handleMessage(BareOutInterceptor.java:68)
	at org.apache.cxf.phase.PhaseInterceptorChain.doIntercept(PhaseInterceptorChain.java:263)
	at org.apache.cxf.phase.PhaseInterceptorChain.resume(PhaseInterceptorChain.java:232)
	at org.mule.module.cxf.CxfInboundMessageProcessor$1.write(CxfInboundMessageProcessor.java:373)
	at org.mule.transport.http.HttpServerConnection.writeResponse(HttpServerConnection.java:351)
	at org.mule.transport.http.HttpMessageProcessTemplate.sendResponseToClient(HttpMessageProcessTemplate.java:176)
	at org.mule.execution.FlowProcessingPhase$1.run(FlowProcessingPhase.java:82)
	at org.mule.work.WorkerContext.run(WorkerContext.java:311)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1110)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:603)
	at java.lang.Thread.run(Thread.java:722)


##############################################################
The fix in org.mule.module.xml.stax.ReversibleXMLStreamReader
#############################################################

In org.mule.module.xml.stax.ReversibleXMLStreamReader, line 584-589 is changed:

public String getText()
    {
        if (replay)
        {
            if (current instanceof CommentEvent)
            {
                return ((CommentEvent) current).getText();
            }
            else
            {
                //#####################################################################
                //FIX for case 00008941, CData not handled in ReversibleXMLStreamReader
                //#####################################################################
                return ((AbstractCharactersEvent) current).getData();
                //return ((CharactersEvent) current).getData();
                //#####################################################################
            }
        }
        else
        {
            return super.getText();
        }
}

############################################
Apply the patch
############################################

Apply the patches-mule-3.3.1-fix-cdata-MULE-8941-1.0.jar in the <MULE_HOME>/lib/user folder and restart mule.

Verify by using a request containing CData elements.