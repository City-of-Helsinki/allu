package fi.hel.allu.servicecore.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.DefaultAttachmentInfo;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.AttachmentInfoJson;
import fi.hel.allu.servicecore.domain.DefaultAttachmentInfoJson;

@Service
public class AttachmentService {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;

  @Autowired
  public AttachmentService(ApplicationProperties applicationProperties, RestTemplate restTemplate, UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
  }

  public List<AttachmentInfoJson> addAttachments(int id, AttachmentInfoJson[] infos, MultipartFile[] files)
      throws IOException, IllegalArgumentException {
    if (infos.length != files.length) {
      throw new IllegalArgumentException("Argument length mismatch: Different amount of infos and files.");
    }
    List<AttachmentInfoJson> result = new ArrayList<>();
    for (int i = 0; i < infos.length; ++i) {
      result.add(addAttachment(id, infos[i], files[i]));
    }
    return result;
  }

  /**
   * Adds default attachents. Note that the given lists should match by index i.e. info with index i should describe binary with index i.
   *
   * @param infos   Attachment information.
   * @param files   Binaries to be attached.
   * @return
   * @throws IOException
   * @throws IllegalArgumentException In case <code>infos</code> size is not equal to <code>files</code> size.
   */
  public List<DefaultAttachmentInfoJson> addDefaultAttachments(DefaultAttachmentInfoJson[] infos, MultipartFile[] files)
      throws IOException, IllegalArgumentException {
    if (infos.length != files.length) {
      throw new IllegalArgumentException("Argument length mismatch: Different amount of infos and files.");
    }
    List<DefaultAttachmentInfoJson> result = new ArrayList<>();
    for (int i = 0; i < infos.length; ++i) {
      result.add(addDefaultAttachment(infos[i], files[i]));
    }
    return result;
  }

