package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.AttributeMetaJson;
import fi.hel.allu.ui.domain.StructureMetaJson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

/**
 * Service for accessing metadata.
 */
@Service
public class MetaService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;

  @Autowired
  public MetaService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Retrieves the metadata for given application type.
   *
   * @param   applicationType   Type of the metadata to be retrieved.
   * @return  the metadata for given application type.
   */
  public StructureMetaJson findMetadataForApplication(ApplicationType applicationType) {
    ResponseEntity<StructureMeta> structureMetaResult = restTemplate.getForEntity(
        applicationProperties.getMetadataUrl(), StructureMeta.class, applicationType);
    return mapStructureMeta(structureMetaResult.getBody());
  }

  /**
   * Retrieves the metadata for given application type and version.
   *
   * @param   applicationType   Type of the metadata to be retrieved.
   * @param   version           Version of metadata to be retrieved.
   * @return  the metadata for given application type.
   */
  public StructureMetaJson findMetadataForApplication(ApplicationType applicationType, int version) {
    ResponseEntity<StructureMeta> structureMetaResult = restTemplate.getForEntity(
        applicationProperties.getMetadataUrl(), StructureMeta.class, applicationType);
    return mapStructureMeta(structureMetaResult.getBody());
  }

  private StructureMetaJson mapStructureMeta(StructureMeta structureMeta) {
    StructureMetaJson structureMetaJson = new StructureMetaJson();
    structureMetaJson.setApplicationType(structureMeta.getApplicationType());
    structureMetaJson.setVersion(structureMeta.getVersion());
    structureMetaJson.setAttributes(structureMeta.getAttributes().stream().map(a -> mapAttribute(a)).collect(Collectors.toList()));
    return structureMetaJson;
  }

  private AttributeMetaJson mapAttribute(AttributeMeta attribute) {
    AttributeMetaJson attributeMetaJson = new AttributeMetaJson();
    attributeMetaJson.setDataType(attribute.getDataType());
    attributeMetaJson.setListType(attribute.getListType());
    attributeMetaJson.setName(attribute.getName());
    attributeMetaJson.setUiName(attribute.getUiName());
    attributeMetaJson.setValidationRule(attribute.getValidationRule());
    if (attribute.getStructureMeta() != null) {
      attributeMetaJson.setStructureMeta(mapStructureMeta(attribute.getStructureMeta()));
    } else {
      attributeMetaJson.setStructureMeta(null);
    }
    return attributeMetaJson;
  }
}
