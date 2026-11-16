package fi.hel.allu.servicecore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Dedicated executor for the WFS payment-zone/payment-class fan-out/fan-in requests.
 *
 * <p>Previously, thread-pool sizing/behavior for these async WFS calls was handled by
 * Spring's (now removed) {@code AsyncRestTemplate}. Since the migration to
 * {@code CompletableFuture.supplyAsync(...)} would otherwise default to
 * {@code ForkJoinPool.commonPool()}, this bean makes the executor explicit instead of
 * leaving it to the JVM default.</p>
 */
@Configuration
public class WfsAsyncConfig {

  @Bean(name = "wfsTaskExecutor")
  public Executor wfsTaskExecutor() {
    return Executors.newCachedThreadPool();
  }
}
