package fi.hel.allu.ui;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan(basePackages = {"fi.hel.allu.ui", "fi.hel.allu.common.controller.filter"})
public class AlluUIServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(AlluUIServiceApplication.class, args);
  }
}
