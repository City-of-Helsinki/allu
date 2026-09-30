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
            <p>Kaupunkitilan käyttö ja maanvuokraus<br/>
              –yksikkö</p>
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
              <h2>Vuokrauspäätöksen irtisanominen</h2>
              <p class="space-above">Vuokrauspäätöksen tunnus: <xsl:value-of select="data/applicationId"/></p>
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
                <p class="indented space-above">Vuokrasopimuksen voimassaolo päättyy <xsl:value-of select="data/expirationDate" />.</p>
              </section>
            </div>

            <div class="unboxed avoid-pb">
              <section>
                <h2>Päätös</h2>
                <p class="indented space-above">Allekirjoittanut viranhaltija päätti irtisanoa vuokrauspäätöksen.</p>
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
                <p>Kaupunkitilan käyttö ja maanvuokraus –yksikkö</p>
              </section>

              <section class="half-right">
                <h2>Käsittelijä</h2>
                <p>
                  <xsl:value-of select="data/handlerTitle"/>,
                  <xsl:value-of select="data/handlerName"/>
                </p>
                <p>
                  <xsl:value-of select="data/handlerEmail"/>
                </p>
              </section>
            </div>
          </div>

          <!-- Muutoksenhakuohjeet -->
          <section class="new-page justify">
            <h1>MUUTOKSENHAKUOHJEET</h1>
            <h2 class="pt-10">OHJEET OIKAISUVAATIMUKSEN TEKEMISEKSI</h2>
            <div class="indent-50">
              <p class="pt-10 indent-50">
                Tähän päätökseen tyytymätön voi tehdä kirjallisen oikaisuvaatimuksen.
                Päätökseen ei saa hakea muutosta valittamalla tuomioistuimeen.
              </p>
            </div>

            <div class="indent-50">
              <h3 class="ml-0">Oikaisuvaatimusoikeus</h3>
              <p class="indent-50">Oikaisuvaatimuksen saa tehdä</p>
              <ul class="align-with-p">
                <li class="mt-0">
                  se, johon päätös on kohdistettu tai jonka oikeuteen, velvollisuuteen
                  tai etuun päätös välittömästi vaikuttaa (asianosainen)
                </li>
                <li class="mt-0">kunnan jäsen.</li>
              </ul>
            </div>

            <div class="indent-50">
              <h3 class="ml-0">Oikaisuvaatimusaika</h3>
              <p class="indented">Oikaisuvaatimus on tehtävä 14 päivän kuluessa päätöksen tiedoksisaannista.</p>
              <p class="pt-10 indent-50">
                Oikaisuvaatimuksen on saavuttava Helsingin kaupungin kirjaamoon
                määräajan viimeisenä päivänä ennen kirjaamon aukioloajan päättymistä.
              </p>
              <p class="pt-10 indent-50">
                Mikäli päätös on annettu tiedoksi postitse, asianosaisen katsotaan saaneen päätöksestä tiedon,
                jollei muuta näytetä, seitsemän päivän kuluttua kirjeen
                lähettämisestä. Kunnan jäsenen katsotaan saaneen päätöksestä tiedon, kun pöytäkirja on asetettu yleisesti nähtäväksi.
              </p>
              <p class="pt-10 indent-50">
                Mikäli päätös on annettu tiedoksi sähköisenä viestinä, asianosaisen
                katsotaan saaneen päätöksestä tiedon, jollei muuta näytetä, kolmen
                päivän kuluttua viestin lähettämisestä.
              </p>
              <p class="pt-10 indent-50">
                Tiedoksisaantipäivää ei lueta oikaisuvaatimusaikaan. Jos oikaisuvaatimusajan viimeinen päivä on pyhäpäivä, itsenäisyyspäivä, vapunpäivä,
                joulu- tai juhannusaatto tai arkilauantai, saa oikaisuvaatimuksen tehdä
                ensimmäisenä arkipäivänä sen jälkeen.
              </p>
            </div>

            <div class="indent-50">
              <h3 class="ml-0">Oikaisuvaatimusviranomainen</h3>
              <p class="indent-50">
                Viranomainen, jolle oikaisuvaatimus tehdään, on Helsingin kaupungin
                kaupunkiympäristölautakunta.
              </p>
              <p class="pt-10 indent-50">Oikaisuvaatimusviranomaisen asiointiosoite on seuraava:</p>
              <table class="contact-table indent-50">
                <tr>
                  <td>Sähköpostiosoite:</td>
                  <td>helsinki.kirjaamo@hel.fi</td>
                </tr>
                <tr>
                  <td>Postiosoite:</td>
                  <td>PL 10
                    00099 HELSINGIN KAUPUNKI
                  </td>
                </tr>
                <tr>
                  <td>Faksinumero:</td>
                  <td>(09) 655 783</td>
                </tr>
                <tr>
                  <td>Käyntiosoite:</td>
                  <td>Pohjoisesplanadi 11–13</td>
                </tr>
                <tr>
                  <td>Puhelinnumero:</td>
                  <td>(09) 310 13700 (Yleishallinto)</td>
                </tr>
              </table>
              <p class="pt-10 indent-50">Kirjaamon aukioloaika on maanantaista perjantaihin klo 08.15–16.00.</p>
            </div>

            <div class="indent-50 avoid-pb">
              <h3 class="ml-0">Oikaisuvaatimuksen muoto ja sisältö</h3>
              <p class="indent-50">
                Oikaisuvaatimus on tehtävä kirjallisena. Myös sähköinen asiakirja täyttää
                vaatimuksen kirjallisesta muodosta.
              </p>
              <p class="pt-10 indent-50">Oikaisuvaatimuksessa on ilmoitettava</p>
              <ul class="mb-0 align-with-p">
                <li class="mt-0">päätös, johon oikaisuvaatimus kohdistuu</li>
                <li class="mt-0">miten päätöstä halutaan oikaistavaks</li>
                <li class="mt-0">millä perusteella päätöstä halutaan oikaistavaksi</li>
                <li class="mt-0">oikaisuvaatimuksen tekijä</li>
                <li class="mt-0">millä perusteella oikaisuvaatimuksen tekijä on oikeutettu tekemään vaatimuksen</li>
                <li class="mt-0">oikaisuvaatimuksen tekijän yhteystiedot</li>
              </ul>
            </div>

            <div class="indent-50 avoid-pb">
              <h3 class="ml-0">Tietopyynnöt</h3>
              <p class="indent-50">Päätöstä koskevia otteita ja liitteitä lähetetään pyynnöstä. Asiakirjoja voi tilata Helsingin kaupungin kirjaamosta.</p>
            </div>
          </section>
        </div>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
