package fi.hel.allu.external.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;

import fi.hel.allu.external.domain.AttachmentInfoExt;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApplicationAttachmentTest extends BaseApplicationRelatedTest {


  private static final String ATTACHMENT_RESOURCE_PATH = "/applications/{id}/attachments";
  private static final String MIME_TYPE = "text/plain";
  private static final String ATTACHMENT_DESCRIPTION = "Liitteen kuvaus";
  private static final String ATTACHMENT_NAME = "Vuokrauksen liite 1";
  private static final String ATTACHMENT_DATA = "Liitteen data";
  private static final String APPLICATION_NAME = "Vuokraus, liitteet - ext";

  @Test
  public void shouldCreateApplicationAttachments() {
    HttpEntity<?> requestEntity = createAttachmentRequest(ATTACHMENT_DATA.getBytes());
    ResponseEntity<Void> response = restTemplate.exchange(getExtServiceUrl(ATTACHMENT_RESOURCE_PATH), HttpMethod.POST,
        requestEntity, Void.class, getApplicationId());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void shouldNotAllowEmptyAttachment() {
    HttpEntity<?> requestEntity = createAttachmentRequest(new byte[0]);
    HttpStatus status = null;
    try {
      restTemplate.exchange(getExtServiceUrl(ATTACHMENT_RESOURCE_PATH), HttpMethod.POST,
        requestEntity, Void.class, getApplicationId());
    } catch (HttpStatusCodeException ex) {
      status = ex.getStatusCode();
    }
    assertEquals(HttpStatus.BAD_REQUEST, status);
  }

  private HttpEntity<?> createAttachmentRequest(byte[] data) {
    AttachmentInfoExt info = new AttachmentInfoExt();
    info.setDescription(ATTACHMENT_DESCRIPTION);
    info.setMimeType(MIME_TYPE);
    info.setName(ATTACHMENT_NAME);

    MultiValueMap<String, Object> requestParts = new LinkedMultiValueMap<>();
    requestParts.add("metadata", info);
    requestParts.add("file", new ByteArrayResource(data) {
      @Override
      public String getFilename() {
        return "file.txt";
      }
    });
    HttpHeaders requestHeader = new HttpHeaders();
    requestHeader.setContentType(MediaType.MULTIPART_FORM_DATA);
    setAuthorization(requestHeader);
    HttpEntity<?> requestEntity = new HttpEntity<>(requestParts, requestHeader);
    return requestEntity;
  }

  @Override
  protected String getApplicationName() {
    return APPLICATION_NAME;
  }
}
