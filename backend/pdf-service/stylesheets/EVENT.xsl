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
    <xsl:if test="data/draft = 'true'">
      <link rel="stylesheet" href="watermark.css" />
    </xsl:if>
  </head>
  <body>
    <div class="body">

      <div class="unboxed">
        <section class="half-left">
          <h1>Päätöksen hakija</h1>
          <!-- <p>[Hakijan nimi], [Y-tunnus]<br/>[Osoite, postinumero, toimipaikka]<br/>
            [Sähköpostiosoite, puhelin]</p> -->
          <p>
            <xsl:for-each select="data/customerAddressLines">
              <xsl:value-of select="." /><br/>
            </xsl:for-each>
          </p>
        </section>

        <section class="half-right">
          <xsl:if test="data/customerContactLines != '' and data/anonymizedDocument = 'false'">
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
          <h1>Kohde</h1>
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
          <xsl:if test="data/siteArea != '' and data/siteArea != 0">
            <p class="pt-10">
               Pinta-ala: <xsl:value-of select="data/siteArea" /> m<sup>2</sup><!-- [Alueen pinta-ala] -->
            </p>
          </xsl:if>
        </section>

        <section class="half-right">
          <h1>Vuokra-aika</h1>

          <xsl:if test="data/numBuildAndTeardownDays != 0">
            <p>
              <!-- Käytetään, jos rakentamis- ja purkamispäiviä täytetty -->
              Kokonaisvuokra-aika:
              <!-- [Varauksen alkupäivämäärä]-[Varauksen loppupäivämäärä] -->
              <xsl:value-of select="data/reservationStartDate"/>
              <xsl:if test="data/numReservationDays > 1">
                <!-- Käytetään, jos varauspäiviä enemmän kuin 1. -->
                <!-- -[Varauksen loppupäivämäärä] -->
                &#8204; &#x2013; <xsl:value-of select="data/reservationEndDate"/>
              </xsl:if>
            </p>
          </xsl:if>
          <p class="pt-10">
            Tapahtumapäivä(t):
            <!-- [Tapahtuman alkupäivämäärä] -->
            <xsl:value-of select="data/eventStartDate"/>
            <xsl:if test="data/numEventDays > 1">
              <!-- Käytetään, jos tapahtumapäiviä enemmän kuin 1. -->
              <!-- -[Tapahtuman loppupäivämäärä] -->
              &#8204; &#x2013; <xsl:value-of select="data/eventEndDate"/>
            </xsl:if>
          </p>

          <xsl:if test="data/numEventDays > 1">
            <p>
              <!-- Käytetään, jos tapahtumapäiviä enemmän kuin 1. -->
              Tapahtumapäiviä: <xsl:value-of select="data/numEventDays"/><!-- [tapahtumapäivien lukumäärä] -->
            </p>
          </xsl:if>

          <xsl:if test="data/buildStartDate != ''">
            <!-- Käytetään, jos rakentamis- ja purkupäiviä täytetty. Jos
              rakentamis- tai purkupäiviä vain yksi, vain yksi päivämäärä
              näkyy päätöksessä (ei päättymispäivämäärää) -->
            <p class="pt-10">
              Rakentamispäivä(t):
              <xsl:value-of select="data/buildStartDate"/><!-- [Rakentamisen alkupäivämäärä] -->
              <xsl:if test="data/buildStartDate != data/buildEndDate">
              &#8204; &#x2013; <xsl:value-of select="data/buildEndDate"/>
              </xsl:if><!-- - [Tapahtuman alkupäivämäärä-1] -->
             </p>
          </xsl:if>
          <xsl:if test="data/teardownEndDate != ''">
            <p>
              Purkupäivä(t): <xsl:value-of select="data/teardownStartDate"/><!-- [Tapahtuman loppupäivämäärä+1] -->
              <xsl:if test="data/teardownStartDate != data/teardownEndDate">
              &#8204; &#x2013; <xsl:value-of select="data/teardownEndDate"/>
              </xsl:if><!-- -[Purkamisen loppupäivämäärä] -->
            </p>
          </xsl:if>
          <xsl:if test="data/numBuildAndTeardownDays > 0">
            <p>
              Rakentamis- ja purkupäiviä: <xsl:value-of select="data/numBuildAndTeardownDays"/>
              <!-- [rakentamis- ja purkupäivien lukumäärä] -->
            </p>
          </xsl:if>
          <xsl:if test="data/reservationTimeExceptions != ''">
            <p class="pt-10">
              <!-- Käytetään, jos Tapahtuma-ajan poikkeukset –kenttä täytetty -->
              <!-- [Tapahtuma-ajan poikkeukset] -->
              Tapahtuma-ajan poikkeukset: <xsl:value-of select="data/reservationTimeExceptions"/>
            </p>
          </xsl:if>
        </section>
      </div>

      <div class="unboxed">
        <section class="half-left">
          <h1>Vuokrauksen tarkoitus</h1>
          <p>
            <!-- [Tapahtuman nimi]  -->
            <xsl:value-of select="data/eventName"/>
          </p>
          <p class="pt-10">
            <!--  [Tapahtuman kuvaus]  -->
            <xsl:value-of select="data/eventDescription"/>
            <xsl:if test="data/eventUrl != ''">,
              <xsl:element name="a">
                <xsl:attribute name="href">
                  <xsl:value-of select="data/eventUrl"/>
                </xsl:attribute>
                <xsl:value-of select="data/eventUrl"/>
              </xsl:element>
            </xsl:if>
          </p>
          <xsl:if test="data/eventNature != ''">
            <p class="pt-10">
              <!-- Ulkoilmatapahtuma/Avoin = Yleisölle pääsymaksuton tapahtuma; Ulkoilmatapahtuma/Maksullinen = Yleisölle pääsymaksullinen tapahtuma; Ulkoilmatapahtuma/Suljettu = Kutsuvierastilaisuus tai muu vastaava suljettu tapahtuma; Promootio = Promootiotapahtuma; Vaalit = Vaalit -->
              <!-- [Tapahtuman luonne] -->
              <xsl:value-of select="data/eventNature"/>
            </p>
          </xsl:if>
          <xsl:if test="data/structureArea > 0">
            <!-- Käytetään, jos tapahtuma sisältää rakenteita. -->
            <p class="pt-10">
              Tapahtuma sisältää
              <!-- [rakenteiden kokonaisneliömäärä] -->
              <xsl:value-of select="data/structureArea" /> m<sup>2</sup>
              rakenteita.
            </p>
            <p class="pt-10">
              <!-- [Rakenteiden kuvaus] -->
              <xsl:value-of select="data/structureDescription" />
            </p>
          </xsl:if>
        </section>

        <section class="half-right">
          <h1>Vuokra</h1>
          <xsl:choose>
            <xsl:when test="data/notBillable = 'false'">
              <p>
                <!-- [Hinta] -->
                <xsl:value-of select="data/totalRent"/>
                <!-- alv 24 % tai alv 0 %, riippuen asiakkaasta -->
                 + ALV <xsl:value-of select="data/vatPercentage"/> %
              </p>
              <!-- Hinnan peruste tyyppikohtaisesti. Erillinen lista -->
              <!-- Confluencessa: -->
              <p class="pt-10">
                <!-- [Hinnan peruste, raakaa HTML:ää] -->
                <xsl:value-of select="data/priceBasisText" disable-output-escaping="yes"/>
              </p>
            </xsl:when>
            <xsl:otherwise>
              <p>Korvauksetta.</p>
              <p class="pt-10">
                Korvauksettomuuden peruste: <xsl:value-of select="data/notBillableReason"/>
              </p>
            </xsl:otherwise>
          </xsl:choose>
          <p class="pt-10">
            Vuokrauspäätöksen hinta perustuu kaupunkiympäristölautakunnan päätökseen 15.1.2019 § 15.
          </p>
          <xsl:if test="data/notBillable = 'false' and data/separateBill = 'true'">
            <p class="pt-10">
              <!-- Käytetään, jos lasku enemmän kuin 0 €. -->
              Lasku lähetetään erikseen.
            </p>
          </xsl:if>
        </section>
      </div>

      <xsl:if test="data/notBillable = 'false' and data/chargeInfoEntries">
        <section class="unboxed">
          <h1>Vuokran erittely</h1>

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
        </section>
      </xsl:if>

      <section class="unboxed">
        <h1>Ehdot</h1>
        <p>
          Liitteenä olevia ehtoja on noudatettava.
        </p>
        <xsl:if test="data/additionalConditions">
          <!-- Käytetään, jos Alluun on kirjoitettu vapaaseen
               tekstikenttään lisäehtoja. -->
          <p class="pt-10 space-below">
            Lisäksi on noudatettava seuraavia ehtoja:
          </p>
          <xsl:for-each select="data/additionalConditions">
            <p>
              <!-- [Ehtokentän teksti]  -->
              <xsl:value-of select="."/>
              <xsl:if test="not(normalize-space(.))">
                <br/>
              </xsl:if>
            </p>
          </xsl:for-each>
        </xsl:if>
      </section>

      <section class="unboxed avoid-pb">
        <h1>Päätös</h1>
        <p>
          Hakija on hakenut oikeutta alueen käyttöön, toimittanut hakemuksen liitteineen kaupunkitilan käyttö ja maanvuokraus –yksikköön ja ilmoittanut sitoutuvansa alueen käyttöä koskevaan ohjeistukseen sekä sopimusehtoihin.
        </p>
        <p class="pt-10">
          Allekirjoittanut viranhaltija päätti myöntää luvan hakijalle haetun alueen käyttämiseen yllä olevin ehdoin.
        </p>
        <p class="pt-10">
          Tämä päätös on sähköisesti allekirjoitettu.
        </p>
        <p class="pt-10">
          <!-- [aikaleima], [päättäjän työnimike], [päättäjän nimi] -->
          <xsl:value-of select="data/decisionTimestamp"/>,
          <xsl:value-of select="data/deciderTitle"/>,
          <xsl:value-of select="data/deciderName"/>
        </p>
      </section>

      <div class="unboxed avoid-pb">
        <section class="half-left">
          <h1>Lisätiedot</h1>
          <p>Kaupunkitilan käyttö ja maanvuokraus –yksikkö</p>
        </section>

        <section class="half-right">
          <h1>Käsittelijä</h1>
          <p>
            <!--  [titteli, tarkastajan nimi] -->
            <xsl:value-of select="data/handlerTitle"/>,
            <xsl:value-of select="data/handlerName"/>
          </p>
          <p>
            <!--  [sähköposti] -->
            <xsl:value-of select="data/handlerEmail"/>
          </p>
        </section>
      </div>

      <section class="unboxed avoid-pb">
        <h1>Liitteet</h1>
        <p>
          <!--  [Lista liitteiden nimistä] -->
          <xsl:for-each select="data/attachmentNames">
            <xsl:value-of select="." /><br/>
          </xsl:for-each>
        </p>
      </section>

      <!-- Ulkoilmatapahtumien yleiset ehdot -->
      <section class="new-page">
        <h1 class="fs-16">Ulkoilmatapahtumien yleiset ehdot 1.2.2019 alkaen</h1>
        <ol>
          <li>
            Luvansaaja tai tapahtuman järjestäjä on velvollinen tekemään kokoontumislain (530/1999)
            mahdollisesti edellyttämän ilmoituksen poliisille. Järjestelyissä on noudatettava poliisin ohjeita
            ja määräyksiä.
          </li>
          <li>
            Luvansaaja tai tapahtuman järjestäjä on velvollinen laatimaan pelastuslain (379/2011, 16 §) ja -
            asetuksen (407/2011, 3 §) mahdollisesti edellyttämän pelastussuunnitelman.
            Pelastussuunnitelma toimitetaan pelastuslaitokselle annettujen ohjeiden mukaisesti.
          </li>
          <li>
            <i>Ehtoja päivitetty 26.3.2020:</i> Toiminnanharjoittaja on velvollinen tekemään
            ympäristönsuojelulain (527/2014) mahdollisesti edellyttämän meluilmoituksen Helsingin
            ympäristöpalveluiden ympäristönseuranta- ja -valvontayksikköön. Ilmoitus on lain mukaan
            tehtävä kunnan ympäristönsuojeluviranomaiselle viimeistään 30 vuorokautta ennen toiminnan
            aloittamista. Äänentoistolaitteiden käyttö on sopeutettava tapahtuman yleisömäärän mukaan
            niin, että vaikutukset tapahtuma-alueen ulkopuolelle jäävät mahdollisimman vähäisiksi.
            Tapahtumasta on jaettava tiedotteet viimeistään viikkoa ennen tapahtumaa melun
            vaikutuspiirissä oleviin lähiympäristön asuinrakennuksiin, ja kuitenkin vähintään tapahtumaaluetta ympäröiviin rakennuksiin. Lisäksi meluntorjunnasta on neuvoteltava etukäteen erikseen
            herkkien kohteiden, kuten hoito- ja oppilaitosten sekä kirkollisten laitosten kanssa.
          </li>
          <li>
            Luvansaaja tai tapahtuman järjestäjä on velvollinen tiedottamaan elintarvikelain (23/2006)
            mukaisesti mahdollisesta elintarvikkeiden tarjoilusta ja myynnistä Helsingin
            ympäristöpalveluiden elintarviketurvallisuusyksikölle.
          </li>
          <li>
            Luvansaaja tai tapahtuman järjestäjä on velvollinen järjestämään paikalle lainsäädännön
            (STM:n asetus 405/2009) edellyttämän määrän yleisökäymälöitä, sekä jäteastioita yms.
            varusteita ja laitteita, sekä tiedottamaan tapahtumaan liittyvistä huoltojärjestelyistä yli 500
            hengen tapahtumissa Helsingin ympäristöpalveluiden elintarviketurvallisuusyksikölle.
            Käymälöiden viereen ja/tai sisälle tulee sijoittaa riittävästi roska-astioita ja opasteita, jotta
            tapahtumayleisö ei heittäisi käymälälietteen joukkoon sinne kuulumattomia jätteitä.
            Tapahtuman jätehuollossa on noudatettava pääkaupunkiseudun ja Kirkkonummen yleisissä
            jätehuoltomääräyksissä annettuja jätteiden lajitteluvelvoitteita.
            <br/><br/>
            <i>Ehtoja päivitetty 27.11.2019:</i> Lisäksi yleisötilaisuuden, johon ennakoidaan osallistuvan yhtä
            aikaa yli 500 henkilöä, järjestäjän on toimitettava kunnan ympäristönsuojeluviranomaiselle
            vähintään 30 vuorokautta ennen tilaisuuden alkua jätehuoltosuunnitelma, sekä toimitettava
            loppuraportti toteutuneista jätemääristä viimeistään kolmen kuukauden kuluttua tilaisuuden
            järjestämisestä. Jätehuoltosuunnitelmasta määrätään tarkemmin pääkaupunkiseudun ja
            Kirkkonummen jätehuoltomääräyksissä.
          </li>
          <li>
            Luvansaaja tai tapahtuman järjestäjä vastaa jätelain (646/2011) 74 §:n 1 mom 3 kohdan nojalla
            tapahtuma-alueen sekä ja tapahtuman välittömän lähialueen pitämisestä siistinä koko
            tapahtuman ajan ja siivoamisesta välittömästi tapahtuman päätyttyä. Muussa tapauksessa
            kaupungilla on oikeus ilman eri kehotusta laskuttaa luvansaajalta alueen siistimisestä
            aiheutuvat kulut.
          </li>
          <li>
            Luvansaaja tai tapahtuman järjestäjä on velvollinen laatimaan kuluttajaturvallisuuslain
            (920/2011) mahdollisesti edellyttämän turvallisuusasiakirjan Turvallisuus- ja kemikaalivirastolle
            (TUKES). Turvallisuusasiakirja on laadittava, mikäli tapahtuma sisältää merkittävän riskin, josta toteutuessaan voi aiheutua vaaraa jonkun turvallisuudelle, esim. huvipuistolaitteet ja
            ohjelmapalvelu (https://tukes.fi/asiointi).
          </li>
          <li>
            Luvansaaja tai tapahtuman järjestäjä on velvollinen tekemään muut tapahtumaan liittyvät
            ilmoitukset ja hakemukset asianmukaisille viranomaisille.
          </li>
          <li>
            Luvansaaja tai tapahtuman järjestäjä vastaa kaikista kaupungille tai kolmannelle osapuolelle
            tapahtumasta tai siihen liittyvästä toimenpiteestä aiheutuvasta vahingosta tai haitasta.
            Kaupungin valvonta ei poista luvansaajan tai tapahtuman järjestäjän vastuuta.
          </li>
          <li>
            Vahinkoriskin ollessa vähäistä suurempi luvansaaja ja tapahtuman järjestäjä ovat velvollisia
            hankkimaan tarpeellisen vakuutussuojan.
          </li>
          <li>
            Kaupunki ei vastaa luvansaajalle tai tapahtuman järjestäjälle aiheutuneista kustannuksista,
            mikäli päätös oikaisuvaatimuksen, kunnallisvalituksen tai ylemmän toimielimen päätöksen
            johdosta muuttuu tai kumoutuu.
          </li>
          <li>
            Luvansaaja tai tapahtuman järjestäjä ei saa siirtää oikeuttaan kolmannelle ilman kaupungin
            kirjallista suostumusta.
          </li>
          <li>
            Viranomaisilla ja kaupunkiympäristön toimialan alueidenkäyttö ja -valvonta -yksikön edustajilla
            tulee olla esteetön pääsy tapahtuma-alueelle työtehtävissä.
          </li>
          <li>
            Luvansaaja tai tapahtuman järjestäjä on velvollinen selvittämään pystytettävien rakenteiden,
            varusteiden tai laitteiden luvanvaraisuuden ja hakemaan kaikki tarvittavat rakentamista tai
            käyttöä koskevat luvat.
          </li>
        </ol>

        <div>
          <h2>Varauksen peruuttaminen</h2>
          <p class="pt-10">
            Varauksen peruutuksesta kaupunki perii 50 %:n maksun kokonaisvuokrasta silloin, kun
            alueenkäytön peruutus tehdään myöhemmin kuin alla mainitussa aikataulussa:
          </p>
          <ul>
            <li class="mt-0">kun varauspäiviä on 1-2 ja peruutus tehdään alle 14 vuorokautta ennen</li>
            <li class="mt-0">kun varauspäiviä on 3-6 ja peruutus tehdään alle 30 vuorokautta ennen</li>
            <li class="mt-0">kun varauspäiviä on 7-13 ja peruutus tehdään alle 3 kuukautta ennen</li>
            <li class="mt-0">kun varauspäiviä on 14 tai enemmän ja peruutus tehdään alle 6 kuukautta ennen</li>
          </ul>
        </div>

        <div>
          <h2>Sopimussakko</h2>
          <p class="pt-10">
            Sopimuksen mukaisen velvoitteen rikkominen oikeuttaa kaupungin laskuttamaan sopimussakkoa
            500 euroa per rikkomus. Mikäli rikkomus on luonteeltaan jatkuva eikä asian tilaa korjata, kaupunki
            voi laskuttaa sopimussakkoa 500 euroa jokaiselta alkavalta päivältä, jolloin sopimusrikkomus on
            yhä olemassa. Sopimussakko voidaan laskuttaa myös mahdollisen sopimuksen päättämisen
            lisäksi.
          </p>
          <p class="pt-10">
            Sopimussakon määräämisellä pyritään varmistamaan maanomistajan suostumuksessa tai
            vuokrasopimuksessa määritettyjen maankäyttöön liittyvien ehtojen noudattaminen. Kaupungilla on
            oikeus saada vahingonkorvausta sopimussakon ohella niiltä osin kuin aiheutuneet vahingot
            ylittävät määrältään sopimussakon määrän.
          </p>
        </div>

        <div class="pt-10">
          <h2>Vakuus ja sen suuruus</h2>
          <p class="pt-10">
            Tapahtumajärjestäjällä on velvollisuus asettaa sopimuksenmukaisten velvollisuuksien
            turvaamiseksi kaupungille vakuus kaupungin näin vaatiessa. Tapahtumissa vakuus määritetään
            kattamaan ennakoitavissa olevat riskit kuten esim. arvioituja ennallistamiskustannuksia vastaava
            summa. Isojen tapahtumien osalta vakuuden suuruus arvioidaan tapauskohtaisesti ennalta
            tunnistettujen riskitekijöiden perusteella.
          </p>
        </div>

        <div class="pt-10 avoid-pb">
          <h2>Muut tapahtumasta aiheutuneet kulut</h2>
          <p class="pt-10">
            Tapahtuman järjestämisestä kaupungille ennakkoon tiedossa olevista toimenpiteistä aiheutuvia
            muita kuluja voidaan periä asiakkaalta kyseisestä työstä kaupungille toteutuneiden kustannusten
            mukaisesti. Näitä ovat esim. kustannukset katukalusteiden tilapäisestä poistamisesta tilasta
            tapahtuman ajaksi ym. vastaavat yksiselitteiset toimenpiteet, joiden välitön vaikutus
            kaupunkikuvaan on vähäinen. Kustannuksista on sovittava kirjallisesti tapahtuman järjestäjän
            kanssa alueen tapahtumakäyttöön luovuttamisen yhteydessä.
          </p>
        </div>

        <div class="pt-10">
          <h2>Rekisteriseloste</h2>
          <p class="pt-10">
            Lupa- ja vuokrausprosessissa antamianne henkilötietoja käsitellään EU:n yleisen tietosuojaasetuksen (679/2016) mukaisesti. Henkilötietojen käsittelyä koskevat rekisteriselosteet ovat
            nähtävissä osoitteessa www.hel.fi/rekisteriseloste.
          </p>
        </div>
      </section>

      <!-- Muutoksenhakuohjeet -->
      <section class="new-page">
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

        <div class="indent-50">
          <h3 class="ml-0">Tietopyynnöt</h3>
          <p class="indent-50">
            Päätöstä koskevia otteita ja liitteitä lähetetään pyynnöstä. Asiakirjoja voi tilata Helsingin kaupungin kirjaamosta.
          </p>
        </div>
      </section>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
