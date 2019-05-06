package io.okanaganrusty;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class Application {  
  public static void main(String[] args) {
    final Logger logger = LoggerFactory.getLogger(Application.class);

    Locale locale = Locale.getDefault();
    System.out.println("Default Locale: " + locale);

    try {
      /* Don't launch tomcat for this app */
      new SpringApplicationBuilder(Application.class)
        .web(WebApplicationType.NONE)
        .run(args);
      
    } catch (Exception e) {
      logger.debug(e.getMessage(), e);      
    }
  }
}
