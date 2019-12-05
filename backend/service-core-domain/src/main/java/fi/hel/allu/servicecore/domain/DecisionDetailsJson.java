package fi.hel.allu.servicecore.domain;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class DecisionDetailsJson {
  @NotEmpty(message = "{decision.distribution}")
  private List<DistributionEntryJson> decisionDistributionList;
  private String messageBody;

  /**
   * @return  Distribution list of the decision.
   */
  public List<DistributionEntryJson> getDecisionDistributionList() {
    return decisionDistributionList;
  }

  public void setDecisionDistributionList(List<DistributionEntryJson> decisionDistributionList) {
    this.decisionDistributionList = decisionDistributionList;
  }

  /**
   * Get the mail body
   */
  public String getMessageBody() {
    return messageBody;
  }

  public void setMessageBody(String messageBody) {
    this.messageBody = messageBody;
  }
}
