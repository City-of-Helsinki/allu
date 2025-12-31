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
        <div class="header" style="width: 100%; margin-top: 3px;">
          <div class="logo">
            <img src="helsinki-logo.png" />
          </div>
          <div class="department">
            <p>Kaupunkiympäristö</p>
            <p>Yleisten alueiden valvonta ja<br/>
              infraluvat -yksikkö</p>
          </div>
          <div class="half-right">
            <h1>Irtisanominen</h1>
            <div class="id">
              <table>
                <tr>
                  <td class="c1">Päätöspäivämäärä:</td>
                  <!-- [päätöspvm] -->
                  <td class="c2"><xsl:value-of select="data/decisionDate" /></td>
                </tr>
              </table>
            </div>
          </div>
        </div>

        <div class="body">
          <div class="unboxed avoid-pb">
            <section>
              <h2>Sijoitussopimuksen irtisanominen</h2>
              <p class="space-above">Sijoitussopimuksen tunnus: <xsl:value-of select="data/applicationId"/></p>
            </section>
          </div>

          <div class="unboxed avoid-pb">
            <section class="space-below">
              <h2>Osapuolet</h2>
              <p class="indented space-above">Helsingin kaupunki</p>
            </section>
            <section class="half-left">
              <p class="indented">
                <xsl:for-each select="data/customerAddressLines">
                  <xsl:value-of select="." /><br/>
                </xsl:for-each>
              </p>
            </section>

            <section class="half-right">
              <xsl:if test="data/customerContactLines != ''">
                <p class="indented">
                  <xsl:for-each select="data/customerContactLines">
                    <xsl:value-of select="."/><br/>
                  </xsl:for-each>
                </p>
              </xsl:if>
            </section>
          </div>

          <div class="unboxed">
            <div class="unboxed avoid-pb">
              <section>
                <h2>Kohde</h2>
                <p class="indented space-above"><xsl:value-of select="data/name" /></p>
                <p class="indented">
                  <!-- [Vuokrattava paikka, Lohko], [Osoite] -->
                  <xsl:value-of select="data/siteAddressLine"/>
                </p>
                <xsl:if test="data/siteAdditionalInfo != ''">
                  <p class="indented space-above">
                    <!-- Käytetään, jos Lisätietoja paikasta täytetty -->
                    <!-- [Lisätietoja paikasta] -->
                    <xsl:value-of select="data/siteAdditionalInfo"/>
                  </p>
                </xsl:if>
                <p class="indented space-above">Kaupunginosa: <xsl:value-of select="data/siteCityDistrict"/></p>
              </section>
            </div>

            <div class="unboxed avoid-pb">
              <section>
                <h2>Irtisanominen</h2>
                <br />
                <xsl:if test="data/terminationInfo">
                  <xsl:for-each select="data/terminationInfo">
                    <p class="indented">
                      <xsl:value-of select="."/>
                      <xsl:if test="not(normalize-space(.))">
                        <br/>
                      </xsl:if>
                    </p>
                  </xsl:for-each>
                </xsl:if>
                <p class="indented space-above">Sijoitussopimuksen voimassaolo päättyy <xsl:value-of select="data/expirationDate" />.</p>
              </section>
            </div>

            <div class="unboxed avoid-pb">
              <section>
                <h2>Päätös</h2>
                <p class="indented space-above">Yleisten alueiden valvonta ja infraluvat -yksikön yleisten alueiden rakenteet -tiimin päällikkö päätti irtisanoa sijoitussopimuksen.</p>
                <p class="indented space-above">Tämä päätös on sähköisesti allekirjoitettu.</p>
                <p class="indented space-above">
                  <xsl:value-of select="data/decisionTimestamp"/>,
                  <xsl:value-of select="data/deciderTitle"/>,
                  <xsl:value-of select="data/deciderName"/></p>
              </section>
            </div>

            <div class="unboxed avoid-pb">
              <section class="half-left">
                <h2>Lisätiedot</h2>
                <p>Yleisten alueiden rakenteet -tiimi</p>
                <p>alueidenkaytto@hel.fi</p>
              </section>

              <section class="half-right">
                <h2>Käsittelijä</h2>
                <p>
                  <xsl:value-of select="data/handlerTitle"/>,
                  <xsl:value-of select="data/handlerName"/>
                </p>
              </section>
            </div>
          </div>
          <section class="new-page unboxed justify">
            <h2 class="new-page">MUUTOKSENHAKUOHJEET</h2>
            <h2>OHJEET OIKAISUVAATIMUKSEN TEKEMISEKSI</h2>
            <div class="indented">
              <p class="space-above">Tähän päätökseen tyytymätön voi tehdä kirjallisen oikaisuvaatimuksen.
                Päätökseen ei saa hakea muutosta valittamalla tuomioistuimeen.</p>
            </div>
            <h3>Oikaisuvaatimusoikeus</h3>
            <div class="indented">
              <p>Oikaisuvaatimuksen saa tehdä</p>
              <ul>
                <li>se, johon päätös on kohdistettu tai jonka oikeuteen, velvollisuuteen tai
                  etuun päätös välittömästi vaikuttaa (asianosainen)</li>
                <li>kunnan jäsen</li>
              </ul>
            </div>
            <h3>Oikaisuvaatimusaika</h3>
            <div class="indented">
              <p>Oikaisuvaatimus on tehtävä 14 päivän kuluessa päätöksen tiedoksisaannista.</p>
              <p>Oikaisuvaatimuksen on saavuttava Helsingin kaupungin kirjaamoon
                määräajan viimeisenä päivänä ennen kirjaamon aukioloajan päättymistä.</p>
              <p>Mikäli päätös on annettu tiedoksi postitse, asianosaisen katsotaan saaneen
                päätöksestä tiedon, jollei muuta näytetä, seitsemän päivän kuluttua kirjeen
                lähettämisestä. Kunnan jäsenen katsotaan saaneen päätöksestä tiedon
                seitsemän päivän kuluttua siitä, kun pöytäkirja on nähtävänä yleisessä
                tietoverkossa.</p>
              <p>Mikäli päätös on annettu tiedoksi sähköisenä viestinä, asianosaisen
                katsotaan saaneen päätöksestä tiedon, jollei muuta näytetä, kolmen päivän
                kuluttua viestin lähettämisestä.</p>
              <p>Tiedoksisaantipäivää ei lueta oikaisuvaatimusaikaan. Jos
                oikaisuvaatimusajan viimeinen päivä on pyhäpäivä, itsenäisyyspäivä,
                vapunpäivä, joulu- tai juhannusaatto tai arkilauantai, saa
                oikaisuvaatimuksen tehdä ensimmäisenä arkipäivänä sen jälkeen.</p>
            </div>
            <h3>Oikaisuvaatimusviranomainen</h3>
            <div class="indented">
              <section>
                <p>Viranomainen, jolle oikaisuvaatimus tehdään, on Helsingin kaupungin
                  kaupunkiympäristölautakunta.</p>
                <p>Oikaisuvaatimusviranomaisen asiointiosoite on seuraava:</p>
                <table style="font-size: 10pt;">
                  <tr>
                    <td>Sähköpostiosoite:</td>
                    <td>helsinki.kirjaamo@hel.fi</td>
                  </tr>
                  <tr>
                    <td>Postiosoite:</td>
                    <td>PL 10</td>
                  </tr>
                  <tr>
                    <td></td>
                    <td>00099 HELSINGIN KAUPUNKI</td>
                  </tr>
                  <tr>
                    <td>Faksinumero:</td>
                    <td>(09) 655 783</td>
                  </tr>
                  <tr>
                    <td>Käyntiosoite:</td>
                    <td>Pohjoisesplanadi 11-13</td>
                  </tr>
                  <tr>
                    <td>Puhelinnumero:</td>
                    <td>(09) 310 13700 (Yleishallinto)</td>
                  </tr>
                </table>
                <p>Kirjaamon aukioloaika on maanantaista perjantaihin klo 08.15-16.00.</p>
              </section>
            </div>
            <h3>Oikaisuvaatimuksen muoto ja sisältö</h3>
            <div class="indented">
              <p>Oikaisuvaatimus on tehtävä kirjallisena. Myös sähköinen asiakirja täyttää
                vaatimuksen kirjallisesta muodosta.</p>
              <p>Oikaisuvaatimuksessa on ilmoitettava</p>
              <ul>
                <li>päätös, johon oikaisuvaatimus kohdistuu</li>
                <li>miten päätöstä halutaan oikaistavaksi</li>
                <li>millä perusteella päätöstä halutaan oikaistavaksi</li>
                <li>oikaisuvaatimuksen tekijä</li>
                <li>millä perusteella oikaisuvaatimuksen tekijä on oikeutettu tekemään vaatimuksen</li>
                <li>oikaisuvaatimuksen tekijän yhteystiedot</li>
              </ul>
            </div>
            <div class="avoid-pb">
              <h3>Pöytäkirja</h3>
              <div class="indented">
                <p>Päätöstä koskevia pöytäkirjan otteita ja liitteitä lähetetään pyynnöstä.
                  Asiakirjoja voi tilata Helsingin kaupungin kirjaamosta.</p>
              </div>
            </div>
          </section>
        </div>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
