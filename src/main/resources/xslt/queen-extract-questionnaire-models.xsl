<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
    xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
    xmlns:table="urn:oasis:names:tc:opendocument:xmlns:table:1.0"
    xmlns:tools="http://www.insee.fr/tools" office:version="1.2"
    office:mimetype="application/vnd.oasis.opendocument.spreadsheet"
    exclude-result-prefixes="tools xs office text table " version="2.0">

    <xsl:import href="./utils.xsl"/>
    <xsl:output indent="yes"/>
    
    <xsl:variable name="campaignId">
        <xsl:variable name="temp" select="//office:spreadsheet/table:table[@table:name='operation']"/>
        <xsl:value-of select="tools:getColumn(tools:get-full-row($temp/table:table-row[2]),1)"/>
    </xsl:variable>

    <xsl:template match="/">
        <xsl:apply-templates
            select="//office:spreadsheet/table:table[@table:name = 'questionnaireModel']"/>
    </xsl:template>


    <xsl:template
        match="office:body/office:spreadsheet/table:table[@table:name = 'questionnaireModel']">
        <QuestionnaireModels>
            <xsl:for-each select="table:table-row[position()>1]">
                <xsl:variable name="row" select="tools:get-full-row(.)"/>
                <xsl:if test="normalize-space($row)!=''">
                    <QuestionnaireModel>
                        <Id><xsl:value-of select="upper-case(tools:getColumn($row, 1))"/></Id>
                        <Label><xsl:value-of select="tools:getColumn($row, 2)"/></Label>
                        <CampaignId><xsl:value-of select="$campaignId"/></CampaignId>
                        <RequiredNomenclatures>
                            <xsl:for-each select="tokenize(tools:getColumn($row, 3), ',')">
                                <Nomenclature>
                                    <xsl:value-of select="current()"/>
                                </Nomenclature>
                            </xsl:for-each>
                        </RequiredNomenclatures>                        
                        <FileName><xsl:value-of select="tools:getColumn($row,4)"/></FileName>
                    </QuestionnaireModel>
                </xsl:if>
            </xsl:for-each>
        </QuestionnaireModels>        
    </xsl:template>
</xsl:stylesheet>
