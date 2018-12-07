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
          <h2>Yhteyshenkilö</h2>
          <!-- <p>[Yhteyshenkilön nimi]<br/>[Sähköpostiosoite, puhelin]</p> -->
          <p>
            <xsl:for-each select="data/customerContactLines">
              <xsl:value-of select="."/><br/>
            </xsl:for-each>
          </p>
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
            <h2>Yhteyshenkilö</h2>
            <p>
              <xsl:for-each select="data/propertyDeveloperContactLines">
                <xsl:value-of select="."/><br/>
              </xsl:for-each>
            </p>
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
          <h2>Vastuuhenkilö</h2>
          <p>
            <xsl:for-each select="data/contractorContactLines">
              <xsl:value-of select="."/><br/>
            </xsl:for-each>
          </p>
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
            <h2>Yhteyshenkilö</h2>
            <p>
              <xsl:for-each select="data/representativeContactLines">
                <xsl:value-of select="."/><br/>
              </xsl:for-each>
            </p>
          </section>
        </div>
      </xsl:if>

      <section class="unboxed">
        <h2>Työn kohde</h2>
        <xsl:for-each select="data/areaAddresses">
          <p>
            <xsl:value-of select="." />
          </p>
        </xsl:for-each>
      </section>

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

      <xsl:if test="data/additionalConditions">
        <section class="unboxed">
          <h2>Päätösehdot</h2>
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

      <section class="unboxed">
        <h2>Vuokra-alueet</h2>
        <table class="area-table">
          <tr>
            <th>Alue ID</th>
            <th>Aika</th>
            <th>Osoite</th>
            <th>Altakuljettava</th>
            <th>Pinta-ala</th>
            <th>Maksuluokka</th>
            <th>á € / alkava 15m²</th>
            <th>pv</th>
            <th>Maksu</th>
          </tr>
          <xsl:for-each select="data/rentalAreas">
            <xsl:variable name="areaFinished">
              <xsl:choose>
                <xsl:when test="./finished = 'true'">
                  finished
                </xsl:when>
                <xsl:otherwise>
                  unfinished
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:choose>
              <xsl:when  test="./text != ''">
                <tr class="{$areaFinished}">
                  <td class="info" colspan="8"><xsl:value-of select="text"/></td>
                  <td style='text-align: right;'><xsl:value-of select="price"/></td>
                </tr>
              </xsl:when>
              <xsl:when test="./chargeBasisText != ''">
                <tr>
                  <td class="info" style="border-right: none;" colspan="3"><xsl:value-of select="chargeBasisText"/></td>
                  <td style="border-left: none; border-right: none;" colspan="2"><xsl:value-of select="quantity"/></td>
                  <td style="border-left: none; border-right: none;" colspan="2"><xsl:value-of select="unitPrice"/></td>
                  <td style="border-left: none; text-align: right;" colspan="2"><xsl:value-of select="price"/></td>
                </tr>
              </xsl:when>
              <xsl:otherwise>
                <tr class="{$areaFinished}">
                  <td><xsl:value-of select="areaId"/></td>
                  <td><xsl:value-of select="time"/></td>
                  <td><xsl:value-of select="address"/></td>
                  <td><xsl:value-of select="underpass"/></td>
                  <td><xsl:value-of select="area"/></td>
                  <td><xsl:value-of select="paymentClass"/></td>
                  <td><xsl:value-of select="unitPrice"/></td>
                  <td><xsl:value-of select="days"/></td>
                  <td style='text-align: right;'><xsl:value-of select="price"/></td>
                </tr>
                <xsl:if test="./additionalInfo != ''">
                  <tr class="{$areaFinished}">
                    <td class="info" colspan="9">Lisätietoja alueesta: <xsl:value-of select="additionalInfo"/></td>
                  </tr>
                </xsl:if>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </table>

        <p class="space-above">
          Yllä olevassa taulukossa näkyy kaikki päätökseen liittyvät alueet ja maksut. Harmaalla merkittyjen
          alueiden voimassaolo on päättynyt. Toteutunut työaika vaikuttaa maksujen suuruuteen ja lopullinen
          laskutettava maksu määräytyy hyväksytysti vastaanotetun työn keston perusteella. Lasku lähetetään erikseen.
        </p>
      </section>

      <xsl:if test="data/notBillable = 'false'">
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

      <section class="unboxed">
        <h2>Yleiset ehdot</h2>
        <p>
          ??Tähän perustetta ja viittaus liitteeseen??. Päätöksenhakija on velvollinen noudattamaan sopimukseen
          iitettyä liikennejärjestelypäätöstä. Mahdolliset lisäehdot ovat kohdassa päätösehdot.
        </p>

        <p class="space-above">
          Työmaajärjestelyjä sekä kaivutöiden ja liikennejärjestelyjen suorittamista koskevat tekniset ohjeet ja
          määräykset on annettu tässä päätöksessä sekä ohjeissa ”Yleisten alueiden käyttö, tilapäiset
          liikennejärjestelyt ja katutyöt” ja ”Tilapäiset liikennejärjestelyt katu- ja yleisillä alueilla”. Ohjeista lisätietoa
          Helsingin kaupungin nettisivuilla osoitteessa https://www.hel.fi/helsinki/fi/asuminen-ja-ymparisto/tontit/luvat/katutyoluvat/.
        </p>

        <p class="space-above">
          Kunnan antamien määräysten lainmukaisuuden ratkaiseminen osoitetaan rakennusvalvontapalveluun.
        </p>

        <p class="space-above">
          Muutoksenhaku kunnan antamiin päätöksiin osoitetaan kaupunkiympäristö lautakunnalle, sähköposti
          helsinki.kirjaamo@hel.fi. Ennen varsinaisen valituksen tekemistä maksuvelvollisen tulee tehdä kirjallinen
          muistutus maksun perimisestä päättävälle kunnan viranomaiselle 14 päivän kuluessa maksulipun (lasku) saamisesta.
        </p>
      </section>

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
          Päätös perustuu lakiin kadun ja eräiden yleisten alueiden kunnossa- ja puhtaanapidosta § 14 b.
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
