package io.okanaganrusty.broadworks.wsdl;

import javax.xml.soap.SOAPException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.broadsoft.broadworks.webservice.ProcessOCIMessage;
import com.broadsoft.broadworks.webservice.ProcessOCIMessageResponse;

public class WsdlClient extends WebServiceGatewaySupport {	
  @Value("${broadworks.wsdl.location}")
  private String wsdlLocation;

  public ProcessOCIMessageResponse getResponse(String xmlPayload) throws SOAPException {
    WebServiceTemplate wsTemplate = getWebServiceTemplate();

    ProcessOCIMessage request = new ProcessOCIMessage();
    request.setIn0(xmlPayload);

    ProcessOCIMessageResponse response = (ProcessOCIMessageResponse)
        wsTemplate.marshalSendAndReceive(
            wsdlLocation, 
            request, 
            new SoapActionCallback(wsdlLocation));

    return response;
  }
}