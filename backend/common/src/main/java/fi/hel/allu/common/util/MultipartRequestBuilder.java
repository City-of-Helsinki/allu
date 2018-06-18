package fi.hel.allu.common.util;

import java.util.Collections;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class MultipartRequestBuilder {

  public static HttpEntity<?> buildByteArrayRequest(String dataPartName, byte[] data) {
    return buildByteArrayRequest(dataPartName, data, Collections.emptyMap());
  }

  public static <T> HttpEntity<?> buildByteArrayRequest(String dataPartName, byte[] data,  Map<String, T> objectParts) {
    MultiValueMap<String, Object> requestParts = new LinkedMultiValueMap<>();
    objectParts.entrySet().forEach(e -> requestParts.add(e.getKey(), e.getValue()));
    requestParts.add(dataPartName, new ByteArrayResource(data) {
      @Override // return some filename so that Spring handles this as file
      public String getFilename() {
        return dataPartName;
      }
    });
    HttpHeaders requestHeader = new HttpHeaders();
    requestHeader.setContentType(MediaType.MULTIPART_FORM_DATA);
    HttpEntity<?> requestEntity = new HttpEntity<>(requestParts, requestHeader);
    return requestEntity;
  }
}
