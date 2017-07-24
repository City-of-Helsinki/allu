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
    <link rel="stylesheet" href="new-style.css" />
  </head>
  <body style="height:30mm;padding:10mm 0 0 0;margin:0">
    <div class="footer">
    
      <div class="visit">
        <h1>Käyntiosoite</h1>
        <p>
          Asiakas- ja yrityspalvelut
        </p>
        <p>
          Johtotietopalvelu
        </p>
        <p>
          Sörnäistenkatu 1, 00580
        </p>
        <p>
          Helsinki
        </p>
      </div>

      <div class="contact">
        <h1>Yhteystiedot</h1>
        <p>
          Puhelin (09) 310 31940
        </p>
        <p>
          johtotietopalvelu@hel.fi
        </p>
      </div>

      <div class="logo">
        <img src="helsinki-logo.png"/>
      </div>

    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
