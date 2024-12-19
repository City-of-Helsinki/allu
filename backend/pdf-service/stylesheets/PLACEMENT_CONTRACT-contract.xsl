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
    <link rel="stylesheet" href="style.css" />
  </head>
  <body>
    <div class="body">

      <div class="unboxed">
        <section>
          <h2>Sopimus oikeudesta sijoittaa rakenteita yleiselle alueelle</h2>
          <p class="space-above">Sopimustunnus: <xsl:value-of select="data/decisionId"/></p>
        </section>
      </div>

      <div class="unboxed">
        <h2>Osapuolet</h2>
        <p class="space-above">Helsingin kaupunki</p>
        <section class="half-left">
          <!-- <p>[Hakijan nimi], [Y-tunnus]<br/>[Osoite, postinumero, toimipaikka]<br/>
            [Sähköpostiosoite, puhelin]</p> -->
          <p class="space-above">
            <xsl:for-each select="data/customerAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>

        <section class="half-right">
          <!-- <p>[Yhteyshenkilön nimi]<br/>[Sähköpostiosoite, puhelin]</p> -->
          <p class="space-above">
            <xsl:for-each select="data/customerContactLines">
              <xsl:value-of select="."/><br/>
            </xsl:for-each>
          </p>
        </section>

        <section>
          <p class="space-above">Tällä sopimuksella <xsl:value-of select="data/decisionId"/> sovitaan rakenteiden
            sijoittamisesta Helsingin kaupungin omistamalle ja hallitsemalle yleiselle alueelle.
            <xsl:if test="data/siteAddressLine != ''">
              Osoite: <xsl:value-of select="data/siteAddressLine"/>.
            </xsl:if>
            Kaupunginosa: <xsl:value-of select="data/siteCityDistrict"/>
          </p>

          <xsl:if test="data/contractText">
            <p class="space-above"> </p>
          </xsl:if>

          <xsl:for-each select="data/contractText">
            <p>
              <xsl:value-of select="."/>
              <xsl:if test="not(normalize-space(.))">
                <br/>
              </xsl:if>
            </p>
          </xsl:for-each>

        </section>
      </div>

      <xsl:if test="data/additionalConditions">
        <div class="unboxed">
          <section>
            <h2>Sopimuksen erityisehdot</h2>
              <p class="space-above"> </p>
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
        </div>
      </xsl:if>

      <div class="unboxed avoid-pb">
        <section>
          <h2>Perittävät maksut</h2>
          <xsl:choose>
            <xsl:when test="data/notBillable = 'false'">
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
            </xsl:when>
            <xsl:otherwise>
              <p>Korvauksetta.</p>
              <p class="space-above">
                Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
              </p>
            </xsl:otherwise>
          </xsl:choose>
          <p class="space-above">
            Maksut perustuvat kaupunkiympäristölautakunnan päätökseen 26.11.2024 §660.
          </p>
          <xsl:if test="data/notBillable = 'false' and data/separateBill = 'true'">
            <p class="space-above">
              <!-- Käytetään, jos lasku enemmän kuin 0 €. -->
              Lasku lähetetään erikseen.
            </p>
          </xsl:if>

        </section>
      </div>

      <div class="unboxed">
        <section>
          <h2>Yleiset sopimusehdot</h2>
	  <h2>1. Sopimuksen tarkoitus ja voimassaolo</h2>
	  <p>
	    Kaupunki antaa tällä sopimuksella oikeuden sijoittaa sopimuksessa yksilöidyt rakenteet
(jäljempänä “rakenteet”) kaupungin omistamalle kiinteistölle. Sopimus on voimassa toistaiseksi,
ellei määräaikaisuudesta ole erikseen sovittu.
	  </p>
	  <h2>2. Sopimuksen raukeaminen ja uusiminen</h2>
	  <p>
	    Sopimus raukeaa ilman erillistä ilmoitusta kaupungin allekirjoituspäivästä lukien yhden (1) vuoden
