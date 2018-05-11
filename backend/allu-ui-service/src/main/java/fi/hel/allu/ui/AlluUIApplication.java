package fi.hel.allu.ui;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = { "fi.hel.allu.ui", "fi.hel.allu.common.controller", "fi.hel.allu.servicecore" })
public class AlluUIApplication {

  public static void main(String[] args) {
    SpringApplication.run(AlluUIApplication.class, args);
  }
}
