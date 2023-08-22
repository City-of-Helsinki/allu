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

      <xsl:if test="data/winterTimeOperation != '' or data/customerWinterTimeOperation != ''">
        <div class="unboxed">
          <section class="half-left">
            <h2>Toiminnallinen kunto</h2>
            <p>Ilmoitettu toiminnallisen kunnon päivä</p>
            <p><xsl:value-of select="data/customerWinterTimeOperation"/></p>
          </section>
          <section class="half-right">
            <h2>&#160;</h2>
            <p>Hyväksytty toiminnallisen kunnon päivä</p>
            <p><xsl:value-of select="data/winterTimeOperation"/></p>
          </section>
        </div>
      </xsl:if>

      <xsl:if test="data/workFinished != '' or data/customerWorkFinished != ''">
        <div class="unboxed">
          <section class="half-left">
            <h2>Työn valmistuminen</h2>
            <p>Ilmoitettu valmistumispäivä</p>
            <p><xsl:value-of select="data/customerWorkFinished"/></p>
            <xsl:if test="data/guaranteeEndTime">
              <p class="space-above">Työn takuu voimassa <xsl:value-of select="data/guaranteeEndTime"/> asti.</p>
            </xsl:if>
          </section>
          <section class="half-right">
            <h2>&#160;</h2>
            <p>Hyväksytty valmistumispäivä</p>
            <p><xsl:value-of select="data/workFinished"/></p>
          </section>
        </div>
      </xsl:if>

      <xsl:if test="data/notBillable = 'false' and data/chargeInfoEntries">
        <section class="unboxed">
          <h2>Perittävät maksut</h2>
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
            <p class="space-above">
              Maksut perustuvat Kaupunkiympäristölautakunnan ympäristö- ja lupajaoston päätökseen 17.2.2022 § 28.
            </p>
            <p>
              Lasku lähetetään erikseen.
            </p>
          </div>
        </section>
      </xsl:if>
      <xsl:if test="data/notBillable = 'true'">
        <section class="unboxed">
          <h2>Perittävät maksut</h2>
          <p>Korvauksetta.</p>
          <p class="space-above">
            Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
          </p>
        </section>
      </xsl:if>

      <section class="unboxed new-page">
        <h2>Muutoksenhaku</h2>
        <p class="space-above">
          Kunnan antamien määräysten lainmukaisuuden ratkaiseminen osoitetaan rakennusvalvontapalveluun.
          Muutoksenhaku kunnan antamiin päätöksiin osoitetaan kaupunkiympäristö lautakunnalle, sähköposti
          helsinki.kirjaamo@hel.fi. Ennen varsinaisen valituksen tekemistä maksuvelvollisen tulee tehdä kirjallinen
          muistutus maksun perimisestä päättävälle kunnan viranomaiselle 14 päivän kuluessa maksulipun (lasku)
          saamisesta.
        </p>
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