kuluttua, ellei rakenteiden toteutusta ole tuona aikana aloitettu. Sopimus on uusittava, mikäli
toteutussuunnitelma muuttuu tai kohteeseen sisältyy muita kuin hakemuksessa esitettyjä rakenteita.
	  </p>
	  <h2>3. Työn suorittaminen</h2>
	  <h2>3.1 Työn suorittamisen ajankohta</h2>
	  <p>
	    Työ on suoritettava tämän sopimuksen erityisehdoissa mainittuna ajankohtana, mikäli erityisehdoissa on
suorittamisajankohtaa koskeva ehto.
	  </p>
	  <h2>3.2 Kaivutyöt</h2>
	  <p>
	    Ennen kaivutyöhön ryhtymistä sijoittavan sopimusosapuolen on haettava johtoselvitys sekä tehtävä
kaupungille ilmoitus yleisellä alueella tehtävästä työstä. Ilmoituksessa on mainittava sijoitussopimuksen
tunnus. Ilmoituksen johdosta annettavan päätöksen yhteydessä kaupunki antaa määräyksiä mm. työn
suorittamisesta, liikennejärjestelyiden toteuttamisesta sekä aloitus- ja loppukatselmuksista.
Liikennealueella tai liikenteeseen vaikuttavalla alueella työskenneltäessä on aina esitettävä
työsuunnitelma sekä hyväksytettävä päätös tilapäisistä liikennejärjestelyistä. Työn aikana tarkastajalle
on sallittava pääsy alueelle valvomaan työn toteutusta.
	  </p>
	  <p>
