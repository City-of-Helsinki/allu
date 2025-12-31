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
          <h2>Työn kohde</h2>
          <xsl:for-each select="data/areaAddresses">
            <p>
              <xsl:value-of select="." />
            </p>
          </xsl:for-each>
        </section>

        <section class="half-right">
          <h2>Voimassaoloaika</h2>
          <p>
            <xsl:value-of select="data/reservationStartDate"/>-
            <xsl:value-of select="data/reservationEndDate"/>
          </p>
        </section>
      </div>

      <section class="unboxed">
        <h2>Työn tarkoitus</h2>
        <p>
          <xsl:value-of select="data/workPurpose"/>
        </p>
      </section>

      <xsl:if test="data/workFinished != '' or data/customerWorkFinished != ''">
        <div class="unboxed">
          <section class="half-left">
            <h2>Työn valmistuminen</h2>
            <p>Ilmoitettu valmistumispäivä</p>
            <p><xsl:value-of select="data/customerWorkFinished"/></p>
          </section>
          <section class="half-right">
            <h2>&#160;</h2>
            <p>Hyväksytty valmistumispäivä</p>
            <p><xsl:value-of select="data/workFinished"/></p>
          </section>
        </div>
      </xsl:if>

      <section class="unboxed">
        <h2>Vuokra-alueet</h2>
        <table class="area-table">
          <tr>
            <th>Aluetunnus</th>
            <th>Voimassaolo</th>
            <th>Osoite</th>
            <th>Altakulj.</th>
            <th>Pinta-ala</th>
            <th>ML</th>
            <th>à € / pv</th>
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

            <xsl:variable name="borderStyle">
              <xsl:choose>
                <xsl:when test="./firstCommon = 'true'">
                  fat-border
                </xsl:when>
                <xsl:otherwise>
                  thin-border
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
                <tr class="{$borderStyle}">
                  <td class="info" style="border-right: none;" colspan="3"><xsl:value-of select="chargeBasisText"/></td>
                  <td style="border-left: none; border-right: none;" colspan="2"><xsl:value-of select="quantity"/></td>
                  <td style="border-left: none; border-right: none;" colspan="2"><xsl:value-of select="unitPrice"/></td>
                  <td style="border-left: none; text-align: right;" colspan="2"><xsl:value-of select="price"/></td>
                </tr>
              </xsl:when>
              <xsl:otherwise>
                <tr class="{$areaFinished} fat-border">
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
        <xsl:choose>
        <xsl:when test="data/notBillable = 'false'">
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
        </xsl:when>
        <xsl:otherwise>
          <p class="space-above">Korvauksetta</p>
          <p>
            Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
          </p>
        </xsl:otherwise>
        </xsl:choose>
      </section>

      <section class="unboxed new-page">
        <h2>Muutoksenhaku</h2>
        <p class="space-above">
          Maksuvelvollisella on oikeus tehdä 14 päivän kuluessa laskun saamisesta kirjallinen muistutus maksun
          perimisestä päättävälle kunnan viranomaiselle. Muistutus osoitetaan Helsingin kaupungin kaupunkiympäristön
          toimialan yleisten alueiden valvonta ja infraluvat -yksikön päällikölle. Asiointiosoite on seuraava:
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
      </section>

      <xsl:if test="data/deciderName != ''">
        <section class="unboxed avoid-pb">
          <h2>Päätös</h2>
          <p class="space-above">
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
      </xsl:if>

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

    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
