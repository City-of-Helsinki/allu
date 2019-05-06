package fi.hel.allu.ui.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.wfs.WfsFilter;
import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.CoordinateJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import fi.hel.allu.ui.geocode.StreetAddressXml;
import fi.hel.allu.servicecore.util.WfsRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for querying Helsinki street addresses and for geocoding them.
 */
@Service
public class AddressService {

  private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
  private static final String NUMBER_REGEX = "\\d+";
  private static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEX);
  private static final String NUMBER_LETTER_REGEX = "\\d+\\s*[a-zA-Z]*";
  private static final String GEOCODE_FILTER = "cql_filter";
  private static final String STREETNAME_FILTER = "katunimi";
  private static final String STREETNUMBER_FILTER = "osoitenumero";
  private static final String STREETLETTER_FILTER = "osoitekirjain";
  private static final Integer DEFAULT_STREET_NUMBER = 1;
  private final ApplicationProperties applicationProperties;
  private final WfsRestTemplate restTemplate;

  @Autowired
  public AddressService(ApplicationProperties applicationProperties, WfsRestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Geocodes given street address and number.
   *
   * @param   streetName    Name of the street to be geocoded.
   * @param   streetNumber  Optional Street number.
   * @param   streetLetter  Optional street letter
   * @return  Coordinates of the given address.
   *
   * @throws NoSuchEntityException in case given address does not exist.
   */
  public CoordinateJson geocodeAddress(String streetName, Optional<Integer> streetNumber, Optional<String> streetLetter) {
    try {
      return getGeoCoordinates(streetName, streetNumber, streetLetter);
    } catch (NoSuchEntityException nsee) {
      logger.debug("No coordinates found with {}, trying with default street number", streetName);
      return getGeoCoordinates(streetName, Optional.of(DEFAULT_STREET_NUMBER), Optional.empty());
    }
  }

  /**
   * List street addresses that start with the given street name.
   *
   * @param   partialStreetName   Street name to be searched.
   * @return  List of street addresses starting with the given string.
   */
  public List<PostalAddressJson> findMatchingStreet(String partialStreetName) {

    Optional<String> optionalStreetNumber = findStreetNumber(partialStreetName);
    String partialStreetNameNoNumber = partialStreetName.replaceAll(NUMBER_LETTER_REGEX,"").trim();

    HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
    HttpEntity<String> wfsXmlEntity = restTemplate.exchange(
        applicationProperties.getStreetSearchUrl(),
        HttpMethod.GET,
        requestEntity,
        String.class,
        partialStreetNameNoNumber);

    logger.debug("For street search {}, WFS service returned {}", partialStreetName, wfsXmlEntity.getBody());
    StreetAddressXml streetAddressXml = WfsUtil.unmarshalWfs(wfsXmlEntity.getBody(), StreetAddressXml.class);
    List<PostalAddressJson> addresses = new ArrayList<>();
    if (streetAddressXml.featureMember != null && streetAddressXml.featureMember.size() > 0) {
      addresses =
          streetAddressXml.featureMember.stream().map(fm -> mapFeatureMemberToAddress(fm)).collect(Collectors.toList());
    }
    logger.debug("For street search {}, the following addresses were found {}", partialStreetName, addresses);

    return addresses.stream().filter(a -> filterByStreetNumber(a, optionalStreetNumber)).collect(Collectors.toList());
  }

  private CoordinateJson getGeoCoordinates(String streetName, Optional<Integer> streetNumber, Optional<String> streetLetter) {
    HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getStreetGeocodeUrl());
    URI uri = builder.queryParam(GEOCODE_FILTER, geoCodeFilter(streetName, streetNumber, streetLetter))
            .buildAndExpand()
            .encode()
            .toUri();

    HttpEntity<String> wfsXmlEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);

    StreetAddressXml streetAddressXml = WfsUtil.unmarshalWfs(wfsXmlEntity.getBody(), StreetAddressXml.class);
    if (streetAddressXml.featureMember == null || streetAddressXml.featureMember.size() < 1) {
      throw new NoSuchEntityException("address.geocoded.notFound");
    } else {
      // assuming that any address geocoding query returns at most a single address
      return new CoordinateJson(
              streetAddressXml.featureMember.get(0).geocodedAddress.x, streetAddressXml.featureMember.get(0).geocodedAddress.y);
    }
  }

  private String geoCodeFilter(String streetName, Optional<Integer> streetNumber, Optional<String> streetLetter) {
    return new WfsFilter(STREETNAME_FILTER, streetName)
        .and(STREETNUMBER_FILTER, streetNumber)
        .and(STREETLETTER_FILTER, streetLetter)
        .build();
  }

  private Optional<String> findStreetNumber(String partialStreetName) {
    Matcher matcher = NUMBER_PATTERN.matcher(partialStreetName);
    if (matcher.find()) {
      return Optional.of(matcher.group());
    } else {
      return Optional.empty();
    }
  }

  private boolean filterByStreetNumber(PostalAddressJson postalAddressJson, Optional<String> optionalStreetNumber) {
    if (optionalStreetNumber.isPresent()) {
      Optional<String> filteredStreetNumber = findStreetNumber(postalAddressJson.getStreetAddress());
      // street numbers that start with given number will return true. If filtered address does not contain street number at all,
      // it will be skipped
      return filteredStreetNumber.map(streetNumber -> streetNumber.startsWith(optionalStreetNumber.get())).orElse(false);
    } else {
      return true;
    }
  }

  private PostalAddressJson mapFeatureMemberToAddress(StreetAddressXml.FeatureMember featureMember) {
    PostalAddressJson postalAddressJson = new PostalAddressJson();
    postalAddressJson.setCity(featureMember.geocodedAddress.city);
    postalAddressJson.setPostalCode(featureMember.geocodedAddress.postalCode);
    StringBuilder sb = new StringBuilder(featureMember.geocodedAddress.streetName);
    Optional.ofNullable(featureMember.geocodedAddress.streetNumberText).ifPresent(streetNumber -> {
      sb.append(" ");
      sb.append(streetNumber);
    });
    postalAddressJson.setStreetAddress(sb.toString());
    return postalAddressJson;
  }
}
