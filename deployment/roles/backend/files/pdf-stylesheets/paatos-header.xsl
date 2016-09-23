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
  <body style='height:40mm;margin:0;padding:0;'>
    <div class="header">
      <div>
        <div class="half-left">
          <img src="HKR_Fin_RGB_png_50734.png"/>
        </div>
        <div class="half-right inline">
          <h1>Vuokrapäätös</h1>
          <p>[sivu]/[sivumäärä]</p>
        </div>
      </div>

      <div>
        <div class="half-left">
          <p>Palveluosasto</p>
          <p>Alueidenkäyttö</p>
          <p>Alueidenkäyttöpäällikkö</p>
        </div>
        <div class="half-right date">
          <p>[Päivämäärä]</p>
        </div>
      </div>
    </div>
    <hr class="top" />
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
