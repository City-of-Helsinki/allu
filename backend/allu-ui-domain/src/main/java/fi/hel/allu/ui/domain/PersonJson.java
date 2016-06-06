package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

/**
 * in Finnish: Henkilön tiedot
 */
public class PersonJson {
    private int id;
    @NotBlank(message="{person.name}")
    private String name;
    private String streetAddress;
    private String postalCode;
    private String city;
    private String email;
    private String phone;
    private String ssn;

    /**
     * in Finnish: Henkilön tunniste
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
     * in Finnish: Henkilön postiosoite
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * in Finnish: Henkilön postinumero
     */
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * in Finnish: Henkilön kaupunki
     */
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
