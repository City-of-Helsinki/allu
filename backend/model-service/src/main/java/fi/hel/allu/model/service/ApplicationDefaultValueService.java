package fi.hel.allu.model.service;

import org.springframework.stereotype.Service;
import fi.hel.allu.model.domain.Application;

@Service
public class ApplicationDefaultValueService {
  public void setByType(Application application) {
    switch (application.getType()) {
      case TEMPORARY_TRAFFIC_ARRANGEMENTS:
        setTemporaryTrafficArrangementDefaults(application);
      default:
        break;
    }
  }

  private void setTemporaryTrafficArrangementDefaults(Application application) {
    application.setNotBillable(true);
    application.setSkipPriceCalculation(true);
  }
}