Työn suorittamisessa ja ennallistamisessa noudatetaan ajantasaista Yleisten alueiden käyttö, tilapäiset
[yhteyshenkilön nimi]
yhteyshenkilon.sahkoposti@test.test,
[0931031222]
liikennejärjestelyt ja katutyöt -ohjetta (PKS-ohje), joka on luettavissa kaupungin verkkosivuilla
(sopimuksentekohetkellä osoitteessa https://www.hel.fi/fi/kaupunkiymparisto-jaliikenne/tontit-ja-rakentamisen-luvat/tyomaan-luvat-ja-ohjeet/kaduilla-ja-puistoissa-tehtavat-tyot). Työalue on
ennallistettava kaupungin hyväksymään tilaan aina rakenteisiin liittyvien kaivutöiden (mm. sijoittaminen,
kunnossapito tai poisto) jälkeen.
	  </p>
	  <p>
Matala-asennusmenetelmien (mm. ketjusahaus, jyrsintä, mikro-ojitus tai auraus) käyttö työn
toteutuksessa ei ole sallittua. Suojaputkitus tulee asentaa vähintään 70 cm peittosyvyyteen ja
varustetaan signaalilangalla, jotta paikannus onnistuu passiivisin peilausvälinein. Tästä ehdosta voi
poiketa vain kaupungin kirjallisella suostumuksella.
	  </p>
	  <h2>3.3 Viheralueiden suojaaminen</h2>
	  <p>
	    Ellei tämän sopimuksen erityisehdoissa toisin mainita, alueella kasvavia puita tai pensaita ei saa poistaa,
kaataa eikä juuristoja vahingoittaa sijoitustyön yhteydessä. Tarvittaessa puiden läheisyydessä työ on
suoritettava lapiokaivuuna puun juuristoa tai runkoa vaurioittamatta. Puiden rungot ja juuristot tulee
suojata asianmukaisesti noudattaen PKS-ohjetta ja InfraRYL-vaatimuksia (InfraRYL 11113.3
Kasvillisuuden ja luontoalueiden suojaaminen tai vastaava ajantasainen versio).
	  </p>
	  <h2>4. Kartoittaminen</h2>
	  <p>
	    Sijoittava sopimusosapuoli on velvollinen kartoittamaan sopimuksen mukaiset rakenteet. Sijaintitietojen
rekisteröimisen osalta on noudatettava kaupunkiympäristön kaupunkimittauspalveluiden ja maa- ja
kallioperä -yksikön ohjeita (sopimuksentekohetkellä luettavissa kaupungin verkkosivuilla osoitteessa
https://www.hel.fi/fi/kaupunkiymparisto-ja-liikenne/tontit-ja-rakentamisen-luvat/tyomaan-luvat-ja-ohjeet/sijoitussopimus) sekä muita kaupungin ilmoittamia ohjeita. Edellä mainittu koskee myös
rakenteiden siirtämistä ja poistamista.
	  </p>
	  <h2>5. Kunnossapito ja kustannukset</h2>
	  <p>
	    Sijoittava sopimusosapuoli vastaa rakenteiden kunnostamisesta ja kunnossapidosta.
	    Sijoittava sopimusosapuoli vastaa kaikista rakenteiden rakentamisen ja käytön kustannuksista.
	  </p>
	  <h2>6. Vakuus</h2>
	  <p>
	    Sijoittava sopimusosapuoli on velvollinen antamaan kaupungille vakuuden, jos vakuudesta on
erillinen ehto tämän sopimuksen erityisehdoissa. Vakuus toimii vakuutena kaikista tästä
sopimuksesta aiheutuvien velvollisuuksien täyttämisestä.
	  </p>
	  <p>
	    Vakuus palautetaan hyväksytyn loppukatselmuksen jälkeen siltä osin kuin sitä ei ole käytetty tai
kaupungilla ei ole vakuuteen kohdistuvia vaatimuksia. Tämän sopimuksen mukainen
loppukatselmus voidaan pitää myös kaivutyötä koskevan loppukatselmuksen yhteydessä.
Vakuudelle ei makseta korkoa.
	  </p>
	  <h2>7. Sopimussakko</h2>
	  <h2>7.1 Sopimussakkojen luokat</h2>
	  <p>
	    Sijoittava sopimusosapuoli voidaan velvoittaa maksamaan kaupungille sopimussakkoa
sopimuksen velvoitteiden laiminlyönneistä alla olevan luokituksen mukaisesti.
	  </p>
	  <h3>7.1.1 A-luokan sopimussakko: 6000 euroa</h3>
	  <ul>
	    <li>Vakavat sopimusrikkomukset</li>
	    <li>
	      Esimerkiksi rakenteita sijoitettu yli 2 kilometrin matkalta sopimuksen vastaisesti esim.
sopimuksesta poikkeavaan sijaintiin tai asennussyvyyteen, tai sijoitettu enemmän rakenteita kuin
sopimuksessa. 2 kilometrin matkaan lasketaan mukaan koko se matka, jolla rakenne on sijoitettu
sopimuksen vastaisesti. Esimerkiksi 1,5 km väärässä asennussyvyydessä oleva rakenne ja 1,5
km väärässä sijainnissa oleva rakenne tarkoittavat, että rakenteita on sijoitettu sopimuksen
vastaisesti yli 2 km matkalta.
	    </li>
	  </ul>
	  <h3>7.1.2 B-luokan sopimussakko: 3000 euroa</h3>
	  <ul>
	    <li>Vakavuustasoltaan keskitason sopimusrikkomukset</li>
	    <li>
	      Esimerkiksi rakenteita sijoitettu alle 2 kilometrin matkalta sopimuksen vastaisesti esim.
sopimuksesta poikkeavaan sijaintiin tai asennussyvyyteen, tai sijoitettu enemmän rakenteita kuin
sopimuksessa.
	    </li>
	  </ul>
	  <h3>7.1.3 C-luokan sopimussakko: 1000 euroa</h3>
	  <ul>
	    <li>Vähäisemmät sopimusrikkomukset</li>
	  </ul>
	  <h2>7.2 Sopimussakon kertyminen jatkuvissa laiminlyönneissä</h2>
	  <p>
	    Mikäli kaupunki katsoo sijoittavan sopimusosapuolen olevan velvollinen maksamaan sopimussakkoa,
kaupungin ilmoittaa siitä kirjallisesti sijoittavalle sopimusosapuolelle riittävästi yksilöitynä ja lähettää
sopimussakosta laskun.
	  </p>
	  <h2>8. Vahingonkorvaus</h2>
	  <h2>8.1 Yleiset vahingonkorvausehdot</h2>
	  <p>
	    Sopimussakosta riippumatta sijoittava sopimusosapuoli on velvollinen korvaamaan kaupungille ja
kolmannelle aiheuttamansa vahingon.
	  </p>
	  <p>
	    Jos kaupunki velvoitetaan tuomioistuimen lainvoimaisella tuomiolla suorittamaan kolmannelle
osapuolelle vahingonkorvausta vahingosta, joka on aiheutunut sijoittavan sopimusosapuolen
sopimusrikkomuksesta tai tuottamuksesta, on kaupungilla oikeus periä sijoittavalta sopimusosapuolelta
kolmannelle osapuolelle tällaisesta sijoittavan sopimusosapuolen sopimusrikkomuksen tai tuottamuksen
johdosta aiheutuneesta vahingosta suorittamansa vahingonkorvaus
	  </p>
	  <p>
	    Kaupunki ei vastaa välillisistä vahingoista. Kaupungin suorittama työn valvonta ei poista sijoittavan
sopimusosapuolen vastuuta aiheutuneista vahingoista.
	  </p>
	  <h2>8.2 Puuvaurioita koskevat vahingonkorvausehdot</h2>
	  <p>
	    Puuvauriosta, joka edellyttää kaupungin arvion mukaan kokonaisen puun uusimista, sijoittava
sopimusosapuoli on velvollinen maksamaan vahingonkorvausta kaupungin arvioiman puun arvon
mukaisesti, kuitenkin vähintään 10 000 euroa. Lisäksi sijoittavan sopimusosapuolen on kaupungin niin
vaatiessa istutettava uusi puu kustannuksellaan ja tehtävä puulle kaupungin määräämät
hoitotoimenpiteet kaupungin määräämän ajan. Kaupunki määrää tällöin istutustavan sekä istutettavan
lajin ja koon.
	  </p>
	  <p>
	    Pienemmistä puuvaurioista (esimerkiksi katkenneet oksat, runkovauriot ja pienemmät juuristovauriot)
sijoittava sopimusosapuoli on velvollinen maksamaan kaupungille vahingonkorvausta kaupungin
arvioiman vaurion arvon mukaisesti, kuitenkin vähintään 3000 euroa. Lisäksi sijoittavan
sopimusosapuolen on tehtävä puulle hoitoleikkaus kustannuksellaan kaupungin määräämällä tavalla.
	  </p>
	  <h2>9. Kaupungin oikeus laiminlyöntien korjaamiseen</h2>
	  <p>
	    os työaluetta ei ole ennallistettu kaupungin ohjeiden ja määräysten mukaisesti kaupungin kaivutyötä
koskevassa päätöksessä mainittuun päivämäärään tai kaupungin erikseen määräämään päivämäärään
mennessä, kaupungilla on oikeus ennallistaa alue sijoittavan sopimusosapuolen kustannuksella.
	  </p>
	  <p>
	    Jos rakenteita sijoitetaan sopimuksen vastaisesti (esim. sopimuksesta poikkeavaan sijaintiin) tai jos
sopimusehtoja muuten laiminlyödään, kaupungilla on oikeus 1) siirtää tai poistaa rakenteet sijoittavan
sopimusosapuolen kustannuksella ja 2) ennallistaa alue sijoittavan sopimusosapuolen kustannuksella.
	  </p>
	  <h2>10. Rakenteiden siirtäminen</h2>
	  <p>
	    Kaupungilla on oikeus välttämättömän syyn vuoksi vaatia rakenteiden siirtämistä väliaikaisesti tai
