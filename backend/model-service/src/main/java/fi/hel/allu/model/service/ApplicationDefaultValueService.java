package fi.hel.allu.model.service;

import org.springframework.stereotype.Service;
import fi.hel.allu.model.domain.Application;

@Service
public class ApplicationDefaultValueService {
  public void setByType(Application application) {
    switch (application.getType()) {
      case TEMPORARY_TRAFFIC_ARRANGEMENTS:
        setNotBillable(application);
      case CABLE_REPORT:
        setNotBillable(application);
      default:
        break;
    }
  }

  private void setNotBillable(Application application) {
    application.setNotBillable(true);
    application.setSkipPriceCalculation(true);
  }
}
