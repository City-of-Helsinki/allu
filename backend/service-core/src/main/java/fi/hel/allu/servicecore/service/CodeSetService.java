package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.CodeSetType;
import fi.hel.allu.model.domain.CodeSet;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CodeSetService {
  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;

  @Autowired
  public CodeSetService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  @Cacheable("codesets")
  public CodeSet findById(Integer id) {
    final ResponseEntity<CodeSet> result = restTemplate
        .getForEntity(applicationProperties.getCodeSetFindByIdUrl(), CodeSet.class, id);
    return result.getBody();
  }

  @Cacheable("codesets")
  public List<CodeSet> findByIds( List<Integer> countryIds) {
    CodeSet[] result = restTemplate.postForObject(applicationProperties.getCodeSetFindByIdsUrl(), countryIds, CodeSet[].class);
    return Arrays.asList(result);
  }

  @Cacheable("codesets")
  public List<CodeSet> findByType(CodeSetType type) {
    final ResponseEntity<CodeSet[]> result = restTemplate
        .getForEntity(applicationProperties.getCodeSetFindByTypeUrl(), CodeSet[].class, type);
    return Arrays.asList(result.getBody());
  }

  @Cacheable("codesets")
  public CodeSet findByTypeAndCode(CodeSetType type, String code) {
    final ResponseEntity<CodeSet> result = restTemplate
        .getForEntity(applicationProperties.getCodeSetFindByTypeAndCodeUrl(), CodeSet.class, type, code);
    return result.getBody();
  }
}
