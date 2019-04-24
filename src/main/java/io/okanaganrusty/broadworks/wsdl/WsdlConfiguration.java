package io.okanaganrusty.broadworks.wsdl;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
 
@Configuration
public class WsdlConfiguration {
	@Value("${broadworks.wsdl.location}")
	private String wsdlLocation;
	
	@Bean
	public Jaxb2Marshaller wsdlMarshaller() {
		Jaxb2Marshaller wsdlMarshaller = new Jaxb2Marshaller();

		wsdlMarshaller.setClassesToBeBound(new Class[] { 
				com.broadsoft.broadworks.webservice.ObjectFactory.class,
				com.broadsoft.broadworks.webservice.ProcessOCIMessage.class,
				com.broadsoft.broadworks.webservice.ProcessOCIMessageResponse.class });

		wsdlMarshaller.setMarshallerProperties(new HashMap<String, Object>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
			}
		});

		return wsdlMarshaller;
	}
	
    @Bean
    public WsdlClient soapConnector(Jaxb2Marshaller wsdlMarshaller) {
    	WsdlClient client = new WsdlClient();
        
        client.setDefaultUri(this.wsdlLocation);
        client.setMarshaller(wsdlMarshaller);
        client.setUnmarshaller(wsdlMarshaller);
        
        return client;
    }
}