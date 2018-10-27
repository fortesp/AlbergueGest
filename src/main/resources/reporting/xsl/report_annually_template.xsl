<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <style>
                    @page {
                    size: A4 portrait;
                    margin: 1cm;
                    }
                    * {
                    font-family: "Times New Roman";
                    font-size: 11pt;
                    line-height: 16pt;
                    }
                    .tbl-header {
                    width: 100%;
                    }
                    .tbl-data {
                    border-collapse: collapse;
                    width: 100%;
                    margin-left:auto;
                    margin-right:auto;
                    empty-cells:hide;
                    }
                    .tbl-data th, .tbl-data td {
                    border:1px solid black;
                    margin:0;
                    padding:0;
                    text-align:left;
                    padding-left: 4pt;
                    width: 14%
                    }
                    .tbl-data th {
                    background: #CCC;
                    font-weight: bold;
                    font-style: italic;
                    }
                </style>
            </head>
            <body>

                <table class="tbl-header" align="center" border="0">
                    <tr>
                        <td width="20%">
                            <img width="122" src="data:image/jpeg;base64,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" />
                            <br /><br />
                        </td>
                        <td  align="center" valign="bottom" style="font-style: italic; font-weight:bold; line-height: 14px;">
                            <br/><br/>
                            MINISTERIO DE SALUD
                            <br/>
                            RÉGION DE SALUD NGABE BUGLÉ
                            <br/>
                            ALBERGUE DE EMBARAZADAS VIRGEN DEL CAMINO
                            <br/>
                            CONSOLIDADO DE RACIONES SERVIDAS MENSALMENTE
                        </td>
                    </tr>
                    <tr>
                        <td align="left" style="font-weight: bold;font-style: italic">Ano: <xsl:value-of select="/report/year"/></td>
                        <td></td>
                    </tr>
                </table>
                <br/>
                <table class="tbl-data">
                    <tr>
                        <th rowspan="2">Mes</th>
                        <th colspan="3">Raciones Servidas</th>
                        <th colspan="3">Acompanante</th>
                    </tr>
                    <tr>
                        <xsl:for-each select="/report/meals/meal">
                            <th><xsl:value-of select="."/></th>
                        </xsl:for-each>

                        <xsl:for-each select="/report/meals/meal">
                            <th><xsl:value-of select="."/></th>
                        </xsl:for-each>

                    </tr>
                    <xsl:for-each select="/report/counts/count">
                        <tr>
                            <td>
                                <xsl:value-of select="month" />
                            </td>

                            <xsl:for-each select="persons/person">
                                <xsl:for-each select="meals/meal">
                                    <td>
                                        <xsl:value-of select="."/>
                                    </td>
                                </xsl:for-each>
                            </xsl:for-each>

                        </tr>
                    </xsl:for-each>
                </table>
                <br />

                <div style="width: 100%; text-align: center; color: #666">"SISTEMA DE SALUD HUMANO, CON EQUIDAD Y CALIDAD, UN DERECHO DE TODOS"</div>

                <br />
            </body>
        </html>

    </xsl:template>
</xsl:stylesheet>

