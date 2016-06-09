package fi.hel.allu.ui.domain;


/**
 * in Finnish: Osoitteen tiedot
 */
public class PostalAddressJson {
    private String streetAddress;
    private String postalCode;
    private String city;

    /**
     * in Finnish: postiosoite
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * in Finnish: postinumero
     */
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * in Finnish: kaupunki
     */
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
