package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

/**
 * in Finnish: Organisaation tiedot
 */
public class OrganizationJson {
    private Integer id;
    @NotBlank(message="{organization.name}")
    private String name;
    @NotBlank(message="{organization.businessId}")
    private String businessId;
    @Valid
    private PostalAddressJson postalAddress;
    private String email;
    private String phone;

    /**
     * in Finnish: Organisaation tunniste
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    /**
     * in Finnish: Organisaation osoitetiedot
     */
    public PostalAddressJson getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(PostalAddressJson postalAddress) {
        this.postalAddress = postalAddress;
    }
}
