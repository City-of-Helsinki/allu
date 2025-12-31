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
          <h1>Työstä vastaava</h1>
          <p>
            <xsl:for-each select="data/customerAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>
        <section class="half-right">
          <xsl:if test="data/anonymizedDocument = 'false'">
            <h1>Yhteyshenkilö</h1>
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
            <h1>Rakennuttaja</h1>
            <p>
              <xsl:for-each select="data/propertyDeveloperAddressLines">
                <xsl:value-of select="." /><br/>
              </xsl:for-each>
            </p>
          </section>
          <section class="half-right">
            <xsl:if test="data/propertyDeveloperContactLines != '' and data/anonymizedDocument = 'false'">
              <h1>Yhteyshenkilö</h1>
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
          <h1>Työn suorittaja</h1>
          <p>
            <xsl:for-each select="data/contractorAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>
        <section class="half-right">
          <xsl:if test="data/anonymizedDocument = 'false'">
            <h1>Yhteyshenkilö</h1>
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
            <h1>Asiamies</h1>
            <p>
              <xsl:for-each select="data/representativeAddressLines">
                <xsl:value-of select="." /><br/>
              </xsl:for-each>
            </p>
          </section>
          <section class="half-right">
            <xsl:if test="data/representativeContactLines != '' and data/anonymizedDocument = 'false'">
              <h1>Yhteyshenkilö</h1>
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
              <h1>Liittyvät johtoselvitykset</h1>
              <p><xsl:value-of select="data/cableReports" /></p>
            </xsl:if>
          </section>
          <section class="half-right">
            <xsl:if test="data/placementContracts != ''">
              <h1>Liittyvät sijoitussopimukset</h1>
              <p><xsl:value-of select="data/placementContracts" /></p>
            </xsl:if>
          </section>
        </div>
      </xsl:if>

      <div class="unboxed">
        <section class="half-left">
          <h1>Työn kohde</h1>
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
          <h1>Voimassaoloaika</h1>
          <p>
            <!-- [alkupvm]-[loppupvm] -->
            <!-- Kesto päivinä: [kesto] -->
            <xsl:value-of select="data/reservationStartDate"/>-
            <xsl:value-of select="data/reservationEndDate"/>
          </p>
          <xsl:if test="data/winterTimeOperation != ''">
            <p class="pt-10">
              Päällystettävä väliaikaisesti viimeistään:
              <xsl:value-of select="data/winterTimeOperation"/>
            </p>
          </xsl:if>
        </section>
      </div>

      <section class="unboxed">
        <h1>Kaivutyön tarkoitus</h1>
        <p>
          <xsl:value-of select="data/workPurpose"/>
        </p>
      </section>

      <!-- A-OSA -->
      <section class="unboxed">
        <h1 class="underlined">A-OSA</h1>
        <h2>Liikennejärjestelyjä koskevat määräykset</h2>
        <p class="pb-10">
          Tämä päätösosa perustuu tieliikennelain (729/2018) 187 § ja 188 §:ään. Päätösosassa käydään
          läpi liikennejärjestelyjä koskevat määräykset, jotka velvoittavat työstä vastaavaa,
          rakennuttajaa ja työn suorittajaa.
        </p>
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

      <!-- B-OSA -->
      <xsl:if test="data/additionalConditions or (data/qualityAssuranceTest = 'true') or (data/compactionAndBearingCapacityMeasurement = 'true')">
        <section class="unboxed">
          <h1 class="underlined">B-OSA</h1>
          <h2>Kaivutyötä koskevat määräykset</h2>
          <p class="pb-10">
            Tämä päätösosa perustuu kadun ja eräiden yleisten alueiden kunnossa- ja puhtaanapidosta
            annetun lain (669/1978) 14 a § ja 14 b §:ään. Päätösosassa käydään läpi työtä koskevat
            määräykset, jotka velvoittavat työstä vastaavaa, rakennuttajaa ja työn suorittajaa.
          </p>
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
          <h1>Työstä vastaava on velvollinen teettämään seuraavat kaivannon laadunvarmistustoimenpiteet</h1>

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

      <!-- Yleiset määräykset -->
      <section class="unboxed">
        <div class="avoid-pb">
          <h1>Yleiset määräykset</h1>
          <h2 class="pt-10 underlined">A-OSA</h2>
          <h3 class="mb-0 ml-0">Liikennejärjestelyjä koskevat yleiset määräykset</h3>
          <p>
            Tämä päätösosa perustuu tieliikennelain
            (729/2018) 187 § ja 188 §:ään. Päätösosassa käydään läpi liikennejärjestelyjä koskevat yleiset
            määräykset, jotka velvoittavat työstä vastaavaa, rakennuttajaa ja työn suorittajaa.
          </p>
        </div>
        <ol class="numbered-list">
          <li>
            Työstä vastaava on velvollinen huolehtimaan tilapäisten liikennejärjestelyiden toteuttamisesta
            sekä poistamisesta kustannuksellaan. Kaupungilla on oikeus teettää tarvittavat toimet työstä
            vastaavan kustannuksella.
          </li>
          <li>
            Liikennejärjestelyiden tulee vastata kutakin työvaihetta työstä vastaavan esittämällä tavalla.
            Liikennejärjestelyiden muuttuessa työstä vastaavan on tehtävä ilmoitus kaupungille.
            Liikennejärjestelyiden muutokset saa toteuttaa uuden päätöksen jälkeen.
          </li>
          <li>
            Jalankulku ja muu liikenne on ohjattava aina turvallista reittiä työkohteen ohi. Työalue on
            eristettävä aukottomasti heijastavilla sulkulaitteilla. Kadunkäyttäjät on suojattava putoavalta
            materiaalilta.
          </li>
          <li>
            Liikenteenohjaajia tulee käyttää, jos kaikille kulkumuodoille ei voida muuten toteuttaa riittävän
            leveää, turvallista ja esteetöntä kulkua työmaan ohi.
          </li>
        </ol>

        <div class="avoid-pb">
          <h2 class="underlined">B-OSA</h2>
          <h3 class="mb-0 ml-0">Työtä koskevat yleiset määräykset</h3>
          <p>
            Tämä päätösosa perustuu kadun ja eräiden yleisten alueiden kunnossa- ja puhtaanapidosta
            annetun lain (669/1978) 14 a § ja 14 b §:ään. Päätösosassa käydään läpi työtä koskevat yleiset
            määräykset, jotka velvoittavat työstä vastaavaa, rakennuttajaa ja työn suorittajaa.
          </p>
        </div>
        <ol class="numbered-list">
          <li>
            Alueen käyttö muuhun kuin ilmoitettuun tarkoitukseen tai kaupungin antamien määräysten
            vastaisesti on kielletty.
          </li>
          <li>
            Työkohteessa on oltava näkyvillä erillinen työmaataulu, josta selviää päätöksen KP-tunnus, työn
            tarkoitus ja kesto sekä työstä vastaavan ja urakoitsijan yhteystiedot.
          </li>
          <li>
            Päätöksessä yksilöity työstä vastaava vastaa kaupunkia kohtaan kaikista työhön liittyvistä
            velvoitteista, kuten maksujen suorittamisesta. Vastuu ei kaupunkia kohtaan siirry, vaikka työssä
            käytettäisiin rakennuttajaa tai urakoitsijaa.
          </li>
          <li>
            Työstä vastaava on velvollinen tekemään kaupungille seuraavat ilmoitukset:
            <ul>
              <li class="mt-0">Ennen työn aloittamista: 1) työn aloittamispäivä ja 2) työn arvioitu valmistumispäivä</li>
              <li class="mt-0">Työn valmistumisen jälkeen: työn lopullinen valmistumispäivä</li>
              <li class="mt-0">Jos alkuperäisen ilmoituksen tiedot muuttuvat: ilmoitettava etukäteen
                päivämäärämuutoksista, lisäaikatarpeesta, työmaa-alueen suurentamisesta tai
                pienentämisestä ja liikennejärjestelyihin liittyvistä muutoksista.</li>
            </ul>
          </li>
          <li>
            Kaivutöiden talvikausi on 1.12.–14.5. (tai kaupungin erikseen määräämä ajankohta).
            Talvikaudella työalueen voi ilmoittaa toiminnalliseen tilaan, jolloin alueenkäyttömaksu katkeaa
            talvikauden ajaksi.
            <br/><br/>
            Alueenkäyttömaksun katkeaminen edellyttää, että 1) toiminnallisesta tilasta on ilmoitettu
            kaupungille viimeistään seuraavana arkipäivänä toiminnallisen tilan valmistumisesta, ja 2) alue on
            kaupungin tarkastajan hyväksymässä kunnossa. Ajoradan, pyörätien ja jalkakäytävän tulee olla
            tilapäisesti päällystetty asfaltilla pintaan saakka. Kaikki liikenteenohjauslaitteet tulee olla poistettu
            maastosta. Jalkakäytävillä ja muilla alueilla voi käyttää kivituhkaa tai hienoa mursketta, jos asiasta
            on sovittu etukäteen tarkastajan kanssa.
            <br/><br/>
            Jos edellä mainittuja ehtoja ei noudateta, alueenkäyttömaksun laskutus jatkuu talvikauden ajan.
            Katuluokan mukainen lopullinen päällystys on tehtävä 14.5. mennessä, tai muussa tapauksessa
            laskutus jatkuu 15.5. alkaen siihen asti, kunnes työ on hyväksytysti valmis.
          </li>
          <li>
            Kaikki näissä määräyksissä mainitut ilmoitukset on tehtävä päätöksessä mainitun työajan
            voimassaoloaikana.
          </li>
          <li>
            Alueen käyttämisestä peritään kaupungin taksapäätöksen mukainen maksu
            (”alueenkäyttömaksu”). Päätöksessä ilmoitettu maksu on vasta arvio. Maksua peritään siihen asti,
            kunnes alue on ennallistettu ja kaupungin tarkastaja on tehnyt työalueelle hyväksytyn
            loppukatselmuksen. Loppukatselmus tehdään, kun työstä vastaava on ilmoittanut kaupungille työn
            lopullisen valmistumispäivän. Alueenkäyttömaksu saattaa vähentyä, jos työalue pienenee työn
            aikana tai jos työn suorittamisen päivämäärät muuttuvat (myöhempi aloittaminen tai aikaisempi
            valmistuminen). Maksua vähennetään aikaisintaan siitä ajankohdasta, kun muutoksista on
            ilmoitettu kaupungille. Maksua ei vähennetä jälkikäteen toimitetun selvityksen perusteella, jos
            muutoksesta ei ole ilmoitettu kaupungille etukäteen.
          </li>
          <li>
            Varatun alueen käyttö pysäköintiin on kielletty.
          </li>
          <li>
            Työalue ja sen ympäristö on pidettävä yleisilmeeltään siistinä. Lietteen, rappauksen sekä muun
            rakennusjätteen kulkeutuminen kadulle ja viemäriverkostoon sekä muu alueen pilaantuminen on
            estettävä. Työalue on ennallistettava ja siivottava työtä edeltäneeseen, kaupungin hyväksymään
            kuntoon työajan kuluessa.
          </li>
          <li>
            Mikäli työ estää normaalin koneellisen kunnossa- ja puhtaanapidon, on työstä vastaavan
            huolehdittava alueen kunnossa- ja puhtaanapidosta sillä alueella, jolta normaali kunnossapito
            estyy. Kaupungilla on oikeus antaa tarkentavia määräyksiä alueen kunnossa- ja puhtaanapidon
            toteuttamisesta, ja työstä vastaavan on noudatettava näitä määräyksiä.
          </li>
          <li>
            Työstä on tiedotettava sen vaikutuspiirissä oleville, joille saattaa aiheutua haittaa tai häiriötä
            työstä.
          </li>
          <li>
            Työstä vastaava on velvollinen tekemään muut tarvittavat viranomaisilmoitukset.
          </li>
          <li>
            Kaupungille on päivitettävä työstä vastaavan sekä muiden ilmoituksessa mainittujen
            osapuolten (mm. rakennuttaja, urakoitsija) ajantasaiset yhteystiedot. Kaupungin yhteydenotot,
            kuten valvontaraportit, kehotukset ja määräykset, lähetetään kaupungille viimeksi ilmoitettuihin
            sähköpostiosoitteisiin.
          </li>
          <li>
            Kaupungilla on oikeus periä työstä vastaavalta ylimääräisiä valvontakuluja kaupungin
            taksapäätöksen mukaisesti. Ylimääräisiä valvontakuluja voidaan periä esimerkiksi toistuvien
            kehotusten tai määräysten antamisesta sekä puuttuvista tai harhaanjohtavista ilmoituksista
            aiheutuneesta ylimääräisestä valvontatyöstä.
          </li>
          <li>
            Työstä vastaava vastaa kaupungille ja kolmannelle osapuolelle aiheuttamistaan vahingoista.
          </li>
          <li>
            Työstä vastaava vastaa alueen ennallistamiseen liittyvistä puutteista kaksi vuotta työn
            hyväksytyn valmistumisen jälkeen. Mikäli kaupungin antamia määräyksiä on rikottu, jatkuu vastuu
            puutteista tämän jälkeenkin.
          </li>
          <li>
            Tässä päätöksessä annettujen määräysten lisäksi työn suorittamisessa on noudatettava
            Helsingin kaupungin verkkosivuilla olevaa ajantasaista ohjeistusta (päätöshetkellä osoitteessa
            https://www.hel.fi/fi/kaupunkiymparisto-ja-liikenne/tontit-ja-rakentamisen-luvat/tyomaan-luvat-ja-ohjeet/kaduilla-ja-puistoissa-tehtavat-tyot kohdassa ”Huomioi nämä ohjeet”). Työn suorittamisessa
            tulee erityisesti huomioida verkkosivuilla oleva ohje ”Kaivutyöt ja tilapäiset liikennejärjestelyt
            pääkaupunkiseudulla”.
          </li>
        </ol>
      </section>

      <section class="unboxed">
        <h1>Arvio perittävistä maksuista</h1>
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
              <p class="pt-10">
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
            <p class="pt-10">
              Toteutunut työaika vaikuttaa maksujen suuruuteen ja lopullinen laskutettava maksu määräytyy
              hyväksytysti vastaanotetun työn keston perusteella. Lasku lähetetään erikseen.
            </p>
          </xsl:if>
          <xsl:if test="data/notBillable = 'true'">
            <p>Korvauksetta.</p>
            <p class="pt-10">
              Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
            </p>
          </xsl:if>
      </section>

      <xsl:if test="data/notBillable = 'false' and data/anonymizedDocument = 'false'">
        <div class="unboxed avoid-pb">
          <h1>Laskutusosoite</h1>
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
        <h1>Päätös</h1>
        <p>
          Tämä päätös on sähköisesti allekirjoitettu.
        </p>
        <p>
          <!-- [aikaleima], [päättäjän työnimike], [päättäjän nimi] -->
          <xsl:value-of select="data/decisionTimestamp"/>,
          <xsl:value-of select="data/deciderTitle"/>,
          <xsl:value-of select="data/deciderName"/>
        </p>
        <p class="pt-10">
          Päätös perustuu kadun ja eräiden yleisten alueiden kunnossa- ja puhtaanapidosta annetun lain (669/1978) kohtiin 14 a § ja 14 b § sekä tieliikennelain (729/2018) kohtiin 187 § ja 188 §
        </p>
      </section>

      <section class="unboxed avoid-pb">
        <h1>Liitteet</h1>
        <p>
          <!--  [Lista liitteiden nimistä] -->
          <xsl:for-each select="data/attachmentNames">
            <xsl:value-of select="." /><br/>
          </xsl:for-each>
        </p>
      </section>

      <div class="unboxed avoid-pb">
        <h1>Valvojan yhteystiedot</h1>
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
        <h1>Ilmoituksen käsittelijän yhteystiedot</h1>
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

      <!-- Muutoksenhakuohjeet -->
      <section class="new-page">
        <h1>MUUTOKSENHAKUOHJEET</h1>

        <!-- A-OSA -->
        <h2 class="pt-10 underlined">A-OSA</h2>
        <div class="indent-50">
          <p class="pt-10">Tähän päätökseen tyytymätön voi tehdä kirjallisen oikaisuvaatimuksen.</p>
          <p class="pt-10">Päätökseen ei saa hakea muutosta valittamalla tuomioistuimeen.</p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimusoikeus</h3>
          <p>
            Oikaisuvaatimuksen saa tehdä se, johon päätös on kohdistettu tai jonka oikeuteen,
            velvollisuuteen tai etuun päätös välittömästi vaikuttaa (asianosainen).
          </p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimusaika</h3>
          <p>Oikaisuvaatimus on tehtävä 30 päivän kuluessa päätöksen tiedoksisaannista.</p>
          <p class="pt-10">
            Oikaisuvaatimuksen on saavuttava Helsingin kaupungin kirjaamoon määräajan
            viimeisenä päivänä ennen kirjaamon aukioloajan päättymistä klo 16.00.
          </p>
          <p class="pt-10">
            Mikäli päätös on annettu tiedoksi postitse, asianosaisen katsotaan saaneen
            päätöksestä tiedon, jollei muuta näytetä, seitsemän päivän kuluttua kirjeen
            lähettämisestä.
          </p>
          <p class="pt-10">
            Mikäli päätös on annettu tiedoksi sähköisenä viestinä, asianosaisen katsotaan
            saaneen päätöksestä tiedon, jollei muuta näytetä, kolmen päivän kuluttua viestin
            lähettämisestä.
          </p>
          <p class="pt-10">
            Mikäli päätös on annettu tiedoksi yleistiedoksiantona, tiedoksisaannin katsotaan
            tapahtuneen seitsemäntenä päivänä siitä, kun asiakirjan nähtäville asettamista
            koskeva ilmoitus on julkaistu viranomaisen verkkosivuilla.
          </p>
          <p class="pt-10">
            Tiedoksisaantipäivää ei lueta oikaisuvaatimusaikaan. Jos oikaisuvaatimusajan
            viimeinen päivä on pyhäpäivä, itsenäisyyspäivä, vapunpäivä, joulu- tai juhannusaatto
            tai arkilauantai, saa oikaisuvaatimuksen tehdä ensimmäisenä arkipäivänä sen
            jälkeen.
          </p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimusviranomainen</h3>
          <p>Oikaisuvaatimus on tehtävä päätöksen tehneelle viranhaltijalle.</p>
          <p class="pt-10">Oikaisuvaatimusviranomaisen asiointiosoite on seuraava:</p>
          <table class="contact-table">
            <tr>
              <td>Sähköpostiosoite:</td>
              <td>helsinki.kirjaamo@hel.fi</td>
            </tr>
            <tr>
              <td>Postiosoite:</td>
              <td>PL 10
                00099 HELSINGIN KAUPUNKI
              </td>
            </tr>
            <tr>
              <td>Käyntiosoite:</td>
              <td>Pohjoisesplanadi 11–13</td>
            </tr>
            <tr>
              <td>Puhelinnumero:</td>
              <td>09 310 13700</td>
            </tr>
          </table>
          <p class="pt-10">Kirjaamon aukioloaika on maanantaista perjantaihin klo 08.15–16.00.</p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimuksen muoto ja sisältö</h3>
          <p>
            Oikaisuvaatimus on tehtävä kirjallisena. Myös sähköinen asiakirja täyttää
            vaatimuksen kirjallisesta muodosta.
          </p>
          <p class="pt-10">Oikaisuvaatimuksessa on ilmoitettava</p>
          <ul class="mt-0 mb-0">
            <li class="mt-0">päätös, johon oikaisuvaatimus kohdistuu</li>
            <li class="mt-0">miten päätöstä halutaan oikaistavaksi</li>
            <li class="mt-0">millä perusteella päätöstä halutaan oikaistavaksi</li>
            <li class="mt-0">oikaisuvaatimuksen tekijä</li>
            <li class="mt-0">millä perusteella oikaisuvaatimuksen tekijä on oikeutettu tekemään vaatimuksen</li>
            <li class="mt-0">oikaisuvaatimuksen tekijän yhteystiedot</li>
          </ul>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Pöytäkirja</h3>
          <p>Päätöstä koskevia pöytäkirjan otteita ja liitteitä lähetetään pyynnöstä.</p>
          <p class="pt-10">Asiakirjoja voi tilata Helsingin kaupungin kirjaamosta.</p>
        </div>

        <!-- B-OSA -->
        <div class="avoid-pb">
          <h2 class="pt-10 underlined">B-OSA</h2>
          <p class="pt-10 bolded indent-50">
            Muutoksenhaku kadun ja eräiden yleisten alueiden kunnossa- ja
            puhtaanapidosta annetun lain 14 b §:n 2–3 momentin nojalla perittyyn
            maksuun:
          </p>
        </div>
        <div class="indent-50">
          <p class="pt-10">
            Maksuvelvollisella on oikeus tehdä 14 päivän kuluessa laskun saamisesta kirjallinen
            muistutus maksun perimisestä päättävälle kunnan viranomaiselle. Muistutus
            osoitetaan Helsingin kaupungin kaupunkiympäristön toimialan yleisten alueiden valvonta ja infraluvat -yksikön
            päällikölle. Asiointiosoite on seuraava:
          </p>
          <table class="contact-table">
            <tr>
              <td>Sähköpostiosoite:</td>
              <td>luvat@hel.fi</td>
            </tr>
            <tr>
              <td>Suojattu sähköposti:</td>
              <td>https://securemail.hel.fi/ (käytäthän aina suojattua sähköpostia, kun lähetät henkilökohtaisia tietojasi)</td>
            </tr>
            <tr>
              <td>Postiosoite:</td>
              <td>Helsingin kaupungin kaupunkiympäristön toimiala, yleisten alueiden valvonta ja infraluvat -yksikkö
                Helsingin kaupungin kirjaamo
                PL 10
                00099 HELSINGIN KAUPUNKI
              </td>
            </tr>
            <tr>
              <td>Käyntiosoite:</td>
              <td>Pohjoisesplanadi 11–13</td>
            </tr>
            <tr>
              <td>Puhelinnumero:</td>
              <td>09 310 13700</td>
            </tr>
          </table>
          <p class="pt-10">Kirjaamon aukioloaika on maanantaista perjantaihin klo 08.15–16.00.</p>
        </div>

        <div class="indent-50">
          <p class="pt-10 bolded">
            Kadun ja eräiden yleisten alueiden kunnossa- ja puhtaanapidosta annetun lain
            14 a §:n 3 momentin nojalla annettujen määräysten lainmukaisuuden
            ratkaistavaksi saattaminen:
          </p>
          <p class="pt-10">
            Kunnan antamien määräysten lainmukaisuus voidaan saattaa kunnan
            rakennusvalvontaviranomaisen ratkaistavaksi. Asia osoitetaan Helsingin kaupungin
            kaupunkiympäristölautakunnan ympäristö- ja lupajaostolle. Muistathan asioinnin
            yhteydessä mainita kirjaamisnumeron (esim. HEL 2021-000123), mikäli asiasi on jo
            vireillä Helsingin kaupungissa.
          </p>
          <p class="pt-10">Kaupunkiympäristölautakunnan ympäristö- ja lupajaoston asiointiosoite on seuraava:</p>
          <table class="contact-table">
            <tr>
              <td>Sähköpostiosoite:</td>
              <td>helsinki.kirjaamo@hel.fi</td>
            </tr>
            <tr>
              <td>Suojattu sähköposti:</td>
              <td>https://securemail.hel.fi/ (käytäthän aina suojattua sähköpostia, kun lähetät henkilökohtaisia tietojasi)</td>
            </tr>
            <tr>
              <td>Postiosoite:</td>
              <td>Kaupunkiympäristölautakunnan ympäristö- ja lupajaosto
                Helsingin kaupungin kirjaamo
                PL 10
                00099 HELSINGIN KAUPUNKI
              </td>
            </tr>
            <tr>
              <td>Käyntiosoite:</td>
              <td>Pohjoisesplanadi 11–13</td>
            </tr>
            <tr>
              <td>Puhelinnumero:</td>
              <td>09 310 13700</td>
            </tr>
          </table>
          <p class="pt-10">Kirjaamon aukioloaika on maanantaista perjantaihin klo 08.15–16.00.</p>
          <p class="pt-10">
            Mikäli asia saatetaan rakennusvalvontaviranomaisen ratkaistavaksi, ei työtä
            kuitenkaan saa tehdä vastoin kunnan antamia määräyksiä siihen saakka, kunnes
            määräyksiä on mahdollisesti lainvoimaisesti muutettu.
          </p>
        </div>
      </section>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
