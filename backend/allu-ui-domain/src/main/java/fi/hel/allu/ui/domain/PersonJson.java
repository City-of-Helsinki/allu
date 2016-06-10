package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

/**
 * in Finnish: Henkilön tiedot
 */
public class PersonJson {
    private Integer id;
    @NotBlank(message="{person.name}")
    private String name;
    @Valid
    private PostalAddressJson postalAddress;
    private String email;
    private String phone;
    private String ssn;

    /**
     * in Finnish: Henkilön tunniste
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * in Finnish: Henkilön nimi
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * in Finnish: Henkilön osoitetiedot
     */
    public PostalAddressJson getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAddressJson postalAddress) {
        this.postalAddress = postalAddress;
    }

    /**
     * in Finnish: Henkilön sähköpostiosoite
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * in Finnish: Henkilön puhelinnumero
     */
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * in Finnish: Henkilön henkilötunnus
     */
    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
}
