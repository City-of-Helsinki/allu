<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="utf-8" indent="yes"/>
<xsl:template match="/root">
<xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
<html lang='fi-FI'>
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
    <hr />
    <div class="footer">
      <div class="postal">
        <h1>Postiosoite</h1>
        <p>
          PL 1520
        </p>
        <p>
          00099 HELSINGIN KAUPUNKI
        </p>
      </div>

      <div class="visit">
        <h1>Käyntiosoite</h1>
        <p>
          Elimäenkatu 5
        </p>
        <p>
          Helsinki
        </p>
      </div>

      <div class="contact">
        <h1>Puhelin</h1>
        <p>
          +358 9 310 39000
        </p>
        <h1>Faksi</h1>
        <p>
          +358 9 310 38674
        </p>
      </div>

      <div class="internet">
        <h1>Sähköpostiosoite</h1>
        <p>
          hkr.ulkoilma@hel.fi
        </p>
        <h1>Internet</h1>
        <p>
          <a href="www.hkr.hel.fi/luvat">www.hkr.hel.fi/luvat</a>
        </p>
      </div>

      <div class="tax">
        <h1>Y-tunnus</h1>
        <p>
          0201256-6
        </p>
        <h1>Alv.nro</h1>
        <p>
          FI02012566
        </p>
      </div>
    </div>
    <hr class="bottom" />
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
