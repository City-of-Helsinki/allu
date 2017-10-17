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
  private List<String> emailAllowedAddresses;
  private String emailSenderAddress;
  private String invoiceDestDir;

  @Autowired
  public ApplicationProperties(@Value("${model.service.host}") @NotEmpty String modelServiceHost,
                               @Value("${model.service.port}") @NotEmpty String modelServicePort,
                               @Value("#{'${email.allowed.addresses:}'.split(',')}") List<String> emailAllowedAddresses,
      @Value("${email.sender.address}") @NotEmpty String emailSenderAddress,
      @Value("${invoice.destdir}") @NotEmpty String invoiceDestDir) {
    this.modelServiceHost = modelServiceHost;
    this.modelServicePort = modelServicePort;
    this.emailAllowedAddresses = emailAllowedAddresses;
    this.emailSenderAddress = emailSenderAddress;
    this.invoiceDestDir = invoiceDestDir;
  }

  private static final String PATH_PREFIX = "http://";

  private String getModelServiceUrl(String path) {
    return PATH_PREFIX + modelServiceHost + ":" + modelServicePort + path;
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
}
