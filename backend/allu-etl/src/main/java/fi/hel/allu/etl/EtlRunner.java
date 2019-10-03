package fi.hel.allu.etl;

import java.io.File;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fi.hel.allu.etl.config.ApplicationProperties;

@Component
public class EtlRunner {

  private static final Logger logger = LoggerFactory.getLogger(EtlRunner.class);

  private static final String SCRIPT_PATH = "/db/etl/";
  private ApplicationProperties applicationProperties;
  private JdbcTemplate jdbcTemplate;

  @Autowired
  public EtlRunner(ApplicationProperties applicationProperties, JdbcTemplate jdbcTemplate) {
    this.applicationProperties = applicationProperties;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Scheduled(cron = "${etl.cronstring}")
  public void run() {
    List<String> files = applicationProperties.getEtlSqlFiles();
    files.forEach(s -> executeEtlSql(s));
  }

  private void executeEtlSql(String sqlFile) {
    try (InputStream file = getClass().getResourceAsStream(SCRIPT_PATH + sqlFile)) {
      String sql = IOUtils.toString(file, StandardCharsets.UTF_8);
      int updatedRows = jdbcTemplate.update(sql);
      logger.info("Executed SQL script {}, number of affected rows {}", sqlFile, updatedRows);
    } catch (IOException e) {
      logger.error("Failed to execute script from file {}.", sqlFile, e);
    }
  }
}
