package fi.hel.allu.search.service;


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

  //Used for test
  public ApplicationIndexConductor(String indexAliasName){super(indexAliasName);}

}