  public AttachmentInfoJson updateAttachment(int id, AttachmentInfoJson attachmentInfoJson) {
    return updateAttachmentCommon(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_UPDATE),
        id,
        attachmentInfoJson);
  }

  /**
   * Updates default attachment info.
   *
   * @param id                          Id of the default attachment to be updated.
   * @param defaultAttachmentInfoJson   Updated info.
   * @return  Updated info.
   */
  public DefaultAttachmentInfoJson updateDefaultAttachment(int id, DefaultAttachmentInfoJson defaultAttachmentInfoJson) {
    return updateAttachmentCommon(
        applicationProperties.getUpdateDefaultAttachmentUrl(),
        id,
        defaultAttachmentInfoJson);
  }

  public DefaultAttachmentInfoJson updateAttachmentCommon(String url, int id, AttachmentInfoJson attachmentInfoJson) {
    DefaultAttachmentInfo attachmentInfo = toAttachmentInfo(attachmentInfoJson);
    attachmentInfo.setUserId(userService.getCurrentUser().getId());
    HttpEntity<AttachmentInfo> requestEntity = new HttpEntity<>(attachmentInfo);
    ResponseEntity<AttachmentInfo> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, AttachmentInfo.class, id);
    return toAttachmentInfoJson(response.getBody());
  }

  public AttachmentInfoJson getAttachment(int id) {
    return getAttachmentCommon(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_FIND_BY_ID), id);
  }

  /**
   * Returns default attachment with given id.
   *
   * @param   id    Id of the default attachment.
   * @return  default attachment with given id.
   */
  public DefaultAttachmentInfoJson getDefaultAttachment(int id) {
    return getAttachmentCommon(applicationProperties.getDefaultAttachmentInfoUrl(), id);
  }

  /**
   * Returns all default attachments.
   *
   * @return  all default attachments.
   */
  public List<DefaultAttachmentInfoJson> getDefaultAttachments() {
    DefaultAttachmentInfo[] response =
        restTemplate.getForObject(applicationProperties.getAllDefaultAttachmentInfoUrl(), DefaultAttachmentInfo[].class);
    return Arrays.stream(response).map(info -> toAttachmentInfoJson(info)).collect(Collectors.toList());
  }

  /**
   * Returns all default attachments for given application type.
   *
   * @return  all default attachments.
   */
  public List<DefaultAttachmentInfoJson> getDefaultAttachmentsByApplicationType(ApplicationType applicationType) {
    DefaultAttachmentInfo[] response =
        restTemplate.getForObject(
            applicationProperties.getDefaultAttachmentInfoByApplicationTypeUrl(),
            DefaultAttachmentInfo[].class,
            applicationType);
    return Arrays.stream(response).map(info -> toAttachmentInfoJson(info)).collect(Collectors.toList());
  }


  public List<AttachmentInfoJson> findAttachmentsForApplication(Integer applicationId) {
    List<AttachmentInfoJson> resultList = new ArrayList<>();
    ResponseEntity<AttachmentInfo[]> attachmentResult = restTemplate.getForEntity(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION),
        AttachmentInfo[].class,
        applicationId);
    for (AttachmentInfo attachmentInfo : attachmentResult.getBody()) {
      AttachmentInfoJson attachmentInfoJson = toAttachmentInfoJson(attachmentInfo);
      resultList.add(attachmentInfoJson);
    }
    return resultList;
  }

  public void deleteAttachment(int applicationId, int attachmentId) {
    restTemplate.delete(applicationProperties.getDeleteAttachmentUrl(), applicationId, attachmentId);
  }

  public void deleteDefaultAttachment(int id) {
    restTemplate.delete(applicationProperties.getDeleteDefaultAttachmentUrl(), id);
  }

  private DefaultAttachmentInfoJson getAttachmentCommon(String url, int id) {
    DefaultAttachmentInfo response = restTemplate.getForObject(url, DefaultAttachmentInfo.class, id);
    return toAttachmentInfoJson(response);
  }

  /**
   * Call model-service to add one attachment
   *
   * @param applicationId   the owning application's ID
   * @param info            The attachment info
   * @param data            Attachment's data
   * @throws IOException
   * @throws RestClientException
   */
  private AttachmentInfoJson addAttachment(int applicationId, AttachmentInfoJson info, MultipartFile data)
      throws IOException {
    // Create the attachment info for model-service:
    AttachmentInfo toModel = toAttachmentInfo(info);
    toModel.setUserId(userService.getCurrentUser().getId());
    HttpEntity<?> requestEntity = createMultipartRequest(toModel, data);
    // ...then execute the request
    ResponseEntity<AttachmentInfo> response = restTemplate.exchange(
        applicationProperties.getAddAttachmentUrl(), HttpMethod.POST, requestEntity, AttachmentInfo.class, applicationId);
    return toAttachmentInfoJson(response.getBody());
  }

  /**
   * Call model-service to add one default attachment
   *
   * @param info            The attachment info
   * @param data            Attachment's data
   * @throws IOException
   */
  private DefaultAttachmentInfoJson addDefaultAttachment(DefaultAttachmentInfoJson info, MultipartFile data)
      throws IOException {
    // Create the attachment info for model-service:
    DefaultAttachmentInfo toModel = toAttachmentInfo(info);
    toModel.setUserId(userService.getCurrentUser().getId());
    HttpEntity<?> requestEntity = createMultipartRequest(toModel, data);
    // ...then execute the request
    ResponseEntity<DefaultAttachmentInfo> response = restTemplate.exchange(
        applicationProperties.getAddDefaultAttachmentUrl(),
        HttpMethod.POST,
        requestEntity,
        DefaultAttachmentInfo.class);
    return toAttachmentInfoJson(response.getBody());
  }

  /**
   * Get the attachment's data
   *
   * @param attachmentId
   *          attachment's ID
   * @return The attachment's data
   */
  public byte[] getAttachmentData(int attachmentId) {
    return restTemplate.getForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_GET_DATA), byte[].class,
        attachmentId);
  }

  /**
   * Gets size of attachment data (bytes)
   * @param attachmentId attachment's ID
   * @return size in bytes
   */
  public Long getAttachmentSize(int attachmentId) {
    return restTemplate.getForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_GET_SIZE), Long.class,
        attachmentId);
  }

  private HttpEntity<?> createMultipartRequest(AttachmentInfo info, MultipartFile data)
      throws IOException {
    // Generate suitable multi-part request...
    MultiValueMap<String, Object> requestParts = new LinkedMultiValueMap<>();
    requestParts.add("info", info);
    requestParts.add("data", new ByteArrayResource(data.getBytes()) {
      @Override // return some filename so that Spring handles this as file
      public String getFilename() {
        return "data";
      }
    });
    HttpHeaders requestHeader = new HttpHeaders();
    requestHeader.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<?> requestEntity = new HttpEntity<>(requestParts, requestHeader);
    return requestEntity;
  }

  // convert (Default)AttachmentInfoJson --> (Default)AttachmentInfo
  private DefaultAttachmentInfo toAttachmentInfo(AttachmentInfoJson attachmentInfoJson) {
    DefaultAttachmentInfo result = new DefaultAttachmentInfo();
    result.setId(attachmentInfoJson.getId());
    result.setType(attachmentInfoJson.getType());
    result.setMimeType(attachmentInfoJson.getMimeType());
    result.setName(attachmentInfoJson.getName());
    result.setDescription(attachmentInfoJson.getDescription());
    result.setCreationTime(attachmentInfoJson.getCreationTime());
    result.setDecisionAttachment(attachmentInfoJson.isDecisionAttachment());
    if (attachmentInfoJson instanceof DefaultAttachmentInfoJson) {
      DefaultAttachmentInfoJson defaultAttachmentInfoJson = (DefaultAttachmentInfoJson) attachmentInfoJson;
      result.setDefaultAttachmentId(defaultAttachmentInfoJson.getDefaultAttachmentId());
      result.setApplicationTypes(defaultAttachmentInfoJson.getApplicationTypes());
      result.setFixedLocationAreaId(defaultAttachmentInfoJson.getFixedLocationId());
      result.setDecisionAttachment(true);
    }
    return result;
  }

  // convert (Default)AttachmentInfo --> (Default)AttachmentInfoJson
  private DefaultAttachmentInfoJson toAttachmentInfoJson(AttachmentInfo attachmentInfo) {
    DefaultAttachmentInfoJson result = new DefaultAttachmentInfoJson();
    result.setId(attachmentInfo.getId());
    result.setHandlerName(userService.getCurrentUser().getRealName());
    result.setType(attachmentInfo.getType());
    result.setMimeType(attachmentInfo.getMimeType());
    result.setName(attachmentInfo.getName());
    result.setDescription(attachmentInfo.getDescription());
    result.setCreationTime(attachmentInfo.getCreationTime());
    result.setSize(getAttachmentSize(attachmentInfo.getId()));
    result.setDecisionAttachment(attachmentInfo.isDecisionAttachment());
    if (attachmentInfo instanceof DefaultAttachmentInfo) {
      DefaultAttachmentInfo defaultAttachmentInfo = (DefaultAttachmentInfo) attachmentInfo;
      result.setDefaultAttachmentId(defaultAttachmentInfo.getDefaultAttachmentId());
      result.setApplicationTypes(defaultAttachmentInfo.getApplicationTypes());
      result.setFixedLocationId(defaultAttachmentInfo.getFixedLocationAreaId());
    }
    return result;
  }
}
