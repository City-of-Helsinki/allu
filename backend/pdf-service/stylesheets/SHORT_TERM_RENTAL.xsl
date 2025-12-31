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
        <section class="half-left">
          <h1>Päätöksen hakija</h1>
          <!-- <p>[Hakijan nimi], [Y-tunnus]</p>
                 <p>[Osoite, postinumero, toimipaikka]</p>
            <p>  [Sähköpostiosoite, puhelin]</p> -->
          <xsl:for-each select="data/customerAddressLines">
            <p><xsl:value-of select="." /></p>
          </xsl:for-each>
        </section>

        <section class="half-right">
          <xsl:if test="data/customerContactLines != '' and data/anonymizedDocument = 'false'">
            <h1>Yhteyshenkilö</h1>
            <!-- <p>[Yhteyshenkilön nimi]</p>
                 <p>[Sähköpostiosoite, puhelin]</p> -->
             <xsl:for-each select="data/customerContactLines">
               <p><xsl:value-of select="."/></p>
             </xsl:for-each>
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

      <div class="unboxed">
        <section class="half-left">
          <h1>Kohde</h1>
          <p>
            <!-- Käytetään vuokrattavaa paikkaa ja lohkoa, jos joku
                 pudotusvalikossa määritetty paikka;
                 käytetään Osoitetta, jos ei pudotusvalikossa määritetty paikka -->
            <!-- [Vuokrattava paikka, Lohko], [Osoite] -->
            <xsl:value-of select="data/siteAddressLine"/>
          </p>
          <xsl:if test="data/siteAdditionalInfo != ''">
            <p class="pt-10">
              <!-- Käytetään, jos Lisätietoja paikasta täytetty -->
              <!-- [Lisätietoja paikasta] -->
              <xsl:value-of select="data/siteAdditionalInfo"/>
            </p>
          </xsl:if>
          <!-- Ei tarvita, jos banderolli: -->
          <xsl:if test="data/siteArea != '' and data/siteArea != 0">
            <p class="pt-10">
               Pinta-ala: <xsl:value-of select="data/siteArea" /> m<sup>2</sup><!-- [Alueen pinta-ala] -->
            </p>
          </xsl:if>
        </section>

        <section class="half-right">
          <h1>Vuokra-aika</h1>

          <p class="text-flow">
            <!-- [Varauksen alkupäivämäärä] -->
            <xsl:choose>
              <xsl:when test="data/recurringEndTime = ''">
                <xsl:value-of select="data/reservationStartDate"/>
                <xsl:if test="data/numReservationDays > 1">
                  <!-- Käytetään, jos varauspäiviä enemmän kuin 1. -->
                  <!-- -[Varauksen loppupäivämäärä] -->
                  &#8211; <xsl:value-of select="data/reservationEndDate"/>
                </xsl:if>
              </xsl:when>
              <!-- Toistuva varaus -->
              <xsl:otherwise>
                Käyttöoikeus vuosittain <xsl:value-of select="data/reservationStartDayMonth"/>.
                &#8211; <xsl:value-of select="data/reservationEndDayMonth"/>.
                <br/>
                <xsl:choose>
                  <xsl:when test="data/recurringIndefinitely = 'true'">
                    vuodesta <xsl:value-of select="data/reservationStartYear"/> lähtien
                  </xsl:when>
                <xsl:otherwise>
                  vuosina <xsl:value-of select="data/reservationStartYear"/> &#8211; <xsl:value-of select="data/recurringEndYear"/>
                </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
          </p>
        </section>
      </div>

      <div class="unboxed">
        <section class="half-left">
          <h1>Vuokrauksen tarkoitus</h1>
          <p>
            <!-- [Hakemuksen tyyppi ja laji]  -->
            <xsl:value-of select="data/eventNature"/>
          </p>
          <p class="pt-10">
            <!-- [Vuokrauksen nimi]  -->
            <xsl:value-of select="data/eventName"/>
          </p>
          <p class="pt-10">
            <!--  [Vuokrauksen kuvaus]  -->
            <xsl:value-of select="data/eventDescription"/>
          </p>
        </section>

        <section class="half-right">
          <h1>Vuokra</h1>
          <xsl:choose>
            <xsl:when test="data/notBillable = 'false'">
              <p>
                <!-- [Hinta] -->
                <xsl:value-of select="data/totalRent"/>
                <!-- alv 25,5 % tai alv 0 %, riippuen asiakkaasta -->
                 + ALV <xsl:value-of select="data/vatPercentage"/> %
              </p>
            </xsl:when>
            <xsl:otherwise>
              <p>Korvauksetta.</p>
              <p class="pt-10">
                Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
              </p>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="data/eventNature = 'Lyhytaikainen maanvuokraus, Liikkuva myynti/myyntiautot ja -vaunut'">
              <p class="pt-10">Vuokrauspäätöksen hinta perustuu kaupunkiympäristölautakunnan päätökseen 20.5.2025 § 307.</p>
            </xsl:when>
            <xsl:otherwise>
              <p class="pt-10">Vuokrauspäätöksen hinta perustuu kaupunkiympäristölautakunnan päätökseen 15.5.2018 § 238
                tai 15.1.2019 § 15 tai 1.2.2022 § 51.</p>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:if test="data/notBillable = 'false' and data/separateBill = 'true'">
            <!-- Käytetään, jos lasku enemmän kuin 0 €: -->
            <p class="pt-10">Lasku lähetetään erikseen.</p>
          </xsl:if>
        </section>
      </div>

      <xsl:if test="data/notBillable = 'false' and data/chargeInfoEntries">
        <section class="unboxed">
          <h1>Vuokran erittely</h1>

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
        </section>
      </xsl:if>

      <section class="unboxed">
        <h1>Ehdot</h1>
        <p>Liitteenä olevia ehtoja on noudatettava.</p>

        <xsl:if test="data/additionalConditions">
          <!-- Käytetään, jos Alluun on kirjoitettu vapaaseen
               tekstikenttään lisäehtoja. -->
          <p class="pt-10">
            Lisäksi on noudatettava seuraavia ehtoja:
          </p>
          <xsl:for-each select="data/additionalConditions">
            <p>
              <!-- [Ehtokentän teksti]  -->
              <xsl:value-of select="."/>
              <xsl:if test=". = ''">
                <br/>
              </xsl:if>
            </p>
          </xsl:for-each>
        </xsl:if>
      </section>

      <section class="unboxed avoid-pb">
        <h1>Päätös</h1>
        <p>
          Hakija on hakenut oikeutta alueen käyttöön, toimittanut
          hakemuksen liitteineen kaupunkitilan käyttö ja maanvuokraus –yksikköön ja
          ilmoittanut sitoutuvansa alueen käyttöä koskevaan
          ohjeistukseen sekä sopimusehtoihin.
        </p>

        <p class="pt-10">Allekirjoittanut viranhaltija
          päätti myöntää luvan hakijalle haetun alueen käyttämiseen yllä olevin ehdoin.</p>

        <p class="pt-10">Tämä päätös on sähköisesti
          allekirjoitettu.</p>

        <p class="pt-10">
          <!-- [aikaleima], [päättäjän työnimike], [päättäjän nimi] -->
          <xsl:value-of select="data/decisionTimestamp"/>,
          <xsl:value-of select="data/deciderTitle"/>,
          <xsl:value-of select="data/deciderName"/>
        </p>
      </section>

      <div class="unboxed avoid-pb">
        <section class="half-left">
          <h1>Lisätiedot</h1>
          <p>Kaupunkitilan käyttö ja maanvuokraus –yksikkö</p>
        </section>

        <section class="half-right">
          <h1>Käsittelijä</h1>
          <p>
            <!--  [titteli, tarkastajan nimi] -->
            <xsl:value-of select="data/handlerTitle"/>,
            <xsl:value-of select="data/handlerName"/>
          </p>
          <p>
            <xsl:value-of select="data/handlerEmail"/>
          </p>
        </section>
      </div>

      <section class="unboxed avoid-pb">
        <h1>Liitteet</h1>
        <!--  [Lista liitteiden nimistä] -->
        <xsl:for-each select="data/attachmentNames">
          <p><xsl:value-of select="." /></p>
        </xsl:for-each>
      </section>

      <xsl:if test="data/distributionNames and data/anonymizedDocument = 'false'">
        <section class="unboxed avoid-pb">
          <h1>Päätöksen jakelu</h1>
          <!--  [Lista päätöksen jakelusta] -->
          <xsl:for-each select="data/distributionNames">
            <p><xsl:value-of select="." /></p>
          </xsl:for-each>
        </section>
      </xsl:if>

      <!-- Muutoksenhakuohjeet -->
      <section class="new-page">
        <h1>MUUTOKSENHAKUOHJEET</h1>
        <h2 class="pt-10">OHJEET OIKAISUVAATIMUKSEN TEKEMISEKSI</h2>
        <div class="indent-50">
          <p class="pt-10 indent-50">
            Tähän päätökseen tyytymätön voi tehdä kirjallisen oikaisuvaatimuksen.
            Päätökseen ei saa hakea muutosta valittamalla tuomioistuimeen.
          </p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimusoikeus</h3>
          <p class="indent-50">Oikaisuvaatimuksen saa tehdä</p>
          <ul class="align-with-p">
            <li class="mt-0">
              se, johon päätös on kohdistettu tai jonka oikeuteen, velvollisuuteen
              tai etuun päätös välittömästi vaikuttaa (asianosainen)
            </li>
            <li class="mt-0">kunnan jäsen.</li>
          </ul>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimusaika</h3>
          <p class="indented">Oikaisuvaatimus on tehtävä 14 päivän kuluessa päätöksen tiedoksisaannista.</p>
          <p class="pt-10 indent-50">
            Oikaisuvaatimuksen on saavuttava Helsingin kaupungin kirjaamoon
            määräajan viimeisenä päivänä ennen kirjaamon aukioloajan päättymistä.
          </p>
          <p class="pt-10 indent-50">
            Mikäli päätös on annettu tiedoksi postitse, asianosaisen katsotaan saaneen päätöksestä tiedon,
            jollei muuta näytetä, seitsemän päivän kuluttua kirjeen
            lähettämisestä. Kunnan jäsenen katsotaan saaneen päätöksestä tiedon, kun pöytäkirja on asetettu yleisesti nähtäväksi.
          </p>
          <p class="pt-10 indent-50">
            Mikäli päätös on annettu tiedoksi sähköisenä viestinä, asianosaisen
            katsotaan saaneen päätöksestä tiedon, jollei muuta näytetä, kolmen
            päivän kuluttua viestin lähettämisestä.
          </p>
          <p class="pt-10 indent-50">
            Tiedoksisaantipäivää ei lueta oikaisuvaatimusaikaan. Jos oikaisuvaatimusajan viimeinen päivä on pyhäpäivä, itsenäisyyspäivä, vapunpäivä,
            joulu- tai juhannusaatto tai arkilauantai, saa oikaisuvaatimuksen tehdä
            ensimmäisenä arkipäivänä sen jälkeen.
          </p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimusviranomainen</h3>
          <p class="indent-50">
            Viranomainen, jolle oikaisuvaatimus tehdään, on Helsingin kaupungin
            kaupunkiympäristölautakunta.
          </p>
          <p class="pt-10 indent-50">Oikaisuvaatimusviranomaisen asiointiosoite on seuraava:</p>
          <table class="contact-table indent-50">
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
              <td>Faksinumero:</td>
              <td>(09) 655 783</td>
            </tr>
            <tr>
              <td>Käyntiosoite:</td>
              <td>Pohjoisesplanadi 11–13</td>
            </tr>
            <tr>
              <td>Puhelinnumero:</td>
              <td>(09) 310 13700 (Yleishallinto)</td>
            </tr>
          </table>
          <p class="pt-10 indent-50">Kirjaamon aukioloaika on maanantaista perjantaihin klo 08.15–16.00.</p>
        </div>

        <div class="indent-50 avoid-pb">
          <h3 class="ml-0">Oikaisuvaatimuksen muoto ja sisältö</h3>
          <p class="indent-50">
            Oikaisuvaatimus on tehtävä kirjallisena. Myös sähköinen asiakirja täyttää
            vaatimuksen kirjallisesta muodosta.
          </p>
          <p class="pt-10 indent-50">Oikaisuvaatimuksessa on ilmoitettava</p>
          <ul class="mb-0 align-with-p">
            <li class="mt-0">päätös, johon oikaisuvaatimus kohdistuu</li>
            <li class="mt-0">miten päätöstä halutaan oikaistavaks</li>
            <li class="mt-0">millä perusteella päätöstä halutaan oikaistavaksi</li>
            <li class="mt-0">oikaisuvaatimuksen tekijä</li>
            <li class="mt-0">millä perusteella oikaisuvaatimuksen tekijä on oikeutettu tekemään vaatimuksen</li>
            <li class="mt-0">oikaisuvaatimuksen tekijän yhteystiedot</li>
          </ul>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Tietopyynnöt</h3>
          <p class="indent-50">Päätöstä koskevia otteita ja liitteitä lähetetään pyynnöstä. Asiakirjoja voi tilata Helsingin kaupungin kirjaamosta.</p>
        </div>
      </section>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
