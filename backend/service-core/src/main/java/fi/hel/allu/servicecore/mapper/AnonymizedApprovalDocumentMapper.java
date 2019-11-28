package fi.hel.allu.servicecore.mapper;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.servicecore.service.*;

@Component
public class AnonymizedApprovalDocumentMapper extends ApprovalDocumentMapper {

  private final CustomerAnonymizer customerAnonymizer;

  @Autowired
  public AnonymizedApprovalDocumentMapper(LocationService locationService, CustomerService customerService,
      ContactService contactService, ChargeBasisService chargeBasisService, MetaService metaService,
      CustomerAnonymizer customerAnonymizer) {
    super(locationService, customerService, contactService, chargeBasisService, metaService);
    this.customerAnonymizer = customerAnonymizer;
  }

  @Override
  protected Optional<CustomerAnonymizer> getCustomerAnonymizer() {
    return Optional.of(customerAnonymizer);
  }
}
