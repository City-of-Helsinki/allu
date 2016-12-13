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

      <div>
        <section class="half-left">
          <h1>Päätöksen hakija</h1>
          <!-- <p>[Hakijan nimi], [Y-tunnus]<br/>[Osoite, postinumero, toimipaikka]<br/>
            [Sähköpostiosoite, puhelin]</p> -->
          <p>
            <xsl:for-each select="data/applicantAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>

        <section class="half-right">
          <h1>Yhteyshenkilö</h1>
          <!-- <p>[Yhteyshenkilön nimi]<br/>[Sähköpostiosoite, puhelin]</p> -->
          <p>
            <xsl:for-each select="data/applicantContactLines">
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

          <xsl:if test="data/numBuildAndTeardownDays != 0">
            <p>
              <!-- Käytetään, jos rakentamis- ja purkamispäiviä täytetty -->
              Kokonaisvuokra-aika:
              <!-- [Rakentamisen alkupäivämäärä]-[Purkamisen loppupäivämäärä] -->
              <xsl:value-of select="data/buildStartDate" /> &#x2013;
              <xsl:value-of select="data/teardownEndDate" />
            </p>
          </xsl:if>
          <p class="text-flow">
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
            <p class="text-flow">
              Rakentamispäivä(t):
              <xsl:value-of select="data/buildStartDate"/><!-- [Rakentamisen alkupäivämäärä] -->
              <xsl:if test="data/buildStartDate != data/buildEndDate">
              &#8204; &#x2013; <xsl:value-of select="data/buildEndDate"/>
              </xsl:if><!-- - [Tapahtuman alkupäivämäärä-1] -->
             </p>
          </xsl:if>
          <xsl:if test="data/teardownStartDate != ''">
            <p class="text-flow">
              Purkupäivä(t): <xsl:value-of select="data/teardownStartDate"/><!-- [Tapahtuman loppupäivämäärä+1] -->
              <xsl:if test="data/tearDownStartDate != data/teardownEndDate">
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
            <p>
              <!-- Käytetään, jos Tapahtuma-ajan poikkeukset –kenttä täytetty -->
              <!-- [Tapahtuma-ajan poikkeukset] -->
              <xsl:value-of select="data/reservationTimeExceptions"/>
            </p>
          </xsl:if>
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
          <p>
            <!-- Ulkoilmatapahtuma/Avoin = Yleisölle pääsymaksuton tapahtuma; Ulkoilmatapahtuma/Maksullinen = Yleisölle pääsymaksullinen tapahtuma; Ulkoilmatapahtuma/Suljettu = Kutsuvierastilaisuus tai muu vastaava suljettu tapahtuma; Promootio = Promootiotapahtuma; Vaalit = Vaalit -->
            <!-- [Tapahtuman luonne] -->
            <xsl:value-of select="data/eventNature"/>
          </p>
          <xsl:if test="data/structureArea != ''">
            <!-- Käytetään, jos tapahtuma sisältää rakenteita. -->
            <p>
              Tapahtuma sisältää
              <!-- [rakenteiden kokonaisneliömäärä] -->
              <xsl:value-of select="data/structureArea" /> m<sup>2</sup>
              rakenteita.
            </p>
            <p>
              <!-- [Rakenteiden kuvaus] -->
              <xsl:value-of select="data/structureDescription" />
            </p>
          </xsl:if>
        </section>

        <section class="half-right">
          <h1>Vuokra</h1>
          <p>
            <!-- [Hinta] -->
            <xsl:value-of select="data/totalRent"/>
            <!-- alv 24 % tai alv 0 %, riippuen asiakkaasta -->
             + alv <xsl:value-of select="data/vatPercentage"/> %
          </p>
          <xsl:if test="data/priceReason != ''">
            <p>
              <!-- Käytetään, jos tapahtuma korvauksetta ja sille valittu peruste. Perusteen pidempi muoto erillisessä taulukossa. -->
              Hinnan peruste: <xsl:value-of select="data/priceReason"/>
            </p>
          </xsl:if>
          <xsl:if test="data/hasCommercialActivities = 'true'">
            <p>
              <!-- Käytetään, jos ”Kaupallista toimintaa (+50)” ruksittu -->
              Tapahtumassa on lisäksi tapahtuman teemaan sisältymätöntä myynti- tai mainostoimintaa, minkä vuoksi tapahtuma-aluekohtaisesta listahinnasta veloitetaan 50 %.
            </p>
          </xsl:if>
          <xsl:if test="data/sportsWithHeavyStructures = 'true'">
            <p>
              <!-- Käytetään, jos ”urheilutapahtuma raskailla rakenteilla (+50%) ruksittu -->
              Yleisölle avoimesta, maksuttomasta urheilutapahtumasta, jossa käytetään raskaita rakenteita tai joka on osallistujille maksullinen, veloitetaan tapahtuma-aluekohtaisesta listahinnasta 50 %.
            </p>
          </xsl:if>
          <xsl:if test="data/hasEkokompassi = 'true'">
            <p>
              <!-- Käytetään, jos ”Ekokompassi (-30%) ruksittu. -->
              Hakijalla on Ekokompassi tapahtuma -sertifikaatti, joka oikeuttaa 30 % hinnanalennukseen.
            </p>
          </xsl:if>
          <p>
            Vuokrauspäätöksen hinta perustuu yleisten töiden lautakunnan päätökseen 11.11.2014 § 431.
          </p>
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
          <xsl:if test="data/additionalConditions != ''">
            <!-- Käytetään, jos Alluun on kirjoitettu vapaaseen tekstikenttään lisäehtoja. -->
            <p class="text-flow">
              Lisäksi on noudatettava seuraavia ehtoja:
            </p>
            <p>
              <!-- [Ehtokentän teksti]  -->
              <xsl:value-of select="data/additionalConditions"/>
            </p>
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
