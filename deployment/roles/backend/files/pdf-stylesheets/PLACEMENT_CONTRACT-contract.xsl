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
    <link rel="stylesheet" href="new-style.css" />
    <xsl:if test="data/draft = 'true'">
      <link rel="stylesheet" href="watermark.css" />
    </xsl:if>
  </head>
  <body>
    <div class="body">

      <div class="unboxed">
        <section>
          <h2>Sopimus oikeudesta sijoittaa rakenteita yleiselle alueelle</h2>
          <p class="space-above">Sopimustunnus: <xsl:value-of select="data/decisionId"/></p>
        </section>
      </div>

      <div class="unboxed">
        <h2>Osapuolet</h2>
        <p class="space-above">Helsingin kaupunki</p>
        <section class="half-left">
          <!-- <p>[Hakijan nimi], [Y-tunnus]<br/>[Osoite, postinumero, toimipaikka]<br/>
            [Sähköpostiosoite, puhelin]</p> -->
          <p class="space-above">
            <xsl:for-each select="data/customerAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>

        <section class="half-right">
          <h2>Yhteyshenkilö</h2>
          <!-- <p>[Yhteyshenkilön nimi]<br/>[Sähköpostiosoite, puhelin]</p> -->
          <p>
            <xsl:for-each select="data/customerContactLines">
              <xsl:value-of select="."/><br/>
            </xsl:for-each>
          </p>
        </section>

        <section>
          <p class="space-above">Tällä sopimuksella <xsl:value-of select="data/decisionId"/> sovitaan
            <xsl:for-each select="data/kinds">
              <xsl:value-of select="kind" />
              <xsl:if test="./specifiers != ''">
               (
                <xsl:for-each select="./specifiers">
                  <xsl:value-of select="." />
                  <xsl:if test="position() != last()">
                      <xsl:text>, </xsl:text>
                  </xsl:if>
                </xsl:for-each>
                )
              </xsl:if>
            </xsl:for-each>
            sijoittamisesta Helsingin kaupungin omistamalle ja hallitsemalle yleiselle alueelle.
          </p>

          <xsl:if test="data/contractText">
            <p class="space-above"> </p>
          </xsl:if>

          <xsl:for-each select="data/contractText">
            <p>
              <xsl:value-of select="."/>
            </p>
          </xsl:for-each>

        </section>
      </div>

      <xsl:if test="data/additionalConditions">
        <div class="unboxed">
          <section>
            <h2>Sopimuksen lisäehdot</h2>
              <p class="space-above"> </p>
              <xsl:for-each select="data/additionalConditions">
                <p>
                  <!-- [Ehtokentän teksti]  -->
                  <xsl:value-of select="."/>
                </p>
              </xsl:for-each>
          </section>
        </div>
      </xsl:if>

      <div class="unboxed">
        <section>
          <h2>Perittävät maksut</h2>
          <xsl:choose>
            <xsl:when test="data/notBillable = 'false'">
              <div class="charge-info">
                <xsl:for-each select="data/chargeInfoEntries">
                  <div class="row">
                    <span class="c1">
                      <xsl:if test="./level > 0">
                        <span class="up-arrow" style="padding-left: {level}em"></span>
                      </xsl:if>
                      <xsl:value-of select="text"/>
                      <xsl:for-each select="explanation">
                        <div class="explanation"><xsl:value-of select="."/></div>
                      </xsl:for-each>
                    </span>
                    <span class="c2">
                      <xsl:value-of select="quantity"/>
                    </span>
                    <span class="c3">
                      <xsl:value-of select="unitPrice"/>
                    </span>
                    <span class="c4">
                      <xsl:value-of select="netPrice"/>
                    </span>
                  </div>
                </xsl:for-each>

                <div class="sum-row">
                  <span class="c1">YHTEENSÄ</span>
                  <span class="c2"></span>
                  <span class="c3"></span>
                  <span class="c4"><xsl:value-of select="data/totalRent"/></span>
                </div>
              </div>
            </xsl:when>
            <xsl:otherwise>
              <p>Korvauksetta.</p>
              <p class="space-above">
                Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
              </p>
            </xsl:otherwise>
          </xsl:choose>
          <p class="space-above">
            Maksut perustuvat yleisten töiden lautakunnan päätökseen 11.11.2014 §431.
          </p>
          <xsl:if test="data/notBillable = 'false' and data/separateBill = 'true'">
            <p class="space-above">
              <!-- Käytetään, jos lasku enemmän kuin 0 €. -->
              Lasku lähetetään erikseen.
            </p>
          </xsl:if>

        </section>
      </div>

      <div class="unboxed">
        <section>
          <h2>Yleiset sopimusehdot</h2>
          <ol>
            <li>Sopimuksessa sovitun rakenteen sijoittamiseen liittyvä rakennustyö on aloitettava vuoden
            kuluessa tämän sopimuksen syntymisestä. Sopimus katsotaan purkautuneeksi, mikäli rakenteen
            sijoittamistyötä ei ole aloitettu edellä mainitussa ajassa.
            Sijoittamisesta on sovittava uudelleen, mikäli toteutussuunnitelma muuttuu tai kohteeseen
            sisältyy muita kuin hakemuksessa tai suunnitelmassa esitettyjä rakenteita tai laitteita.</li>

            <li>Ennen kaivutyön aloittamista on hankittava johtoselvitys ja tehtävä kaivuilmoitus.
            Kaivuilmoituksessa on mainittava sijoitussopimustunnus. Kaivuilmoituksen yhteydessä kaupunki
            antaa ohjeita ja määräyksiä mm. työn suorittamisesta, liikennejärjestelyiden toteuttamisesta
            sekä aloitus- ja loppuvalvonnasta. Liikennealueella työskenneltäessä tai työ, joka vaikuttaa
            olemassa olevaan liikenteeseen, on aina esitettävä suunnitelma. Vallitsevia liikennejärjestelyjä
            voi muuttaa ainoastaan kaupungin päätöksellä tilapäisistä liikennejärjestelyistä. Työn aikana on
            valvojalle järjestettävä esteetön pääsy alueelle ja mahdollisuus tarkastaa kohde.</li>

            <li>Rakenteen, laiteen tai johdon omistajaa on velvollinen kartoittamaan rakentamisen aikana
            sopimuksessa tarkoitetut johdot, rakenteet ja laitteet. Sijaintitietojen rekisteröimisen osalta on
            noudatettava kaupunkiympäristön kaupunkimittauspalveluiden ohjeita ja sovittava kohteen
            kartoittamisesta sekä sijaintitietojen toimittamisesta rekisteröintiä varten. Edellä mainittu
            koskee myös valmistumisen jälkeen tehtäviä pysyviä siirtoja ja poistamista.</li>

            <li>Rakenteen, laiteen tai johdon omistaja vastaa rakenteiden, laitteiden ja johtojen
            kunnostamisesta ja kunnossapidosta.</li>

            <li>Rakenteen, laiteen tai johdon omistaja vastaa kaikista sopimuksessa tarkoitettujen rakenteiden,
            laitteiden ja johtojen rakentamisen ja käytön kustannuksista sekä kaupungille tai kolmannelle
            osapuolelle mahdollisesti edellä mainituista toimenpiteistä aiheutuvasta vahingosta tai haitasta.
            Kaupungin valvonta ei poista vastuuta.</li>

            <li>Sopimuksen irtisanomisaika on kuusi kuukautta. Kaupunki ilmoittaa sopimusosapuolelle, mikäli
            välttämättömän tai perustellun syyn johdosta rakenteet, laitteet tai johdot on tarpeellista
            poistaa tai siirtää väliaikaisesti tai pysyvästi. Työn suorittaa rakenteen, laiteen tai johdon
            omistaja kustannuksellaan ja saattaa paikan alueidenkäytön tarkastajan hyväksymään kuntoon.
            Käytöstä poistetuista rakenteista, laitteista ja johdoista on ilmoitettava kaupunkiympäristön
            kaupunkimittauspalveluille.</li>

            <li>Mikäli alueiden käytön ja valvonnan yksikön päällikön päätös kumoutuu tai muuttuu
            oikaisuvaatimuksen, kunnallisvalituksen tai ylemmän toimielimen päätöksen johdosta taikka jos
            oikaisuvaatimuksen käsittelevä toimielin tai viranomainen kieltää täytäntöönpanon, katsotaan
            sopimus purkautuneeksi. Kaupunki ei vastaa sopimusosapuolille aiheutuneista kustannuksista,
            mikäli sopimus edellä mainitun mukaisesti katsotaan purkautuneeksi.</li>
          </ol>
        </section>
      </div>

      <div class="unboxed">
        <section>
          <h2>Sopimuksen allekirjoitukset</h2>

          <p class="indented">Helsingin kaupungin puolesta alueidenkäyttö ja -valvonta yksikön päällikkö on
                              allekirjoittanut tämän asiakirjan sähköisesti</p>
          <p class="indented">
            <xsl:value-of select="data/decisionTimestamp"/>,
            <xsl:value-of select="data/deciderTitle"/>,
            <xsl:value-of select="data/deciderName"/>
          </p>
          <xsl:choose>
            <xsl:when test="data/frameAgreement = 'true'">
              <p class="indented space-above">
                Hakijan kanssa on voimassaoleva muu erillinen sopimus ja siksi tämä sijoitussopimus ei
                tarvitse erillistä asiakkaan allekirjoitusta.
              </p>
            </xsl:when>
            <xsl:when test="data/contractAsAttachment = 'true'">
              <p class="indented space-above">
                Hakijan tai hakijan edustajan allekirjoitus on tämän sopimuksen liitteenä olevassa
                asiakirjassa.
              </p>
            </xsl:when>
            <xsl:otherwise>
              <p class="indented space-above">
                <xsl:value-of select="data/applicantName"/> puolesta <xsl:value-of select="data/contractSigner"/> <!-- [asioinnissa sopimuksen hyväksyjä] -->
                    on allekirjoittanut tämän asiakirjan sähköisesti <xsl:value-of select="data/contractSigningDate"/> <!-- [aikaleima sopimuksen hyväksynnästä asioinnissa]-->
              </p>
            </xsl:otherwise>
          </xsl:choose>
        </section>
      </div>

      <div class="unboxed">
        <section class="half-left">
          <h2>Lisätiedot</h2>
          <p>Kaupunkiympäristön alueidenkäyttö</p>
          <p>alueidenkaytto@hel.fi</p>
        </section>

        <section class="half-right">
          <h2>Käsittelijä</h2>
          <p><xsl:value-of select="data/handlerTitle"/>, <xsl:value-of select="data/handlerName"/></p>
        </section>
      </div>

      <div class="unboxed">
        <h2>Sijoitussopimukset:</h2>
        <p>Kaupunkiympäristö/Alueidenkäyttö ja -valvonta</p>
        <p>Sörnäistenkatu 1</p>
        <p>PL 58231, 00099 Helsingin kaupunki</p>
        <p>asiakaspalvelu puh. (09) 310 22111</p>
        <p>sähköposti alueidenkaytto@hel.fi</p>
      </div>
      <div class="unboxed">
        <h2>Kaivuilmoitukset:</h2>
        <p>Kaupunkiympäristö/Alueidenkäyttö ja -valvonta</p>
        <p>Sörnäistenkatu 1</p>
        <p>PL 58231, 00099 Helsingin kaupunki</p>
        <p>asiakaspalvelu puh. (09) 310 22111</p>
        <p>sähköposti luvat@hel.fi</p>
      </div>
      <div class="unboxed">
        <h2>Kartoitus- ja sijaintitiedot:</h2>
        <p>Kaupunkiympäristö/Kaupunkimittauspalvelut</p>
        <p>Sörnäistenkatu 1</p>
        <p>PL 58232, 00099 Helsingin kaupunki</p>
        <p>puh. (09) 310 31930</p>
        <p>maanalaiset.kaupunkimittaus@hel.fi</p>
      </div>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
