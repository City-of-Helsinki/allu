package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import org.springframework.stereotype.Component;

@Component
public class ProjectIndexConductor extends IndexConductor {

	public ProjectIndexConductor() {
		super(ElasticSearchMappingConfig.PROJECT_INDEX_ALIAS);
	}

}