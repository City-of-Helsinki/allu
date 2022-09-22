package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.util.Constants;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactSearchService extends GenericSearchService<ContactES, QueryParameters> {

  @Autowired
  public ContactSearchService(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client,
      ContactIndexConductor contactIndexConductor) {
    super(elasticSearchMappingConfig,
          client,
          Constants.CONTACT_TYPE_NAME,
          contactIndexConductor,
          c -> c.getId().toString(),
          ContactES.class);
  }
}
