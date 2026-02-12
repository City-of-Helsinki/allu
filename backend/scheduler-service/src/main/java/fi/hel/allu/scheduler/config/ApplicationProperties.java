package fi.hel.allu.scheduler.config;

import fi.hel.allu.model.domain.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Component
public class ApplicationProperties {

  private final String modelServiceHost;
  private final String modelServicePort;
  private final String extServiceHost;
  private final String extServicePort;
  private final List<String> emailAllowedAddresses;
  private final String emailSenderAddress;
  private final String invoiceArchiveDir;
  private final String sapFtpInvoiceHost;
  private final int sapFtpInvoicePort;
  private final String sapFtpInvoiceUser;
  private final String sapFtpInvoicePassword;
  private final String sapFtpInvoiceDirectory;
  private final boolean customerUpdateEnabled;
  private final String customerSourceDir;
  private final String customerArchiveDir;
  private final String failedCustomerUpdateDir;
  private final String sapFtpCustomerHost;
  private final int sapFtpCustomerPort;
  private final String sapFtpCustomerUser;
  private final String sapFtpCustomerPassword;
  private final String sapFtpCustomerDirectory;
  private final String sapFtpCustomerArchive;
  private final String serviceAuth;
  private final String customerNotificationSubject;
  private final String removedCustomersNotificationSubject;
  private final String uiBaseUrl;
  private final String invoiceNotificationSubject;
  private final int searchSyncStartupDelay;
  private final boolean invoiceSendingEnabled;
  private final String knownHosts;
  private final String signatureAlgorithm;
  private final String keyAlgorithm;
  private final int sftpTimeout;

  @Autowired
  public ApplicationProperties(
      @Value("${model.service.host}") @NotEmpty String modelServiceHost,
      @Value("${model.service.port}") @NotEmpty String modelServicePort,
      @Value("${ext.service.host}") @NotEmpty String extServiceHost,
      @Value("${ext.service.port}") @NotEmpty String extServicePort,
      @Value("#{'${email.allowed.addresses:}'.split(',')}") List<String> emailAllowedAddresses,
      @Value("${email.sender.address}") @NotEmpty String emailSenderAddress,
      @Value("${invoice.archivedir}") @NotEmpty String invoiceArchiveDir,
      @Value("${sap.ftp.invoice.host}") @NotEmpty String sapFtpInvoiceHost,
      @Value("${sap.ftp.invoice.port}") int sapFtpInvoicePort,
      @Value("${sap.ftp.invoice.user}") @NotEmpty String sapFtpInvoiceUser,
      @Value("${sap.ftp.invoice.password}") @NotEmpty String sapFtpInvoicePassword,
      @Value("${sap.ftp.invoice.directory}") @NotEmpty String sapFtpInvoiceDirectory,
      @Value("${customer.update.enabled}") boolean customerUpdateEnabled,
      @Value("${customer.sourcedir}") @NotEmpty String customerSourceDir,
      @Value("${customer.archivedir}") @NotEmpty String customerArchiveDir,
      @Value("${customer.failedupdatedir}") @NotEmpty String failedCustomerUpdateDir,
      @Value("${sap.ftp.customer.host}") @NotEmpty String sapFtpCustomerHost,
      @Value("${sap.ftp.customer.port}") int sapFtpCustomerPort,
      @Value("${sap.ftp.customer.user}") @NotEmpty String sapFtpCustomerUser,
      @Value("${sap.ftp.customer.password}") @NotEmpty String sapFtpCustomerPassword,
      @Value("${sap.ftp.customer.directory}") @NotEmpty String sapFtpCustomerDirectory,
      @Value("${sap.ftp.customer.archive}") @NotEmpty String sapFtpCustomerArchive,
      @Value("${service.authkey}") @NotEmpty String serviceAuth,
      @Value("${customer.notification.subject}") @NotEmpty String customerNotificationSubject,
      @Value("${removed.customers.subject}") @NotEmpty String removedCustomersNotificationSubject,
      @Value("${invoice.notification.subject}") @NotEmpty String invoiceNotificationSubject,
      @Value("${ui.baseurl}") @NotEmpty String uiBaseUrl,
      @Value("${search.sync.startup.delay}") int searchSyncStartupDelay,
      @Value("${invoice.sending.enabled}") boolean invoiceSendingEnabled,
      @Value("${sftp.settings.knownHosts}") String knownHosts,
      @Value("${sftp.settings.signatureAlgorithm}") String signatureAlgorithm,
      @Value("${sftp.settings.keyAlgorithm}") String keyAlgorithm,
      @Value("${sftp.settings.timeout}") int sftpTimeout) {
    this.modelServiceHost = modelServiceHost;
    this.modelServicePort = modelServicePort;
    this.extServiceHost = extServiceHost;
    this.extServicePort = extServicePort;
    this.emailAllowedAddresses = emailAllowedAddresses;
    this.emailSenderAddress = emailSenderAddress;
    this.invoiceArchiveDir = invoiceArchiveDir;
    this.sapFtpInvoiceHost = sapFtpInvoiceHost;
    this.sapFtpInvoicePort = sapFtpInvoicePort;
    this.sapFtpInvoiceUser = sapFtpInvoiceUser;
    this.sapFtpInvoicePassword = sapFtpInvoicePassword;
    this.sapFtpInvoiceDirectory = sapFtpInvoiceDirectory;
    this.customerUpdateEnabled = customerUpdateEnabled;
    this.customerSourceDir = customerSourceDir;
    this.customerArchiveDir = customerArchiveDir;
    this.failedCustomerUpdateDir = failedCustomerUpdateDir;
    this.sapFtpCustomerHost = sapFtpCustomerHost;
    this.sapFtpCustomerPort = sapFtpCustomerPort;
    this.sapFtpCustomerUser = sapFtpCustomerUser;
    this.sapFtpCustomerPassword = sapFtpCustomerPassword;
    this.sapFtpCustomerDirectory = sapFtpCustomerDirectory;
    this.sapFtpCustomerArchive = sapFtpCustomerArchive;
    this.serviceAuth = serviceAuth;
    this.customerNotificationSubject = customerNotificationSubject;
    this.removedCustomersNotificationSubject = removedCustomersNotificationSubject;
    this.invoiceNotificationSubject = invoiceNotificationSubject;
    this.uiBaseUrl = uiBaseUrl;
    this.searchSyncStartupDelay = searchSyncStartupDelay;
    this.invoiceSendingEnabled = invoiceSendingEnabled;
    this.knownHosts = knownHosts;
    this.signatureAlgorithm = signatureAlgorithm;
    this.keyAlgorithm = keyAlgorithm;
    this.sftpTimeout = sftpTimeout;
  }

