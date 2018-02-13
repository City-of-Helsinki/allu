package fi.hel.allu.scheduler.service;

import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.sap.mapper.AlluMapper;
import fi.hel.allu.sap.marshaller.AlluMarshaller;
import fi.hel.allu.sap.model.SalesOrder;
import fi.hel.allu.sap.model.SalesOrderContainer;
import fi.hel.allu.scheduler.config.ApplicationProperties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for sending invoices
 */
@Service
public class InvoicingService {
  private static final Logger logger = LoggerFactory.getLogger(InvoicingService.class);
  private static final String INVOICE_FILE_PREFIX = "MTIL_IN_ID341_";
  private static final DateTimeFormatter INVOICE_DATETIME_FORMATTER = DateTimeFormatter
      .ofPattern("yyyyMMddHHmmssSSS");
  private static final String MAIL_TEMPLATE = "/templates/invoice-notification-mail-template.txt";

  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private SftpService sftpService;
  private AlluMailService alluMailService;
  private ApplicationStatusUpdaterService applicationStatusUpdaterService;

  @Autowired
  public InvoicingService(RestTemplate restTemplate, ApplicationProperties applicationProperties,
      SftpService sftpService, AlluMailService alluMailService, ApplicationStatusUpdaterService applicationStatusUpdaterService) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.sftpService = sftpService;
    this.alluMailService = alluMailService;
    this.applicationStatusUpdaterService = applicationStatusUpdaterService;
  }

  @PostConstruct
  public void createDirectories() throws IOException {
    Files.createDirectories(Paths.get(applicationProperties.getInvoiceArchiveDir()));
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
        .map(i -> {
          final Application app = applicationsById.get(i.getApplicationId());
          return AlluMapper.mapToSalesOrder(app, getCustomer(app.getInvoiceRecipientId()), i.getRows());
        })
        .collect(Collectors.toList());
    SalesOrderContainer salesOrderContainer = new SalesOrderContainer();
    salesOrderContainer.setSalesOrders(salesOrders);
    if (sendToSap(salesOrderContainer)) {
      markInvoicesSent(invoices.stream().map(i -> i.getId()).collect(Collectors.toList()));
      sendNotificationEmail(applicationsById.values().stream().map(a -> a.getApplicationId()).collect(Collectors.toList()));
      applicationStatusUpdaterService.archiveApplications(new ArrayList<>(applicationsById.keySet()));
    }
  }

  private boolean sendToSap(SalesOrderContainer salesOrderContainer) {
    return writeToTempFile(salesOrderContainer).map(dir -> {
      boolean sentOk = sftpService.uploadFiles(
          applicationProperties.getSapFtpInvoiceHost(),
          applicationProperties.getSapFtpInvoicePort(),
          applicationProperties.getSapFtpInvoiceUser(),
          applicationProperties.getSapFtpInvoicePassword(),
          dir.toString(),
          applicationProperties.getInvoiceArchiveDir(),
          applicationProperties.getSapFtpInvoiceDirectory());
      FileSystemUtils.deleteRecursively(dir.toFile());
      return sentOk;
    }).orElse(false);
  }

  private Optional<Path> writeToTempFile(SalesOrderContainer salesOrderContainer) {
    try {
      Path dir = Files.createTempDirectory("allu_temp");
      Path file = Files.createFile(
          dir.resolve(INVOICE_FILE_PREFIX + ZonedDateTime.now().format(INVOICE_DATETIME_FORMATTER) + ".xml"));
      try (OutputStream outputStream = Files.newOutputStream(file)) {
        AlluMarshaller alluMarshaller = new AlluMarshaller();
        alluMarshaller.marshal(salesOrderContainer, outputStream);
        return Optional.of(dir);
      } catch (JAXBException e) {
        logger.error("Error in marshalling invoice XML", e);
      }
    } catch (IOException e) {
      logger.error("Error creating the temporary invoice file", e);
    }
    return Optional.empty();
  }

  private List<Invoice> getPendingInvoices() {
    return Arrays.asList(restTemplate.getForObject(applicationProperties.getPendingInvoicesUrl(), Invoice[].class));
  }

  private List<Application> getApplications(List<Integer> applicationIds) {
    return Arrays.asList(restTemplate.postForObject(applicationProperties.getFindApplicationsUrl(), applicationIds,
        Application[].class));
  }

  private Customer getCustomer(int id) {
    return restTemplate.getForObject(applicationProperties.getFindCustomerUrl(), Customer.class, id);
  }

  private void markInvoicesSent(List<Integer> invoiceIds) {
    restTemplate.postForObject(applicationProperties.getMarkInvoicesSentUrl(), invoiceIds, Void.class);
  }

  private void sendNotificationEmail(List<String> applicationIds) {
    String receiverEmail = applicationProperties.getInvoiceNotificationReceiverEmail();
    if (StringUtils.isEmpty(receiverEmail)) {
      return;
    }
    String subject = applicationProperties.getInvoiceNotificationSubject();
    String mailTemplate = null;
    try {
      mailTemplate = ResourceUtil.readClassPathResource(MAIL_TEMPLATE);
      String body = StrSubstitutor.replace(mailTemplate, mailVariables(applicationIds));
      alluMailService.sendEmail(Collections.singletonList(receiverEmail), subject, body);
    } catch (IOException e) {
      logger.error("Error reading mail template: " + e);
    }
  }

  private Map<String, String> mailVariables(List<String> applicationIds) {
    Map<String, String> result = new HashMap<>();
    result.put("sentDate", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    result.put("nrOfInvoices", String.valueOf(applicationIds.size()));
    result.put("invoiceApplicationIds", String.join(", ", applicationIds));
    return result;
  }

}
