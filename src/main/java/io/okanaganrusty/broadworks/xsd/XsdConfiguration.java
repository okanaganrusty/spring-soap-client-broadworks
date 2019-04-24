package io.okanaganrusty.broadworks.xsd;

import java.util.HashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class XsdConfiguration {
	@Bean
	public Jaxb2Marshaller xsdMarshaller() {
		Jaxb2Marshaller xsdMarshaller = new Jaxb2Marshaller();
				
		xsdMarshaller.setClassesToBeBound(new Class[] { 
				com.broadsoft.broadworks.schema.ObjectFactory.class,
				com.broadsoft.broadworks.schema.OCICommand.class,
				com.broadsoft.broadworks.schema.OCIDataResponse.class,
				com.broadsoft.broadworks.schema.OCIMessage.class,
				com.broadsoft.broadworks.schema.OCIRequest.class,
				com.broadsoft.broadworks.schema.OCIResponse.class,
				com.broadsoft.broadworks.schema.OCITable.class,
				com.broadsoft.broadworks.schema.OCITableRow.class,
				com.broadsoft.broadworks.schema.AuthenticationRequest.class,
				com.broadsoft.broadworks.schema.AuthenticationResponse.class,
				com.broadsoft.broadworks.schema.LoginRequest22.class,
				com.broadsoft.broadworks.schema.LoginResponse22.class });
				
		xsdMarshaller.setMarshallerProperties(new HashMap<String, Object>() {
				private static final long serialVersionUID = 1L;
								
				{
					put(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
				}
			});
				
		return xsdMarshaller;
	}
}
