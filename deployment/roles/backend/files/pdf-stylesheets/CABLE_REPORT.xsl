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
      <div class="boxed">
        <h1>TÄMÄ SELVITYS JA KARTAT ON ANNETTAVA KAIVUTYÖTÄ
          SUORITTAVALLE</h1>
        <p>Johtoselvitys on voimassa 
          <!-- [voimassa]  -->
          <xsl:value-of select="data/cableReportValidUntil"/> asti (1kk)</p>
      </div>

      <div class="boxed">
        <section>
          <div class="half-left">
            <h2>Johtoselvityksen tilaaja</h2>
            <p><!-- [Valittu johtoselvityksen tilaaja. (nimi, s.posti ja
              puhelin)]  -->
              <xsl:for-each select="data/customerContactLines">
                <xsl:value-of select="."/>
                <xsl:if test="position() != last()">, </xsl:if>
              </xsl:for-each>
              </p>
          </div>

          <div class="half-right">
            <h1>Kaivajayritys/Toimija</h1>
            <p><!-- [Kaivajayritys/Toimija (nimi)] -->
              <xsl:for-each select="data/customerAddressLines">
                <xsl:value-of select="."/>
                <xsl:if test="position() != last()">, </xsl:if>
              </xsl:for-each>
            </p>
          </div>
        </section>
      </div>

      <div class="boxed">
        <section>
          <h2>Työn kuvaus</h2>
          <div class="half-left">
            <p><!-- [Työn kuvaus] -->
              <xsl:value-of select="data/workDescription"/></p>
          </div>

          <div class="half-right">
            <p><!-- [Kohteen osoite tai lisätietoja paikasta] -->
              <xsl:choose>
                <xsl:when test="data/siteAddressLine != ''">
                  <xsl:value-of select="data/siteAddressLine"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="data/siteAdditionalInfo"/>
                </xsl:otherwise>
              </xsl:choose>
            </p>
            <p>Arvioitu aloitus:<!-- [Alkupäivämäärä]  -->
              <xsl:value-of select="data/reservationStartDate"/>
            </p>
            <p>Arvioitu valmistuminen:<!-- [Loppupäivämäärä] -->
              <xsl:value-of select="data/reservationEndDate"/>
            </p>
          </div>
        </section>
      </div>

      <div class="boxed">
        <section>
          <h2>Yleiset ehdot</h2>
          <ul>
            <li>Johtoselvityksen saaja on velvollinen valvomaan
              työalueen johtokarttaan vaikuttavia muutoksia (työalueella
              tapahtuvat muut työt). Johtoselvitys on tarvittaessa
              uusittava.</li>
            <li>Johdon sijainnin ollessa epävarma (~) johdon
              maastonäyttö tilattava johdon omistajalta.</li>
            <li>Selvitys sisältää karttaotteita<!-- [karttaotteiden lkm] -->
              <xsl:value-of select="data/mapExtractCount"/>
              kpl.</li>
            <li>Kiinteistöjen johtotiedot sekä tonttiliitokset
              puuttuvat pääosin johtokartalta.</li>
            <li>Kaivualueen rajauksen ylitys edellyttää aina uutta
              johtoselvitystä.</li>
          </ul>
        </section>
      </div>

      <div class="boxed" style="min-height: 200px">
        <section>
          <h2>Kaivualueella olevat johdot</h2>
          <p><ul>
            <xsl:for-each select="data/cableInfoEntries">
            <li>
            <b><xsl:value-of select="type"/>:</b>
            <xsl:value-of select="text"/>
            </li>
            </xsl:for-each>
          </ul></p>
        </section>
      </div>

      <div class="boxed">
        <section>
          <div style="min-height:3em">
            <div class="threecols-left">
              <p>Teitä palveli</p>
            </div>
            <div class="threecols-center">
              <p><!-- [Käsittelijä] -->
                <xsl:value-of select="data/handlerTitle"/>&#160;<xsl:value-of select="data/handlerName"/>
              </p>
            </div>
            <div class="threecols-right">
              <p>Puhelin: (09) 310 31940</p>
            </div>
          </div>
          <div>
            <div class="threecols-left">
              <p>Johtoselvityksen tilaajan allekirjoitus</p>
            </div>
            <div class="threecols-center">
              <hr class="signature"/>
              <p>[Johtoselvityksen jättäjä]</p>
            </div>
            <div class="threecols-right">
              <p>Tulostettu: <!-- [pvm + aika] -->
                <xsl:value-of select="data/decisionTimeStamp"/>
              </p>
            </div>
          </div>
        </section>
      </div>
  
    </div>    
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
