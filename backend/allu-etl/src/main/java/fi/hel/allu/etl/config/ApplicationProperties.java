package fi.hel.allu.etl.config;

import java.util.Arrays;
import java.util.List;


import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

  private final String etlScripts;
  private final String etlCleanupScripts;

  @Autowired
  public ApplicationProperties(@Value("${etl.sql.files}") @NotNull String etlScripts,
      @Value("${etl.cleanup.sql.files:cleanup.sql}") @NotNull String etlCleanupScripts) {
    this.etlScripts = etlScripts;
    this.etlCleanupScripts = etlCleanupScripts;
  }

  public List<String> getEtlSqlFiles() {
    return splitScriptList(etlScripts);
  }

  public List<String> getEtlCleanupSqlFiles() {
    return splitScriptList(etlCleanupScripts);
  }

  private List<String> splitScriptList(String scripts) {
    return Arrays.stream(scripts.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();
  }
}
