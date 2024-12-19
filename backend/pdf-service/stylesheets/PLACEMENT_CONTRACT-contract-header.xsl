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
    <script type="text/javascript">
      <![CDATA[
      var pdfInfo = {};
      var x = document.location.search.substring(1).split('&');
      for (var i in x) { var z = x[i].split('=',2); pdfInfo[z[0]] = unescape(z[1]); }
      function getPdfInfo() {
        var page = pdfInfo.page || 1;
        var pageCount = pdfInfo.topage || 1;
        document.getElementById('pdfkit_page_current').textContent = page;
        document.getElementById('pdfkit_page_count').textContent = pageCount;
      }
      ]]>
    </script>
  </head>
  <body style='height:34mm;margin:0;padding:0;' onLoad='getPdfInfo()'>
    <div class="header">
      <div class="logo">
        <img src="helsinki-logo.png" />
      </div>
      <div class="department">
        <p>Kaupunkiympäristö</p>
        <p>Alueiden käyttö ja valvonta</p>
	<p>Katutyö- ja sijaintipalvelut</p>
      </div>
      <div class="half-right">
        <h1>Sopimus</h1>
        <p class="page">
          <span id="pdfkit_page_current" />/<span id="pdfkit_page_count" />
        </p>
        <div class="id">
          <table>
            <tr>
              <td class="c1">Päätöspäivämäärä:</td>
              <!-- [päätöspvm] -->
              <td class="c2"><xsl:value-of select="data/decisionDate" /></td>
            </tr>
            <xsl:if test="data/identificationNumber != ''">
              <tr>
                <td class="c1">Asiointitunnus:</td>
                <!-- [asiointitunnus] -->
                <td class="c2"><xsl:value-of select="data/identificationNumber" /></td>
              </tr>
            </xsl:if>
          </table>
        </div>
      </div>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