  private static final String PATH_PREFIX = "http://";

  private String getModelServiceUrl(String path) {
    return PATH_PREFIX + modelServiceHost + ":" + modelServicePort + path;
  }

  private String getExtServiceUrl(String path) {
    return PATH_PREFIX + extServiceHost + ":" + extServicePort + path;
  }

  /**
   * @return url to check applications that are close to a notification deadline
   */
  public String getDeadlineCheckUrl() {
    return getModelServiceUrl("/applications/deadline-check");
  }

  /**
   * @return url to mark applications for having a deadline reminder sent
   */
  public String getMarkReminderSentUrl() {
    return getModelServiceUrl("/applications/reminder-sent");
  }

  /**
   * @return url to get list of applications by their ids
   */
  public String getFindApplicationsUrl() {
    return getModelServiceUrl("/applications/find");
  }

  /**
   * @return url to get authentication token
   */
  public String getTokenRequestUrl() {
    return getExtServiceUrl("/token");
  }

  /**
   * @return url to update customer
   */
  public String getCustomerUpdateUrl() {
    return getExtServiceUrl("/v1/customers");
  }

  /*
   * @return url to find Customer info by id.
   */
  public String getFindCustomerUrl() {
    return getModelServiceUrl("/customers/{id}");
  }
  /**
   * @return url to get list of pending invoices
   */
  public String getPendingInvoicesUrl() {
    return getModelServiceUrl("/applications/invoices/ready-to-send");
  }

  /**
   * @return url to mark invoices as sent
   */
  public String getMarkInvoicesSentUrl() {
    return getModelServiceUrl("/applications/invoices/mark-as-sent");
  }

  /**
   * @return list of allowed email recipients.
   */
  public List<String> getEmailAllowedAddresses() {
    return emailAllowedAddresses;
  }

  /**
   * Get the address that should be used when sending email from the system
   *
   * @return sender address
   */
  public String getEmailSenderAddress() {
    return emailSenderAddress;
  }

  /**
   * Get the local directory where sent invoices should be archived
   *
   * @return directory path
   */
  public String getInvoiceArchiveDir() {
    return invoiceArchiveDir;
  }

  /**
   * FTP server host for SAP invoice upload
   */
  public String getSapFtpInvoiceHost() {
    return sapFtpInvoiceHost;
  }

  /**
   * FTP server port for SAP invoice upload
   */
  public int getSapFtpInvoicePort() {
    return sapFtpInvoicePort;
  }

  /**
   * FTP user name for SAP invoice upload
   */
  public String getSapFtpInvoiceUser() {
    return sapFtpInvoiceUser;
  }

  /**
   * FTP user password for SAP invoice upload
   */
  public String getSapFtpInvoicePassword() {
    return sapFtpInvoicePassword;
  }

  /**
   * FTP server directory of SAP invoice upload
   */
  public String getSapFtpInvoiceDirectory() {
    return sapFtpInvoiceDirectory;
  }

  /**
   * Get source directory for customer update files.
   */
  public String getCustomerSourceDir() {
    return customerSourceDir;
  }

