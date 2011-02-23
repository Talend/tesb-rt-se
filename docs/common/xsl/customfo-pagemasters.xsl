<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<!--  See here: http://www.sagehill.net/docbookxsl/PageDesign.html#UsePageMasters for how to 
	      customize page margins -->
	
	<xsl:template name="select.user.pagemaster">
		<xsl:param name="element" />
		<xsl:param name="pageclass" />
		<xsl:param name="default-pagemaster" />

		<xsl:choose>
			<xsl:when test="$default-pagemaster = 'titlepage'">
				<xsl:value-of select="'talend-titlepage'" />
			</xsl:when>
			<xsl:when test="$default-pagemaster = 'body'">
				<xsl:value-of select="'talend-body'" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$default-pagemaster" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="user.pagemasters">
			<!-- title pages -->
			<fo:simple-page-master master-name="talend-titlepage-first"
				page-width="{$page.width}" page-height="{$page.height}" margin-top="0"
				margin-bottom="{$page.margin.bottom}" margin-left="0" margin-right="0">
				<fo:region-body margin-bottom="{$body.margin.bottom}"
					margin-top="0" column-gap="{$column.gap.titlepage}" column-count="{$column.count.titlepage}">
				</fo:region-body>
				<fo:region-before region-name="xsl-region-before-first"
					extent="0" display-align="before" />
				<fo:region-after region-name="xsl-region-after-first"
					extent="{$region.after.extent}" display-align="after" />
			</fo:simple-page-master>

			<!-- body pages -->
			<fo:simple-page-master master-name="talend-body-first"
				page-width="{$page.width}" page-height="{$page.height}" margin-top="0"
				margin-bottom="{$page.margin.bottom}">
				<xsl:attribute name="margin-{$direction.align.start}">
                    <xsl:value-of select="$page.margin.inner" />
                    <xsl:if test="$fop.extensions != 0">
                        <xsl:value-of
					select="concat(' - (',$title.margin.left,')')" />
                    </xsl:if>
                    <xsl:if test="$fop.extensions != 0">
                        <xsl:value-of
					select="concat(' - (',$title.margin.left,')')" />
                    </xsl:if>
                </xsl:attribute>
				<xsl:attribute name="margin-{$direction.align.end}">
                    <xsl:value-of select="$page.margin.outer" />
                </xsl:attribute>
				<xsl:if test="$axf.extensions != 0">
					<xsl:call-template name="axf-page-master-properties">
						<xsl:with-param name="page.master">
							body-first
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
				<fo:region-body margin-bottom="{$body.margin.bottom}"
					margin-top="330pt" column-gap="{$column.gap.body}" column-count="{$column.count.body}">
				</fo:region-body>
				<fo:region-before region-name="xsl-region-before-first"
					extent="{$region.before.extent}" display-align="before" />
				<fo:region-after region-name="xsl-region-after-first"
					extent="{$region.after.extent}" display-align="after" />
			</fo:simple-page-master>

			<!-- setup for title page(s) -->
			<fo:page-sequence-master master-name="talend-titlepage">
				<fo:repeatable-page-master-alternatives>
					<fo:conditional-page-master-reference
						master-reference="blank" blank-or-not-blank="blank" />
					<fo:conditional-page-master-reference
						master-reference="talend-titlepage-first" page-position="first" />
					<fo:conditional-page-master-reference
						master-reference="titlepage-odd" odd-or-even="odd" />
                    <fo:conditional-page-master-reference
                                                          odd-or-even="even">
                        <xsl:attribute name="master-reference">
                            <xsl:choose>
                                <xsl:when test="$double.sided != 0">titlepage-even</xsl:when>
                                <xsl:otherwise>titlepage-odd</xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                    </fo:conditional-page-master-reference>
				</fo:repeatable-page-master-alternatives>
			</fo:page-sequence-master>

			<!-- setup for body pages -->
			<fo:page-sequence-master master-name="talend-body">
				<fo:repeatable-page-master-alternatives>
					<fo:conditional-page-master-reference
						master-reference="blank" blank-or-not-blank="blank" />
                    <!-- Original page masters have different margin rules for odd/even/first pages -->
					<fo:conditional-page-master-reference
						master-reference="talend-body-first" page-position="first" />
					<fo:conditional-page-master-reference
						master-reference="body-odd" odd-or-even="odd" />
                    <fo:conditional-page-master-reference
                                                          odd-or-even="even">
                        <xsl:attribute name="master-reference">
                            <xsl:choose>
                                <xsl:when test="$double.sided != 0">body-even</xsl:when>
                                <xsl:otherwise>body-odd</xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                    </fo:conditional-page-master-reference>
				</fo:repeatable-page-master-alternatives>
			</fo:page-sequence-master>
	</xsl:template>

</xsl:stylesheet>
