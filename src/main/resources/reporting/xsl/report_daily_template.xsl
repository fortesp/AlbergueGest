<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <style>
                    @page {
                    size: A4 landscape;
                    margin: 1cm;
                    margin-top: 2cm;
                    }
                    * {
                    font-family: Calibri;
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
                    }
                    .tbl-data th {
                    background: #CCC;
                    font-weight: bold;
                    }
                    .tbl-data td {
                    line-height: 20px;
                    }
                </style>

            </head>
            <body>

                <table class="tbl-header" align="center" border="0">
                    <tr>
                        <td colspan="2" align="center" style="font-style: italic">
                            <br/>
                            MINISTERIO DE SALUD
                            <br/>
                            RÉGION DE SALUD NGABE BUGLÉ
                            <br/>
                            ALBERGUE DE EMBARAZADAS VIRGEN DEL CAMINO
                            <br/>
                            RACIONES SERVIDAS DIARIAS
                            <br/>
                            <br/>
                        </td>
                    </tr>
                    <tr>
                        <td height="30"></td>
                    </tr>
                    <tr>
                        <td align="left" width="49%" style="font-weight: bold">
                            <div style="margin-left:2cm">FECHA:
                                <xsl:value-of select="/report/date"/>
                            </div>
                        </td>
                        <td align="center" style="font-weight: bold;">FIRMA RESPONSABILE:
                            _________________________________
                        </td>
                    </tr>
                    <tr>
                        <td height="30"></td>
                    </tr>
                </table>
                <table class="tbl-data">
                    <tr>
                        <th colspan="4" style="visibility:hidden; border:0"></th>
                        <xsl:for-each select="/report/meals/meal">
                            <th colspan="2">
                                <xsl:value-of select="."/>
                            </th>
                        </xsl:for-each>
                    </tr>
                    <tr>
                        <th>#</th>
                        <th>Nombre</th>
                        <th>Edad</th>
                        <th>Cédula</th>

                        <xsl:for-each select="/report/meals/meal">
                            <th width="35">E</th>
                            <th width="35">A</th>
                        </xsl:for-each>

                        <th>Firma Embarazada</th>
                        <th>Firma Acompanante</th>
                    </tr>
                    <xsl:for-each select="/report/patients/patient">
                        <tr>
                            <td>
                                <xsl:number/>
                            </td>
                            <td>
                                <xsl:value-of select="name"/>
                            </td>
                            <td>
                                <xsl:value-of select="dob"/>
                            </td>
                            <td>
                                <xsl:value-of select="idcard"/>
                            </td>

                            <xsl:for-each select="servings/serving">
                                <td style="text-align:center">
                                    <xsl:choose>
                                        <xsl:when test="e=1">
                                            <img src="data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAAMCAgIDAgMDAwMDAwMDBAUEAwMEBQYFBQUFBQYIBgYGBgYGCAcICQoJCAcLCwwMCwsPDw8PDxAQEBAQEBAQEBD/2wBDAQQEBAcGBwwICAwQDQsNEBISEhISEhIQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAARAA4DAREAAhEBAxEB/8QAGAAAAgMAAAAAAAAAAAAAAAAAAAYHCAn/xAAjEAACAgIBBAIDAAAAAAAAAAABAgMFBAYSAAcRIQgTFBcx/8QAFAEBAAAAAAAAAAAAAAAAAAAAAP/EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAMAwEAAhEDEQA/ANNt53jVNO1G12fZrLHqKKlx2yrKxyD4SKNfX8HkszEhVVQSzEAAkjoE3tJZdxtqEm57JHla1T2sPjVdGZBHlY2C7B0zbiT235kwUH6UISBDwbnJyYBBmT8IbrJ+UdjfyXQ/S9hZ4O8Z+hvK8iZe440ckAZ4WBX6PITIcFvDPxXjxX0FwegOgOg//9k="/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            -
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                                <td  style="text-align:center">
                                    <xsl:choose>
                                        <xsl:when test="a=1">
                                            <img src="data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAAMCAgIDAgMDAwMDAwMDBAUEAwMEBQYFBQUFBQYIBgYGBgYGCAcICQoJCAcLCwwMCwsPDw8PDxAQEBAQEBAQEBD/2wBDAQQEBAcGBwwICAwQDQsNEBISEhISEhIQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBD/wAARCAARAA4DAREAAhEBAxEB/8QAGAAAAgMAAAAAAAAAAAAAAAAAAAYHCAn/xAAjEAACAgIBBAIDAAAAAAAAAAABAgMFBAYSAAcRIQgTFBcx/8QAFAEBAAAAAAAAAAAAAAAAAAAAAP/EABQRAQAAAAAAAAAAAAAAAAAAAAD/2gAMAwEAAhEDEQA/ANNt53jVNO1G12fZrLHqKKlx2yrKxyD4SKNfX8HkszEhVVQSzEAAkjoE3tJZdxtqEm57JHla1T2sPjVdGZBHlY2C7B0zbiT235kwUH6UISBDwbnJyYBBmT8IbrJ+UdjfyXQ/S9hZ4O8Z+hvK8iZe440ckAZ4WBX6PITIcFvDPxXjxX0FwegOgOg//9k="/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                           -
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                            </xsl:for-each>

                            <td></td>
                            <td></td>
                        </tr>
                    </xsl:for-each>
                </table>
                <br/>
            </body>
        </html>

    </xsl:template>
</xsl:stylesheet>

