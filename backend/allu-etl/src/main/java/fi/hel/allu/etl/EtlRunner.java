package fi.hel.allu.etl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.etl.config.ApplicationProperties;

@Component
public class EtlRunner {

  private static final Logger logger = LoggerFactory.getLogger(EtlRunner.class);

  private static final String SCRIPT_PATH = "/db/etl/";

  private final ApplicationProperties applicationProperties;
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public EtlRunner(ApplicationProperties applicationProperties, JdbcTemplate jdbcTemplate) {
    this.applicationProperties = applicationProperties;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Scheduled(cron = "${etl.cronstring}")
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public void run() {
    runScheduled(() -> {
      List<String> files = applicationProperties.getEtlSqlFiles();
      files.forEach(file -> executeScript(file, "load"));
    }, "ETL batch run failed at {} - all batch changes (transaction) were rolled back");
  }

  @Scheduled(cron = "${etl.cleanup.cronstring}")
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public void runCleanup() {
    runScheduled(() -> {
      List<String> files = applicationProperties.getEtlCleanupSqlFiles();
      files.forEach(file -> executeScript(file, "cleanup"));
    }, "ETL cleanup run failed at {}");
  }

  private void runScheduled(Runnable task, String errorMsg) {
    try {
      task.run();
    } catch (RuntimeException e) {
      logger.error(errorMsg, LocalDateTime.now(), e);
      throw e;
    }
  }

  private void executeScript(String sqlFile, String label) {
    try (InputStream file = getClass().getResourceAsStream(SCRIPT_PATH + sqlFile)) {
      if (file == null) {
        throw new IllegalStateException("SQL script not found: " + sqlFile);
      }
      String sql = IOUtils.toString(file, StandardCharsets.UTF_8);
      int updatedRows = jdbcTemplate.update(sql);
      logger.info("Executed {} script {}, number of affected rows {}", label, sqlFile, updatedRows);
    } catch (IOException e) {
      logger.error("{} script {} failed (resource could not be read)", label, sqlFile, e);
      throw new IllegalStateException("Failed to read script " + sqlFile, e);
    } catch (DataAccessException e) {
      logger.error("{} script {} failed - the whole batch will be rolled back", label, sqlFile, e);
      throw e;
    }
  }
}
