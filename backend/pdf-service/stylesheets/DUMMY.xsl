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

      <div>
        <section class="half-left">
          <h1>Päätöksen hakija</h1>
          <!-- <p>[Hakijan nimi], [Y-tunnus]<br/>[Osoite, postinumero, toimipaikka]<br/>
            [Sähköpostiosoite, puhelin]</p> -->
          <p>
            <xsl:for-each select="data/customerAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>

        <section class="half-right">
          <h1>Yhteyshenkilö</h1>
          <!-- <p>[Yhteyshenkilön nimi]<br/>[Sähköpostiosoite, puhelin]</p> -->
          <p>
            <xsl:for-each select="data/customerContactLines">
              <xsl:value-of select="."/><br/>
            </xsl:for-each>
          </p>
        </section>
      </div>

      <hr/>

      <div>

        <section class="half-left">
          <h1>Kohde</h1>
          <p>
            <!-- Käytetään vuokrattavaa paikkaa ja lohkoa, jos joku pudotusvalikossa määritetty paikka;
                 käytetään Osoitetta, jos ei pudotusvalikossa määritetty paikka -->
            <!-- [Vuokrattava paikka, Lohko], [Osoite] -->
            <xsl:value-of select="data/siteAddressLine"/>
          </p>
          <p>
            <!-- Käytetään, jos Lisätietoja paikasta täytetty -->
            <!-- [Lisätietoja paikasta] -->
            <xsl:value-of select="data/siteAdditionalInfo"/>
          </p>
          <p>
             Pinta-ala: <xsl:value-of select="data/siteArea" /> m<sup>2</sup><!-- [Alueen pinta-ala] -->
          </p>
        </section>

        <section class="half-right">
          <h1>Vuokra-aika</h1>

          <p class="text-flow">
            <!-- [Varauksen alkupäivämäärä] -->
            <xsl:value-of select="data/reservationStartDate"/>
            <xsl:if test="data/numReservationDays > 1">
              <!-- Käytetään, jos varauspäiviä enemmän kuin 1. -->
              <!-- -[Varauksen loppupäivämäärä] -->
              &#8204; &#x2013; <xsl:value-of select="data/reservationEndDate"/>
            </xsl:if>
          </p>
        </section>
      </div>

      <hr/>

      <div>
        <section class="half-left">
          <h1>Vuokrauksen tarkoitus</h1>
          <p>
            <!-- [Tapahtuman nimi]  -->
            <xsl:value-of select="data/eventName"/>
          </p>
          <p>
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
         </section>

        <section class="half-right">
          <h1>Vuokra</h1>
          <p>
            <!-- [Hinta] -->
            <xsl:value-of select="data/totalRent"/>
            <!-- alv 24 % tai alv 0 %, riippuen asiakkaasta -->
             + alv <xsl:value-of select="data/vatPercentage"/> %
          </p>
          <xsl:if test="data/notBillableReason != ''">
            <p>
              <!-- Käytetään, jos tapahtuma korvauksetta ja sille valittu peruste. Perusteen pidempi muoto erillisessä taulukossa. -->
              Hinnan peruste: <xsl:value-of select="data/notBillableReason"/>
            </p>
          </xsl:if>
           <xsl:if test="data/priceReason = ''">
            <p>
              <!-- Ei käytetä, jos hinta syötetty erikseen ja erilliset perustelut kirjoitettu. -->
              Vuokrauspäätöksen hinta perustuu kaupunkiympäristölautakunnan päätökseen 15.1.2019 § 15.
            </p>
          </xsl:if>
          <xsl:if test="data/separateBill = 'true'">
            <p>
              <!-- Käytetään, jos lasku enemmän kuin 0 €. -->
              Lasku lähetetään erikseen.
            </p>
          </xsl:if>
        </section>
      </div>
      
      <hr/>

      <div>
        <section>
          <h1>Ehdot</h1>
          <p>
            Liitteenä olevia ehtoja on noudatettava.
          </p>
          <xsl:if test="data/additionalConditions">
            <!-- Käytetään, jos Alluun on kirjoitettu vapaaseen
                 tekstikenttään lisäehtoja. -->
            <p class="text-flow">
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

        <section>
          <h1>Päätös</h1>
          <p>
            Hakija on hakenut oikeutta alueen käyttöön, toimittanut hakemuksen liitteineen rakennusvirastolle ja ilmoittanut sitoutuvansa alueen käyttöä koskevaan ohjeistukseen sekä sopimusehtoihin.
          </p>
          <p>
            Rakennustoimen johtosäännön 4 § 8 kohdan mukaan yleisten töiden lautakunnan tehtävänä on hyväksyä perusteet, joiden mukaan viranhaltija päättää vuokralle antamisesta ja muusta käyttöön luovuttamisesta. Yleisten töiden lautakunta on päätöksissään Ytlk 30.9.2014 § 364 ja Ytlk 11.11.2014 § 431 päättänyt nämä edellä mainitut perusteet.
          </p>
          <p>
            Rakennusviraston palveluosaston alueidenkäyttöpäällikkö päätti myöntää luvan hakijalle haetun alueen käyttämiseen yllä olevin ehdoin.
          </p>
          <p>
            Tämä päätös on sähköisesti allekirjoitettu.
          </p>
          <p>
            <!-- [aikaleima], [päättäjän työnimike], [päättäjän nimi] -->
            <xsl:value-of select="data/decisionTimestamp"/>,
            <xsl:value-of select="data/deciderTitle"/>,
            <xsl:value-of select="data/deciderName"/>
          </p>
        </section>
      </div>
    
      <div>
        <section class="half-left">
          <h1>Lisätiedot</h1>
          <p class="text-flow">
            Rakennusviraston palveluosaston alueidenkäyttö
          </p>
          <p>
            hkr.ulkoilma@hel.fi, 09 310 39869
          </p>
        </section>

        <section class="half-right">
          <h1>Käsittelijä</h1>
          <p>
            <!--  [titteli, tarkastajan nimi] -->
            <xsl:value-of select="data/handlerTitle"/>,
            <xsl:value-of select="data/handlerName"/>
          </p>
        </section>
      </div>

      <hr/>

      <div>
        <section class="half-left">
          <h1>Liitteet</h1>
          <p>
            <!--  [Lista liitteiden nimistä] -->
            <xsl:for-each select="data/attachmentNames">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>

        <section class="half-right">
          <h1>Muutoksenhaku</h1>
          <p>
            <!-- [Muutoksenhakuohjeet] -->
            <xsl:value-of select="data/appealInstructions"/>
          </p>
        </section>
      </div>

    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
