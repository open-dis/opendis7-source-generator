<?xml version="1.0" encoding="UTF-8"?>
<!--
    title       : MergeOpenDisTemplatesIntoDis7Schema.xslt
    created     : 24 September 2020
    creator     : Don Brutzman
    description : Read all OpenDIS XML templates and convert into DIS 7 Schema
    reference   : AllX3dElementsAttributesTextTemplate.xslt
    reference   : https://www.w3.org/TR/xslt
    identifier  : https://www.web3d.org/x3d/stylesheets/AllX3dElementsAttributesTextTemplate.xslt
    license     : ../license.html
-->

<!-- TODO authors can edit this example to customize all transformation rules -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:xs ="http://www.w3.org/2001/XMLSchema"
	            xmlns:fn ="http://www.w3.org/2005/xpath-functions"
                xmlns:xi ="http://www.w3.org/2001/XInclude">
	<!--  extension-element-prefixes="xs" -->
    <xsl:output method="html"/> <!-- output methods:  xml html text -->
    
    <!-- ======================================================= -->
    
    <xsl:template match="/"> <!-- process root of input document -->
    
        <!-- https://stackoverflow.com/questions/6129262/xslt-transform-multiple-files-from-subdirectory ?select= ;recurse=yes-->

        <xsl:variable name="relativePathToFiles" select="concat('./','dis_7_2012','/')"/>
        <xsl:message>
            <xsl:text>$relativePathToFiles=</xsl:text>
            <xsl:value-of select="$relativePathToFiles"/>
        </xsl:message>
    
        <xsl:text disable-output-escaping="yes"><![CDATA[<?xml version="1.0" encoding="utf-8"?>]]></xsl:text>
        <xsl:text>&#10;</xsl:text>

        <xsl:element name="schema">
            
            <xsl:comment> 
                <xsl:text>How to integrate these attributes?</xsl:text>
                <xsl:text>&#10;</xsl:text>
                <xsl:text>   </xsl:text>
                <xsl:text disable-output-escaping="yes"><![CDATA[ xmlns:xs="http://www.w3.org/2001/XMLSchema"]]></xsl:text>
                <xsl:text disable-output-escaping="yes"><![CDATA[ elementFormDefault="unqualified"]]></xsl:text>
                <xsl:text disable-output-escaping="yes"><![CDATA[ attributeFormDefault="unqualified"]]></xsl:text>
                <xsl:text disable-output-escaping="yes"><![CDATA[ version="7.0"]]></xsl:text>
            </xsl:comment>
            
        <xsl:apply-templates select="//*"/> <!-- classes/class -->          

        
        <xsl:for-each select="//xi:include">
            
            <xsl:message>
                <xsl:text>====================================================</xsl:text>
            </xsl:message>
            <xsl:message>
                <xsl:text>xi:import href=</xsl:text>
                <xsl:value-of select="@href"/>
            </xsl:message>
            <xsl:message>
                <xsl:text> relative path=</xsl:text>
                <xsl:value-of select="concat($relativePathToFiles,@href)"/>
            </xsl:message>
            
            <xsl:variable name="nodes" select="doc(concat($relativePathToFiles,@href))"/>
            <xsl:message>
                <xsl:text> count($nodes/*/*)=</xsl:text>
                <xsl:value-of select="count($nodes/*/*)"/>
                <xsl:text>, count($nodes/classes/class)=</xsl:text>
                <xsl:value-of select="count($nodes/classes/class)"/>
            </xsl:message>
            <xsl:if test="count($nodes/*/*) != count($nodes/classes/class)">
                <xsl:message>
                    <xsl:text>*** unexpected top-level element detected</xsl:text>
                </xsl:message>
            </xsl:if>
            
            <xsl:for-each select="$nodes/*">
                
                <xsl:apply-templates select="//class"/>
                
            </xsl:for-each>
            
        </xsl:for-each>
        
        <xsl:message>
            <xsl:text>====================================================</xsl:text>
        </xsl:message>
        
        <!-- process elements TODO and comments  | comment() -->
        
        </xsl:element><!-- end schema -->
        
    </xsl:template>

    <!-- ===================================================== -->
    
    <xsl:template match="classes"> 
        
        <xsl:apply-templates select="class"/><!-- process each contained class element -->
        
    </xsl:template>

    <!-- ===================================================== -->
    
    <xsl:template match="xi:include | cpp | java | csharp "> <!-- rule to process each class element -->
        
        <!-- ignore -->
        
    </xsl:template>

    <!-- ===================================================== -->
    
    <xsl:template match="*"> <!-- rule to process each element -->
    
        <xsl:variable name="correctedName">
            <xsl:call-template name="substitute-element-name"/>
        </xsl:variable>
<!-- debug
<xsl:message>
    <xsl:text>*** debug $correctedName=</xsl:text>
    <xsl:value-of select="$correctedName"/>
</xsl:message>  -->
        
        <xsl:element name="{$correctedName}">
            
            <xsl:for-each select="@*[not(local-name() = 'comment')]">
                <!-- process attributes for this element -->
                <xsl:attribute name="{local-name()}" select="."/>
            </xsl:for-each>
            <xsl:call-template name="handle-documentation"/>

            <xsl:choose>
                <xsl:when test="(count(*) > 0)"> <!-- has child elements -->

                    <xsl:choose>
                        <xsl:when test="(count(attribute) > 0)">
                            <xsl:element name="complexType">
                                <xsl:element name="complexContent">
                                    <xsl:for-each select="attribute">
                                        <xsl:element name="attribute">
                                            <xsl:attribute name="name" select="@name"/>
                                        </xsl:element>
                                    </xsl:for-each>
                                </xsl:element>
                            </xsl:element>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="*"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                </xsl:otherwise>
            </xsl:choose>
            
        </xsl:element>
        
        <!-- common final processing for each element -->
    </xsl:template>

    <!-- ===================================================== -->
    
    <xsl:template match="@*"> <!-- rule to process each attribute -->
        
        <!-- common processing for each attribute -->
        <xsl:text> </xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:text>='</xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>'</xsl:text>
        
    </xsl:template>

    <!-- ===================================================== -->
    
    <xsl:template match="comment()"> <!-- rule to process each comment -->
    
        <xsl:text disable-output-escaping="yes">&lt;!--</xsl:text>
        <xsl:value-of select="."/>
        <xsl:text disable-output-escaping="yes">--&gt;</xsl:text>
        <xsl:text>&#10;</xsl:text>
        
    </xsl:template>

    <!-- ===================================================== -->

    <xsl:template name="substitute-element-name">

        <!-- use names of corresponding schema elements -->
        <xsl:choose>
            <xsl:when test="(local-name() = 'class')">
                <xsl:text>element</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="local-name()"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>

    <!-- ===================================================== -->

    <xsl:template name="handle-documentation">
        <xsl:if test="(string-length(@comment) > 0)">
            <xsl:element name="annotation">
                <xsl:element name="appinfo">
                    <xsl:value-of select="@comment"/>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <!-- ===================================================== -->

    <!-- print-indent keeps track of indenting level -->
    <xsl:template name="print-indent">
        <xsl:for-each select="ancestor::*">
            <xsl:text>  </xsl:text>
        </xsl:for-each>
    </xsl:template>


</xsl:stylesheet>
