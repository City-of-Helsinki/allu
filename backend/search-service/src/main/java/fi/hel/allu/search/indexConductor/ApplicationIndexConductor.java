package fi.hel.allu.search.indexConductor;

import fi.hel.allu.search.util.Constants;
import org.springframework.stereotype.Component;

/**
 * Conductor component for the application index.
 */
@Component
public class ApplicationIndexConductor extends IndexConductor {

  public ApplicationIndexConductor() {
    super(Constants.APPLICATION_INDEX_ALIAS);
  }

}