package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.AttributeMetaJson;
import fi.hel.allu.servicecore.domain.StructureMetaJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for accessing metadata.
 */
@Service
public class MetaService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;

  private static final String APPLICATION = "Application";
  private static final String PROJECT = "Project";
  private static final String EXTENSION_PATH = "/extension";

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
    Map<String, String> overrides = Collections.singletonMap(EXTENSION_PATH, applicationType.name());
    return findMetadataFor(APPLICATION, overrides);
  }

  /**
   * Retrieves the metadata for given application type and version.
   *
   * @param   applicationType   Type of the metadata to be retrieved.
   * @param   version           Version of metadata to be retrieved.
   * @return  the metadata for given application type.
   */
  public StructureMetaJson findMetadataForApplication(ApplicationType applicationType, int version) {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION_PATH, applicationType.name());
    return findMetadataFor(APPLICATION, version, overrides);
  }

  public StructureMetaJson findMetadataFor(String type) {
    return findMetadataFor(type, Collections.EMPTY_MAP);
  }

  public StructureMetaJson findMetadataFor(String type, Map<String, String> pathOverrides) {
    ResponseEntity<StructureMeta> structureMetaResult = restTemplate.postForEntity(
        applicationProperties.getMetadataUrl(), pathOverrides, StructureMeta.class, type);
    return mapStructureMeta(structureMetaResult.getBody());
  }

  public StructureMetaJson findMetadataFor(String type, Integer version, Map<String, String> pathOverrides) {
    ResponseEntity<StructureMeta> structureMetaResult = restTemplate.postForEntity(
        applicationProperties.getMetadataVersionedUrl(), pathOverrides, StructureMeta.class, type, version);
    return mapStructureMeta(structureMetaResult.getBody());
  }

  public String findTranslation(String type, String text) {
    ResponseEntity<String> result = restTemplate.getForEntity(
        applicationProperties.getMetadataTranslationUrl(), String.class, type, text);
    return result.getBody();
  }

  private StructureMetaJson mapStructureMeta(StructureMeta structureMeta) {
    StructureMetaJson structureMetaJson = new StructureMetaJson();
    structureMetaJson.setTypeName(structureMeta.getTypeName());
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
    return attributeMetaJson;
  }
}
