package fi.hel.allu.servicecore.domain;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class DecisionDetailsJson {
  @NotEmpty(message = "{decision.distribution}")
  private List<DistributionEntryJson> decisionDistributionList;
  private String messageBody;

  public DecisionDetailsJson() {
  }

  public DecisionDetailsJson(List<DistributionEntryJson> decisionDistributionList) {
    this.decisionDistributionList = decisionDistributionList;
  }

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
