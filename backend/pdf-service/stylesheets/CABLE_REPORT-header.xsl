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
    <body style="height:37mm;margin:0;padding:0;" onLoad="getPdfInfo()">
    <div class="header">
      <div class="logo">
        <img src="helsinki-logo.png"/>
      </div>
        <div class="department">
          <p>Kaupunkiympäristö</p>
          <p>Yleisten alueiden valvonta<br/>
            ja infraluvat -yksikkö</p>
        </div>
      <div class="half-right">
        <h1>Johtoselvitys</h1>
        <p class="page">
          <span id="pdfkit_page_current"></span>/<span id="pdfkit_page_count"></span>
        </p>
        <div class="id">
          <table>
            <tr>
              <td class="c1">Päivämäärä:</td>
              <td class="c2"><!-- [päätöspvm] -->
                <xsl:value-of select="data/decisionDate" />
              </td>
            </tr>
            <tr>
              <td class="c1">Tunnus:</td>
              <td class="c2"><!-- [tunnus]  -->
                <xsl:value-of select="data/decisionId" />
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
