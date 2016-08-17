package fi.hel.allu.ui.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.AttachmentInfoJson;

@Service
public class AttachmentService {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;

  @Autowired
  public AttachmentService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  public AttachmentInfoJson addAttachment(AttachmentInfoJson attachmentInfoJson) {
    AttachmentInfo response = restTemplate.postForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_CREATE),
        toAttachmentInfo(attachmentInfoJson), AttachmentInfo.class);
    return toAttachmentInfoJson(response);
  }

  public AttachmentInfoJson updateAttachment(int id, AttachmentInfoJson attachmentInfoJson) {
    HttpEntity<AttachmentInfo> requestEntity = new HttpEntity<>(toAttachmentInfo(attachmentInfoJson));
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

  public void deleteAttachment(int id) {
    restTemplate.delete(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_DELETE),
        id);
  }

  /**
   * Set attachment's data
   *
   * @param attachmentId
   * @param data
   * @throws IOException
   * @throws RestClientException
   */
  public void setAttachmentData(@PathVariable int attachmentId, MultipartFile data)
      throws IOException {
    restTemplate.postForObject(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_SET_DATA), data.getBytes(),
        String.class, attachmentId);
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
    result.setApplicationId(null);
    result.setId(attachmentInfoJson.getId());
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
    result.setName(attachmentInfo.getName());
    result.setDescription(attachmentInfo.getDescription());
    result.setCreationTime(attachmentInfo.getCreationTime());
    result.setSize(attachmentInfo.getSize());
    return result;
  }
}
