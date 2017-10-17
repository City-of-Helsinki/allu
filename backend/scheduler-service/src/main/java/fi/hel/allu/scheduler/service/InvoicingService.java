package fi.hel.allu.scheduler.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.sap.mapper.AlluMapper;
import fi.hel.allu.sap.marshaller.AlluMarshaller;
import fi.hel.allu.sap.model.SalesOrder;
import fi.hel.allu.sap.model.SalesOrderContainer;
import fi.hel.allu.scheduler.config.ApplicationProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for sending invoices
 */
@Service
public class InvoicingService {
  private static final Logger logger = LoggerFactory.getLogger(InvoicingService.class);

  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;

  @Autowired
  public InvoicingService(RestTemplate restTemplate, ApplicationProperties applicationProperties) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
  }

  public void sendInvoices() {
    final List<Invoice> invoices = getPendingInvoices();
    if (invoices.isEmpty()) {
      logger.info("No invoices to send.");
      return;
    }
    final Map<Integer, Application> applicationsById =
        getApplications(new ArrayList<>(invoices.stream().map(i -> i.getApplicationId()).collect(Collectors.toSet())))
            .stream().collect(Collectors.toMap(Application::getId, Function.identity()));
    final List<SalesOrder> salesOrders = invoices.stream()
        .map(i -> AlluMapper.mapToSalesOrder(applicationsById.get(i.getApplicationId()), i.getRows()))
        .collect(Collectors.toList());
    SalesOrderContainer salesOrderContainer = new SalesOrderContainer();
    salesOrderContainer.setSalesOrders(salesOrders);
    sendToSap(salesOrderContainer);
    markInvoicesSent(invoices.stream().map(i -> i.getId()).collect(Collectors.toList()));
  }

  private void sendToSap(SalesOrderContainer salesOrderContainer) {
    Path dir = Paths.get(applicationProperties.getInvoiceDestDir());
    try {
      if (!Files.isDirectory(dir)) {
        Files.createDirectories(dir);
      }
      Path file = Files.createTempFile(dir, "invoice", ".xml");
      try (OutputStream outputStream = Files.newOutputStream(file)) {
        AlluMarshaller alluMarshaller = new AlluMarshaller();
        alluMarshaller.marshal(salesOrderContainer, outputStream);
      } catch (JAXBException e) {
        logger.error("Error in marshalling invoice XML", e);
      }
    } catch (IOException e) {
      logger.error("Error creating the invoice file in " + dir.toString(), e);
    }
  }

  private List<Invoice> getPendingInvoices() {
    return Arrays.asList(restTemplate.getForObject(applicationProperties.getPendingInvoicesUrl(), Invoice[].class));
  }

  private List<Application> getApplications(List<Integer> applicationIds) {
    return Arrays.asList(restTemplate.postForObject(applicationProperties.getFindApplicationsUrl(), applicationIds,
        Application[].class));
  }

  private void markInvoicesSent(List<Integer> invoiceIds) {
    restTemplate.postForObject(applicationProperties.getMarkInvoicesSentUrl(), invoiceIds, Void.class);
  }
}
