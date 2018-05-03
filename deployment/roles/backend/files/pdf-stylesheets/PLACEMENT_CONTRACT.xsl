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
    <link rel="stylesheet" href="new-style.css" />
    <xsl:if test="data/draft = 'true'">
      <link rel="stylesheet" href="watermark.css" />
    </xsl:if>
  </head>
  <body>
    <div class="header">
      <div class="logo">
        <img src="helsinki-logo.png" />
      </div>
      <div class="department">
        <p>Kaupunkiympäristö</p>
        <p>Alueidenkäyttö ja -valvonta</p>
        <p>yksikön päällikkö</p>
      </div>
      <div class="half-right">
        <h1>Pöytäkirja</h1>
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

    <p> </p>

    <div class="unboxed">
      <section>
        <h2>Pykälä <xsl:value-of select="data/sectionNumber"/>§</h2>
        <p>Sopimus oikeudesta sijoittaa
            <xsl:for-each select="data/kinds">
              <xsl:value-of select="kind" /> (
              <xsl:for-each select="./specifiers">
                <xsl:value-of select="." />
                <xsl:if test="position() != last()">
                    <xsl:text>, </xsl:text>
                </xsl:if>
              </xsl:for-each>
              )
            </xsl:for-each>
            Helsingin kaupungin yleiselle alueelle.</p>
      </section>
    </div>

    <div class="unboxed">
      <section>
        <h2>Päätös</h2>
        <p class="indented">
          Yksikön päällikkö päätti hyväksyä ja allekirjoittaa sopimuksen <xsl:value-of select="data/decisionId"/> koskien
          <xsl:for-each select="data/kinds">
            <xsl:value-of select="kind" /> (
            <xsl:for-each select="./specifiers">
              <xsl:value-of select="." />
              <xsl:if test="position() != last()">
                  <xsl:text>, </xsl:text>
              </xsl:if>
            </xsl:for-each>
            )
          </xsl:for-each>
          sijoittamisesta kaupungin yleiselle alueelle.</p>
        <p class="indented">Tämä päätös on sähköisesti allekirjoitettu.</p>
        <p class="indented">
        <!-- [aikaleima], [päättäjän työnimike], [päättäjän nimi] -->
        <xsl:value-of select="data/decisionTimestamp"/>,
        <xsl:value-of select="data/deciderTitle"/>,
        <xsl:value-of select="data/deciderName"/></p>
      </section>
    </div>

    <div class="unboxed">
      <section>
        <h2>Päätöksen perustelut</h2>
        <p class="indented">
          ”TÄHÄN TULEE KAIKENLASTA LAKITEKSTIPROOSAA MONTA PYKÄLÄÄ” Antti kaivaa...
          Esimerkiksi operaattoreiden laki (teitoliikennekaari?) sähkömarkkinalaki? maankäyttö- ja
          rakennuslaki?
        </p>
      </section>
    </div>

    <div class="unboxed">
      <section>
        <h2>Käsittelijä</h2>
        <!-- <p>[tehtävänimike, käsittelijän nimi]</p> -->
        <p class="indented">
          <xsl:value-of select="data/handlerTitle"/>, <xsl:value-of select="data/handlerName"/>
        </p>
      </section>
    </div>

    <div class="unboxed">
      <section>
        <h2>Muutoksenhaku</h2>
        <p class="indented">
          Oikaisuvaatimusohje, kaupunkiympäristölautakunta
        </p>
      </section>
    </div>

    <div class="unboxed">
      <section>
        <h2>Lisätiedot</h2>
        <p class="indented">Kaupunkiympäristön alueidenkäyttö<br/>
        alueidenkaytto@hel.fi, 09 310 22111<br/>
        Sörnäistenkatu 1, 00580 Helsinki</p>
      </section>
    </div>

  <h2 class="new-page">MUUTOKSENHAKUOHJEET</h2>
  <h2>OHJEET OIKAISUVAATIMUKSEN TEKEMISEKSI</h2>
  <div class="indented">
    <p>Tähän päätökseen tyytymätön voi tehdä kirjallisen oikaisuvaatimuksen.
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
  <h3>Pöytäkirja</h3>
  <div class="indented">
    <p>Päätöstä koskevia pöytäkirjan otteita ja liitteitä lähetetään pyynnöstä.
    Asiakirjoja voi tilata Helsingin kaupungin kirjaamosta.</p>
  </div>
  </body>
  </html>
  </xsl:template>
  </xsl:stylesheet>
