package fi.hel.allu.common.controller.handler;

public class ControllerExceptionHandlerConfig {
  private boolean translateErrorMessages = false;
  private boolean translateValidationMessages = false;

  public boolean isTranslateErrorMessages() {
    return translateErrorMessages;
  }

  public void setTranslateErrorMessages(boolean translateErrorMessages) {
    this.translateErrorMessages = translateErrorMessages;
  }

  public boolean isTranslateValidationMessages() {
    return translateValidationMessages;
  }

  public void setTranslateValidationMessages(boolean translateValidationMessages) {
    this.translateValidationMessages = translateValidationMessages;
  }

}
