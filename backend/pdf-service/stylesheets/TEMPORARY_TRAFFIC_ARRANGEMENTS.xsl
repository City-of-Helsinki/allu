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
          <h2>Päätöksen hakija</h2>
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
            <h2>Vastuuhenkilö</h2>
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

      <div class="unboxed">
        <section class="half-left">
          <h2>Liikennejärjestelyn kohde</h2>
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
        </section>
        <section class="half-right">
          <h2>Voimassaoloaika</h2>
          <p>
            <!-- [alkupvm]-[loppupvm] -->
            <!-- Kesto päivinä: [kesto] -->
            <xsl:value-of select="data/reservationStartDate"/>-
            <xsl:value-of select="data/reservationEndDate"/>
          </p>
          <p>
            Kesto päivinä:
            <xsl:value-of select="data/numReservationDays"/>
          </p>
        </section>
      </div>

      <section class="unboxed">
        <h2>Työn tarkoitus</h2>
        <p>
          <xsl:value-of select="data/workPurpose"/>
        </p>
      </section>

      <section class="unboxed">
        <h2>Liikennejärjestelypäätös</h2>
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

      <section class="unboxed">
        <h2>Päätösehdot</h2>
        <ol style="margin-top: 0px;">
          <li>Päätöksenhakija on velvollinen noudattamaan liikennejärjestelypäätöksessä määriteltyjä ehtoja</li>
          <li>Alueen käyttö muuhun kuin haettuun tarkoitukseen, tai muussa laajuudessa, tai ehtojen vastaisesti,
            johtaa laiminlyöntimaksuun, tai päätöksen purkamiseen</li>
          <li>Päätöksenhakijan suorittamien liikennejärjestelyjen sisältäessä puutteita päätöksenantaja voi korjata
            puutteet päätöksenhakijan kustannuksella, mikäli niitä ei kehotuksista huolimatta korjata</li>
          <li>Päätös ei oikeuta suorittamaan kaivu- tai louhintatöitä alueella</li>
          <li>Päätöksenhakija vastaa kaupungille ja kolmannelle osapuolelle aiheutuneista vahingoista</li>
          <li>Käytetyt alueet on ennallistettava välittömästi tilapäisten liikennejärjestelyjen päätyttyä</li>
        </ol>
        <p>
          Liikennejärjestelyjen suorittamista koskevat tekniset ohjeet ja määräykset on annettu tässä päätöksessä
          sekä ohjeissa ”Yleisten alueiden käyttö, tilapäiset liikennejärjestelyt ja katutyöt” ja
          ”Tilapäiset liikennejärjestelyt katu- ja yleisillä alueilla”. Ohjeista lisätietoa Helsingin kaupungin
          nettisivuilla osoitteessa https://www.hel.fi/fi/kaupunkiymparisto-ja-liikenne/tontit-ja-rakentamisen-luvat/tyomaan-luvat-ja-ohjeet/kaduilla-ja-puistoissa-tehtavat-tyot.
        </p>
      </section>

      <xsl:if test="data/additionalConditions">
        <section class="unboxed">
          <h2>Muut ehdot</h2>
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
          Päätös perustuu tieliikennelain (729/2018) ja tieliikenneasetuksen (379/2020) mukaisiin oikeuksiin asettaa liikennemerkki.
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
        <h2>Lupakäsittelijän yhteystiedot</h2>
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
