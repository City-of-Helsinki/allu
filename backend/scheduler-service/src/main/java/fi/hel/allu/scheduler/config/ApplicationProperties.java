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

  @Autowired
  public ApplicationProperties(@Value("${model.service.host}") @NotEmpty String modelServiceHost,
                               @Value("${model.service.port}") @NotEmpty String modelServicePort,
                               @Value("#{'${email.allowed.addresses:}'.split(',')}") List<String> emailAllowedAddresses,
                               @Value("${email.sender.address}") @NotEmpty String emailSenderAddress) {
    this.modelServiceHost = modelServiceHost;
    this.modelServicePort = modelServicePort;
    this.emailAllowedAddresses = emailAllowedAddresses;
    this.emailSenderAddress = emailSenderAddress;
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
}