  /**
   * Get archive directory for customer update files.
   */
  public String getCustomerArchiveDir() {
    return customerArchiveDir;
  }

  public String getFailedCustomerUpdateDir() {
    return failedCustomerUpdateDir;
  }

  /**
   * FTP server host for SAP customer update
   */
  public String getSapFtpCustomerHost() {
    return sapFtpCustomerHost;
  }

  /**
   * FTP server port for SAP customer update
   */
  public int getSapFtpCustomerPort() {
    return sapFtpCustomerPort;
  }

  /**
   * FTP user name for SAP customer update
   */
  public String getSapFtpCustomerUser() {
    return sapFtpCustomerUser;
  }

  /**
   * FTP user password for SAP customer update
   */
  public String getSapFtpCustomerPassword() {
    return sapFtpCustomerPassword;
  }

  /**
   * FTP server directory of SAP customer files
   */
  public String getSapFtpCustomerDirectory() {
    return sapFtpCustomerDirectory;
  }

  /**
   * FTP server directory where files are moved after successful transfer
   */
  public String getSapFtpCustomerArchive() {
    return sapFtpCustomerArchive;
  }

  /**
   * Value indicating whether SAP customer update
   * is enabled.
   */
  public boolean isCustomerUpdateEnabled() {
    return customerUpdateEnabled;
  }

  /**
   * Get the auth token for the service user
   *
   * @return auth token
   */
  public String getServiceAuth() {
    return serviceAuth;
  }

  /**
   * Url for receiver emails for SAP customer notifications
   */
  public String getCustomerNotificationReceiverEmailsUrl() {
    return getModelServiceUrl("/configurations/" + ConfigurationKey.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL);
  }

  /**
   * Get subject for SAP customer notification email
   */
  public String getCustomerNotificationMailSubject() {
    return customerNotificationSubject;
  }

  /**
   * Get URL for downloading SAP customer order excel
   */
  public String getCustomerDownloadUrl() {
    return uiBaseUrl + "/download/customers/saporder/xlsx";
  }

  /**
   * @return url for getting number of invoice recipients without SAP number.
   */
  public String getNrOfInvoiceRecipientsWithoutSapNumberUrl() {
    return getExtServiceUrl("/v1/customers/saporder/count");
  }

  public String getNrOfSapCustomerUpdatesUrl() {
    return getExtServiceUrl("/v1/customers/sapupdates/count");
  }

  /**
   * @return url for starting search sync.
   */
  public String getStartSearchSyncUrl() {
    return getExtServiceUrl("/v1/search/sync");
  }

  /**
   * @return url for receiver email for SAP invoice notifications
   */
  public String getInvoiceNotificationReceiverEmailsUrl() {
    return getModelServiceUrl("/configurations/" + ConfigurationKey.INVOICE_NOTIFICATION_RECEIVER_EMAIL);
  }

  /**
   * Get email subject for SAP invoice notifications
   */
  public String getInvoiceNotificationSubject() {
    return invoiceNotificationSubject;
  }

  public int getSearchSyncStartupDelay() {
    return searchSyncStartupDelay;
  }

  public String getUpdateFinishedApplicationsUrl() {
    return getExtServiceUrl("/v1/applications/finished/status");
  }

  public String getArchiveApplicationsUrl() {
    return getExtServiceUrl("/v1/applications/finished/archive");
  }

  public String getMailSenderLogUrl() {
    return getModelServiceUrl("/logs/mailsender");
  }

  public boolean isInvoiceSendingEnabled() {
    return invoiceSendingEnabled;
  }

  public String getUpdateCityDistrictsUrl() {
    return getExtServiceUrl("/v1/citydistricts");
  }

  public String getUpdateTerminatedApplicationsUrl() {
    return getExtServiceUrl("/v1/applications/terminated/status");
  }

  public String getCheckAnonymizableApplicationsUrl() {
    return getExtServiceUrl("/v1/applications/checkanonymizable");
  }

  public String getKnownHosts() {
    return knownHosts;
  }

  public String getSignatureAlgorithm() {
    return signatureAlgorithm;
  }

  public String getKeyAlgorithm() {
    return keyAlgorithm;
  }

  public int getSftpTimeout() {
    return sftpTimeout;
  }

  /**
   * Url for getting a list of removed/archived SAP customers that need to be notified by email
   */
  public String getRemovedSapCustomersUrl() {
    return getModelServiceUrl("/customers/archived/sap/unnotified");
  }

  /**
   * Url for marking archived SAP customers as notified by email
   */
  public String getMarkRemovedSapCustomersNotifiedUrl() {
      return getModelServiceUrl("/customers/archived/sap/mark-notified");
  }

  /**
   * Get subject for removed SAP customers notification email
   */
  public String getRemovedSapCustomersSubject() {
    return removedCustomersNotificationSubject;
  }
}
