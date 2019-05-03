package io.okanaganrusty;

import java.util.Locale;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class Application {
  public static void main(String[] args) {
    Locale locale = Locale.getDefault();
    System.out.println("Default Locale: " + locale);

    new SpringApplicationBuilder(Application.class).run(args);
  }
}
