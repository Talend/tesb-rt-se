<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xslthl="http://xslthl.sf.net" xmlns:d="http://docbook.org/ns/docbook"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    exclude-result-prefixes="xslthl" version="1.0">

    <xsl:import href="urn:docbkx:stylesheet" />

    <xsl:include href="customfo-pagemasters.xsl"/>

    <xsl:param name="ulink.show" select="0" />
    <xsl:param name="section.autolabel" select="1" />
    <xsl:param name="section.label.includes.component.label" select="1" />
    <xsl:param name="section.autolabel.max.depth" select="3" />
    <xsl:param name="admon.graphics" select="1" />    
    <xsl:param name="admon.graphics.extension">.svg</xsl:param>    
    <xsl:param name="admon.graphics.path">../../../common/images/</xsl:param> 
   <!-- set a large enough size for chapter and book name texts. These three numbers are relative to each other. -->  
    <xsl:param name="header.column.widths">1 1 3</xsl:param>
    <xsl:param name="footer.column.widths">3 1 1</xsl:param>
    
    <!-- Background shading for tips, notes, caution, etc. -->
	<xsl:attribute-set name="admonition.properties">
	  <xsl:attribute name="border">0.5pt solid blue</xsl:attribute>
	  <xsl:attribute name="background-color">#E0E0E0</xsl:attribute>
	  <xsl:attribute name="padding">0.1in</xsl:attribute>
	</xsl:attribute-set>

    <!-- Make hyperlinks blue and don't display the underlying URL -->
    <xsl:attribute-set name="xref.properties">
        <xsl:attribute name="color">blue</xsl:attribute>
    </xsl:attribute-set>
    
    <xsl:attribute-set name="section.title.properties">
        <xsl:attribute name="space-before.minimum">3.0em</xsl:attribute>
        <xsl:attribute name="space-before.optimum">3.5em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">4.0em</xsl:attribute>
        
        <xsl:attribute name="space-after.minimum">1.0em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1.5em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.5em</xsl:attribute>
    </xsl:attribute-set>

    <xsl:template match="d:caption">
        <fo:block>
            <xsl:if
                test="@align = 'right' or @align = 'left' or @align='center'">
                <xsl:attribute name="text-align">
                    <xsl:value-of select="@align" />
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="font-weight">bold</xsl:attribute>
            <xsl:attribute name="font-style">italic</xsl:attribute>
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>

    <xsl:template name="footer.content">
        <xsl:param name="pageclass" select="''"/>
        <xsl:param name="sequence" select="''"/>
        <xsl:param name="position" select="''"/>
        <xsl:param name="gentext-key" select="''"/>

        <xsl:choose>
            <xsl:when test="$pageclass='talend-titlepage'">
                <!-- nop: other titlepage sequences have no footer -->
            </xsl:when>

            <xsl:when test="$position='right'">
                <fo:page-number/>
            </xsl:when>

            <xsl:when test="$position='left'">
                <xsl:call-template name="document.footer.name"/>
            </xsl:when>

            <xsl:otherwise>
                <!-- nop -->
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="document.footer.name">
        (Need to override template document.footer.name)
    </xsl:template>

    <xsl:template name="head.sep.rule">
        <xsl:param name="pageclass"/>
        <xsl:param name="sequence"/>
        <xsl:param name="gentext-key"/>

        <xsl:if test="$header.rule != 0">
            <xsl:choose>
                <xsl:when test="$pageclass = 'talend-titlepage'">
                    <!-- off -->
                </xsl:when>
                <xsl:when test="$pageclass = 'talend-body' and $sequence = 'first'">
                    <!-- off -->
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="border-bottom-width">0.5pt</xsl:attribute>
                    <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
                    <xsl:attribute name="border-bottom-color">black</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>
    
	<xsl:template name="foot.sep.rule">
	  <xsl:param name="pageclass"/>
	  <xsl:param name="sequence"/>
	  <xsl:param name="gentext-key"/>
	
	  <xsl:if test="$footer.rule != 0">
	     <xsl:choose>
	        <xsl:when test="$pageclass = 'talend-titlepage'">
	            <!-- off -->
	        </xsl:when>
	        <xsl:otherwise>
			    <xsl:attribute name="border-top-width">0.5pt</xsl:attribute>
			    <xsl:attribute name="border-top-style">solid</xsl:attribute>
			    <xsl:attribute name="border-top-color">black</xsl:attribute>
		    </xsl:otherwise>
		 </xsl:choose>
	  </xsl:if>
	</xsl:template>
    
    
    <xsl:template name="header.content">
        <xsl:param name="pageclass" select="''"/>
        <xsl:param name="sequence" select="''"/>
        <xsl:param name="position" select="''"/>
        <xsl:param name="gentext-key" select="''"/>

        <xsl:choose>
            <xsl:when test="$pageclass='talend-titlepage'">
                <!-- nop: other titlepage sequences have no header -->
            </xsl:when>
            <xsl:when test="$pageclass='lot'">
                <!-- nop: other titlepage sequences have no header -->
            </xsl:when>
            <xsl:when test="$pageclass='front' and $position='center'">
                <!-- nop: other titlepage sequences have no header -->
            </xsl:when>
            <xsl:when test="$pageclass='talend-body' and $sequence='first' and $position='center'">
                <fo:external-graphic src="url(../../../common/images/chapter_header1.jpg)" width="auto" height="auto" content-width="100%" content-type="content-type:image/png"/>
            </xsl:when>

            <xsl:when test="$position='right' and not($pageclass='talend-body' and $sequence='first')">
                <xsl:apply-templates select="."  mode="object.title.markup"/>
            </xsl:when>

            <xsl:when test="$position='left'">
                 <!--xsl:value-of   select="$pageclass" />   <xsl:value-of   select="$sequence" /-->  
                <!-- nop -->
            </xsl:when>

            <xsl:otherwise>
                <!-- nop -->
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="book.titlepage.before.recto">
        <fo:external-graphic src="url(../../../common/images/ESB_CoverBridge.jpg)" width="auto" height="auto" content-width="80%" content-type="content-type:image/png"/>
    </xsl:template>
    
    

</xsl:stylesheet>

