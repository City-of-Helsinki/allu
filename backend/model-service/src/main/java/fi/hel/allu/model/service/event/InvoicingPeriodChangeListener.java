package fi.hel.allu.model.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;

@Service
public class InvoicingPeriodChangeListener {

  private final ChargeBasisService chargeBasisService;
  private final ApplicationService applicationService;

  @Autowired
  public InvoicingPeriodChangeListener(ChargeBasisService chargeBasisService, ApplicationService applicationService) {
    this.chargeBasisService = chargeBasisService;
    this.applicationService = applicationService;
  }

  @EventListener
  public void onInvoicingPeriodChange(InvoicingPeriodChangeEvent event) {
    // Update manual charge basis
    chargeBasisService.setInvoicingPeriodForManualEntries(event.getApplicationId());
    // Update calculated charge basis
    applicationService.updateChargeBasis(event.getApplicationId());
  }
}

