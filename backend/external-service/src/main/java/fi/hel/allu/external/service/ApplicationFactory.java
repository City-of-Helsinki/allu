package fi.hel.allu.external.service;

import java.util.Collections;

import org.geolatte.geom.Geometry;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.PublicityType;
import fi.hel.allu.external.domain.ApplicationExt;
import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.external.domain.PostalAddressExt;
import fi.hel.allu.external.mapper.CustomerExtMapper;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ClientApplicationDataJson;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.domain.PlacementContractJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;

public class ApplicationFactory {

  public static ApplicationJson fromPlacementContractExt(PlacementContractExt placementContract, Integer externalOwnerId) {

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setType(ApplicationType.PLACEMENT_CONTRACT);
    setCommonApplicationFields(placementContract, externalOwnerId, applicationJson);
    applicationJson.setLocations(Collections.singletonList(createLocation(placementContract, placementContract.getGeometry(), placementContract.getPostalAddress())));

    ClientApplicationDataJson clientApplicationData = new ClientApplicationDataJson();
    clientApplicationData.setCustomer(CustomerExtMapper.mapCustomerWithContactsJson(placementContract.getCustomerWithContacts()));
    clientApplicationData.setInvoicingCustomer(CustomerExtMapper.mapCustomerJson(placementContract.getInvoicingCustomer()));
    clientApplicationData.setClientApplicationKind(placementContract.getClientApplicationKind());
    applicationJson.setClientApplicationData(clientApplicationData);

    PlacementContractJson extension = new PlacementContractJson();
    extension.setPropertyIdentificationNumber(placementContract.getPropertyIdentificationNumber());
    extension.setAdditionalInfo(placementContract.getWorkDescription());
    applicationJson.setExtension(extension);

    return applicationJson;
  }


  private static <T extends ApplicationExt> void setCommonApplicationFields(T applicationExt, Integer externalOwnerId,
      ApplicationJson applicationJson) {
    applicationJson.setName(applicationExt.getName());
    applicationJson.setStartTime(applicationExt.getStartTime());
    applicationJson.setEndTime(applicationExt.getEndTime());
    applicationJson.setName(applicationExt.getName());
    applicationJson.setDecisionPublicityType(PublicityType.PUBLIC);
    applicationJson.setNotBillable(Boolean.FALSE);
    applicationJson.setIdentificationNumber(applicationExt.getIdentificationNumber());
    applicationJson.setExternalOwnerId(externalOwnerId);
    applicationJson.setCustomerReference(applicationExt.getCustomerReference());
  }

  private static LocationJson createLocation(ApplicationExt application, Geometry geometry, PostalAddressExt postalAddress) {
    LocationJson location = new LocationJson();
    location.setGeometry(geometry);
    location.setPostalAddress(postalAddress != null ? new PostalAddressJson(postalAddress.getStreetAddress(), postalAddress.getPostalCode(),postalAddress.getCity()) : null);
    location.setStartTime(application.getStartTime());
    location.setEndTime(application.getEndTime());
    return location;

  }

}
