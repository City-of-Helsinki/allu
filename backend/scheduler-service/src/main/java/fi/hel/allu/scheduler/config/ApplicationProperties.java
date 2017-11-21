package fi.hel.allu.scheduler.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationProperties {

  private String modelServiceHost;
  private String modelServicePort;
  private String extServiceHost;
  private String extServicePort;
  private List<String> emailAllowedAddresses;
  private String emailSenderAddress;
  private String invoiceDestDir;
  private boolean customerUpdateEnabled;
  private String customerSourceDir;
  private String customerArchiveDir;
  private String failedCustomerUpdateDir;
  private String sapFtpCustomerHost;
  private String sapFtpCustomerUser;
  private String sapFtpCustomerPassword;
  private String sapFtpCustomerDirectory;
  private String sapFtpCustomerArchive;
  private String serviceAuth;

  @Autowired
  public ApplicationProperties(@Value("${model.service.host}") @NotEmpty String modelServiceHost,
                               @Value("${model.service.port}") @NotEmpty String modelServicePort,
                               @Value("${ext.service.host}") @NotEmpty String extServiceHost,
                               @Value("${ext.service.port}") @NotEmpty String extServicePort,
                               @Value("#{'${email.allowed.addresses:}'.split(',')}") List<String> emailAllowedAddresses,
      @Value("${email.sender.address}") @NotEmpty String emailSenderAddress,
      @Value("${invoice.destdir}") @NotEmpty String invoiceDestDir,
      @Value("${customer.update.enabled}") boolean customerUpdateEnabled,
      @Value("${customer.sourcedir}") @NotEmpty String customerSourceDir,
      @Value("${customer.archivedir}") @NotEmpty String customerArchiveDir,
      @Value("${customer.failedupdatedir}") @NotEmpty String failedCustomerUpdateDir,
      @Value("${sap.ftp.customer.host}") @NotEmpty String sapFtpCustomerHost,
      @Value("${sap.ftp.customer.user}") @NotEmpty String sapFtpCustomerUser,
      @Value("${sap.ftp.customer.password}") @NotEmpty String sapFtpCustomerPassword,
      @Value("${sap.ftp.customer.directory}") @NotEmpty String sapFtpCustomerDirectory,
      @Value("${sap.ftp.customer.archive}") @NotEmpty String sapFtpCustomerArchive,
      @Value("${service.authkey}") @NotEmpty String serviceAuth) {
    this.modelServiceHost = modelServiceHost;
    this.modelServicePort = modelServicePort;
    this.emailAllowedAddresses = emailAllowedAddresses;
    this.emailSenderAddress = emailSenderAddress;
    this.invoiceDestDir = invoiceDestDir;
    this.customerUpdateEnabled = customerUpdateEnabled;
    this.customerSourceDir = customerSourceDir;
    this.customerArchiveDir = customerArchiveDir;
    this.failedCustomerUpdateDir = failedCustomerUpdateDir;
    this.extServiceHost = extServiceHost;
    this.extServicePort = extServicePort;
    this.sapFtpCustomerHost = sapFtpCustomerHost;
    this.sapFtpCustomerUser = sapFtpCustomerUser;
    this.sapFtpCustomerPassword = sapFtpCustomerPassword;
    this.sapFtpCustomerDirectory = sapFtpCustomerDirectory;
    this.sapFtpCustomerArchive = sapFtpCustomerArchive;
    this.serviceAuth = serviceAuth;
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
   * Get the directory where generated invoices should be put
   *
   * @return directory path
   */
  public String getInvoiceDestDir() {
    return invoiceDestDir;
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
}