pysyvästi, jolloin sijoittavan sopimusosapuolen on tehtävä siirtotyö kustannuksellaan.
	  </p>
	  <h2>11. Sopimuksen irtisanominen</h2>
	  <h2>11.1 Irtisanomisaika</h2>
	  <p>
	    Sopimuksen irtisanomisaika on kuusi (6) kuukautta pois lukien maalämpöporakaivot varusteineen, joita
koskevan sijoitussopimuksen irtisanomisaika on kaksi (2) vuotta.
	  </p>
	  <h2>11.2 Menettely kaupungin irtisanoessa</h2>
	  <p>
	    Sopimuksen irtisanomisaika on kuusi (6) kuukautta pois lukien maalämpöporakaivot varusteineen, joita
koskevan sijoitussopimuksen irtisanomisaika on kaksi (2) vuotta.
11.2 Menettely kaupungin irtisanoessa
Kaupungin irtisanoessa sopimuksen sijoitetut rakenteet on poistettava, ellei toisin sovita. Sijoittavan
sopimusosapuolen on tehtävä työ kustannuksellaan. Vaihtoehtoisesti kaupungin erillisellä kirjallisella
suostumuksella rakenteet voi jättää paikoilleen, jolloin kaupunki merkitsee ne käytöstä poistetuksi
johtokartalle.
	  </p>
	  <h2>11.3 Menettely sijoittavan sopimusosapuolen irtisanoessa</h2>
	  <p>
	    Sijoittavan sopimusosapuolen irtisanoessa sopimuksen rakenteita ei lähtökohtaisesti tarvitse poistaa.
