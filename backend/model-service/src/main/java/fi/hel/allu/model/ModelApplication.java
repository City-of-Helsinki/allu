package fi.hel.allu.model;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"fi.hel.allu.model", "fi.hel.allu.common.controller"})
public class ModelApplication {
  public static void main(String[] args) {
    SpringApplication.run(ModelApplication.class, args);
  }
}
