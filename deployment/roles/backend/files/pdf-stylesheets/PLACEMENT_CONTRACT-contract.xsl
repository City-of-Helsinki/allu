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
          <!-- <p>[Yhteyshenkilön nimi]<br/>[Sähköpostiosoite, puhelin]</p> -->
          <p class="space-above">
            <xsl:for-each select="data/customerContactLines">
              <xsl:value-of select="."/><br/>
            </xsl:for-each>
          </p>
        </section>

        <section>
          <p class="space-above">Tällä sopimuksella <xsl:value-of select="data/decisionId"/> sovitaan rakenteiden
            sijoittamisesta Helsingin kaupungin omistamalle ja hallitsemalle yleiselle alueelle.
            <xsl:if test="data/siteAddressLine != ''">
              Osoite: <xsl:value-of select="data/siteAddressLine"/>.
            </xsl:if>
            Kaupunginosa: <xsl:value-of select="data/siteCityDistrict"/>
          </p>

          <xsl:if test="data/contractText">
            <p class="space-above"> </p>
          </xsl:if>

          <xsl:for-each select="data/contractText">
            <p>
              <xsl:value-of select="."/>
              <xsl:if test="not(normalize-space(.))">
                <br/>
              </xsl:if>
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
                  <xsl:if test="not(normalize-space(.))">
                    <br/>
                  </xsl:if>
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
            Maksut perustuvat kaupunkiympäristölautakunnan päätökseen 11.12.2018 §639.
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
            <li>Sijoittamissuostumuksesta tehdään sopimus ja se on voimassa toistaiseksi, ellei määräaikaisuudesta ole
            erikseen sovittu. Sopimus raukeaa ilman erillistä ilmoitusta myöntämispäivästä lukien yhden (1) vuoden
            kuluttua, ellei toteutusta ole tuona aikana aloitettu. Sopimus on uusittava, mikäli toteutussuunnitelma
            muuttuu merkittävästi, tai kohteeseen sisältyy muita kuin hakemuksessa esitettyjä rakenteita tai laitteita.</li>

            <li>Ennen kaivutyöhön ryhtymistä on haettava johtoselvitys sekä tehtävä kaivuilmoitus. Kaivuilmoituksessa
            on mainittava sopimuksen tunnus ja myöntämisajankohta. Kaivuilmoituksen yhteydessä kaupunki antaa
            ohjeita ja ehtoja mm. työn suorittamisesta, liikennejärjestelyiden toteuttamisesta sekä aloitus- ja
            loppukatselmuksista. Liikennealueella tai liikenteeseen vaikuttavalla alueella työskenneltäessä on aina
            esitettävä työsuunnitelma sekä hyväksytettävä päätös tilapäisistä liikennejärjestelyistä. Työn aikana on
            tarkastajalle sallittava pääsy alueelle valvomaan työn toteutusta.</li>

            <li>Sijoittava sopimusosapuoli on velvollinen kartoittamaan sopimuksen mukaiset johdot, rakenteet ja
            laitteet. Sijaintitietojen rekisteröimisen osalta on noudatettava kaupunkiympäristön
            kaupunkimittauspalveluiden ohjeita, sovittava kohteen kartoittamisesta ja sijaintitietojen toimittamisesta
            rekisteröintiä varten. Edellä mainittu koskee myös valmistumisen jälkeen tehtäviä pysyviä siirtoja ja
            rakenteiden poistamista.</li>

            <li>Sijoittava sopimusosapuoli vastaa rakenteiden, laitteiden ja johtojen kunnostamisesta ja
            kunnossapidosta.</li>

            <li>Sijoittava sopimusosapuoli vastaa kaikista luvan tarkoittamien rakenteiden, laitteiden ja johtojen
            rakentamisen ja käytön kustannuksista, sekä kaupungille tai kolmannelle osapuolelle mahdollisesti edellä
            mainituista toimenpiteistä aiheutuvasta vahingosta tai haitasta. Kaupungin suorittama työn valvonta ei
            poista sijoittavan vastuuta.</li>

            <li>Mikäli rakenteet, laitteet tai johdot on tarpeellista poistaa tai siirtää välttämättömän syyn johdosta
            väliaikaisesti tai pysyvästi, sijoittavan sopimusosapuolen on tehtävä työ kustannuksellaan. Työalue tulee
            palauttaa kaupungin hyväksymään tilaan. Sopimuksen irtisanomisaika on kuusi (6) kuukautta. Käytöstä
            poistetuista rakenteista, laitteista ja johdoista on ilmoitettava kaupunkiympäristön
            kaupunkimittauspalveluille.</li>

            <li>Mikäli alueiden käytön ja valvonnan yksikön päällikön päätös kumoutuu tai muuttuu
            oikaisuvaatimuksen, kunnallisvalituksen tai ylemmän toimielimen päätöksen johdosta taikka jos
            oikaisuvaatimuksen käsittelevä toimielin tai viranomainen kieltää täytäntöönpanon, katsotaan
            sopimus purkautuneeksi. Kaupunki ei vastaa sopimusosapuolille aiheutuneista kustannuksista,
            mikäli sopimus edellä mainitun mukaisesti katsotaan purkautuneeksi.</li>
          </ol>
        </section>
      </div>

      <div class="unboxed avoid-pb">
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

      <div class="unboxed avoid-pb">
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

      <div class="unboxed avoid-pb">
        <h2>Sijoitussopimukset:</h2>
        <p>Kaupunkiympäristö/Alueidenkäyttö ja -valvonta</p>
        <p>Sörnäistenkatu 1</p>
        <p>PL 58231, 00099 Helsingin kaupunki</p>
        <p>asiakaspalvelu puh. (09) 310 22111</p>
        <p>sähköposti alueidenkaytto@hel.fi</p>
      </div>
      <div class="unboxed avoid-pb">
        <h2>Kaivuilmoitukset:</h2>
        <p>Kaupunkiympäristö/Alueidenkäyttö ja -valvonta</p>
        <p>Sörnäistenkatu 1</p>
        <p>PL 58231, 00099 Helsingin kaupunki</p>
        <p>asiakaspalvelu puh. (09) 310 22111</p>
        <p>sähköposti luvat@hel.fi</p>
      </div>
      <div class="unboxed avoid-pb">
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
