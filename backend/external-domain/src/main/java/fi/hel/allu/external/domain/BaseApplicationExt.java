package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import io.swagger.annotations.ApiModelProperty;

/**
 * Abstract base class for applications in external API
 *
 */
public abstract class BaseApplicationExt {

  private PostalAddressExt postalAddress;
  @NotBlank(message = "{application.name}")
  private String name;
  @NotNull(message = "{application.customersWithContacts}")
  @Valid
  private CustomerWithContactsExt customerWithContacts;
  @Valid
  private CustomerWithContactsExt representativeWithContacts;
  @Valid
  private CustomerExt invoicingCustomer;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  @NotNull(message = "{application.geometry.missing}")
  private Geometry geometry;
  @NotNull(message = "{application.starttime}")
  private ZonedDateTime startTime;
  @NotNull(message = "{application.endtime}")
  private ZonedDateTime endTime;
  @NotNull(message = "{application.pendingOnClient}")
  private Boolean pendingOnClient;
  @NotBlank(message = "{application.identificationNumber}")
  private String identificationNumber;
  private String customerReference;
  private Double area;
  private List<Integer> trafficArrangementImages = new ArrayList<>();

  @ApiModelProperty(value = "Postal address")
  public PostalAddressExt getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressExt postalAddress) {
    this.postalAddress = postalAddress;
  }

  @ApiModelProperty(value="Name for the application", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value="Applicant (hakija) of the application", required = true)
  public CustomerWithContactsExt getCustomerWithContacts() {
    return customerWithContacts;
  }

  public void setCustomerWithContacts(CustomerWithContactsExt customerWithContacts) {
    this.customerWithContacts = customerWithContacts;
  }

  @ApiModelProperty(value = "Representative (asiamies) of the customer")
  public CustomerWithContactsExt getRepresentativeWithContacts() {
    return representativeWithContacts;
  }

  public void setRepresentativeWithContacts(CustomerWithContactsExt representativeWithContacts) {
    this.representativeWithContacts = representativeWithContacts;
  }


  @ApiModelProperty(value="Recipient of the invoice")
  public CustomerExt getInvoicingCustomer() {
    return invoicingCustomer;
  }

  public void setInvoicingCustomer(CustomerExt invoicingCustomer) {
    this.invoicingCustomer = invoicingCustomer;
  }

  @ApiModelProperty(value =
      "Application geometry in <a href=\"https://tools.ietf.org/html/rfc7946\">GeoJSON</a> with following limitations:"
      +"<ul>"
      +"<li>Feature / FeatureCollection is currently not supported, geometry should be given as <a href=\"https://tools.ietf.org/html/rfc7946#section-3.1.8\">GeometryCollection</a>.</li>"
     + "<li>Only named CRS is supported, the given name must either be of the form: urn:ogc:def:crs:EPSG:x.y:4326 (x.y: the version of the EPSG) or of the form EPSG:4326</li>"
      +"</ul>",
  required = true)
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  @ApiModelProperty(value = "Start time of the application i.e. the starting time certain land area is reserved by application.", required = true)
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @ApiModelProperty(value = "End time of the application i.e. the time certain land area stops being reserved by the application", required = true)
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @ApiModelProperty(value = "Value indicating whether application is still pending on client side (and not yet ready to be handled in Allu)", required = true)
  public boolean isPendingOnClient() {
    return pendingOnClient;
  }

  public void setPendingOnClient(boolean pendingOnClient) {
    this.pendingOnClient = pendingOnClient;
  }

  @ApiModelProperty(value = "Identification number (in Finnish: asiointunnus)", required = true)
  public String getIdentificationNumber() {
    return identificationNumber;
  }

  public void setIdentificationNumber(String identificationNumber) {
    this.identificationNumber = identificationNumber;
  }

  @ApiModelProperty(value = "Customer reference to the invoice")
  public String getCustomerReference() {
    return customerReference;
  }

  public void setCustomerReference(String customerReference) {
    this.customerReference = customerReference;
  }

  @ApiModelProperty(value = "Area in square meters")
  public Double getArea() {
    return area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  @ApiModelProperty(value = "Traffic arrangement image (tyyppikuva) IDs selected for application")
  public List<Integer> getTrafficArrangementImages() {
    return trafficArrangementImages;
  }

  public void setTrafficArrangementImages(List<Integer> trafficArrangementImages) {
    this.trafficArrangementImages = trafficArrangementImages;
  }

}
