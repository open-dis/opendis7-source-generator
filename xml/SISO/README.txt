SISO-REF-010-2022 v30
2022-04-17

Copyright (c) 2015, 2017-2022 by the Simulation Interoperability Standards Organization, Inc.

7901 4th St N, Suite 300-4043
St. Petersburg, FL 33702
All rights reserved.

Schema and API: SISO hereby grants a general, royalty-free license to copy, distribute, display, and
make derivative works from this material, for all purposes, provided that any use of the material
contains the following attribution: “Reprinted with permission from SISO Inc.” Should
a reader require additional information, contact the SISO Inc. Board of Directors.

Documentation: SISO hereby grants a general, royalty-free license to copy, distribute,
display, and make derivative works from this material, for noncommercial purposes, provided that
any use of the material contains the following attribution: “Reprinted with permission from SISO Inc.” 
The material may not be used for a commercial purpose without express written
permission from the SISO Inc Board of Directors.

SISO Inc. Board of Directors
7901 4th St N, Suite 300-4043
St. Petersburg, FL 33702

FILES LISTING
=============

Makefile                                 Makefile to automate translation of XML to Excel and C99 Header.
README.txt                               This file
RPR-Enumerations_v2.0.xml                RPR FOM Enumerations output file, generated from the RPR tools
SISO-REF-010.xsd                         Enumerations Schema file
SISO-REF-010-c99h.xsl                    Enumerations to C99 Header translator
SISO-REF-010-spreadsheetml.xsl           Enumerations to Microsoft Excel translator
SISO-REF-010-2022 Enumerations v30.doc   Enumerations in word format
SISO-REF-010-2022 Enumerations v30.htm   Enumerations in HTML format
SISO-REF-010-2022 Enumerations v30.pdf   PDF of the Enumerations word format file
SISO-REF-010-2022 Enumerations v30.xlsx  Enumerations in excel format – generated using xsl file
SISO-REF-010.xml                         Enumerations data file


USAGE EXAMPLES
=================
There are many ways to use the Machine Readable Enumerations. Here are some simple examples.

Web-browser: (e.g. Internet Explorer, Firefox, Opera, Safari)
  File->Open and select 'SISO-REF-010.xml'
  You will be presented with a HTML view of the document.

Microsoft Word:
  File->Open and select 'SISO-REF-010.xml'
  When prompted to choose a data view, select 'SISO-REF-010-html.xsl'
  Press CTRL+A to select all content in the document, and press F9 to update dynamic fields (such as the table of contents).

Microsoft Excel:
  File->Open and select 'SISO-REF-010.xml'
  When prompted, choose 'Open the file with the following stylesheet applied' and select 'siso-ref-010-spreadsheetml.xsl'.

     NOTE:
       ‘Strikethrough’ is used to identify deprecated entries.
       ‘Italics’ is used to identify retired entries.

       Font colour is used to identify the following:
       * New addition in this release - red
       * Future addition (if viewing DRAFT pre-release version) - light blue
       * Pending addition (if viewing DRAFT pre-release version) - orange
       * On Hold (if viewing DRAFT pre-release version) - pink

Microsoft Windows - Generate Excel file:
  Install Cygwin (http://www.cygwin.com/) and select the "libxslt" package.
  $ xsltproc --output SISO-REF-010.xls SISO-REF-010-spreadsheetml.xsl SISO-REF-010.xml

  (or)

  Install Microsoft Command-line XSL processor (http://www.microsoft.com/DOWNLOADS/details.aspx?familyid=2FB55371-C94E-4373-B0E9-DB4816552E41).
  C:\> MSXSL.EXE SISO-REF-010.xml SISO-REF-010-spreadsheetml.xsl -o SISO-REF-010.xls

Ubuntu - Generate Excel and C99 Header files:
  Install make, xsltproc and xmllint ('sudo apt-get install make xsltproc libxml2-utils').
  $ make
  
