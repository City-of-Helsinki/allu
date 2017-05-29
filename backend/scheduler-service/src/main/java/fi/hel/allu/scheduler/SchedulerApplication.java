package fi.hel.allu.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
public class SchedulerApplication {
  public static void main(String[] args) {
    SpringApplication schedulerApp = new SpringApplication(SchedulerApplication.class);
    schedulerApp.setWebEnvironment(false);
    schedulerApp.run(args);
  }
}
