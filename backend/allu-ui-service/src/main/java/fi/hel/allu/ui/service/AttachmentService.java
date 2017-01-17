package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.AttachmentInfoJson;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

  public AttachmentInfoJson updateAttachment(int id, AttachmentInfoJson attachmentInfoJson) {
    AttachmentInfo attachmentInfo = toAttachmentInfo(attachmentInfoJson);
    attachmentInfo.setUserId(userService.getCurrentUser().getId());
    HttpEntity<AttachmentInfo> requestEntity = new HttpEntity<>(attachmentInfo);
    ResponseEntity<AttachmentInfo> response = restTemplate.exchange(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_UPDATE), HttpMethod.PUT,
        requestEntity, AttachmentInfo.class, id);
    return toAttachmentInfoJson(response.getBody());
  }

  public AttachmentInfoJson getAttachment(int id) {
    AttachmentInfo response = restTemplate.getForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_FIND_BY_ID),
        AttachmentInfo.class, id);
    return toAttachmentInfoJson(response);
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

  public void deleteAttachment(int id) {
    restTemplate.delete(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_DELETE),
        id);
  }

  /**
   * Call model-service to add one attachment
   *
   * @param applicationId
   *          the owning application's ID
   * @param info
   *          The attachment info
   * @param data
   *          Attachment's data
   * @throws IOException
   * @throws RestClientException
   */
  private AttachmentInfoJson addAttachment(int applicationId, AttachmentInfoJson info, MultipartFile data)
      throws IOException {
    // Create the attachment info for model-service:
    AttachmentInfo toModel = toAttachmentInfo(info);
    toModel.setUserId(userService.getCurrentUser().getId());
    toModel.setApplicationId(applicationId);
    // Generate suitable multi-part request...
    MultiValueMap<String, Object> requestParts = new LinkedMultiValueMap<>();
    requestParts.add("info", toModel);
    requestParts.add("data", new ByteArrayResource(data.getBytes()) {
      @Override // return some filename so that Spring handles this as file
      public String getFilename() {
        return "data";
      }
    });
    HttpHeaders requestHeader = new HttpHeaders();
    requestHeader.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<?> requestEntity = new HttpEntity<>(requestParts, requestHeader);
    // ...then execute the request
    ResponseEntity<AttachmentInfo> response = restTemplate.exchange(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_CREATE), HttpMethod.POST,
        requestEntity, AttachmentInfo.class);
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

  // convert AttachmentInfoJson --> AttachmentInfo
  private AttachmentInfo toAttachmentInfo(AttachmentInfoJson attachmentInfoJson) {
    AttachmentInfo result = new AttachmentInfo();
    result.setId(attachmentInfoJson.getId());
    result.setType(attachmentInfoJson.getType());
    result.setName(attachmentInfoJson.getName());
    result.setDescription(attachmentInfoJson.getDescription());
    result.setCreationTime(attachmentInfoJson.getCreationTime());
    result.setSize(attachmentInfoJson.getSize());
    return result;
  }

  // convert AttachmentInfo --> AttachmentInfoJson
  private AttachmentInfoJson toAttachmentInfoJson(AttachmentInfo attachmentInfo) {
    AttachmentInfoJson result = new AttachmentInfoJson();
    result.setId(attachmentInfo.getId());
    result.setHandlerName(userService.getCurrentUser().getRealName());
    result.setType(attachmentInfo.getType());
    result.setName(attachmentInfo.getName());
    result.setDescription(attachmentInfo.getDescription());
    result.setCreationTime(attachmentInfo.getCreationTime());
    result.setSize(attachmentInfo.getSize());
    return result;
  }
}
