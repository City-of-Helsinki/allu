package fi.hel.allu.scheduler.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
  private final String sapFtpInvoiceUser;
  private final String sapFtpInvoicePassword;
  private final String sapFtpInvoiceDirectory;
  private final boolean customerUpdateEnabled;
  private final String customerSourceDir;
  private final String customerArchiveDir;
  private final String failedCustomerUpdateDir;
  private final String sapFtpCustomerHost;
  private final String sapFtpCustomerUser;
  private final String sapFtpCustomerPassword;
  private final String sapFtpCustomerDirectory;
  private final String sapFtpCustomerArchive;
  private final String serviceAuth;
  private final String customerNotificationReceiverEmail;
  private final String customerNotificationSubject;
  private final String uiBaseUrl;
  private final String invoiceNotificationReceiverEmail;
  private final String invoiceNotificationSubject;
  private final int searchSyncStartupDelay;

  @Autowired
  public ApplicationProperties(@Value("${model.service.host}") @NotEmpty String modelServiceHost,
                               @Value("${model.service.port}") @NotEmpty String modelServicePort,
                               @Value("${ext.service.host}") @NotEmpty String extServiceHost,
                               @Value("${ext.service.port}") @NotEmpty String extServicePort,
      @Value("#{'${email.allowed.addresses:}'.split(',')}") List<String> emailAllowedAddresses,
      @Value("${email.sender.address}") @NotEmpty String emailSenderAddress,
      @Value("${invoice.archivedir}") @NotEmpty String invoiceArchiveDir,
      @Value("${sap.ftp.invoice.host}") @NotEmpty String sapFtpInvoiceHost,
      @Value("${sap.ftp.invoice.user}") @NotEmpty String sapFtpInvoiceUser,
      @Value("${sap.ftp.invoice.password}") @NotEmpty String sapFtpInvoicePassword,
      @Value("${sap.ftp.invoice.directory}") @NotEmpty String sapFtpInvoiceDirectory,
      @Value("${customer.update.enabled}") boolean customerUpdateEnabled,
      @Value("${customer.sourcedir}") @NotEmpty String customerSourceDir,
      @Value("${customer.archivedir}") @NotEmpty String customerArchiveDir,
      @Value("${customer.failedupdatedir}") @NotEmpty String failedCustomerUpdateDir,
      @Value("${sap.ftp.customer.host}") @NotEmpty String sapFtpCustomerHost,
      @Value("${sap.ftp.customer.user}") @NotEmpty String sapFtpCustomerUser,
      @Value("${sap.ftp.customer.password}") @NotEmpty String sapFtpCustomerPassword,
      @Value("${sap.ftp.customer.directory}") @NotEmpty String sapFtpCustomerDirectory,
      @Value("${sap.ftp.customer.archive}") @NotEmpty String sapFtpCustomerArchive,
      @Value("${service.authkey}") @NotEmpty String serviceAuth,
      @Value("${customer.notification.receiveremail}") String customerNotificationReceiverEmail,
      @Value("${customer.notification.subject}") @NotEmpty String customerNotificationSubject,
      @Value("${invoice.notification.receiveremail}") String invoiceNotificationReceiverEmail,
      @Value("${invoice.notification.subject}") @NotEmpty String invoiceNotificationSubject,
      @Value("${ui.baseurl}") @NotEmpty String uiBaseUrl,
      @Value("${search.sync.startup.delay}") int searchSyncStartupDelay) {
    this.modelServiceHost = modelServiceHost;
    this.modelServicePort = modelServicePort;
    this.extServiceHost = extServiceHost;
    this.extServicePort = extServicePort;
    this.emailAllowedAddresses = emailAllowedAddresses;
    this.emailSenderAddress = emailSenderAddress;
    this.invoiceArchiveDir = invoiceArchiveDir;
    this.sapFtpInvoiceHost = sapFtpInvoiceHost;
    this.sapFtpInvoiceUser = sapFtpInvoiceUser;
    this.sapFtpInvoicePassword = sapFtpInvoicePassword;
    this.sapFtpInvoiceDirectory = sapFtpInvoiceDirectory;
    this.customerUpdateEnabled = customerUpdateEnabled;
    this.customerSourceDir = customerSourceDir;
    this.customerArchiveDir = customerArchiveDir;
    this.failedCustomerUpdateDir = failedCustomerUpdateDir;
    this.sapFtpCustomerHost = sapFtpCustomerHost;
    this.sapFtpCustomerUser = sapFtpCustomerUser;
    this.sapFtpCustomerPassword = sapFtpCustomerPassword;
    this.sapFtpCustomerDirectory = sapFtpCustomerDirectory;
    this.sapFtpCustomerArchive = sapFtpCustomerArchive;
    this.serviceAuth = serviceAuth;
    this.customerNotificationReceiverEmail = customerNotificationReceiverEmail;
    this.customerNotificationSubject = customerNotificationSubject;
    this.invoiceNotificationReceiverEmail = invoiceNotificationReceiverEmail;
    this.invoiceNotificationSubject = invoiceNotificationSubject;
    this.uiBaseUrl = uiBaseUrl;
    this.searchSyncStartupDelay = searchSyncStartupDelay;
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
   * Get receiver email for SAP customer notifications
   */
  public String getCustomerNotificationReceiverEmail() {
    return customerNotificationReceiverEmail;
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

  /**
   * @return url for starting search sync.
   */
  public String getStartSearchSyncUrl() {
    return getExtServiceUrl("/v1/search/sync");
  }

  /**
  * Get receiver email for SAP invoice notifications
  */
  public String getInvoiceNotificationReceiverEmail() {
    return invoiceNotificationReceiverEmail;
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

}
