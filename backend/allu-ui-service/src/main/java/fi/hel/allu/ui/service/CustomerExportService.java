package fi.hel.allu.ui.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.model.domain.CustomerUpdateLog;
import fi.hel.allu.model.domain.InvoiceRecipientCustomer;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.mapper.CustomerMapper;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.ui.controller.CustomerExport;
import fi.hel.allu.ui.domain.CustomerExportJson;

@Service
public class CustomerExportService {

  @Autowired
  private CustomerService customerService;
  @Autowired
  private CustomerMapper customerMapper;

  public void writeExportFile(CustomerExport exportWriter) {
    List<InvoiceRecipientCustomer> invoiceRecipientsWithoutSapNumber = customerService.findInvoiceRecipientsWithoutSapNumber();
    List<CustomerExportJson> exportCustomers = invoiceRecipientsWithoutSapNumber
        .stream()
        .map(r -> new CustomerExportJson(customerMapper.createCustomerJson(r.getCustomer()), r.getApplicationIdentifiers()))
        .collect(Collectors.toList());
    List<CustomerUpdateLog> customerUpdateLogs = customerService.getCustomerUpdateLog();
    exportCustomers.addAll(customerService.getCustomersById(getCustomerIds(customerUpdateLogs))
        .stream()
        .map(c -> new CustomerExportJson(c, Collections.emptyList()))
        .collect(Collectors.toList())
    );
    exportWriter.write(exportCustomers);
    setUpdateLogsProcessed(customerUpdateLogs);
  }

  private void setUpdateLogsProcessed(List<CustomerUpdateLog> customerUpdateLogs) {
    customerService.setUpdateLogsProcessed(customerUpdateLogs.stream().map(CustomerUpdateLog::getId).collect(Collectors.toList()));
  }

  private List<Integer> getCustomerIds(List<CustomerUpdateLog> customerUpdateLogs) {
    Set<Integer> customerIds = customerUpdateLogs.stream().map(CustomerUpdateLog::getCustomerId).collect(Collectors.toSet());
    return new ArrayList<>(customerIds);
  }
}
