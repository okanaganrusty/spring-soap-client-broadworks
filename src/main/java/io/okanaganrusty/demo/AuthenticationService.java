package io.okanaganrusty.demo;

import java.io.StringWriter;
import java.util.Random;
import java.net.InetAddress;

import javax.xml.bind.JAXBElement;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import com.broadsoft.broadworks.schema.AuthenticationRequest;
import com.broadsoft.broadworks.schema.AuthenticationResponse;
import com.broadsoft.broadworks.schema.LoginRequest22;
import com.broadsoft.broadworks.schema.OCIMessage;
import com.broadsoft.broadworks.schema.ObjectFactory;
import com.broadsoft.broadworks.webservice.ProcessOCIMessageResponse;

import io.okanaganrusty.Application;
import io.okanaganrusty.broadworks.wsdl.WsdlClient;
import io.okanaganrusty.broadworks.xsd.XsdConfiguration;

@Component
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final String USERNAME = "foobar";
    private static final String PASSWORD = "foobar";
		
    @Autowired
    private XsdConfiguration xsdConfiguration;
		
    @SuppressWarnings("unchecked")
    @Bean
    CommandLineRunner authenticate(WsdlClient wsdlClient) {
	return args -> {
	    try {
		ObjectFactory xsdObjectFactory = new ObjectFactory();

		/*
		 * Construct authentication request
		 */

		Random randomGenerator = new Random();

		AuthenticationRequest authRequest = xsdObjectFactory.createAuthenticationRequest();

		authRequest.setEcho("");
		authRequest.setUserId(USERNAME);

		StringWriter sessionIdWriter = new StringWriter();
		sessionIdWriter.append(InetAddress.getLocalHost().getHostAddress());
		sessionIdWriter.append(",");
		sessionIdWriter.append(String.valueOf(randomGenerator.nextInt(999999999)));

		OCIMessage ociMessage = new OCIMessage();
		ociMessage.setProtocol("OCI");
		ociMessage.setSessionId(sessionIdWriter.toString());
		ociMessage.getCommand().add(authRequest);

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

		AuthenticationResponse authResponse = (AuthenticationResponse) ociMessageResponse.getCommand().get(0);

		/*
		 * Construct login request
		 */

		String password = DigestUtils.sha1Hex(PASSWORD);

		StringWriter passwordWithNonce = new StringWriter();
		passwordWithNonce.append(authResponse.getNonce());
		passwordWithNonce.append(":");
		passwordWithNonce.append(password);

		String passwordHash = DigestUtils.md5Hex(passwordWithNonce.toString());

		LoginRequest22 loginRequest22 = xsdObjectFactory.createLoginRequest22();

		loginRequest22.setEcho("");
		loginRequest22.setUserId(USERNAME);
		loginRequest22.setPassword(passwordHash);

		ociMessage.getCommand().clear();
		ociMessage.getCommand().add(loginRequest22);

		bsDocument = (JAXBElement<OCIMessage>) xsdObjectFactory.createBroadsoftDocument(ociMessage);

		result = new StringResult();
		xsdConfiguration.xsdMarshaller().marshal(bsDocument, result);

		response = wsdlClient.getResponse(result.toString());
		log.debug(response.getProcessOCIMessageReturn());
						
	    } catch (Exception ex) { 
						
	    }
	};
    }
}
