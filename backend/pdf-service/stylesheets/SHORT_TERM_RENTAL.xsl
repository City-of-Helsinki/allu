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
          <h2>Päätöksen hakija</h2>
          <!-- <p>[Hakijan nimi], [Y-tunnus]</p>
                 <p>[Osoite, postinumero, toimipaikka]</p>
            <p>  [Sähköpostiosoite, puhelin]</p> -->
          <xsl:for-each select="data/customerAddressLines">
            <p><xsl:value-of select="." /></p>
          </xsl:for-each>
        </section>

        <section class="half-right">
          <xsl:if test="data/customerContactLines != '' and data/anonymizedDocument = 'false'">
            <h2>Yhteyshenkilö</h2>
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

      <div class="unboxed">
        <section class="half-left">
          <h2>Kohde</h2>
          <p>
            <!-- Käytetään vuokrattavaa paikkaa ja lohkoa, jos joku
                 pudotusvalikossa määritetty paikka;
                 käytetään Osoitetta, jos ei pudotusvalikossa määritetty paikka -->
            <!-- [Vuokrattava paikka, Lohko], [Osoite] -->
            <xsl:value-of select="data/siteAddressLine"/>
          </p>
          <xsl:if test="data/siteAdditionalInfo != ''">
            <p class="space-above">
              <!-- Käytetään, jos Lisätietoja paikasta täytetty -->
              <!-- [Lisätietoja paikasta] -->
              <xsl:value-of select="data/siteAdditionalInfo"/>
            </p>
          </xsl:if>
          <!-- Ei tarvita, jos banderolli: -->
          <xsl:if test="data/siteArea != '' and data/siteArea != 0">
            <p class="space-above">
               Pinta-ala: <xsl:value-of select="data/siteArea" /> m<sup>2</sup><!-- [Alueen pinta-ala] -->
            </p>
          </xsl:if>
        </section>

        <section class="half-right">
          <h2>Vuokra-aika</h2>

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
          <h2>Vuokrauksen tarkoitus</h2>
          <p>
            <!-- [Hakemuksen tyyppi ja laji]  -->
            <xsl:value-of select="data/eventNature"/>
          </p>
          <p class="space-above">
            <!-- [Vuokrauksen nimi]  -->
            <xsl:value-of select="data/eventName"/>
          </p>
          <p class="space-above">
            <!--  [Vuokrauksen kuvaus]  -->
            <xsl:value-of select="data/eventDescription"/>
          </p>
        </section>

        <section class="half-right">
          <h2>Vuokra</h2>
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
              <p class="space-above">
                Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
              </p>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:choose>
            <xsl:when test="data/eventNature = 'Lyhytaikainen maanvuokraus, Liikkuva myynti/myyntiautot ja -vaunut'">
              <p class="space-above">Vuokrauspäätöksen hinta perustuu kaupunkiympäristölautakunnan päätökseen 20.5.2025 § 307.</p>
            </xsl:when>
            <xsl:otherwise>
              <p class="space-above">Vuokrauspäätöksen hinta perustuu kaupunkiympäristölautakunnan päätökseen 15.5.2018 § 238
                tai 15.1.2019 § 15 tai 1.2.2022 § 51.</p>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:if test="data/notBillable = 'false' and data/separateBill = 'true'">
            <!-- Käytetään, jos lasku enemmän kuin 0 €: -->
            <p class="space-above">Lasku lähetetään erikseen.</p>
          </xsl:if>
        </section>
      </div>

      <xsl:if test="data/notBillable = 'false' and data/chargeInfoEntries">
        <section class="unboxed">
          <h2>Vuokran erittely</h2>

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
        <h2>Ehdot</h2>
        <p>Liitteenä olevia ehtoja on noudatettava.</p>

        <xsl:if test="data/additionalConditions">
          <!-- Käytetään, jos Alluun on kirjoitettu vapaaseen
               tekstikenttään lisäehtoja. -->
          <p class="space-above">
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
        <h2>Päätös</h2>
        <p>
          Hakija on hakenut oikeutta alueen käyttöön, toimittanut
          hakemuksen liitteineen asukas- ja yrityspalveluihin ja
          ilmoittanut sitoutuvansa alueen käyttöä koskevaan
          ohjeistukseen sekä sopimusehtoihin.
        </p>

        <p class="space-above">Alueiden käyttö ja –valvontayksikön tapahtumat ja maanvuokraus –tiimin tiimipäällikkö
          päätti myöntää luvan hakijalle haetun alueen käyttämiseen yllä olevin ehdoin.</p>

        <p class="space-above">Tämä päätös on sähköisesti
          allekirjoitettu.</p>

        <p class="space-above">
          <!-- [aikaleima], [päättäjän työnimike], [päättäjän nimi] -->
          <xsl:value-of select="data/decisionTimestamp"/>,
          <xsl:value-of select="data/deciderTitle"/>,
          <xsl:value-of select="data/deciderName"/>
        </p>
      </section>

      <div class="unboxed avoid-pb">
        <section class="half-left">
          <h2>Lisätiedot</h2>
          <p>Alueidenkäyttö ja -valvontayksikkö</p>
          <p>ulkoilma@hel.fi</p>
        </section>

        <section class="half-right">
          <h2>Käsittelijä</h2>
          <p>
            <!--  [titteli, tarkastajan nimi] -->
            <xsl:value-of select="data/handlerTitle"/>,
            <xsl:value-of select="data/handlerName"/>
          </p>
        </section>
      </div>

      <section class="unboxed avoid-pb">
        <h2>Liitteet</h2>
        <!--  [Lista liitteiden nimistä] -->
        <xsl:for-each select="data/attachmentNames">
          <p><xsl:value-of select="." /></p>
        </xsl:for-each>
      </section>

      <xsl:if test="data/distributionNames">
        <section class="unboxed avoid-pb">
          <h2>Päätöksen jakelu</h2>
          <!--  [Lista päätöksen jakelusta] -->
          <xsl:for-each select="data/distributionNames">
            <p><xsl:value-of select="." /></p>
          </xsl:for-each>
        </section>
      </xsl:if>

    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
