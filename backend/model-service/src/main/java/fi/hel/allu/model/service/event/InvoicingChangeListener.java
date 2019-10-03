package fi.hel.allu.model.service.event;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.InvoiceService;

@Service
public class InvoicingChangeListener {

  private final InvoiceService invoiceService;
  private final ApplicationService applicationService;

  @Autowired
  public InvoicingChangeListener(InvoiceService invoiceService, ApplicationService applicationService) {
    this.invoiceService = invoiceService;
    this.applicationService = applicationService;
  }

  @EventListener
  public void onApplicationInvoicingChange(InvoicingChangeEvent event) {
    Application application = applicationService.findById(event.getApplicationId());
    if (BooleanUtils.isFalse(application.getNotBillable())) {
      invoiceService.createInvoices(event.getApplicationId(), applicationService.isSapIdPending(application));
    }
  }
}
