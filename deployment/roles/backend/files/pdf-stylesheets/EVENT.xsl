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
        <section class="half-left">
          <h2>Päätöksen hakija</h2>
          <!-- <p>[Hakijan nimi], [Y-tunnus]<br/>[Osoite, postinumero, toimipaikka]<br/>
            [Sähköpostiosoite, puhelin]</p> -->
          <p>
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
      </div>

      <div class="unboxed">
        <section class="half-left">
          <h2>Kohde</h2>
          <p>
            <!-- Käytetään vuokrattavaa paikkaa ja lohkoa, jos joku pudotusvalikossa määritetty paikka;
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
          <p class="space-above">
             Pinta-ala: <xsl:value-of select="data/siteArea" /> m<sup>2</sup><!-- [Alueen pinta-ala] -->
          </p>
        </section>

        <section class="half-right">
          <h2>Vuokra-aika</h2>

          <xsl:if test="data/numBuildAndTeardownDays != 0">
            <p>
              <!-- Käytetään, jos rakentamis- ja purkamispäiviä täytetty -->
              Kokonaisvuokra-aika:
              <!-- [Varauksen alkupäivämäärä]-[Varauksen loppupäivämäärä] -->
              <xsl:value-of select="data/reservationStartDate"/>
              <xsl:if test="data/numReservationDays > 1">
                <!-- Käytetään, jos varauspäiviä enemmän kuin 1. -->
                <!-- -[Varauksen loppupäivämäärä] -->
                &#8204; &#x2013; <xsl:value-of select="data/reservationEndDate"/>
              </xsl:if>
            </p>
          </xsl:if>
          <p class="space-above">
            Tapahtumapäivä(t):
            <!-- [Tapahtuman alkupäivämäärä] -->
            <xsl:value-of select="data/eventStartDate"/>
            <xsl:if test="data/numEventDays > 1">
              <!-- Käytetään, jos tapahtumapäiviä enemmän kuin 1. -->
              <!-- -[Tapahtuman loppupäivämäärä] -->
              &#8204; &#x2013; <xsl:value-of select="data/eventEndDate"/>
            </xsl:if>
          </p>

          <xsl:if test="data/numEventDays > 1">
            <p>
              <!-- Käytetään, jos tapahtumapäiviä enemmän kuin 1. -->
              Tapahtumapäiviä: <xsl:value-of select="data/numEventDays"/><!-- [tapahtumapäivien lukumäärä] -->
            </p>
          </xsl:if>

          <xsl:if test="data/buildStartDate != ''">
            <!-- Käytetään, jos rakentamis- ja purkupäiviä täytetty. Jos
              rakentamis- tai purkupäiviä vain yksi, vain yksi päivämäärä
              näkyy päätöksessä (ei päättymispäivämäärää) -->
            <p class="space-above">
              Rakentamispäivä(t):
              <xsl:value-of select="data/buildStartDate"/><!-- [Rakentamisen alkupäivämäärä] -->
              <xsl:if test="data/buildStartDate != data/buildEndDate">
              &#8204; &#x2013; <xsl:value-of select="data/buildEndDate"/>
              </xsl:if><!-- - [Tapahtuman alkupäivämäärä-1] -->
             </p>
          </xsl:if>
          <xsl:if test="data/teardownEndDate != ''">
            <p>
              Purkupäivä(t): <xsl:value-of select="data/teardownStartDate"/><!-- [Tapahtuman loppupäivämäärä+1] -->
              <xsl:if test="data/teardownStartDate != data/teardownEndDate">
              &#8204; &#x2013; <xsl:value-of select="data/teardownEndDate"/>
              </xsl:if><!-- -[Purkamisen loppupäivämäärä] -->
            </p>
          </xsl:if>
          <xsl:if test="data/numBuildAndTeardownDays > 0">
            <p>
              Rakentamis- ja purkupäiviä: <xsl:value-of select="data/numBuildAndTeardownDays"/>
              <!-- [rakentamis- ja purkupäivien lukumäärä] -->
            </p>
          </xsl:if>
          <xsl:if test="data/reservationTimeExceptions != ''">
            <p class="space-above">
              <!-- Käytetään, jos Tapahtuma-ajan poikkeukset –kenttä täytetty -->
              <!-- [Tapahtuma-ajan poikkeukset] -->
              <xsl:value-of select="data/reservationTimeExceptions"/>
            </p>
          </xsl:if>
        </section>
      </div>

      <div class="unboxed">
        <section class="half-left">
          <h2>Vuokrauksen tarkoitus</h2>
          <p>
            <!-- [Tapahtuman nimi]  -->
            <xsl:value-of select="data/eventName"/>
          </p>
          <p class="space-above">
            <!--  [Tapahtuman kuvaus]  -->
            <xsl:value-of select="data/eventDescription"/>
            <xsl:if test="data/eventUrl != ''">,
              <xsl:element name="a">
                <xsl:attribute name="href">
                  <xsl:value-of select="data/eventUrl"/>
                </xsl:attribute>
                <xsl:value-of select="data/eventUrl"/>
              </xsl:element>
            </xsl:if>
          </p>
          <p class="space-above">
            <!-- Ulkoilmatapahtuma/Avoin = Yleisölle pääsymaksuton tapahtuma; Ulkoilmatapahtuma/Maksullinen = Yleisölle pääsymaksullinen tapahtuma; Ulkoilmatapahtuma/Suljettu = Kutsuvierastilaisuus tai muu vastaava suljettu tapahtuma; Promootio = Promootiotapahtuma; Vaalit = Vaalit -->
            <!-- [Tapahtuman luonne] -->
            <xsl:value-of select="data/eventNature"/>
          </p>
          <xsl:if test="data/structureArea > 0">
            <!-- Käytetään, jos tapahtuma sisältää rakenteita. -->
            <p class="space-above">
              Tapahtuma sisältää
              <!-- [rakenteiden kokonaisneliömäärä] -->
              <xsl:value-of select="data/structureArea" /> m<sup>2</sup>
              rakenteita.
            </p>
            <p class="space-above">
              <!-- [Rakenteiden kuvaus] -->
              <xsl:value-of select="data/structureDescription" />
            </p>
          </xsl:if>
        </section>

        <section class="half-right">
          <h2>Vuokra</h2>
          <xsl:choose>
            <xsl:when test="data/notBillable = 'false'">
              <p>
                <!-- [Hinta] -->
                <xsl:value-of select="data/totalRent"/>
                <!-- alv 24 % tai alv 0 %, riippuen asiakkaasta -->
                 + ALV <xsl:value-of select="data/vatPercentage"/> %
              </p>
              <!-- Hinnan peruste tyyppikohtaisesti. Erillinen lista -->
              <!-- Confluencessa: -->
              <p class="space-above">
                <!-- [Hinnan peruste, raakaa HTML:ää] -->
                <xsl:value-of select="data/priceBasisText" disable-output-escaping="yes"/>
              </p>
            </xsl:when>
            <xsl:otherwise>
              <p>Korvauksetta.</p>
              <p class="space-above">
                Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
              </p>
            </xsl:otherwise>
          </xsl:choose>
          <p class="space-above">
            Vuokrauspäätöksen hinta perustuu yleisten töiden lautakunnan päätökseen 11.11.2014 § 431.
          </p>
          <xsl:if test="data/notBillable = 'false' and data/separateBill = 'true'">
            <p class="space-above">
              <!-- Käytetään, jos lasku enemmän kuin 0 €. -->
              Lasku lähetetään erikseen.
            </p>
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
        <p>
          Liitteenä olevia ehtoja on noudatettava.
        </p>
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
            </p>
          </xsl:for-each>
        </xsl:if>
      </section>

      <section class="unboxed">
        <h2>Päätös</h2>
        <p>
          Hakija on hakenut oikeutta alueen käyttöön, toimittanut hakemuksen liitteineen asukas- ja yrityspalveluihin ja ilmoittanut sitoutuvansa alueen käyttöä koskevaan ohjeistukseen sekä sopimusehtoihin.
        </p>
        <p class="space-above">
          Alueidenkäyttö ja -valvontayksikön tiimipäällikkö päätti myöntää luvan hakijalle haetun alueen käyttämiseen yllä olevin ehdoin.
        </p>
        <p class="space-above">
          Tämä päätös on sähköisesti allekirjoitettu.
        </p>
        <p class="space-above">
          <!-- [aikaleima], [päättäjän työnimike], [päättäjän nimi] -->
          <xsl:value-of select="data/decisionTimestamp"/>,
          <xsl:value-of select="data/deciderTitle"/>,
          <xsl:value-of select="data/deciderName"/>
        </p>
      </section>

      <div class="unboxed">
        <section class="half-left">
          <h2>Lisätiedot</h2>
          <p>
            Alueidenkäyttö ja -valvontayksikkö
          </p>
          <p>
            ulkoilma@hel.fi
          </p>
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

      <section class="unboxed">
        <h2>Liitteet</h2>
        <p>
          <!--  [Lista liitteiden nimistä] -->
          <xsl:for-each select="data/attachmentNames">
            <xsl:value-of select="." /><br/>
          </xsl:for-each>
        </p>
      </section>

    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
