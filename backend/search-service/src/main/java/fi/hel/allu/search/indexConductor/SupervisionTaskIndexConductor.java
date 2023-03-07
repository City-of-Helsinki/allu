package fi.hel.allu.search.indexConductor;

import fi.hel.allu.search.util.Constants;
import org.springframework.stereotype.Component;

@Component
public class SupervisionTaskIndexConductor extends IndexConductor {

    public SupervisionTaskIndexConductor() {
        super(Constants.SUPERVISION_TASK_INDEX);
    }
}