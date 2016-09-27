<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="utf-8" indent="yes"/>
<xsl:template match="/root">
<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <xsl:element name="base">
      <xsl:attribute name="href">
        <xsl:value-of select="basedir"/>
      </xsl:attribute>
    </xsl:element>  
    <link rel="stylesheet" href="style.css" />
  </head>
  <body>
    <div class="body">
      <h1>YLEISEN ALUEEN VUOKRAUSPÄÄTÖS</h1>
      <p>
        [Tunnus]
      </p>

      <section>
        <h1>Päättäjä</h1>
        <p>
          Rakennusviraston palveluosaston alueiden käytön alueidenkäyttöpäällikkö
        </p>
      </section>

      <section>
        <h1>Päätöksen hakija</h1>
          <!-- <p>[Hakijan nimi], [Y-tunnus]</p>
           <p>[Osoite, postinumero, toimipaikka]</p> -->
         <xsl:choose>
       <xsl:when test="data/application/applicant/type = 'PERSON'">
          <p><xsl:value-of select="data/application/applicant/person/name"/>,
            <xsl:value-of select="data/application/applicant/person/ssn"/></p>
          <p><xsl:value-of select="data/application/applicant/person/postalAddress/streetAddress"/>,
            <xsl:value-of select="data/application/applicant/person/postalAddress/postalCode"/>,
            <xsl:value-of select="data/application/applicant/person/postalAddress/city"/></p>
          </xsl:when>
        <xsl:otherwise>
          <p><xsl:value-of select="data/application/applicant/organization/name"/>,
            <xsl:value-of select="data/application/applicant/organization/businessId"/></p>
          <p><xsl:value-of select="data/application/applicant/organization/postalAddress/streetAddress"/>,
            <xsl:value-of select="data/application/applicant/organization/postalAddress/postalCode"/>,
            <xsl:value-of select="data/application/applicant/organization/postalAddress/city"/></p>
        </xsl:otherwise>
          </xsl:choose>
      </section>


      <section>
        <h1>Yhteyshenkilö</h1>
            <!-- <p>[Yhteyshenkilön nimi]</p>
            <p>[Puhelinnumero, sähköpostiosoite]</p> -->
        <xsl:for-each select="data/application/contactList">
            <p><xsl:value-of select="name"/></p>
            <p><xsl:value-of select="phone"/>, <xsl:value-of select="email"/></p>
        </xsl:for-each>
      </section>

      <section>
        <h1>Kohde</h1>
        <p>
          [Vuokrattava paikka, Lohko], [Osoite], [Lisätietoja paikasta]
        </p>
        <p>
      	   [Alueen pinta-ala]
        </p>
     </section>

     <section>
       <h1>Vuokra-aika</h1>

       <xsl:choose>
       <xsl:when test="not(data/buildStartDate)">
       <!-- Ei erillistä purkua ja rakentamista -->
         <p>
           <!-- [Tapahtuman alkupäivämäärä]-[Tapahtuman loppupäivämäärä] -->
           <xsl:value-of select="data/reservationStartDate" /> &#x2013;
           <xsl:value-of select="data/reservationEndDate" />
         </p>
         <p>
           Tapahtumapäiviä
           <xsl:value-of select="data/numReservationDays" />.<!-- [päivien lukumäärä]. -->
         </p>
       </xsl:when>
       <xsl:otherwise>
       <!--  Purku ja rakentaminen huomioitu -->
         <p>
           Kokonaisvuokra-aika
           <!-- [Rakentamisen alkupäivämäärä]-[Purkamisen loppupäivämäärä], --> 
           <xsl:value-of select="data/reservationStartDate" /> &#x2013;
           <xsl:value-of select="data/reservationEndDate" />,
           josta tapahtumapäiviä
           <!-- [Tapahtuman alkupäivämäärä]-[Tapahtuman loppupäivämäärä], -->
           <xsl:value-of select="data/eventStartDate" /> &#x2013;
           <xsl:value-of select="data/eventEndDate" />,
           rakentamispäiviä
           <!--  [Rakentamisen alkupäivämäärä]- [Tapahtuman alkupäivämäärä-1] -->
           <xsl:value-of select="data/buildStartDate" /> &#x2013;
           <xsl:value-of select="data/buildEndDate" />
           sekä purkupäiviä
           <!-- [Tapahtuman loppupäivämäärä+1]-[Purkamisen loppupäivämäärä]. -->
           <xsl:value-of select="data/teardownStartDate" /> &#x2013;
           <xsl:value-of select="data/teardownEndDate" />.
         </p>
         <p>
           Tapahtumapäiviä
           <!-- [tapahtumapäivien lukumäärä] -->
           <xsl:value-of select="data/numEventDays" />
           ja rakentamis- ja purkupäiviä
           <!-- [rakentamis- ja purkupäivien lukumäärä]. -->
           <xsl:value-of select="data/numBuildAndTeardownDays" />
         </p>
       </xsl:otherwise>
       </xsl:choose>

         <p>
           <!-- [Tapahtuma-ajan poikkeukset] -->
           <xsl:value-of select="data/application/event/timeExceptions"/>
         </p>

     </section>

      <section>
        <h1>Vuokrauksen tarkoitus</h1>
        <p>
          <!-- [Tapahtuman nimi] -->
          <xsl:value-of select="data/application/name"/>
        </p>
        <p>
          [Tapahtuman tyyppi] (avoin, maksullinen, suljettu, promootio)
        </p>
        <p>
          <!-- [Tapahtuman kuvaus] -->
          <xsl:value-of select="data/application/event/description"/>
          <xsl:if test="data/application/event/url != ''">, <xsl:element name="a">
                <xsl:attribute name="href">
                  <xsl:value-of select="data/application/event/url"/>
                </xsl:attribute>
                <xsl:value-of select="data/application/event/url"/>
              </xsl:element>
          </xsl:if>
        </p>
        <p>
          Tapahtuma sisältää rakenteita
          <!-- [rakenteiden kokonaisneliömäärä]. -->
          <xsl:value-of select="data/application/event/structureArea"/>
          m<sup>2</sup>.
        </p>
        <p>
          <!-- [Rakenteiden kuvaus] -->
          <xsl:value-of select="data/application/event/structureDescription"/>
        </p>
      </section>

      <section>
        <h1>Vuokra</h1>
        <p>
          *LISTAHINTA*
        </p>
        <p>
          [Hinta]
        </p>
        <p>
          Vuokrauspäätöksen hinta perustuu yleisten töiden lautakunnan päätökseen 11.11.2014 § 431.
        </p>
        <p>
          Ilmoitettu maanvuokra on maksettava eräpäivään mennessä rakennusviraston erikseen lähettämää laskua vastaan. Ellei vuokraerää erääntymispäivänä suoriteta, vuokralainen on velvollinen maksamaan langenneelle erälle erääntymispäivästä maksupäivään asti korkoa ja perimispalkkiota kaupunginhallituksen vahvistaman päätöksen mukaan.
        </p>
        <p>
          Laskutusviite on [asiakkaan laskutusviite].
        </p>
        <p>
          *
        </p>
        <p>
          *ALENNETTU HINTA*
        </p>
        <p>
          [Hinta]
        </p>
        <p>
          Vuokrauspäätöksen alennettu hinta perustuu yleisten töiden lautakunnan päätökseen 11.11.2014 § 431. Syy: [pudotusvalikosta valittu syy], joka sisältää teemaan sisältymätöntä elintarvikemyyntiä tai tarjoilua.
        </p>
        <p>
          *
        </p>
        <p>
          Vuokrauspäätöksen alennettu hinta perustuu yleisten töiden lautakunnan päätökseen 11.11.2014 § 431. Syy: Hakijalla on Ekokompassi-tapahtumapassi, joka oikeuttaa -30 % hinnan alennukseen.
        </p>
        <p>
          *
        </p>
        <p>
          *KORVAUKSETTA*
        </p>
        <p>
          Vuokrauspäätös myönnetään korvauksetta yleisten töiden lautakunnan päätöksen 11.11.2014 § 431 mukaisesti. Syy: [pudotusvalikosta valittu syy]
        </p>
      </section>

      <section>
        <h1>Ehdot</h1>
        <p>
          [Ehdot]
        </p>
      </section>

      <section>
        <h1>Päätöksen perustelut</h1>
        <p>
          Hakija on hakenut oikeutta alueen käyttöön, toimittanut hakemuksen liitteineen rakennusvirastolle ja ilmoittanut sitoutuvansa alueen käyttöä koskevaan ohjeistukseen sekä sopimusehtoihin.
        </p>
        <p>
          Rakennustoimen johtosäännön 4 § 8 kohdan mukaan yleisten töiden lautakunnan tehtävänä on hyväksyä perusteet, joiden mukaan viranhaltija päättää vuokralle antamisesta ja muusta käyttöön luovuttamisesta. Yleisten töiden lautakunta on päätöksissä Ytlk 30.9.2014 § 364 &amp; Ytlk 11.11.2014 § 431päättänyt nämä edellä mainitut perusteet.
        </p>
      </section>

      <section>
        <h1>Päätös</h1>
        <p>
          Rakennusviraston palveluosaston alueidenkäyttöpäällikkö päätti myöntää luvan hakijalle haetun alueen käyttämiseen yllä olevin ehdoin.
        </p>
        <p>
          Tämä päätös on sähköisesti allekirjoitettu.
        </p>
        <p>
          [aikaleima]
        </p>
        <p>
          [päättäjän työnimike]
        </p>
        <p>
          [päättäjän nimi]
        </p>
      </section>

      <section>
        <h1>Lisätiedot</h1>
        <p>
          hkr.ulkoilma@hel.fi, 09 310 39869
        </p>
      </section>

      <section>
        <h1>Valmistelija</h1>
        <p>
          [titteli, tarkastajan nimi, puhelinnumero, sähköpostiosoite]
        </p>
      </section>

      <section>
        <h1>Liitteet</h1>
        <p>
          1 ...
        </p>
        <p>
          2 ...
        </p>
      </section>

      <section>
        <h1>Muutoksenhaku</h1>
        <p>
          Muutoksenhakuohjeet liitteenä.
        </p>
      </section>

      <section>
        <h1>Otteet</h1>
        <div class="shift">
          <div class="half-left">
            <h1>Ote</h1>
          </div>
          <div class="half-right">
            <h1>Otteen liitteet</h1>
          </div>
        </div>
      </section>

    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
