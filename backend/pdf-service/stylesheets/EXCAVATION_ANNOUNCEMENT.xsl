<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
        <section class="half-left">
          <h2>Työstä vastaava</h2>
          <p>
            <xsl:for-each select="data/customerAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>
        <section class="half-right">
          <xsl:if test="data/anonymizedDocument = 'false'">
            <h2>Yhteyshenkilö</h2>
            <!-- <p>[Yhteyshenkilön nimi]<br/>[Sähköpostiosoite, puhelin]</p> -->
            <p>
              <xsl:for-each select="data/customerContactLines">
                <xsl:value-of select="."/><br/>
              </xsl:for-each>
            </p>
          </xsl:if>
        </section>
      </div>

      <xsl:if test="data/propertyDeveloperAddressLines != ''">
        <div class="unboxed">
          <section class="half-left">
            <h2>Rakennuttaja</h2>
            <p>
              <xsl:for-each select="data/propertyDeveloperAddressLines">
                <xsl:value-of select="." /><br/>
              </xsl:for-each>
            </p>
          </section>
          <section class="half-right">
            <xsl:if test="data/propertyDeveloperContactLines != '' and data/anonymizedDocument = 'false'">
              <h2>Yhteyshenkilö</h2>
              <p>
                <xsl:for-each select="data/propertyDeveloperContactLines">
                  <xsl:value-of select="."/><br/>
                </xsl:for-each>
              </p>
            </xsl:if>
          </section>
        </div>
      </xsl:if>

      <div class="unboxed">
        <section class="half-left">
          <h2>Työn suorittaja</h2>
          <p>
            <xsl:for-each select="data/contractorAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>
        <section class="half-right">
          <xsl:if test="data/anonymizedDocument = 'false'">
            <h2>Yhteyshenkilö</h2>
            <p>
              <xsl:for-each select="data/contractorContactLines">
                <xsl:value-of select="."/><br/>
              </xsl:for-each>
            </p>
          </xsl:if>
        </section>
      </div>

      <xsl:if test="data/representativeAddressLines != ''">
        <div class="unboxed">
          <section class="half-left">
            <h2>Asiamies</h2>
            <p>
              <xsl:for-each select="data/representativeAddressLines">
                <xsl:value-of select="." /><br/>
              </xsl:for-each>
            </p>
          </section>
          <section class="half-right">
            <xsl:if test="data/representativeContactLines != '' and data/anonymizedDocument = 'false'">
              <h2>Yhteyshenkilö</h2>
              <p>
                <xsl:for-each select="data/representativeContactLines">
                  <xsl:value-of select="."/><br/>
                </xsl:for-each>
              </p>
            </xsl:if>
          </section>
        </div>
      </xsl:if>

      <xsl:if test="data/cableReports != '' or data/placementContracts != ''">
        <div class="unboxed">
          <section class="half-left">
            <xsl:if test="data/cableReports != ''">
              <h2>Liittyvät johtoselvitykset</h2>
              <p><xsl:value-of select="data/cableReports" /></p>
            </xsl:if>
          </section>
          <section class="half-right">
            <xsl:if test="data/placementContracts != ''">
              <h2>Liittyvät sijoitussopimukset</h2>
              <p><xsl:value-of select="data/placementContracts" /></p>
            </xsl:if>
          </section>
        </div>
      </xsl:if>

      <div class="unboxed">
        <section class="half-left">
          <h2>Työn kohde</h2>
          <p>
            <!-- Käytetään vuokrattavaa paikkaa ja lohkoa, jos joku pudotusvalikossa määritetty paikka;
                 käytetään Osoitetta, jos ei pudotusvalikossa määritetty paikka -->
            <!-- [Vuokrattava paikka, Lohko], [Osoite] -->
            <xsl:value-of select="data/siteAddressLine"/>
          </p>
          <xsl:if test="data/siteAdditionalInfo != ''">
            <p>
              <!-- Käytetään, jos Lisätietoja paikasta täytetty -->
              <!-- [Lisätietoja paikasta] -->
              <xsl:value-of select="data/siteAdditionalInfo"/>
            </p>
          </xsl:if>
        </section>
        <section class="half-right">
          <h2>Voimassaoloaika</h2>
          <p>
            <!-- [alkupvm]-[loppupvm] -->
            <!-- Kesto päivinä: [kesto] -->
            <xsl:value-of select="data/reservationStartDate"/>-
            <xsl:value-of select="data/reservationEndDate"/>
          </p>
          <xsl:if test="data/winterTimeOperation != ''">
            <p class="space-above">
              Päällystettävä väliaikaisesti viimeistään:
              <xsl:value-of select="data/winterTimeOperation"/>
            </p>
          </xsl:if>
        </section>
      </div>

      <section class="unboxed">
        <h2>Kaivutyön tarkoitus</h2>
        <p>
          <xsl:value-of select="data/workPurpose"/>
        </p>
      </section>

      <section class="unboxed">
        <h2>Liikennejärjestelyitä koskevat määräykset</h2>
        <p>
          <xsl:for-each select="data/trafficArrangements">
            <p>
              <xsl:value-of select="."/>
              <xsl:if test="not(normalize-space(.))">
                <br/>
              </xsl:if>
            </p>
          </xsl:for-each>
        </p>
      </section>

      <xsl:if test="data/additionalConditions or (data/qualityAssuranceTest = 'true') or (data/compactionAndBearingCapacityMeasurement = 'true')">
        <section class="unboxed">
          <h2>Kaivutyötä koskevat määräykset</h2>
          <xsl:for-each select="data/additionalConditions">
            <p>
              <!-- [Ehtokentän teksti]  -->
              <xsl:value-of select="."/>
              <xsl:if test="not(normalize-space(.))">
                <br/>
              </xsl:if>
            </p>
          </xsl:for-each>
          <xsl:if test="data/qualityAssuranceTest = 'true' or data/compactionAndBearingCapacityMeasurement = 'true'">
            <p>
              <xsl:if test="data/additionalConditions">
                <br/>
              </xsl:if>
              Työstä vastaava on velvollinen teettämään kohteessa laadunvarmistuskokeet sekä toimittamaan
              niiden tulokset lausuntoineen kaupungille osoitteeseen luvat@hel.fi.
              Työstä vastaavan tulee tilata vaaditut laadunvarmistuskokeet sertifioidulta taholta.
            </p>
          </xsl:if>
        </section>
      </xsl:if>

      <xsl:if test="data/qualityAssuranceTest = 'true' or data/compactionAndBearingCapacityMeasurement = 'true'">
        <div class="unboxed">
          <h2>Työstä vastaava on velvollinen teettämään seuraavat kaivannon laadunvarmistustoimenpiteet</h2>

          <section class="half-left">
            <div style="margin-top: 5px; height: 22px;">
              <xsl:choose>
                <xsl:when test="data/qualityAssuranceTest = 'true'">
                  <img src="checkbox-c.png" class="checkbox"/>
                </xsl:when>
                <xsl:otherwise>
                  <img src="checkbox.png" class="checkbox"/>
                </xsl:otherwise>
              </xsl:choose>
              <p style="height: 22px; line-height: 22px;">
                Päällysteen laadunvarmistuskoe
              </p>
            </div>
          </section>

          <section class="half-right">
            <div style="margin-top: 5px; height: 22px;">
              <xsl:choose>
                <xsl:when test="data/compactionAndBearingCapacityMeasurement = 'true'">
                  <img src="checkbox-c.png" class="checkbox"/>
                </xsl:when>
                <xsl:otherwise>
                  <img src="checkbox.png" class="checkbox"/>
                </xsl:otherwise>
              </xsl:choose>
              <p style="height: 22px; line-height: 22px;">
                Kantavuus- ja tiiveysmittaus
              </p>
            </div>
          </section>
        </div>
      </xsl:if>

      <section class="unboxed">
        <h2>Yleiset määräykset</h2>
        <ol style="margin-top: 0px;">
          <li>Työstä vastaava on velvollinen noudattamaan annettuja määräyksiä.</li>
          <li>Alueen käyttö muuhun kuin ilmoitettuun tarkoitukseen, muussa laajuudessa tai määräysten vastaisesti
            voi johtaa uhkasakkomenettelyyn tai asiasta voidaan tehdä rikosilmoitus. Työstä vastaavalta voidaan
            periä myös ylimääräisiä valvontakuluja sekä korvauksia kaupungille aiheutuneista kustannuksista, jotka
            johtuvat välitöntä vaaraa aiheuttaneen laiminlyönnin poistamisesta.</li>
          <li>Työkohteessa on oltava näkyvillä erillinen työmaataulu, josta selviää päätöksen KP-tunnus, kaivutyön
            tarkoitus ja kesto sekä työstä vastaavan ja urakoitsijan yhteystiedot.</li>
          <li>Työalue on ennallistettava hyväksytysti työtä edeltäneeseen kuntoon työajan kuluessa.</li>
          <li>Jos työn kesto muuttuu alkuperäiseen ilmoitukseen nähden, on työstä vastaavan ilmoitettava
            päivämäärämuutoksesta, valmistumisesta tai lisäaikatarpeesta kaupungille viimeistään seuraavana
            arkipäivänä (https://www.hel.fi/static/hkr/luvat/ilmoituskaavake.pdf).</li>
          <li>Työstä vastaavan on ilmoitettava talvityökautena työalueen toiminnallisesta kunnosta kaupungille
            viimeistään seuraavana arkipäivänä. Jos ilmoitusta toiminnallisesta kunnosta ei tehdä, jatkuu
            alueenkäyttömaksun veloitus.</li>
          <li>Työalueen laajuuteen liittyvistä muutoksista tulee ilmoittaa ennen muutoksen toteuttamista.</li>
          <li>Ilmoitusten laiminlyönnistä voi aiheutua ylimääräisiä valvontakuluja ja alueenkäyttömaksuja.</li>
          <li>Työstä vastaava on velvollinen huolehtimaan tilapäisten liikennejärjestelyiden toteuttamisesta
            sekä poistamisesta kustannuksellaan. Kaupungilla on oikeus teettää tarvittavat toimet työstä vastaavan
            kustannuksella, mikäli työstä vastaava kaupungin antamasta kehotuksesta huolimatta laiminlyö
            tilapäisten liikennejärjestelyiden toteuttamisen tai poistamisen.</li>
          <li>Työstä vastaava vastaa kaupungille ja kolmannelle osapuolelle aiheuttamistaan vahingoista.</li>
          <li>Työstä vastaava vastaa kahden vuoden ajan esiin tulevista, alueen ennallistamiseen liittyvistä
            virheistä ja puutteista. Takuuaika lasketaan siitä, kun kaupunki on hyväksynyt työn valmiiksi.
            Mikäli kaupungin antamia määräyksiä on rikottu, vastaa työstä vastaava esiin tulevien virheiden ja
            puutteiden korjaamisesta takuuajan jälkeenkin.</li>
        </ol>
        <p>
          Työmaajärjestelyjä sekä kaivutöiden ja liikennejärjestelyjen suorittamista koskevat tekniset ohjeet
          ja määräykset on annettu tässä päätöksessä sekä ohjeessa ” Kaivutyöt ja tilapäiset liikennejärjestelyt
          pääkaupunkiseudulla” ja ”Tilan varaaminen siirtokehotuskyltein”. Edellä mainituissa ohjeissa annettuja
          määräyksiä on noudatettava soveltuvin osin. Ohjeet löytyvät Helsingin kaupungin nettisivuilla osoitteessa
          https://www.hel.fi/helsinki/fi/asuminen-ja-ymparisto/tontit/luvat/kaduilla-ja-puistoissa-tehtavat-tyot
          kohdasta ”Noudata näitä työohjeita”.
        </p>
        <p class="space-above">
          Kunnan antamien määräysten lainmukaisuus voidaan saattaa kunnan rakennusvalvontaviranomaisen ratkaistavaksi.
          Ennen muutoksen hakemista kadulla ja yleisillä alueilla tehtävistä töistä perittäviin maksuihin on
          tehtävä kirjallinen muistutus kaupungille 14 päivän kuluessa maksulipun (lasku) saamisesta.
        </p>
      </section>

      <section class="unboxed">
        <h2>Arvio perittävistä maksuista</h2>
          <xsl:if test="data/notBillable = 'false' and data/chargeInfoEntries">
            <div class="indented-charge-info">
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
              <p class="space-above">
		<!-- Käytössä on XSLT 1.0, joten saatavissa ei ole asiallista päivämäärävertailua :-( -->
		<xsl:variable name="day" select="format-number(substring-before(data/reservationStartDate,'.'),'00')"/>
		<xsl:variable name="restOfDate" select="substring-after(data/reservationStartDate,'.')"/>
		<xsl:variable name="month" select="format-number(substring-before($restOfDate,'.'),'00')"/>
		<xsl:variable name="year" select="substring-after($restOfDate,'.')"/>
		<xsl:variable name="date" select="concat($year, $month, $day)"/>
		<xsl:choose>
		  <xsl:when test="$date &lt; 20250301">
                    Maksut perustuvat Kaupunkiympäristölautakunnan ympäristö- ja lupajaoston päätökseen 17.2.2022 § 28.
		  </xsl:when>
		  <xsl:otherwise>
		    Maksut perustuvat Kaupunkiympäristölautakunnan ympäristö- ja lupajaoston päätökseen 10.10.2024 § 157.
		  </xsl:otherwise>
		</xsl:choose>
              </p>
            </div>
            <p class="space-above">
              Toteutunut työaika vaikuttaa maksujen suuruuteen ja lopullinen laskutettava maksu määräytyy
              hyväksytysti vastaanotetun työn keston perusteella. Lasku lähetetään erikseen.
            </p>
          </xsl:if>
          <xsl:if test="data/notBillable = 'true'">
            <p>Korvauksetta.</p>
            <p class="space-above">
              Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
            </p>
          </xsl:if>
      </section>

      <xsl:if test="data/notBillable = 'false' and data/anonymizedDocument = 'false'">
        <div class="unboxed avoid-pb">
          <h2>Laskutusosoite</h2>
          <section class="half-left">
            <p>
              <xsl:for-each select="data/invoiceRecipientAddressLines">
                <xsl:value-of select="." /><br/>
              </xsl:for-each>
            </p>
          </section>
          <section class="half-right">
            <xsl:if test="data/ovt != ''">
              <p>
                OVT-tunnus: <xsl:value-of select="data/ovt"/>
              </p>
            </xsl:if>
            <xsl:if test="data/invoicingOperator != ''">
              <p>
                Välittäjän tunnus: <xsl:value-of select="data/invoicingOperator"/>
              </p>
            </xsl:if>
            <xsl:if test="data/customerReference != ''">
              <p>
                Laskutusviite: <xsl:value-of select="data/customerReference"/>
              </p>
            </xsl:if>
          </section>
        </div>
      </xsl:if>

      <section class="unboxed avoid-pb">
        <h2>Päätös</h2>
        <p>
          Tämä päätös on sähköisesti allekirjoitettu.
        </p>
        <p>
          <!-- [aikaleima], [päättäjän työnimike], [päättäjän nimi] -->
          <xsl:value-of select="data/decisionTimestamp"/>,
          <xsl:value-of select="data/deciderTitle"/>,
          <xsl:value-of select="data/deciderName"/>
        </p>
        <p class="space-above">
          Päätös perustuu kadun ja eräiden yleisten alueiden kunnossa- ja puhtaanapidosta annetun lain (669/1978) kohtiin 14 a § ja 14 b § sekä tieliikennelain (729/2018) kohtiin 187 § ja 188 §
        </p>
      </section>

      <section class="unboxed avoid-pb">
        <h2>Liitteet</h2>
        <p>
          <!--  [Lista liitteiden nimistä] -->
          <xsl:for-each select="data/attachmentNames">
            <xsl:value-of select="." /><br/>
          </xsl:for-each>
        </p>
      </section>

      <div class="unboxed avoid-pb">
        <h2>Valvojan yhteystiedot</h2>
        <section class="half-left">
          <p class="small">Nimi</p>
          <p><xsl:value-of select="data/supervisorName"/></p>
        </section>
        <section class="half-right">
          <p class="small">Sähköposti</p>
          <p><xsl:value-of select="data/supervisorEmail"/></p>
          <p class="small">Puhelin</p>
          <p><xsl:value-of select="data/supervisorPhone"/></p>
        </section>
      </div>

      <div class="unboxed avoid-pb">
        <h2>Ilmoituksen käsittelijän yhteystiedot</h2>
        <section class="half-left">
          <p class="small">Nimi</p>
          <p><xsl:value-of select="data/handlerName"/></p>
        </section>
        <section class="half-right">
          <p class="small">Sähköposti</p>
          <p><xsl:value-of select="data/handlerEmail"/></p>
          <p class="small">Puhelin</p>
          <p><xsl:value-of select="data/handlerPhone"/></p>
        </section>
      </div>

    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
