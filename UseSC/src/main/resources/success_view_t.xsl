<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" doctype-system="about:legacy-compat"
                encoding="UTF-8" indent="yes" />
    <xsl:template match="/">
        <html>
            <head>
                <title><xsl:value-of select="view/header/title"/></title>
                <link rel="stylesheet" href="logout.html" />
            </head>
            <body>
                <xsl:for-each select="view/body/form">
                    <form name="{name}" action="{action}" method="{method}">
                        <xsl:for-each select="textView">
                            <xsl:value-of select="label"/><br/>
                            <input type="text" name="{name}" value="{value}"/><br/>
                        </xsl:for-each>
                        <button type="button" name="{buttonView/name}">
                            <xsl:value-of select="buttonView/label"/>
                        </button>
                    </form>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>

