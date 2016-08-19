package fi.hel.allu.ui.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

  public List<AttachmentInfoJson> addAttachments(int id, AttachmentInfoJson[] infos, MultipartFile[] files)
      throws IOException, IllegalArgumentException {
    if (infos.length != files.length) {
      throw new IllegalArgumentException("Argument length mismatch: Different amount of infos and files.");
    }
    List<AttachmentInfoJson> result = new ArrayList<>();
    for (int i = 0; i < infos.length; ++i) {
      // Create the attachment info, receive attachment ID in response
      AttachmentInfo toModel = toAttachmentInfo(infos[i]);
      toModel.setApplicationId(id);
      AttachmentInfo response = restTemplate.postForObject(
          applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_CREATE), toModel,
          AttachmentInfo.class);
      // Set the data for the created attachment info.
      setAttachmentData(response.getId(), files[i]);
      result.add(toAttachmentInfoJson(response));
    }
    return result;
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
  private void setAttachmentData(int attachmentId, MultipartFile data) throws IOException {
    // Generate suitable multi-part request...
    MultiValueMap<String, Object> requestParts = new LinkedMultiValueMap<>();
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
    restTemplate.exchange(
        applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_ATTACHMENT_SET_DATA), HttpMethod.POST,
        requestEntity, String.class, attachmentId);
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
