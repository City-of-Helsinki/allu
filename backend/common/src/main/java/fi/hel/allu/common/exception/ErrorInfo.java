package fi.hel.allu.common.exception;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;

@Schema(name = "Error information")
public class ErrorInfo {
  private String errorMessage;
  private String additionalInfo;

  @SchemaProperty(name = "Error message")
  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  @SchemaProperty(name = "Additional information, typically name of the field causing validation error.")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }
}

