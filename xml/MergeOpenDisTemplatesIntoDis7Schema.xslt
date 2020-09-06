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
                xmlns:xi ="http://www.w3.org/2001/XInclude"
  exclude-result-prefixes="fn xs xi">
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

        <xsl:element name="xs:schema">
            <xsl:attribute namespace="xmlns" name="xs">
                <xsl:text>http://www.w3.org/2001/XMLSchema</xsl:text>
            </xsl:attribute>
            <xsl:attribute namespace="xmlns" name="fn">
                <xsl:text>http://www.w3.org/2005/xpath-functions</xsl:text>
            </xsl:attribute>
            <xsl:attribute namespace="xmlns" name="xi">
                <xsl:text>http://www.w3.org/2001/XInclude</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="targetNamespace">
                <xsl:text>https://github.com/open-dis/open-dis7-source-generator/raw/master/xml/DIS_7_2012.autogenerated.xsd</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="elementFormDefault">
                <xsl:text>qualified</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="attributeFormDefault">
                <xsl:text>unqualified</xsl:text>
            </xsl:attribute>
            <xsl:attribute name="version">
                <xsl:text>7.0</xsl:text>
            </xsl:attribute>
            
            <!--
            <xsl:comment> 
                <xsl:text>How to integrate these attributes?</xsl:text>
                <xsl:text>&#10;</xsl:text>
                <xsl:text>   </xsl:text>
                <xsl:text disable-output-escaping="yes"><![CDATA[ xmlns:xs="http://www.w3.org/2001/XMLSchema"]]></xsl:text>
                <xsl:text disable-output-escaping="yes"><![CDATA[ targetNamespace="https://github.com/open-dis/open-dis7-source-generator/raw/master/xml/DIS_7_2012.autogenerated.xsd"]]></xsl:text>
                <xsl:text disable-output-escaping="yes"><![CDATA[ elementFormDefault="unqualified"]]></xsl:text>
                <xsl:text disable-output-escaping="yes"><![CDATA[ attributeFormDefault="unqualified"]]></xsl:text>
                <xsl:text disable-output-escaping="yes"><![CDATA[ version="7.0"]]></xsl:text>
            </xsl:comment>
            -->
            
