package fi.hel.allu.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class SchedulerApplication {
  public static void main(String[] args) {
    SpringApplication schedulerApp = new SpringApplication(SchedulerApplication.class);
    schedulerApp.setWebApplicationType(WebApplicationType.NONE);
    schedulerApp.run(args);
  }
}
