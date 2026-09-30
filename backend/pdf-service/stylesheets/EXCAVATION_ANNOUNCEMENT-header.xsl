<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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

  <xsl:variable name="headerHeight">
    <xsl:text><xsl:value-of select="30 + data/headerRows * 4"/></xsl:text>
  </xsl:variable>

  <body onLoad='getPdfInfo()'>
    <xsl:attribute name="style">
      <xsl:text>height:</xsl:text>
      <xsl:value-of select="$headerHeight"/>
      <xsl:text>mm;margin:0;padding:0;</xsl:text>
    </xsl:attribute>
    <div class="header">
      <div class="logo">
        <img src="helsinki-logo.png" />
      </div>
      <div class="department">
        <p>Kaupunkiympäristö</p>
        <p>Yleisten alueiden valvonta <br/> ja infraluvat -yksikkö</p>
        <p>luvat@hel.fi</p>
      </div>
      <div class="half-right" style="position:relative;left:-20pt;">
        <xsl:choose>
          <xsl:when test="data/replacingDecision = 'false'">
            <h1>Päätös yleisellä alueella tehtävästä kaivutyöstä</h1>
          </xsl:when>
          <xsl:otherwise>
           <h1>Korvaava päätös yleisellä alueella tehtävästä kaivutyöstä</h1>
          </xsl:otherwise>
        </xsl:choose>
        <div class="page">
          <span id="pdfkit_page_current" />/<span id="pdfkit_page_count" />
        </div>
        <div class="id">
          <table>
            <tr>
              <td class="c1">Päätöspäivämäärä:</td>
              <!-- [päätöspvm] -->
              <td class="c2"><xsl:value-of select="data/decisionDate" /></td>
            </tr>
            <tr>
              <td class="c1">Tunnus:</td>
              <!-- [tunnus]  -->
              <td class="c2"><xsl:value-of select="data/decisionId" /></td>
            </tr>
            <xsl:if test="data/identificationNumber != ''">
              <td class="c1">Asiointitunnus:</td>
              <td class="c2"><xsl:value-of select="data/identificationNumber" /></td>
            </xsl:if>
          </table>
        </div>
      </div>
    </div>
  </body>
</html>
</xsl:template>
</xsl:stylesheet>
