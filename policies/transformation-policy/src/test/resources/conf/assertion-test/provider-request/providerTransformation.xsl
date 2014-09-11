<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <!--  This potentially transforms request -->
    <xsl:template match="AuthorLastName/text()[.='Panda']">Icebear</xsl:template>

    <!--  This potentially transforms response -->
    <xsl:template match="LastName/text()[.='Icebear']">Grizzly</xsl:template>
</xsl:stylesheet>