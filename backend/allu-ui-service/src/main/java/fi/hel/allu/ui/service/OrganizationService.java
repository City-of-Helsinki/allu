package fi.hel.allu.ui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.model.domain.Organization;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.OrganizationJson;
import fi.hel.allu.ui.domain.PostalAddressJson;

@Service
public class OrganizationService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

  private ApplicationProperties applicationProperties;

  private RestTemplate restTemplate;

  @Autowired
  public OrganizationService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Create a new organization.
   *
   * @param organizationJson Organization that is going to be created
   * @return Created organization
   */
  public OrganizationJson createOrganization(OrganizationJson organizationJson) {
    if (organizationJson != null && organizationJson.getId() == null) {
      Organization organizationModel = restTemplate.postForObject(applicationProperties
              .getModelServiceUrl(ApplicationProperties.PATH_MODEL_ORGANIZATION_CREATE), createOrganizationModel(organizationJson),
          Organization.class);
      mapOrganizationToJson(organizationJson, organizationModel);
    }
    return organizationJson;
  }

  /**
   * Update the given organization. Organization id is needed to update the given organization.
   *
   * @param organizationJson Organization that is going to be updated
   */
  public void updateOrganization(OrganizationJson organizationJson) {
    if (organizationJson != null && organizationJson.getId() != null && organizationJson.getId() > 0) {
      restTemplate.put(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ORGANIZATION_UPDATE), createOrganizationModel
          (organizationJson), organizationJson.getId().intValue());
    }
  }

  /**
   * Find given organization details.
   *
   * @param organizationId Organization identifier that is used to find details
   * @return Organization details or empty organization object
   */
  public OrganizationJson findOrganizationById(int organizationId) {
    OrganizationJson organizationJson = new OrganizationJson();
    ResponseEntity<Organization> organizationResult = restTemplate.getForEntity(applicationProperties
        .getModelServiceUrl(ApplicationProperties.PATH_MODEL_ORGANIZATION_FIND_BY_ID), Organization.class, organizationId);
    mapOrganizationToJson(organizationJson, organizationResult.getBody());
    return organizationJson;
  }


  private Organization createOrganizationModel(OrganizationJson organizationJson) {
    Organization organizationModel = new Organization();
    if (organizationJson.getId() != null) {
      organizationModel.setId(organizationJson.getId());
    }
    if (organizationJson.getPostalAddress() != null) {
      organizationModel.setPostalCode(organizationJson.getPostalAddress().getPostalCode());
      organizationModel.setStreetAddress(organizationJson.getPostalAddress().getStreetAddress());
      organizationModel.setCity(organizationJson.getPostalAddress().getCity());
    }
    organizationModel.setPhone(organizationJson.getPhone());
    organizationModel.setName(organizationJson.getName());
    organizationModel.setBusinessId(organizationJson.getBusinessId());
    organizationModel.setEmail(organizationJson.getEmail());
    return organizationModel;
  }

  private void mapOrganizationToJson(OrganizationJson organizationJson, Organization organization) {
    organizationJson.setId(organization.getId());
    PostalAddressJson postalAddressJson = new PostalAddressJson();
    postalAddressJson.setStreetAddress(organization.getStreetAddress());
    postalAddressJson.setPostalCode(organization.getPostalCode());
    postalAddressJson.setCity(organization.getCity());
    organizationJson.setPostalAddress(postalAddressJson);
    organizationJson.setPhone(organization.getPhone());
    organizationJson.setName(organization.getName());
    organizationJson.setEmail(organization.getEmail());
    organizationJson.setBusinessId(organization.getBusinessId());
  }
}
