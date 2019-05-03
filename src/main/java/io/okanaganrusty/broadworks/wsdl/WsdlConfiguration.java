package io.okanaganrusty.broadworks.wsdl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class WsdlConfiguration {
    Logger logger = LoggerFactory.getLogger(WsdlConfiguration.class);
    
    @Value("${broadworks.wsdl.connectTimeout:3000}")
    private int connectTimeout;

    @Value("${broadworks.wsdl.requestTimeout:3000}")
    private int requestTimeout;

    @Value("${broadworks.wsdl.socketTimeout:3000}")
    private int socketTimeout;

    @Value("${broadworks.wsdl.location}")
    private String location;

    private WsdlClient wsdlClient;
    private HttpClient httpClient;
    private RequestConfig requestConfiguration;
    private HttpComponentsMessageSender messageSender;
    private SSLContext sslContext;
	
    private CookieStore cookieStore;
	
    public WsdlConfiguration() throws
	KeyManagementException, 
	NoSuchAlgorithmException, 
	KeyStoreException {
		
	messageSender = new HttpComponentsMessageSender();
	cookieStore = new BasicCookieStore(); 
		
	requestConfiguration = RequestConfig.custom()
	    .setConnectTimeout(connectTimeout)
	    .setConnectionRequestTimeout(requestTimeout)
	    .setSocketTimeout(socketTimeout)
	    .build();
	    
	sslContext = new SSLContextBuilder()
	    .loadTrustMaterial(null, 
			       TrustAllStrategy.INSTANCE)
	    .build();
	    
	httpClient = HttpClients    			
	    .custom()
	    .setDefaultCookieStore(cookieStore)
	    .setDefaultRequestConfig(requestConfiguration)
	    .setSSLContext(sslContext) 		
	    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE) 
	    .addInterceptorFirst(new ContentLengthHeaderRemover())
	    .addInterceptorFirst(new HttpRequestInterceptor() {
		    public void process(HttpRequest request, HttpContext context) {
			System.out.println(">> Request URI: " + request.getRequestLine().getUri());
			
			Header[] headers = request.getAllHeaders();
			for (Header header : headers) { 
			    System.out.println(">> Request Header [" + header.getName() + "]: " + header.getValue());
			}
		    }     	    	
		})
	    .addInterceptorFirst(new HttpResponseInterceptor() {
		    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
			System.out.println("<< Response: " + response.getStatusLine());

			Header[] headers = response.getAllHeaders();
			for (Header header : headers) { 
			    System.out.println("<< Response Header [" + header.getName() + "]: " + header.getValue());
			}			           			            
		    }
		})
	    .build();		
    }
	
    private static class ContentLengthHeaderRemover implements HttpRequestInterceptor {
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            request.removeHeaders(HTTP.CONTENT_LEN);
        }
    }
    
    @Bean
    public Jaxb2Marshaller wsdlMarshaller() {
	Jaxb2Marshaller wsdlMarshaller = new Jaxb2Marshaller();
	
	wsdlMarshaller.setClassesToBeBound(new Class[] { 
		com.broadsoft.broadworks.webservice.ObjectFactory.class,
		com.broadsoft.broadworks.webservice.ProcessOCIMessage.class,
		com.broadsoft.broadworks.webservice.ProcessOCIMessageResponse.class 
	    });
	
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
    public WsdlClient soapConnector(Jaxb2Marshaller wsdlMarshaller) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException  {    	    	            	
    	messageSender.setHttpClient(httpClient);
    	
    	wsdlClient = new WsdlClient();    	
    	wsdlClient.setDefaultUri(location);    	
    	wsdlClient.setMessageSender(messageSender);
    	wsdlClient.setMarshaller(wsdlMarshaller);
    	wsdlClient.setUnmarshaller(wsdlMarshaller);
        
        return wsdlClient;
    }
}
