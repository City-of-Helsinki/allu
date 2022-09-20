package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import org.springframework.stereotype.Component;

@Component
public class ContactIndexConductor extends IndexConductor {

	public ContactIndexConductor() {
		super(ElasticSearchMappingConfig.CONTACT_INDEX_ALIAS);
	}

}