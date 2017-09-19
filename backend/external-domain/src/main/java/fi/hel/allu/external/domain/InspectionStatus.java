package fi.hel.allu.external.domain;

public class InspectionStatus {
  private State state;
  private String comment;

  public InspectionStatus() {
    // For deserialization
  }

  public InspectionStatus(State state, String comment) {
    this.state = state;
    this.comment = comment;
  }

  public enum State {
    NOT_INSPECTED, ACCEPTED, REJECTED
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  };
}
