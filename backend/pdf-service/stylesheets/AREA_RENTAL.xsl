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

      <section class="unboxed">
        <h2>Työkohde</h2>
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
          <xsl:if test="data/hasAreaEntries = 'true'">
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
          </xsl:if>
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

        <p class="space-above">
          Yllä olevassa taulukossa näkyy kaikki päätökseen liittyvät alueet ja maksut. Harmaalla merkittyjen
          alueiden voimassaolo on päättynyt.
          <xsl:choose>
          <xsl:when test="data/notBillable = 'false'">
            Toteutunut työaika vaikuttaa maksujen suuruuteen ja lopullinen
            laskutettava maksu määräytyy hyväksytysti vastaanotetun työn keston perusteella.
            <xsl:choose>
            <xsl:when test="data/invoicingPeriodLength != ''">
              Aluevuokraus laskutetaan jaksotetusti <xsl:value-of select="data/invoicingPeriodLength"/> kk välein.
              Laskut lähetetään erikseen.
            </xsl:when>
            <xsl:otherwise>
              Lasku lähetetään erikseen.
            </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <p class="space-above">Korvauksetta</p>
            <p>
              Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
            </p>
          </xsl:otherwise>
        </xsl:choose>
        </p>
      </section>

      <xsl:if test="data/notBillable = 'false' and data/anonymizedDocument = 'false'">
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
          Alueen vuokraukseen sovelletaan maanvuokralakia. Lisäksi sovelletaan liitteenä olevien yleisten vuokrausehtojen kohtia.
          Päätöksenhakija on velvollinen noudattamaan liikennejärjestelypäätöstä sekä vuokrausehtoja.
        </p>

        <p class="space-above">
          Työmaajärjestelyjä sekä kaivutöiden ja liikennejärjestelyjen suorittamista koskevat tekniset ohjeet ja määräykset on annettu
          tässä päätöksessä sekä ohjeissa ”Yleisten alueiden käyttö, tilapäiset liikennejärjestelyt ja katutyöt” ja ”Tilapäiset
          liikennejärjestelyt katu- ja yleisillä alueilla”. Ohjeista lisätietoa Helsingin kaupungin nettisivuilla osoitteessa
          https://www.hel.fi/fi/kaupunkiymparisto-ja-liikenne/tontit-ja-rakentamisen-luvat/tyomaan-luvat-ja-ohjeet/kaduilla-ja-puistoissa-tehtavat-tyot.
        </p>

        <p class="space-above">
          Kunnan antamien määräysten lainmukaisuuden ratkaiseminen osoitetaan rakennusvalvontapalveluun. Muutoksenhaku
          kunnan antamiin päätöksiin osoitetaan kaupunkiympäristölautakunnalle, sähköposti helsinki.kirjaamo@hel.fi. Ennen
          varsinaisen valituksen tekemistä maksuvelvollisen tulee tehdä kirjallinen muistutus maksun perimisestä päättävälle kunnan
          viranomaiselle 14 päivän kuluessa maksulipun (lasku) saamisesta.
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

      <section class="unboxed">
        <h2 class="new-page">Vuokrausehdot</h2>
        <p class="space-above">
          1. Vuokran irtisanomisaika on kaksi (2) viikkoa päätöksenantajan osalta. Irtisanominen on toimitettava
          kirjallisena. Vuokrasopimus päättyy kuitenkin alkuperäisen hakemuksen mukaisesti, ellei kumpikaan
          ilmoita kirjallisesti haluavansa siihen muutoksia sopimuksen voimassaoloaikana.
        </p>

        <p class="space-above">
          2. Muutokset vuoraukseen tulee ilmoittaa etukäteen kirjallisena, jälkikäteen tehtyjä muutosilmoituksia
          ei käsitellä.
        </p>

        <p class="space-above">
          3. Päätöksensaaja on velvollinen noudattamaan päätöksen osana olevaa liikennejärjestelypäätöstä.
          Päätöksenantajalla on oikeus teettää tarvittavat liikennejärjestelyt vuokralaisen kustannuksella, mikäli
          vuokralainen ei niitä kehotuksesta huolimatta suorita.
        </p>

        <p class="space-above">
          4. Vuokrasopimus ei oikeuta vuokralaista suorittamaan kaivu- tai louhintatöitä vuokra-alueella.
          Kaupungin valvonta ei poista vuokralaisen vastuuta vuokralaisen aiheuttamista vahingoista kaupungille
          tai kolmannelle osapuolelle. Vuokralainen on velvollinen hankkimaan työhön tarvittavat rakennus-,
          toimenpide-, ympäristö-, ym. luvat sekä tekemään tarvittavat ilmoitukset muille viranomaisille.
        </p>

        <p class="space-above">
          5. Alueen tai sen osan vuokraoikeus ei ole siirrettävissä, eikä aluetta saa edelleen vuokrata
          kolmannelle osapuolelle.
        </p>

        <p class="space-above">
          6. Altakuljettavattelineet on varusteltava riittävin suojakatoksin ja suojapeittein jalankulun
          turvaamiseksi.
        </p>

        <p class="space-above">
          7. Käytetyt aidat on pidettävä töhryistä vapaana. Työmaa-aidoissa mainostaminen yli kahden (2)
          viikon ajan vaatii rakennusvalvonnan luvan.
        </p>

        <p class="space-above">
          8. Jalankulku ja muu liikenne on ohjattava aina turvallista reittiä ohi työkohteen. Telineiden
          pystytysvaiheen tai muun mahdollisesti putoavia esineitä käsittelevien työvaiheiden ajaksi
          on vaadittaessa rakennettava katettu jalankulkusuoja.
        </p>

        <p class="space-above">
          9. Autojen pysäköinti alueella on kielletty.
        </p>

        <p class="space-above">
          10. Päätöksensaajan on huolehdittava alueen kunnossa ja puhtaanapidosta sekä aina tarvittaessa
          kulkuväylän talvikunnossa pidosta koko vuokrauksen voimassaoloajan, myös normaalin työajan
          ulkopuolella ja viikonloppuisin.
        </p>

        <p class="space-above">
          11. Vuokra-alue on pidettävä yleisilmeeltään siistinä. Vuokra-alue on tyhjennettävä, aita ja muut
          rakennelmat poistettava sekä alue siivottava vuokra-ajan päättymiseen mennessä. Mikäli aluetta ei ole
          tyhjennetty (1) kuukauden kuluessa vuokra-ajan päättymisestä, kaupungilla on oikeus menetellä
          alueella olevan omaisuuden suhteen parhaaksi katsomallaan tavalla. Alueen tyhjentämis- ja
          siivoamiskulut sekä maanvuokraa vastaava korvaus kuntoonsaattamiseen asti kuluvalta ajalta peritään
          vuokralaiselta.
        </p>

        <p class="space-above">
          12. Päätöksensaajan on estettävä laastiaseman lietteen, rappaus- ym. rakennusjätteiden
          kulkeutuminen kadulle ja viemäriverkostoon sekä estämään alueen pilaantuminen. Mikäli vuokra-alue
          tai osa siitä on kuitenkin vuokra-aikana ympäristösuojelulain tarkoittamalla tavalla saastunut,
          vuokralainen on velvollinen huolehtimaan alueen puhdistamisesta, kuten sanotussa laissa määrätään.
          Mikäli vuokralainen laiminlyö tässä tarkoitetun velvollisuutensa, vuokranantajalla on oikeus tutkia ja
          puhdistaa saastunut maa vuokralaisen lukuun ja periä toimenpiteistä aiheutuneet kustannukset
          vuokralaiselta.
        </p>

        <p class="space-above">
          13. Alkoholimainonta vuokra-alueella on kielletty.
        </p>

        <p class="space-above">
          14. Päätöksenantaja ei vastaa päätöksensaajalle aiheutuneista kuluista mikäli päätös
          oikaisuvaatimuksen, kunnallisvalituksen tai ylemmän toimielimen päätöksen johdosta muuttuu tai
          kumoutuu. Päätöksenantaja ei myöskään vastaa muiden viranomaisten tekemien päätösten
          aiheuttamista kuluista.
        </p>
      </section>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