<xsl:message>
    <xsl:text>*** debug count(//*/*[local-name() = 'class'])=</xsl:text>
    <xsl:value-of select="count(//*/*[local-name() = 'class'])"/>
    <xsl:text>, count(//class)=</xsl:text>
    <xsl:value-of select="count(//class)"/>
</xsl:message>
<!-- TODO simple types -->
            <!-- TODO combine the following node lists so that overall result can be alphabetized -->
            <xsl:variable name="allClassNodeList">
                <xsl:for-each select="//xi:include"> <!-- now repeat for each include -->
                    <xsl:variable name="nodes" select="doc(concat($relativePathToFiles,@href))"/>
                    <xsl:value-of select="$nodes/classes/class"/>
<xsl:message>
    <xsl:text>*** debug count($nodes/classes/class)=</xsl:text>
    <xsl:value-of select="count($nodes/classes/class)"/>
</xsl:message>
                </xsl:for-each>
                
            </xsl:variable>
<!-- debug  -->
<xsl:message>
    <xsl:text>*** debug count($allClassNodeList/*)=</xsl:text>
    <xsl:value-of select="count($allClassNodeList/*)"/>
    <xsl:text> string($allClassNodeList/*)=</xsl:text>
    <xsl:value-of select="string($allClassNodeList/*)"/>
    <xsl:text> [</xsl:text>
    <xsl:for-each select="$allClassNodeList/*">
        <xsl:text>, </xsl:text>
        <xsl:value-of select="local-name()"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@name"/>
    </xsl:for-each>
    <xsl:text>]</xsl:text>
</xsl:message>
            
            <xsl:call-template name="create-complex-types"/> <!-- classes/class in topmost document -->          

            <xsl:for-each select="//xi:include"> <!-- now repeat for each include -->

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
    
    <!-- rule to ignore already-handled elements -->
    <xsl:template match="xi:include | cpp | java | csharp | primitive | sisoenum">
        
        <!-- ignore -->
        
    </xsl:template>

    <!-- ===================================================== -->
    
    <xsl:template match="*"> <!-- rule to process each element -->
    
        <!-- use names of corresponding schema elements -->
        <xsl:choose>
            <xsl:when test="(local-name() = 'class')">
                <xsl:element name="xs:element">
                    <xsl:attribute name="name" select="@name"/>
                    
                    <xsl:call-template name="handle-comment-documentation"/>
                    <xsl:apply-templates select="*"/>
                </xsl:element>
            </xsl:when>
            <!-- ===================================== -->
            <xsl:when test="(local-name() = 'attribute')">
                <xsl:element name="xs:attribute">
                    <xsl:attribute name="name" select="@name"/>
                    <xsl:attribute name="type">
                        <xsl:choose>
                            <xsl:when test="(count(*[local-name() = 'primitive']) > 0) and (string-length(primitive/@type) > 0)">
                                <xsl:value-of select="primitive/@type"/>
                            </xsl:when>
                            <xsl:when test="*[local-name() = 'primitive']">
                                <xsl:text>xs:string</xsl:text>
                                <xsl:message>
                                    <xsl:text>*** Attribute </xsl:text>
                                    <xsl:value-of select="@name"/>
                                    <xsl:text> contained primitive has no type definition</xsl:text>
                                </xsl:message>
                            </xsl:when>
                            <!-- TODO 
                            <xsl:when test="(local-name() = 'classRef')">

                            </xsl:when> -->
                            <xsl:otherwise>
                                <xsl:text>xs:string</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                                <!-- TODO 
                                <xsl:when test="(local-name() = 'classRef')">

                                </xsl:when> -->
                    <xsl:call-template name="handle-comment-documentation"/>
                    <xsl:apply-templates select="*"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="{local-name()}">
                    <xsl:attribute name="name" select="@name"/>
                    
                    <xsl:call-template name="handle-comment-documentation"/>
                    <xsl:apply-templates select="*"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>

        <!-- process attributes for this element -->
        <xsl:variable name="unhandledAttributes" select="@*[not(local-name() = 'comment')][not(local-name() = 'name')]"/>
        <xsl:if test="(count($unhandledAttributes) > 0)">
            <xsl:comment>
                <xsl:for-each select="$unhandledAttributes">
                    <xsl:value-of select="."/>
                </xsl:for-each>
            </xsl:comment>
            <xsl:message>
                <xsl:text>*** Element </xsl:text>
                <xsl:value-of select="local-name()"/>
                <xsl:text> has unhandled attribute definitions: </xsl:text>
                <xsl:for-each select="$unhandledAttributes">
                    <xsl:value-of select="."/>
                </xsl:for-each>
            </xsl:message>
        </xsl:if>
<!-- debug 
<xsl:message>
    <xsl:text>*** debug checking attribute for contained node: </xsl:text>
    <xsl:value-of select="$correctedName"/>
    <xsl:text> name='</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>'</xsl:text>
</xsl:message>
-->
            <xsl:choose>
                <xsl:when test="(local-name()='attribute')">   
<!-- debug already handled -->
<!-- debug
<xsl:message>
    <xsl:text>*** debug $correctedName=</xsl:text>
    <xsl:value-of select="$correctedName"/>
</xsl:message>  -->
                </xsl:when>
                <xsl:when test="(count(*) > 0)"> <!-- element has child elements -->
                    <xsl:choose>
                        <xsl:when test="(count(attribute) > 0)">
                            <xsl:element name="xs:complexType">
                            <!--<xsl:element name="xs:complexContent"> -->
                                    <xsl:for-each select="attribute">
                                        <xsl:element name="xs:attribute">
                                            <xsl:attribute name="name" select="@name"/>
                                            <xsl:if test="(string-length(@initialValue) > 0)">
                                                <xsl:attribute name="default" select="@initialValue"/>
                                            </xsl:if>
                                            <xsl:call-template name="handle-comment-documentation"/>
                                            <xsl:if test="(count(sisoenum) > 0)">
                                                <xsl:element name="xs:simpleType">
                                                    <xsl:element name="xs:restriction">
                                                        <!-- TODO
                                                        -->
                                                        <xs:attribute name="base">
                                                            <xsl:text>xs:NMTOKEN</xsl:text>
                                                        </xs:attribute>
                                                        <xsl:element name="xs:enumeration">
                                                            <xs:attribute name="xs:value">
                                                                <xsl:value-of select="@type"/>
                                                            </xs:attribute>
                                                        <!-- TODO
                                                        -->
                                                            <xsl:call-template name="handle-comment-documentation"/>
                                                        </xsl:element>
                                                    </xsl:element>
                                                </xsl:element>
                                            </xsl:if>
                                            <xsl:variable name="remainingAttributes" select="@*[local-name() != 'name'][local-name() != 'comment'][local-name() != 'initialValue']"/>
                                            <xsl:if test="(count($remainingAttributes) > 0)">
                                                <xsl:comment>
                                                    <xsl:apply-templates select="@*[local-name() != 'name'][local-name() != 'comment']"/>
                                                </xsl:comment>
                                                <xsl:message>
                                                    <xsl:text>*** Element </xsl:text>
                                                    <xsl:value-of select="../@name"/>
                                                    <xsl:text> attribute </xsl:text>
                                                    <xsl:value-of select="@name"/>
                                                    <xsl:text> has unhandled definitions: </xsl:text>
                                                    <xsl:apply-templates select="@*[local-name() != 'name'][local-name() != 'comment']"/>
                                                </xsl:message>
                                            </xsl:if>
                                        </xsl:element>
                                    </xsl:for-each>
                            <!--</xsl:element>-->
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

        
    </xsl:template>

    <!-- ===================================================== -->

    <xsl:template name="handle-comment-documentation">
        <xsl:variable name="combinedAppinfo">
            <xsl:if test="(string-length(@comment) > 0)">
                <xsl:value-of select="normalize-space(@comment)"/>
            </xsl:if>
            <xsl:if test="(count(sisoenum) > 0)">
                <xsl:text> (sisoenum </xsl:text>
                <xsl:value-of select="normalize-space(concat(sisoenum/@type,' ',sisoenum/@comment))"/>
                <xsl:text>)</xsl:text>
            </xsl:if>
        </xsl:variable>
        <xsl:if test="(string-length(normalize-space($combinedAppinfo)) > 0)">
            <xsl:element name="xs:annotation">
                <xsl:element name="xs:appinfo">
                    <xsl:value-of select="normalize-space($combinedAppinfo)"/>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>

    <!-- ===================================================== -->
    
    <xsl:template name="create-complex-types">
        <xsl:for-each select="//classes/class">
            <xs:complexType name="{@name}">
                <xsl:call-template name="handle-comment-documentation"/>
                <xsl:apply-templates select="attribute"/>
            </xs:complexType>
        </xsl:for-each>
    </xsl:template>

    <!-- ===================================================== -->

    <!-- print-indent keeps track of indenting level -->
    <xsl:template name="print-indent">
        <xsl:for-each select="ancestor::*">
            <xsl:text>  </xsl:text>
        </xsl:for-each>
    </xsl:template>


</xsl:stylesheet>