package fi.hel.allu.search.indexConductor;

import fi.hel.allu.search.util.Constants;
import org.springframework.stereotype.Component;

@Component
public class ProjectIndexConductor extends IndexConductor {

    public ProjectIndexConductor() {
        super(Constants.PROJECT_INDEX_ALIAS);
    }

}