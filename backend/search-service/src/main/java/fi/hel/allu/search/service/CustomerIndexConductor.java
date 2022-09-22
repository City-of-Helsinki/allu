package fi.hel.allu.search.service;


import fi.hel.allu.search.util.Constants;
import org.springframework.stereotype.Component;

/**
 * Conductor component for the application index.
 */
@Component
public class CustomerIndexConductor extends IndexConductor {

  public CustomerIndexConductor() {
    super(Constants.CUSTOMER_INDEX_ALIAS);
  }

}
