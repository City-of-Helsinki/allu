package fi.hel.allu.search.indexConductor;

import fi.hel.allu.search.util.Constants;
import org.springframework.stereotype.Component;

@Component
public class ContactIndexConductor extends IndexConductor {

    public ContactIndexConductor() {
        super(Constants.CONTACT_INDEX_ALIAS);
    }

}