package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import fi.hel.allu.common.domain.types.ApplicationKind;
import io.swagger.annotations.ApiModelProperty;

/**
 * Abstract base class for applications in external API
 *
 */
public abstract class ApplicationExt {

  private String name;
  private CustomerWithContactsExt customerWithContacts;
  private CustomerExt invoicingCustomer;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private boolean pendingOnClient;


  @ApiModelProperty(value="Name for the application")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value="Applicant of the application")
  public CustomerWithContactsExt getCustomerWithContacts() {
    return customerWithContacts;
  }

  public void setCustomerWithContacts(CustomerWithContactsExt customerWithContacts) {
    this.customerWithContacts = customerWithContacts;
  }

  @ApiModelProperty(value="Recipient of the invoice", allowEmptyValue = true)
  public CustomerExt getInvoicingCustomer() {
    return invoicingCustomer;
  }

  public void setInvoicingCustomer(CustomerExt invoicingCustomer) {
    this.invoicingCustomer = invoicingCustomer;
  }

  @ApiModelProperty(value = "Application location geometry")
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  @ApiModelProperty(value = "Start time of the application i.e. the starting time certain land area is reserved by application.")
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @ApiModelProperty(value = "End time of the application i.e. the time certain land area stops being reserved by the application")
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @ApiModelProperty(value = "Value indicating whether application is still pending on client side (and not yet ready to be handled in Allu)")
  public boolean isPendingOnClient() {
    return pendingOnClient;
  }

  public void setPendingOnClient(boolean pendingOnClient) {
    this.pendingOnClient = pendingOnClient;
  }
}
