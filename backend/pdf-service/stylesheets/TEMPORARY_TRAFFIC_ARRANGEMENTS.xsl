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
          <h1>Työstä vastaava</h1>
          <p>
            <xsl:for-each select="data/customerAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>
        <section class="half-right">
          <xsl:if test="data/anonymizedDocument = 'false'">
            <h1>Yhteyshenkilö</h1>
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
            <h1>Rakennuttaja</h1>
            <p>
              <xsl:for-each select="data/propertyDeveloperAddressLines">
                <xsl:value-of select="." /><br/>
              </xsl:for-each>
            </p>
          </section>
          <section class="half-right">
            <xsl:if test="data/propertyDeveloperContactLines != '' and data/anonymizedDocument = 'false'">
              <h1>Yhteyshenkilö</h1>
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
          <h1>Työn suorittaja</h1>
          <p>
            <xsl:for-each select="data/contractorAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>
        <section class="half-right">
          <xsl:if test="data/anonymizedDocument = 'false'">
            <h1>Yhteyshenkilö</h1>
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
            <h1>Asiamies</h1>
            <p>
              <xsl:for-each select="data/representativeAddressLines">
                <xsl:value-of select="." /><br/>
              </xsl:for-each>
            </p>
          </section>
          <section class="half-right">
            <xsl:if test="data/representativeContactLines != '' and data/anonymizedDocument = 'false'">
              <h1>Yhteyshenkilö</h1>
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
          <h1>Liikennejärjestelyn kohde</h1>
          <p>
            <!-- Käytetään vuokrattavaa paikkaa ja lohkoa, jos joku pudotusvalikossa määritetty paikka;
                 käytetään Osoitetta, jos ei pudotusvalikossa määritetty paikka -->
            <!-- [Vuokrattava paikka, Lohko], [Osoite] -->
            <xsl:value-of select="data/siteAddressLine"/>
          </p>
          <xsl:if test="data/siteAdditionalInfo != ''">
            <p class="pt-10">
              <!-- Käytetään, jos Lisätietoja paikasta täytetty -->
              <!-- [Lisätietoja paikasta] -->
              <xsl:value-of select="data/siteAdditionalInfo"/>
            </p>
          </xsl:if>
        </section>
        <section class="half-right">
          <h1>Voimassaoloaika</h1>
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
        <h1>Työn tarkoitus</h1>
        <p>
          <xsl:value-of select="data/workPurpose"/>
        </p>
      </section>

      <section class="unboxed">
        <h1>Liikennejärjestelyjä koskevat määräykset</h1>
        <p>
          Tämä päätösosa perustuu tieliikennelain (729/2018) 187 § ja 188 §:ään. Päätösosassa käydään
          läpi liikennejärjestelyjä koskevat yleiset määräykset, jotka velvoittavat työstä vastaavaa,
          rakennuttajaa ja työn suorittajaa.
        </p>
        <p class="pt-10">
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

      <!-- Yleiset määräykset -->
      <section class="unboxed">
        <h1>Yleiset määräykset</h1>
        <h2 class="pt-10 mb-0">Liikennejärjestelyjä koskevat yleiset määräykset</h2>
        <p>
          Tämä päätösosa perustuu tieliikennelain
          (729/2018) 187 § ja 188 §:ään. Päätösosassa käydään läpi liikennejärjestelyjä koskevat yleiset
          määräykset, jotka velvoittavat työstä vastaavaa, rakennuttajaa ja työn suorittajaa.
        </p>
        <ol class="numbered-list">
          <li>
            Alueen käyttö muuhun kuin ilmoitettuun tarkoitukseen tai kaupungin antamien määräysten
            vastaisesti on kielletty.
          </li>
          <li>
            Päätöksenhakija on velvollinen huolehtimaan tilapäisten liikennejärjestelyiden toteuttamisesta
            sekä poistamisesta kustannuksellaan. Liikennejärjestelyjen sisältäessä puutteita kaupunki voi korjata
            puutteet päätöksenhakijan kustannuksella, mikäli niitä ei kehotuksista huolimatta korjata.
          </li>
          <li>
            Päätös ei oikeuta suorittamaan kaivu- tai louhintatöitä alueella eli töitä, joissa työn suorittamiseksi
            rikotaan kadun tai muun yleisen alueen pintarakennetta.
          </li>
          <li>
            Päätöksenhakija vastaa tilapäisten liikennejärjestelyiden kaupungille ja kolmannelle osapuolelle
            aiheuttamista vahingoista.
          </li>
          <li>
            Liikennejärjestelyt on purettava välittömästi päätöksen voimassaolon päätyttyä.
          </li>
          <li>
            Jalankulku ja muu liikenne on ohjattava aina turvallista reittiä työkohteen ohi. Käytettävät
            liikennejärjestelyt on toteutettava heijastavilla sulkulaitteilla.
          </li>
          <li>
            Liikenteenohjaajia tulee käyttää, jos kaikille kulkumuodoille ei voida muuten toteuttaa riittävän
            leveää, turvallista ja esteetöntä kulkua alueen ohi.
          </li>
          <li>
            Liikennejärjestelyihin liittyvistä muutoksista tulee ilmoittaa alueen tarkastajalle ennen muutoksen
            toteuttamista.
          </li>
          <li>
            Tämän päätöksen ehtojen lisäksi liikennejärjestelyiden toteuttamisessa on noudatettava Helsingin
            kaupungin verkkosivuilla olevaa ajantasaista ohjeistusta (päätöshetkellä osoitteessa
            https://www.hel.fi/fi/kaupunkiymparisto-ja-liikenne/tontit-ja-rakentamisen-luvat/tyomaan-luvat-jaohjeet/kaduilla-ja-puistoissa-tehtavat-tyot kohdassa ”Huomioi nämä ohjeet”). Liikennejärjestelyjen
            toteuttamisessa tulee erityisesti huomioida verkkosivuilla oleva ohje ”Kaivutyöt ja tilapäiset
            liikennejärjestelyt pääkaupunkiseudulla”.
          </li>
        </ol>
      </section>

      <xsl:if test="data/additionalConditions">
        <section class="unboxed">
          <h1>Muut ehdot</h1>
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
        <h1>Päätös</h1>
        <p>
          Tämä päätös on sähköisesti allekirjoitettu.
        </p>
        <p>
          <!-- [aikaleima], [päättäjän työnimike], [päättäjän nimi] -->
          <xsl:value-of select="data/decisionTimestamp"/>,
          <xsl:value-of select="data/deciderTitle"/>,
          <xsl:value-of select="data/deciderName"/>
        </p>
        <p class="pt-10">
          Päätös perustuu tieliikennelain (729/2018) ja tieliikenneasetuksen (379/2020) mukaisiin oikeuksiin asettaa liikennemerkki.
        </p>
      </section>

      <section class="unboxed avoid-pb">
        <h1>Liitteet</h1>
        <p>
          <!--  [Lista liitteiden nimistä] -->
          <xsl:for-each select="data/attachmentNames">
            <xsl:value-of select="." /><br/>
          </xsl:for-each>
        </p>
      </section>

      <div class="unboxed avoid-pb">
        <h1>Valvojan yhteystiedot</h1>
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
        <h1>Ilmoituksen käsittelijän yhteystiedot</h1>
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

      <!-- Muutoksenhakuohjeet -->
      <section class="new-page">
        <h1>MUUTOKSENHAKUOHJEET</h1>

        <div class="indent-50">
          <p class="pt-10">Tähän päätökseen tyytymätön voi tehdä kirjallisen oikaisuvaatimuksen.</p>
          <p class="pt-10">Päätökseen ei saa hakea muutosta valittamalla tuomioistuimeen.</p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimusoikeus</h3>
          <p>
            Oikaisuvaatimuksen saa tehdä se, johon päätös on kohdistettu tai jonka oikeuteen,
            velvollisuuteen tai etuun päätös välittömästi vaikuttaa (asianosainen).
          </p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimusaika</h3>
          <p>Oikaisuvaatimus on tehtävä 30 päivän kuluessa päätöksen tiedoksisaannista.</p>
          <p class="pt-10">
            Oikaisuvaatimuksen on saavuttava Helsingin kaupungin kirjaamoon määräajan
            viimeisenä päivänä ennen kirjaamon aukioloajan päättymistä klo 16.00.
          </p>
          <p class="pt-10">
            Mikäli päätös on annettu tiedoksi postitse, asianosaisen katsotaan saaneen
            päätöksestä tiedon, jollei muuta näytetä, seitsemän päivän kuluttua kirjeen
            lähettämisestä.
          </p>
          <p class="pt-10">
            Mikäli päätös on annettu tiedoksi sähköisenä viestinä, asianosaisen katsotaan
            saaneen päätöksestä tiedon, jollei muuta näytetä, kolmen päivän kuluttua viestin
            lähettämisestä.
          </p>
          <p class="pt-10">
            Mikäli päätös on annettu tiedoksi yleistiedoksiantona, tiedoksisaannin katsotaan
            tapahtuneen seitsemäntenä päivänä siitä, kun asiakirjan nähtäville asettamista
            koskeva ilmoitus on julkaistu viranomaisen verkkosivuilla.
          </p>
          <p class="pt-10">
            Tiedoksisaantipäivää ei lueta oikaisuvaatimusaikaan. Jos oikaisuvaatimusajan
            viimeinen päivä on pyhäpäivä, itsenäisyyspäivä, vapunpäivä, joulu- tai juhannusaatto
            tai arkilauantai, saa oikaisuvaatimuksen tehdä ensimmäisenä arkipäivänä sen
            jälkeen.
          </p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimusviranomainen</h3>
          <p>Oikaisuvaatimus on tehtävä päätöksen tehneelle viranhaltijalle.</p>
          <p class="pt-10">Oikaisuvaatimusviranomaisen asiointiosoite on seuraava:</p>
          <table class="contact-table">
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
              <td>Käyntiosoite:</td>
              <td>Pohjoisesplanadi 11–13</td>
            </tr>
            <tr>
              <td>Puhelinnumero:</td>
              <td>09 310 13700</td>
            </tr>
          </table>
          <p class="pt-10">Kirjaamon aukioloaika on maanantaista perjantaihin klo 08.15–16.00.</p>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Oikaisuvaatimuksen muoto ja sisältö</h3>
          <p>
            Oikaisuvaatimus on tehtävä kirjallisena. Myös sähköinen asiakirja täyttää
            vaatimuksen kirjallisesta muodosta.
          </p>
          <p class="pt-10">Oikaisuvaatimuksessa on ilmoitettava</p>
          <ul class="mt-0 mb-0">
            <li class="mt-0">päätös, johon oikaisuvaatimus kohdistuu</li>
            <li class="mt-0">miten päätöstä halutaan oikaistavaks</li>
            <li class="mt-0">millä perusteella päätöstä halutaan oikaistavaksi</li>
            <li class="mt-0">oikaisuvaatimuksen tekijä</li>
            <li class="mt-0">millä perusteella oikaisuvaatimuksen tekijä on oikeutettu tekemään vaatimuksen</li>
            <li class="mt-0">oikaisuvaatimuksen tekijän yhteystiedot</li>
          </ul>
        </div>

        <div class="indent-50">
          <h3 class="ml-0">Pöytäkirja</h3>
          <p>Päätöstä koskevia pöytäkirjan otteita ja liitteitä lähetetään pyynnöstä.</p>
          <p class="pt-10">Asiakirjoja voi tilata Helsingin kaupungin kirjaamosta.</p>
        </div>
      </section>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