Kaupungilla on kuitenkin tarvittaessa oikeus vaatia rakenteiden poistoa myös sijoittavan
sopimusosapuolen irtisanoessa sopimuksen, jolloin sijoittavan sopimusosapuolen on poistettava
rakenteet kustannuksellaan.
	  </p>
	  <p>
	    Kaikista käytöstä poistetuista rakenteista on ilmoitettava kaupungin alueiden käyttö- ja valvontayksikön
katutyö- ja sijaintipalvelut –tiimille (kymp.alueidenkaytto@hel.fi) viipymättä. Vaikka rakenteet
poistettaisiin käytöstä, ne säilyvät edelleen sijoittavan sopimusosapuolen omistuksessa ja
kunnossapitovastuulla.
	  </p>
	  <h2>11.4 Toimenpiteet irtisanomisajan päättymisen jälkeen</h2>
	  <p>
	    Jos rakenteita ei ole poistettu irtisanomisajan päättymiseen mennessä, kaupungilla on oikeus 1) siirtää
tai poistaa rakenteet sijoittavan sopimusosapuolen kustannuksella ja 2) ennallistaa alue sijoittavan
sopimusosapuolen kustannuksella.
	  </p>
	  <h2>12. Hylätyt rakenteet</h2>
	  <p>
	    Jos kaupungilla on perusteltu syy epäillä, ettei rakenteita enää käytetä, ne aiheuttavat vaaraa tai niiden
omistajuus on epäselvä, kaupungilla on tarvittaessa oikeus 1) siirtää tai poistaa rakenteet sijoittavan
sopimusosapuolen kustannuksella ja 2) ennallistaa alue sijoittavan sopimusosapuolen kustannuksella.
Vaihtoehtoisesti kaupunki voi jättää rakenteet paikoilleen ja merkitä ne käytöstä poistetuksi johtokartalle.
	  </p>
	  <p>
	    Edellä tarkoitettu perusteltu syy voi olla esimerkiksi se, ettei sijoittava sopimusosapuoli vastaa kaupungin
yhteydenottoon kahden (2) kuukauden kuluessa.
	  </p>
	  <h2>13. Omistajanvaihdokset</h2>
	  <p>
	    Jos sijoitettavien rakenteiden omistaja vaihtuu, sijoittava sopimusosapuoli on velvollinen siirtämään
tämän sopimuksen rakenteiden luovutuksensaajalle.
	  </p>
	  <p>
	    Jos sijoitettavat rakenteet palvelevat kiinteistöä tai rakennusta, sijoittava sopimusosapuoli on velvollinen
siirtämään tämän sopimuksen kiinteistön tai rakennuksen luovutuksensaajalle.
	  </p>
	  <p>
	    Edellä kuvatusta sopimuksen siirtämisestä on ilmoitettava kaupungille.
	  </p>
	  <h2>14. Muutoksenhaun vaikutus sopimukseen</h2>
	  <p>
	    Mikäli alueiden käyttö ja valvonta -yksikön katutyö- ja sijaintipalveluiden tiimipäällikön päätös kumoutuu
