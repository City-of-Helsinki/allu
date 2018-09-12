package fi.hel.allu.model.service;

import java.time.ZonedDateTime;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.Application;

@Service
public class ApplicationDefaultValueService {

  private static final int INVOICINGDATE_DAYS_BEFORE_START = 15;

  public void setByType(Application application) {
    switch (application.getType()) {
      case TEMPORARY_TRAFFIC_ARRANGEMENTS:
        setNotBillable(application);
      case CABLE_REPORT:
        setNotBillable(application);
      default:
        break;
    }
    setDefaultInvoicingDate(application);
  }

  private void setDefaultInvoicingDate(Application application) {
    if (BooleanUtils.isNotTrue(application.getNotBillable()) && application.getInvoicingDate() == null
        && application.getType() != ApplicationType.EXCAVATION_ANNOUNCEMENT) {
      ZonedDateTime now = ZonedDateTime.now();
      ZonedDateTime defaultInvoicingDate = application.getStartTime().minusDays(INVOICINGDATE_DAYS_BEFORE_START);
      application.setInvoicingDate(defaultInvoicingDate.isAfter(now) ? defaultInvoicingDate : now);
    }
  }

  private void setNotBillable(Application application) {
    application.setNotBillable(true);
    application.setSkipPriceCalculation(true);
  }
}
