package fi.hel.allu.ui.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.model.domain.CustomerUpdateLog;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.service.CustomerService;
import fi.hel.allu.ui.controller.CustomerExport;

@Service
public class CustomerExportService {

  @Autowired
  private CustomerService customerService;

  public void writeExportFile(CustomerExport exportWriter) {
    List<CustomerJson> invoiceRecipientsWithoutSapNumber = customerService.findInvoiceRecipientsWithoutSapNumber();
    List<CustomerUpdateLog> customerUpdateLogs = customerService.getCustomerUpdateLog();
    List<CustomerJson> updatedSapCustomers = customerService.getCustomersById(getCustomerIds(customerUpdateLogs));
    exportWriter.write(Stream.concat(invoiceRecipientsWithoutSapNumber.stream(), updatedSapCustomers.stream()).collect(Collectors.toList()));
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
