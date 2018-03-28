package fi.hel.allu.etl.config;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

  private String etlScripts;

  @Autowired
  public ApplicationProperties(@Value("${etl.sql.files}") @NotNull String etlScripts) {
    this.etlScripts = etlScripts;
  }

  public List<String> getEtlSqlFiles() {
    return Arrays.asList(etlScripts.split(","));
  }
}
