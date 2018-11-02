package fi.hel.allu.external.api.data;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.geolatte.geom.Geometry;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.external.domain.*;

import static org.geolatte.geom.builder.DSL.*;

public class TestData {

  public static final StreetAddressExt STREET_ADDRESS = new StreetAddressExt() {{
    setStreetName("Arkadiankatu");
    setPremiseNumber("23");
    setEntranceLetter("B");
 }};

 public static final PostalAddressExt POSTAL_ADDRESS = new PostalAddressExt() {{
   setCity("Helsinki");
   setPostalCode("00101");
   setStreetAddress(STREET_ADDRESS);
 }};

 public static final ContactExt CONTACT = new ContactExt() {{
   setEmail("contact@foo.bar.fi");
   setName("Testi Kontakti");
   setPhone("12121233");
   setPostalAddress(POSTAL_ADDRESS);
 }};

 public static final CustomerExt CUSTOMER = new CustomerExt() {{
   setType(CustomerType.COMPANY);
   setRegistryKey("121212-1");
   setCountry("FI");
   setEmail("asiakas@foo.bar.fi");
   setInvoicingOperator("operator");
   setName("Testi Asiakas");
   setOvt("ovt");
   setPhone("12312312");
   setPostalAddress(POSTAL_ADDRESS);
 }};

 public static final CustomerWithContactsExt CUSTOMER_WITH_CONTACTS = new CustomerWithContactsExt() {{
    setCustomer(CUSTOMER);
    setContacts(Collections.singletonList(CONTACT));
 }};

 public static final CustomerExt CONTRACTOR = new CustomerExt() {{
   setType(CustomerType.COMPANY);
   setRegistryKey("131313-1");
   setCountry("FI");
   setEmail("tyonsuorittaja@foo.bar.fi");
   setInvoicingOperator("operator");
   setName("Severi Suorittaja");
   setOvt("ovt");
   setPhone("12312312");
   setPostalAddress(POSTAL_ADDRESS);
 }};

 public static final ContactExt CONTRACTOR_CONTACT = new ContactExt() {{
   setEmail("ville.vastuullinen@foo.bar.fi");
   setName("Ville Vastuullinen");
   setPhone("12121233");
   setPostalAddress(POSTAL_ADDRESS);
 }};

 public static final CustomerWithContactsExt CONTRACTOR_WITH_CONTACTS = new CustomerWithContactsExt() {{
   setCustomer(CONTRACTOR);
   setContacts(Collections.singletonList(CONTRACTOR_CONTACT));
}};

 public static final ZonedDateTime START_TIME = ZonedDateTime.now().plusDays(2);
 public static final ZonedDateTime END_TIME = ZonedDateTime.now().plusDays(11);
 public static final String CUSTOMER_REFERENCE = "Asiakkaan viite";
 public static final String IDENTIFICATION_NUMBER = "Asiakasjärjestelmän tunniste";
 public static final Double AREA = Double.valueOf(48.0);

 public static final Geometry EVENT_GEOMETRY = geometrycollection(3879, polygon(ring(
     c(2.5494887994040444E7,6673140.94535369),
     c(2.549488801625527E7,6673156.877715736),
     c(2.5494940030358132E7,6673156.805560306),
     c(2.5494940008369345E7,6673140.873198048),
     c(2.5494887994040444E7,6673140.94535369))));

 public static final Geometry PLACEMENT_CONTRACT_GEOMETRY = geometrycollection(3879, polygon(ring(
     c(2.5495411934058994E7,6673778.662236115),
     c(2.5495411999186374E7,6673830.693149966),
     c(2.549542803900701E7,6673830.673107884),
     c(2.549542797410732E7,6673778.642193841),
     c(2.5495411934058994E7,6673778.662236115))));

 public static final Geometry SHORT_TERM_RENTAL_GEOMETRY = geometrycollection(3879, polygon(ring(
     c(2.5496289375E7,6673376.749999999),
     c(2.5496333375E7,6673244.249999997),
     c(2.5496343875E7,6673249.749999999),
     c(2.5496302875E7,6673383.249999999),
     c(2.5496302875E7,6673383.249999999),
     c(2.54962871875E7,6673379.374999997),
     c(2.5496289375E7,6673376.749999999))));

 public static final Geometry EXCAVATION_ANNOUNCEMENT_GEOMETRY = geometrycollection(3879, polygon(ring(
     c(2.5499019881E7, 6673529.359827016),
     c(2.5499019914E7, 6673653.810669076),
     c(2.5499139969E7, 6673653.780535805),
     c(2.5499139939E7, 6673529.329693058),
     c(2.5499019881E7, 6673529.359827016))));

 public static final Geometry CABLE_REPORT_GEOMETRY = geometrycollection(3879, polygon(ring(
     c(2.5499139417E7, 6673434.515440254),
     c(2.5499155530E7, 6673499.57819621),
     c(2.5499173306E7, 6673561.74385622),
     c(2.5499287751E7, 6673528.183841352),
     c(2.5499250537E7, 6673404.520340249),
     c(2.5499139417E7, 6673434.515440254))));
}