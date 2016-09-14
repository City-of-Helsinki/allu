<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>
<xsl:template match="/root">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <xsl:element name="base">
  <xsl:attribute name="href">
  <xsl:value-of select="basedir"/>
  </xsl:attribute>
  </xsl:element>
    <title></title>
    <style type="text/css">
      @page { margin-left: 2cm; margin-right: 2cm; margin-top: 0.75cm; margin-bottom: 0.47cm }
      p { font-family: Arial, serif; font-size: 12pt; margin: 0em }
      table.heading { width: 100%; padding: 0pt; margin: 0pt; line-height: 90% }
      div.line {border-bottom: 1pt solid #000000; }
      .sisennys { margin-left: 2.3cm}
      p.otsikko { margin-left: -2.3cm; font-weight:bold }
      th { font-weight: bold; text-align: left }
    </style>
  </head>
  <body>
    <!-- Ylätunniste -->
    <table class="heading">
      <colgroup>
	<col style="width:50%"/>
	<col style="width:30%"/>
	<col style="width:20%"/>
      </colgroup>
      <tr><td/><td/><td><img style="float:right" src="rakennusvirasto_logo.png"  width="171" height="38" border="0"/></td></tr>
      <tr><td/><td><b>Vuokrauspäätös</b></td><td>[sivu]/[sivumäärä]</td></tr>
      <tr><td>Palveluosasto</td><td/><td/></tr>
      <tr><td>Alueidenkäyttö</td><td>[Päivämäärä]</td><td/></tr>
      <tr><td>Alueidenkäyttöpäällikkö</td><td/><td/></tr>
    </table>
    <div class="line"></div>
    <p style="margin-bottom:1.65cm"></p>

    <!-- Dokumentti -->

    <p><b>YLEISEN ALUEEN VUOKRAUSPÄÄTÖS</b></p>
    <p>[Tunnus]</p>
    <p style="margin-bottom:1.65cm"></p>
    <div class="sisennys">

      <p class="otsikko">Päättäjä</p>
      <p>Rakennusviraston palveluosaston alueidenkäytön alueidenkäyttöpäällikkö</p>

      <p class="otsikko">Päätöksen hakija</p>
      <!-- <p>[Hakijan nimi], [Y-tunnus]</p>
	   <p>[Osoite, postinumero, toimipaikka]</p> -->
      <xsl:choose>
	<xsl:when test="data/application/applicant/type = 'PERSON'">
	  <p><xsl:value-of select="data/application/applicant/person/name"/>,
	    <xsl:value-of select="data/application/applicant/person/ssn"/></p>
	  <p><xsl:value-of select="data/application/applicant/person/postalAddress/streetAddress"/>,
	    <xsl:value-of select="data/application/applicant/person/postalAddress/postalCode"/>,
	    <xsl:value-of select="data/application/applicant/person/postalAddress/city"/></p>
  	  </xsl:when>
	<xsl:otherwise>
	  <p><xsl:value-of select="data/application/applicant/organization/name"/>,
	    <xsl:value-of select="data/application/applicant/organization/businessId"/></p>
	  <p><xsl:value-of select="data/application/applicant/organization/postalAddress/streetAddress"/>,
	    <xsl:value-of select="data/application/applicant/organization/postalAddress/postalCode"/>,
	    <xsl:value-of select="data/application/applicant/organization/postalAddress/city"/></p>
	</xsl:otherwise>
      </xsl:choose>
 
      <p class="otsikko">Yhteyshenkilö</p>
      <!-- <p>[Yhteyshenkilön nimi]</p>
	   <p>[Puhelinnumero, sähköpostiosoite]</p> -->
      <xsl:for-each select="data/application/contactList">
	<p><xsl:value-of select="name"/></p>
	<p><xsl:value-of select="phone"/>, <xsl:value-of select="email"/></p>
      </xsl:for-each>

      <p class="otsikko">Kohde</p>
      <p>[Vuokrattava paikka, Lohko], [Osoite], [Lisätietoja paikasta]</p>
      <p>[Alueen pinta-ala]</p>

      <p class="otsikko">Vuokra-aika</p>

      <!-- Ei erillisiä rakentamis- tai purkupäiviä: -->
      <p>[Tapahtuman alkupäivämäärä]-[Tapahtuman loppupäivämäärä]</p>
      <p>Tapahtumapäiviä: [päivien lukumäärä].</p>
      <p>[Tapahtuma-ajan poikkeukset]</p>
      <p><font color="#ff0000">TAI</font></p>
      <!-- Jos rakentamis- tai purkupäiviä on täytetty: -->
      <p>Kokonaisvuokra-aika [Rakentamisen alkupäivämäärä]-[Purkamisen loppupäivämäärä], josta tapahtumapäiviä [Tapahtuman alkupäivämäärä]-[Tapahtuman loppupäivämäärä], rakentamispäiviä [Rakentamisen alkupäivämäärä]-[Tapahtuman alkupäivämäärä-1] sekä purkupäiviä [Tapahtuman loppupäivämäärä+1]-[Purkamisen loppupäivämäärä].</p>
      <p>Tapahtumapäiviä [Tapahtumapäivien lukumäärä] ja rakentamis- ja purkupäiviä [Rakentamis- ja purkupäivien lukumäärä].</p>

      <p class="otsikko">Vuokrauksen tarkoitus</p>
      <p>[Tapahtuman nimi]</p>
      <p>[Tapahtuman tyyppi] <i>(avoin, maksullinen, suljettu, promootio)</i></p>
      <p>Tapahtuma sisältää rakenteita [Rakenteiden kokonaisneliömäärä].</p>
      <p>[Rakenteiden kuvaus]<!-- Jos tapahtuma sisältää rakenteita --></p>

      <p class="otsikko">Vuokra</p>
      <p><font color="#ff0000">*LISTAHINTA*</font></p>
      <p>[Hinta]</p>
      <p>Vuokrauspäätöksen hinta perustuu yleisten töiden lautakunnan päätökseen 11.11.2014 § 431.</p>
      <p>Ilmoitettu maanvuokra on maksettava eräpäivään mennessä rakennusviraston erikseen lähettämää laskua vastaan. Ellei vuokraerää erääntymispäivänä suoriteta, vuokralainen on velvollinen maksamaan langenneelle erälle erääntymispäivästä maksupäivään asti korkoa ja perimispalkkiota kaupunginhallituksen vahvistaman päätöksen mukaan.</p>
      <p>Laskutusviite on [asiakkaan laskutusviite].</p>
      <p>*</p>
      <p><font color="#ff0000">*ALENNETTU HINTA*</font></p>
      <p>[Hinta]</p>
      <p>Vuokrauspäätöksen alennettu hinta perustuu yleisten töiden lautakunnan päätökseen 11.11.2014 § 431. Syy: [pudotusvalikosta valittu syy], joka sisältää teemaan sisältymätöntä elintarvikemyyntiä tai tarjoilua.</p>
      <p>*</p>
      <p>Vuokrauspäätöksen alennettu hinta perustuu yleisten töiden lautakunnan päätökseen 11.11.2014 § 431. Syy: Hakijalla on Ekokompassi-tapahtumapassi, joka oikeuttaa -30 % hinnan alennukseen.</p>
      <p>*</p>
      <p><font color="#ff0000">*KORVAUKSETTA*</font></p>
      <p>Vuokrauspäätös myönnetään korvauksetta yleisten töiden lautakunnan päätöksen 11.11.2014 § 431 mukaisesti. Syy: [pudotusvalikosta valittu syy].</p>

      <p class="otsikko">Ehdot</p>
      <p>[Ehdot]</p>

      <p class="otsikko">Päätöksen perustelut</p>
      <p>Hakija on hakenut oikeutta alueen käyttöön, toimittanut hakemuksen liitteineen rakennusvirastolle ja ilmoittanut sitoutuvansa alueen käyttöä koskevaan ohjeistukseen sekä sopimusehtoihin.</p>
      <p>Rakennustoimen johtosäännön 4 § 8 kohdan mukaan yleisten töiden lautakunnan tehtävänä on hyväksyä perusteet, joiden mukaan viranhaltija päättää vuokralle antamisesta ja muusta käyttöön luovuttamisesta. Yleisten töiden lautakunta on päätöksissä Ytlk 30.9.2014 § 364 &amp; Ytlk 11.11.2014 § 431 päättänyt nämä edellä mainitut perusteet.</p>

      <p class="otsikko">Päätös</p>
      <p>Rakennusviraston palveluosaston alueidenkäyttöpäällikkö päätti myöntää luvan hakijalle haetun alueen käyttämiseen yllä olevin ehdoin.</p>
      <p>Tämä päätös on sähköisesti allekirjoitettu.</p>
      <p>[Aikaleima]</p>
      <p>[Päättäjän työnimike]</p>
      <p>[Päättäjän nimi]</p>

      <p class="otsikko">Lisätiedot</p>
      <p>hkr.ulkoilma@hel.fi, 09 310 39869</p>

      <p class="otsikko">Valmistelija</p>
      <p>[titteli, tarkastajan nimi, puhelinnumero, sähköpostiosoite]</p>

      <p class="otsikko">Liitteet</p>
      <p>1 …</p>
      <p>2 …</p>

      <p class="otsikko">Muutoksenhaku</p>
      <p>Muutoksenhakuohjeet liitteenä.</p>

      <p class="otsikko">Otteet</p>
      <table style="width:100%">
	<colgroup><col style="width:50%"/><col style="width:50%"/></colgroup>
	<tr><th>Ote</th><th>Otteen liitteet</th></tr>
	<!-- silmukassa kaikki otteet -->
	<tr><td>Lorem Ipsum</td><td>Irem Lopsum Quod Lipsilum est Mitigare</td></tr>
      </table>
    </div>

    <!-- Alatunniste -->
    <table style="width:100%; font-face: Arial; font-size: 9pt">
      <tr style="vertical-align:top">
	<th>Postiosoite</th><th>Käyntiosoite</th><th>Puhelin</th>
	<th>Sähköposti</th><th>Y-tunnus</th></tr>
      <tr style="vertical-align:top">
	<td>PL 1520<br/>00099 HELSINGIN KAUPUNKI</td><td>Elimäenkatu 5<br/>Helsinki</td><td>+358 9 310 39000</td>
	<td>hkr.ulkoilma@hel.fi</td><td>0201256-6</td></tr>
      <tr style="vertical-align:top">
	<th>Faksi</th><th>Internet</th><th>Alv.nro</th></tr>
      <tr style="vertical-align:top">
      	<td>+358 9 310 38674</td><td><a href="http://www.hkr.hel.fi/luvat">www.hkr.hel.fi/luvat</a></td><td>FI02012566</td></tr>
    </table>

  </body>
</html>
</xsl:template>
</xsl:stylesheet>

