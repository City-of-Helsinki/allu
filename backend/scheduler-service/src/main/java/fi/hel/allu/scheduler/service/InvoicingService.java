package fi.hel.allu.scheduler.service;

import fi.hel.allu.common.util.ResourceUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.sap.mapper.AlluMapper;
import fi.hel.allu.sap.marshaller.AlluMarshaller;
import fi.hel.allu.sap.model.SalesOrder;
import fi.hel.allu.sap.model.SalesOrderContainer;
import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.domain.SFTPSettings;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
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

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final SftpService sftpService;
  private final AlluMailService alluMailService;
  private final ApplicationStatusUpdaterService applicationStatusUpdaterService;

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
    Path archiveDirectory = Paths.get(applicationProperties.getInvoiceArchiveDir());
    if (!Files.exists(archiveDirectory)) {
      Files.createDirectories(archiveDirectory);
    }
  }

  public boolean isInvoiceSendingEnabled() {
    return applicationProperties.isInvoiceSendingEnabled();
  }

  public void sendInvoices() {
    final List<Invoice> invoices = getPendingInvoices();
    if (invoices.isEmpty()) {
      logger.info("No invoices to send.");
      sendNotificationEmail(new ArrayList<String>(),0);
      return;
    }

    List<Invoice> netZeroInvoices = invoices.stream().filter(this::isNetZero).collect(Collectors.toList());
    List<Invoice> nonNetZeroInvoices = invoices.stream().filter(invoice -> !isNetZero(invoice)).collect(Collectors.toList());

    Set<Integer> applicationIdsWithZeroInvoices = netZeroInvoices.stream().map(Invoice::getApplicationId).collect(Collectors.toSet());
    Set<Integer> applicationIdsWithNonZeroInvoices = nonNetZeroInvoices.stream().map(Invoice::getApplicationId).collect(Collectors.toSet());

    final Map<Integer, Application> applicationsWithNonZeroInvoicesById =
        getApplications(new ArrayList<>(applicationIdsWithNonZeroInvoices))
            .stream().collect(Collectors.toMap(Application::getId, Function.identity()));

    List<Integer> invoiceIdsToSend = new ArrayList<>();
    invoiceIdsToSend.addAll(netZeroInvoices.stream().map(Invoice::getId).collect(Collectors.toList()));

    Set<Integer> applicationIdsToArchive = new HashSet<>();
    applicationIdsToArchive.addAll(applicationIdsWithZeroInvoices);

    SalesOrderContainer salesOrderContainer = createSalesOrderContainer(nonNetZeroInvoices, applicationsWithNonZeroInvoicesById);
    if (sendToSap(salesOrderContainer)) {
      invoiceIdsToSend.addAll(nonNetZeroInvoices.stream().map(Invoice::getId).collect(Collectors.toList()));
      sendNotificationEmail(applicationsWithNonZeroInvoicesById.values().stream().map(Application::getApplicationId).collect(Collectors.toList()), nonNetZeroInvoices.size());
      applicationIdsToArchive.addAll(applicationIdsWithNonZeroInvoices);
    }

    markInvoicesSent(invoiceIdsToSend);
    applicationStatusUpdaterService.archiveApplications(new ArrayList<>(applicationIdsToArchive));
  }

  private SalesOrderContainer createSalesOrderContainer(List<Invoice> invoices, Map<Integer, Application> applicationsById) {
    final List<SalesOrder> salesOrders = invoices.stream()
        .map(i -> createSalesOrder(i, applicationsById))
        .collect(Collectors.toList());
    SalesOrderContainer salesOrderContainer = new SalesOrderContainer();
    salesOrderContainer.setSalesOrders(salesOrders);
    return salesOrderContainer;
  }

  private SalesOrder createSalesOrder(Invoice i, Map<Integer, Application> applicationsById) {
    final Application app = applicationsById.get(i.getApplicationId());
    return AlluMapper.mapToSalesOrder(app, i.getInvoiceRecipient(), getCustomer(app.getInvoiceRecipientId()).getSapCustomerNumber(), i.getRows());
  }

  private boolean isNetZero(Invoice invoice) {
    return 0 == invoice.getRows().stream().reduce(0, (netSumAccumulator, invoiceRow) -> netSumAccumulator + invoiceRow.getNetPrice(), Integer::sum);
  }

  public boolean sendToSap(SalesOrderContainer salesOrderContainer) {
    return writeToTempFile(salesOrderContainer).map(dir -> {
      boolean sentOk = sftpService.uploadFiles(
          createSFTPSettings(),
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

  private Customer getCustomer(Integer id) {
    return restTemplate.getForObject(applicationProperties.getFindCustomerUrl(), Customer.class, id);
  }

  private void markInvoicesSent(List<Integer> invoiceIds) {
    restTemplate.postForObject(applicationProperties.getMarkInvoicesSentUrl(), invoiceIds, Void.class);
  }

  public void sendNotificationEmail(List<String> applicationIds, Integer nrOfInvoices) {
    List<String> receiverEmails = getInvoiceNotificationReceiverEmails();
    if (receiverEmails.isEmpty()) {
      return;
    }

    String subject = applicationProperties.getInvoiceNotificationSubject();
    try {
      String mailTemplate = ResourceUtil.readClassPathResource(MAIL_TEMPLATE);
      String body = StringSubstitutor.replace(mailTemplate, mailVariables(applicationIds, nrOfInvoices));
      alluMailService.sendEmail(receiverEmails, subject, body, null, null);
    } catch (IOException e) {
      logger.error("Error reading mail template: " + e);
    }
  }

  private Map<String, String> mailVariables(List<String> applicationIds, int nrOfInvoices) {
    Map<String, String> result = new HashMap<>();
    result.put("sentDate", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    result.put("nrOfInvoices", String.valueOf(nrOfInvoices));
    result.put("invoiceApplicationIds", String.join(", ", applicationIds));
    return result;
  }

  private List<String> getInvoiceNotificationReceiverEmails() {
    final List<Configuration> emails = restTemplate.exchange(
        applicationProperties.getInvoiceNotificationReceiverEmailsUrl(),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<Configuration>>() {}).getBody();
    return emails.stream().map(Configuration::getValue).collect(Collectors.toList());
  }

  private SFTPSettings createSFTPSettings(){
    return new SFTPSettings(applicationProperties.getSapFtpInvoiceHost(),
                            applicationProperties.getSapFtpInvoiceUser(),
                            applicationProperties.getSapFtpInvoicePort(),
                            applicationProperties.getSapFtpInvoicePassword(),
                            applicationProperties.getKnownHosts(),
                            applicationProperties.getSignatureAlgorithm(),
                            applicationProperties.getKeyAlgorithm(),
                            applicationProperties.getSftpTimeout());
  }
}
