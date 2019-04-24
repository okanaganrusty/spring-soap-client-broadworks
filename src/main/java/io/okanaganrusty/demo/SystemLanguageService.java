package io.okanaganrusty.demo;

import java.io.StringWriter;
import java.util.Random;
import java.net.InetAddress;

import javax.xml.bind.JAXBElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import com.broadsoft.broadworks.schema.OCIMessage;
import com.broadsoft.broadworks.schema.ObjectFactory;
import com.broadsoft.broadworks.schema.SystemLanguageGetListRequest;
import com.broadsoft.broadworks.schema.SystemLanguageGetListResponse;
import com.broadsoft.broadworks.webservice.ProcessOCIMessageResponse;

import io.okanaganrusty.Application;
import io.okanaganrusty.broadworks.wsdl.WsdlClient;
import io.okanaganrusty.broadworks.xsd.XsdConfiguration;

@Component
public class SystemLanguageService {
		private static final Logger log = LoggerFactory.getLogger(Application.class);
		
		@Autowired
		private XsdConfiguration xsdConfiguration;
		
		@SuppressWarnings({ "unchecked", "unused" })
		@Bean
		CommandLineRunner getLanguages(WsdlClient wsdlClient) {
				return args -> {
					try {
						ObjectFactory xsdObjectFactory = new ObjectFactory();

						/*
						 * Construct authentication request
						 */

						Random randomGenerator = new Random();

						SystemLanguageGetListRequest systemLanguageGetRequest = xsdObjectFactory.createSystemLanguageGetListRequest();
						systemLanguageGetRequest.setEcho("");

						StringWriter sessionIdWriter = new StringWriter();
						sessionIdWriter.append(InetAddress.getLocalHost().getHostAddress());
						sessionIdWriter.append(",");
						sessionIdWriter.append(String.valueOf(randomGenerator.nextInt(999999999)));

						OCIMessage ociMessage = new OCIMessage();
						ociMessage.setProtocol("OCI");
						ociMessage.setSessionId(sessionIdWriter.toString());
						ociMessage.getCommand().add(systemLanguageGetRequest);

						JAXBElement<OCIMessage> bsDocument = (JAXBElement<OCIMessage>) xsdObjectFactory
								.createBroadsoftDocument(ociMessage);

						StringResult result = new StringResult();
						xsdConfiguration.xsdMarshaller().marshal(bsDocument, result);

						ProcessOCIMessageResponse response = wsdlClient.getResponse(result.toString());
						log.debug(response.getProcessOCIMessageReturn());

						JAXBElement<OCIMessage> jaxbUnmarshallResponse = (JAXBElement<OCIMessage>) xsdConfiguration.xsdMarshaller()
								.unmarshal(new StringSource(response.getProcessOCIMessageReturn()));

						OCIMessage ociMessageResponse = jaxbUnmarshallResponse.getValue();

						// Get the first message in the response (you can have multiple responses based
						// on the number of
						// commands you send in your request.

						SystemLanguageGetListResponse systemLanguageGetResponse = 
								(SystemLanguageGetListResponse) ociMessageResponse.getCommand().get(0);
						
					} catch (Exception ex) { 
						
					}
				};
		}
}
