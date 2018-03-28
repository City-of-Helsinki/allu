package fi.hel.allu.etl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"fi.hel.allu.etl"})
public class AlluEtlApplication {

  public static void main(String[] args) {
    SpringApplication etlApp = new SpringApplication(AlluEtlApplication.class);
    etlApp.setWebEnvironment(false);
    etlApp.run(args);
  }
}