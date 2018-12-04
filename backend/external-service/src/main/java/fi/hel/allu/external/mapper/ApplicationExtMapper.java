package fi.hel.allu.external.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.geolatte.geom.Geometry;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.types.PublicityType;
import fi.hel.allu.external.domain.ApplicationExt;
import fi.hel.allu.external.domain.BaseApplicationExt;
import fi.hel.allu.external.domain.PostalAddressExt;
import fi.hel.allu.external.domain.UserExt;
import fi.hel.allu.servicecore.domain.*;

public abstract class ApplicationExtMapper<T extends BaseApplicationExt> {

  protected abstract ApplicationExtensionJson createExtension(T application);
  protected abstract ApplicationType getApplicationType();

  /**
   * Creates ApplicationJson from ext application and sets data common to all application types
   */
  public ApplicationJson mapExtApplication(T application, Integer externalOwnerId) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setType(getApplicationType());
    applicationJson.setName(application.getName());
    applicationJson.setStartTime(application.getStartTime());
    applicationJson.setEndTime(application.getEndTime());
    applicationJson.setName(application.getName());
    applicationJson.setDecisionPublicityType(PublicityType.PUBLIC);
    applicationJson.setNotBillable(Boolean.FALSE);
    applicationJson.setIdentificationNumber(application.getIdentificationNumber());
    applicationJson.setExternalOwnerId(externalOwnerId);
    applicationJson.setCustomerReference(application.getCustomerReference());
    applicationJson.setLocations(Collections.singletonList(createLocation(application, application.getGeometry(), application.getPostalAddress())));
    applicationJson.setExtension(createExtension(application));
    applicationJson.setClientApplicationData(createClientApplicationData(application));
    applicationJson.setKind(getApplicationKind(application));
    return applicationJson;
  }

  private ClientApplicationDataJson createClientApplicationData(T application) {
    ClientApplicationDataJson clientApplicationData = new ClientApplicationDataJson();
    clientApplicationData.setCustomer(CustomerExtMapper.mapCustomerWithContactsJson(application.getCustomerWithContacts(), CustomerRoleType.APPLICANT));
    clientApplicationData.setInvoicingCustomer(CustomerExtMapper.mapInvoicingCustomerJson(application.getInvoicingCustomer()));
    clientApplicationData.setRepresentative(CustomerExtMapper.mapCustomerWithContactsJson(application.getRepresentativeWithContacts(), CustomerRoleType.REPRESENTATIVE));
    clientApplicationData.setClientApplicationKind(getClientApplicationKind(application));
    addApplicationTypeSpecificData(application, clientApplicationData);
    return clientApplicationData;
  }

  protected void addApplicationTypeSpecificData(T application, ClientApplicationDataJson clientApplicationData) {
  }

  private LocationJson createLocation(T application, Geometry geometry, PostalAddressExt postalAddress) {
    LocationJson location = new LocationJson();
    location.setGeometry(geometry);
    location.setPostalAddress(postalAddress != null ? new PostalAddressJson(postalAddress.getStreetAddressAsString(), postalAddress.getPostalCode(),postalAddress.getCity()) : null);
    location.setStartTime(application.getStartTime());
    location.setEndTime(application.getEndTime());
    location.setFixedLocationIds(getFixedLocationIds(application));
    location.setAreaOverride(getArea(application));
    return location;
  }

  protected Double getArea(T application) {
    return application.getArea();
  }

  protected ApplicationKind getApplicationKind(T application) {
    return null;
  }

  protected String getClientApplicationKind(T application) {
    return null;
  }

  protected List<Integer> getFixedLocationIds(T application) {
    return null;
  }

  public static ApplicationExt mapToApplicationExt(ApplicationJson application) {
    ApplicationExt applicationExt = new ApplicationExt();
    applicationExt.setName(application.getName());
    applicationExt.setApplicationId(application.getApplicationId());
    applicationExt.setStartTime(application.getStartTime());
    applicationExt.setEndTime(application.getEndTime());
    applicationExt.setId(application.getExternalApplicationId());
    Optional.ofNullable(application.getOwner()).ifPresent(o -> applicationExt.setOwner(new UserExt(o.getRealName(), o.getTitle())));
    applicationExt.setStatus(application.getStatus());
    applicationExt.setKindsWithSpecifiers(application.getKindsWithSpecifiers());
    applicationExt.setTerms(application.getExtension().getTerms());
    return applicationExt;
  }

}
