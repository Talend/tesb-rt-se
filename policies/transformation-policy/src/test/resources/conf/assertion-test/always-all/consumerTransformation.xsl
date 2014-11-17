<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:include href="include.xsl" />
    
    <!--  This potentially transforms response -->
    <xsl:template match="LastName/text()[.='Grizzly']">Icebear</xsl:template>
</xsl:stylesheet>