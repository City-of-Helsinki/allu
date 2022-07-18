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
  <body style='height:30mm;margin:0;padding:0;' onLoad='getPdfInfo()'>
    <div class="header">
      <div class="half-left">
        <img src="HKR_Fin_RGB_png_50734.png" />
      </div>
      <div class="half-right inline">
        <h1>Päätösluontoinen päätöspäätös</h1>
        <p class="page">
          <span id="pdfkit_page_current" />/<span id="pdfkit_page_count" />
        </p>
        <div class="id">
          <span>Päätöspäivämäärä:</span>
          <p style="float: right">
            <!--  [päätöspvm] -->
            <xsl:value-of select="data/decisionDate" />
          </p>
          <span>Tunnus:</span>
          <p style="float: right">
            <!-- [tunnus]  -->
            <xsl:value-of select="data/decisionId" />
          </p>
        </div>
      </div>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