tai muuttuu oikaisuvaatimuksen, kunnallisvalituksen tai ylemmän toimielimen päätöksen johdosta taikka
jos oikaisuvaatimuksen tai valituksen käsittelevä viranomainen, toimielin tai valitusviranomainen kieltää
täytäntöönpanon, katsotaan sopimus purkautuneeksi. Kaupunki ei vastaa sijoittavalle
sopimusosapuolelle aiheutuneista kustannuksista, mikäli sopimus edellä mainitun mukaisesti katsotaan
purkautuneeksi.
	  </p>
        </section>
      </div>

      <div class="unboxed avoid-pb">
        <section>
          <h2>Sopimuksen allekirjoitukset</h2>

          <xsl:if test="data/draft = 'false'">
            <p class="indented">Helsingin kaupungin puolesta alueiden käyttö ja -valvontayksikön katutyö- ja sijaintipalveluiden
              tiimipäällikkö on allekirjoittanut tämän asiakirjan sähköisesti</p>
            <p class="indented">
              <xsl:value-of select="data/decisionTimestamp"/>,
              <xsl:value-of select="data/deciderTitle"/>,
              <xsl:value-of select="data/deciderName"/>
            </p>
          </xsl:if>

          <xsl:choose>
            <xsl:when test="data/frameAgreement = 'true'">
              <p class="indented space-above">
                Hakijan kanssa on voimassaoleva muu erillinen sopimus ja siksi tämä sijoitussopimus ei
                tarvitse erillistä asiakkaan allekirjoitusta.
              </p>
            </xsl:when>
            <xsl:when test="data/contractAsAttachment = 'true'">
              <p class="indented space-above">
                Hakijan tai hakijan edustajan allekirjoitus on tämän sopimuksen liitteenä olevassa
                asiakirjassa.
              </p>
            </xsl:when>
            <xsl:otherwise>
              <xsl:if test="data/draft = 'true'">
                <p class="indented space-above">
                  <xsl:value-of select="data/applicantName"/>
                </p>
                <p class="indented signature"></p>
              </xsl:if>

              <xsl:if test="data/draft = 'false'">
                <p class="indented space-above">
                  <xsl:value-of select="data/applicantName"/> puolesta <xsl:value-of select="data/contractSigner"/> <!-- [asioinnissa sopimuksen hyväksyjä] -->
                  on allekirjoittanut tämän asiakirjan sähköisesti <xsl:value-of select="data/contractSigningDate"/> <!-- [aikaleima sopimuksen hyväksynnästä asioinnissa]-->
                </p>
              </xsl:if>

            </xsl:otherwise>
          </xsl:choose>
        </section>
      </div>

      <div class="unboxed avoid-pb">
        <section class="half-left">
          <h2>Lisätiedot</h2>
        </section>

        <section class="half-right">
          <h2>Käsittelijä</h2>
          <p><xsl:value-of select="data/handlerTitle"/>, <xsl:value-of select="data/handlerName"/></p>
        </section>
      </div>

      <div class="unboxed avoid-pb">
        <h2>Sijoitussopimukset:</h2>
        <p>Katutyö- ja sijaintipalvelut -tiimi</p>
        <p>Työpajankatu 8</p>
        <p>PL 58231, 00099 Helsingin kaupunki</p>
        <p>asiakaspalvelu puh. (09) 310 22111</p>
        <p>sähköposti alueidenkaytto@hel.fi</p>
      </div>
      <div class="unboxed avoid-pb">
        <h2>Kaivuilmoitukset:</h2>
        <p>Katutyö- ja sijaintipalvelut -tiimi</p>
        <p>Työpajankatu 8</p>
        <p>PL 58231, 00099 Helsingin kaupunki</p>
        <p>asiakaspalvelu puh. (09) 310 22111</p>
        <p>sähköposti luvat@hel.fi</p>
      </div>
      <div class="unboxed avoid-pb">
        <h2>Kartoitus- ja sijaintitiedot:</h2>
        <p>Karttatiedot -tiimi</p>
        <p>Työpajankatu 8</p>
        <p>PL 58232, 00099 Helsingin kaupunki</p>
        <p>puh. (09) 310 31930</p>
        <p>sähköposti karttatiedot@hel.fi</p>
      </div>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
