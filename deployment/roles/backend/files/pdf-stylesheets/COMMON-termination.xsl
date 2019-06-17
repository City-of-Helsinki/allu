<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
          <div class="unboxed avoid-pb">
            <section>
              <xsl:if test="data/applicationType='PLACEMENT_CONTRACT'">
                <h2>Sopimuksen irtisanominen</h2>
                <p class="space-above">Sopimuksen tunnus: <xsl:value-of select="data/applicationId"/></p>
              </xsl:if>
              <xsl:if test="data/applicationType='SHORT_TERM_RENTAL'">
                <h2>Vuokrauspäätöksen irtisanominen</h2>
                <p class="space-above">Vuokrauspäätöksen tunnus: <xsl:value-of select="data/applicationId"/></p>
              </xsl:if>
            </section>
          </div>

          <div class="unboxed avoid-pb">
            <section class="space-below">
              <h2>Osapuolet</h2>
              <p class="indented space-above">Helsingin kaupunki</p>
            </section>
            <section class="half-left">
              <p class="indented">
                <xsl:for-each select="data/customerAddressLines">
                  <xsl:value-of select="." /><br/>
                </xsl:for-each>
              </p>
            </section>

            <section class="half-right">
              <xsl:if test="data/customerContactLines != ''">
                <p class="indented">
                  <xsl:for-each select="data/customerContactLines">
                    <xsl:value-of select="."/><br/>
                  </xsl:for-each>
                </p>
              </xsl:if>
            </section>
          </div>


          <div class="unboxed avoid-pb">
            <section>
              <h2>Irtisanominen</h2>
              <br />
              <xsl:if test="data/terminationInfo">
                <xsl:for-each select="data/terminationInfo">
                  <p class="indented">
                    <xsl:value-of select="."/>
                    <xsl:if test="not(normalize-space(.))">
                      <br/>
                    </xsl:if>
                  </p>
                </xsl:for-each>
              </xsl:if>
              <p class="indented space-above">Irtisanominen astuu voimaan <xsl:value-of select="data/terminationDate" />.</p>
            </section>
          </div>

          <div class="unboxed avoid-pb">
            <section>
              <h2>Päätös</h2>

              <xsl:if test="data/applicationType='PLACEMENT_CONTRACT'">
                <p class="indented space-above">Alueidenkäyttö ja -valvonta yksikön päällikkö päätti irtisanoa sijoitussopimuksen.</p>
              </xsl:if>

              <xsl:if test="data/applicationType='SHORT_TERM_RENTAL'">
                <p class="indented space-above">Alueidenkäyttö ja -valvontayksikön tiimipäällikkö päätti irtisanoa vuokrauspäätöksen.</p>
              </xsl:if>

              <p class="indented space-above">Tämä päätös on sähköisesti allekirjoitettu.</p>
              <p class="indented space-above">
                <xsl:value-of select="data/decisionTimestamp"/>,
                <xsl:value-of select="data/deciderTitle"/>,
                <xsl:value-of select="data/deciderName"/></p>
            </section>
          </div>

          <div class="unboxed avoid-pb">
            <section class="half-left">
              <h2>Lisätiedot</h2>
              <xsl:if test="data/applicationType='PLACEMENT_CONTRACT'">
                <p>Kaupunkiympäristön alueidenkäyttö</p>
                <p>alueidenkaytto@hel.fi</p>
              </xsl:if>

              <xsl:if test="data/applicationType='SHORT_TERM_RENTAL'">
                <p>Alueidenkäyttö ja –valvontayksikkö</p>
                <p>ulkoilma@hel.fi</p>
              </xsl:if>
            </section>

            <section class="half-right">
              <h2>Käsittelijä</h2>
              <p>
                <xsl:value-of select="data/handlerTitle"/>,
                <xsl:value-of select="data/handlerName"/>
              </p>
            </section>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>