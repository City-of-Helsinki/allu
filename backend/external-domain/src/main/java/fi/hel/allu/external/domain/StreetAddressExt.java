package fi.hel.allu.external.domain;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Street address in JHS106 format. "
    + "If structured street address format is not available in client system, "
    + "street address can be set into field streetName.")
public class StreetAddressExt {
  private String streetName;
  private String premiseNumber;
  private String entranceLetter;
  private String apartmentNumber;
  private String divisionLetter;

  public StreetAddressExt() {
  }

  public StreetAddressExt(String streetAddress) {
    this.streetName = streetAddress;
  }

  @ApiModelProperty("Street name (kadun tai tien nimi)")
  public String getStreetName() {
    return streetName;
  }

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  @ApiModelProperty("Premise number (osoitenumero)")
  public String getPremiseNumber() {
    return premiseNumber;
  }

  public void setPremiseNumber(String premiseNumber) {
    this.premiseNumber = premiseNumber;
  }

  @ApiModelProperty("Entrance letter (kirjainosa, porras)")
  public String getEntranceLetter() {
    return entranceLetter;
  }

  public void setEntranceLetter(String entranceLetter) {
    this.entranceLetter = entranceLetter;
  }

  @ApiModelProperty("Apartment number (numero-osa, huoneistonumero)")
  public String getApartmentNumber() {
    return apartmentNumber;
  }

  public void setApartmentNumber(String apartmentNumber) {
    this.apartmentNumber = apartmentNumber;
  }

  @ApiModelProperty("Division letter (jakokirjainosa)")
  public String getDivisionLetter() {
    return divisionLetter;
  }

  public void setDivisionLetter(String divisionLetter) {
    this.divisionLetter = divisionLetter;
  }

  @Override
  public String toString() {
    return Stream.of(streetName, premiseNumber, entranceLetter, apartmentNumber, divisionLetter)
        .filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
  }

}
