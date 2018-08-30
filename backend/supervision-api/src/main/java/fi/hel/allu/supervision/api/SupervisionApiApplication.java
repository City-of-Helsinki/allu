package fi.hel.allu.supervision.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "fi.hel.allu.supervision.api", "fi.hel.allu.servicecore" })
public class SupervisionApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(SupervisionApiApplication.class, args);
  }

}
