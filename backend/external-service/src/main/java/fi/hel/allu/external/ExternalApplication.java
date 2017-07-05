package fi.hel.allu.external;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "fi.hel.allu.external" })
public class ExternalApplication {
  public static void main(String[] args) {
    SpringApplication.run(ExternalApplication.class, args);
  }
}
