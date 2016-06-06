package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

/**
 * in Finnish: Organisaation tiedot
 */
public class OrganizationJson {
    private int id;
    @NotBlank(message="{organization.name}")
    private String name;
    @NotBlank(message="{organization.businessId}")
    private String businessId;
    private String streetAddress;
    private String postalCode;
    private String city;
    private String email;
    private String phone;

    /**
     * in Finnish: Organisaation tunniste
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * in Finnish: Organisaation nimi
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * in Finnish: Organisaation Y-tunnus
     */

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    /**
     * in Finnish: Organisaation postiosoite
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * in Finnish: Organisaation postinumero
     */
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * in Finnish: Organisaation kaupunki
     */
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * in Finnish: Organisaation sähköpostiosoite
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * in Finnish: Organisaation puhelinnumero
     */
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
