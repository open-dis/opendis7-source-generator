/**
 * Copyright (c) 2008-2021, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */
package edu.nps.moves.dis7.source.generator.enumerations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * GenerateEnumerations.java created on Apr 16, 2019 MOVES Institute, Naval Postgraduate School (NPS), Monterey California USA https://www.nps.edu
 *
 * @author Don McGregor, Mike Bailey and Don Brutzman
 * @version $Id$
 */
public class GenerateEnumerations
{
    // set defaults to allow direct run
    private        File   outputDirectory;
    private static String outputDirectoryPath = "src-generated/java/edu/nps/moves/dis7/enumerations";
    private static String         packageName =                    "edu.nps.moves.dis7.enumerations";
    private static String            language = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_LANGUAGE;
    private static String         sisoXmlFile = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_SISO_XML_FILE;
    
    private Properties uid2ClassName;
    private Properties uid4aliases;
    private Properties interfaceInjection;
    private Map<String,String> uidClassNames;
    private Set<String> uidDoNotGenerate;
    private Map<String,String> uid2ExtraInterface;

    private String enumTemplate1;
    private String enumTemplate1WithFootnote;
    private String enumCommentTemplate;
    private String enumFootnoteCommentTemplate;
    private String enumTemplate2;
    private String enumTemplate21;
    private String enumTemplate25;
    private String enumTemplate3_8;
    private String enumTemplate3_16;
    private String enumTemplate3_32;
    private String dictEnumTemplate1;
    private String dictEnumTemplate2;
    private String dictEnumTemplate3;
    private String bitsetTemplate1;
    private String bitsetTemplate15;
    private String bitsetTemplate16;
    private String bitsetTemplate2;
    private String bitsetXrefCommentTemplate;
    private String bitsetCommentTemplate;

    private String specTitleDate = null;

    // https://stackoverflow.com/questions/11883043/does-an-enum-class-containing-20001-enum-constants-hit-any-limit
    final int MAX_ENUMERATIONS = 2000;

    private int additionalEnumClassesCreated = 0;

    public GenerateEnumerations(String xmlFile, String outputDir, String packageName)
    {
        System.out.println (GenerateEnumerations.class.getName());
        if (!xmlFile.isEmpty())
             sisoXmlFile = xmlFile;
        if (!outputDir.isEmpty())
            outputDirectoryPath = outputDir;
        if (!packageName.isEmpty())
             GenerateEnumerations.packageName = packageName;
        System.out.println ("              xmlFile=" + sisoXmlFile);
        System.out.println ("          packageName=" + GenerateEnumerations.packageName);
        System.out.println ("  outputDirectoryPath=" + outputDirectoryPath);
        
        outputDirectory  = new File(outputDirectoryPath);
        System.out.println ("actual directory path=" + outputDirectory.getAbsolutePath());
    }

    private void run() throws SAXException, IOException, ParserConfigurationException
    {
        outputDirectory.mkdirs();
    //  FileUtils.cleanDirectory(outputDirectory); // do NOT clean directory, results can co-exist with other classes
        // Manual:
        uid2ClassName = new Properties();
        uid2ClassName.load(getClass().getResourceAsStream("Uid2ClassName.properties"));
        uid4aliases = new Properties();
        uid4aliases.load(getClass().getResourceAsStream("uid4aliases.properties"));
        
        // Final:
        uidClassNames = new HashMap<>();

        interfaceInjection = new Properties();
        interfaceInjection.load(getClass().getResourceAsStream("interfaceInjection.properties"));
        loadEnumTemplates();

        // These 2 are to support the special case of uid 55, where each enum row should be a BitField
        // TBD: figure out how to do this through the normal methods
       // uidDoNotGenerate = new HashSet<>();
       // uidDoNotGenerate.add("55");  // Entity Capabilities
        
        uid2ExtraInterface = new HashMap<>();
        uid2ExtraInterface.put("450", "EntityCapabilities"); //Land Platform Entity Capabilities
        uid2ExtraInterface.put("451", "EntityCapabilities");
        uid2ExtraInterface.put("452", "EntityCapabilities");
        uid2ExtraInterface.put("453", "EntityCapabilities");
        uid2ExtraInterface.put("454", "EntityCapabilities");
        uid2ExtraInterface.put("455", "EntityCapabilities");
        uid2ExtraInterface.put("456", "EntityCapabilities");
        uid2ExtraInterface.put("457", "EntityCapabilities");
        uid2ExtraInterface.put("458", "EntityCapabilities");
        uid2ExtraInterface.put("459", "EntityCapabilities");
        uid2ExtraInterface.put("460", "EntityCapabilities");
        uid2ExtraInterface.put("461", "EntityCapabilities");
        uid2ExtraInterface.put("462", "EntityCapabilities"); //Sensor/Emitter Entity Capabilities

        /*
        for (Entry<Object, Object> ent : uid2ClassName.entrySet()) {
            System.out.println(ent.getKey() + " " + ent.getValue());
        }
         */
        File xmlFile = new File(sisoXmlFile);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        System.out.println("Begin uid preprocess...");
        factory.newSAXParser().parse(xmlFile,new UidCollector());

        System.out.println("Begin enum enumeration generation...");
        MyHandler handler = new MyHandler();
        factory.newSAXParser().parse(xmlFile, handler); // apparently can't reuse xmlFile

        System.out.println (GenerateEnumerations.class.getName() + " complete, " + (handler.enums.size() + additionalEnumClassesCreated) + " enum classes created.");
    }
        /**
         * Replace special characters in name with underscore _ character
         * @param name name value (typically from XML)
         * @return normalized name
         */
        public final static String fixName(String name)
        {
            if ((name==null) || name.trim().isEmpty())
            {
                System.out.flush();
                System.err.println("fixName() found empty name... replaced with \"undefinedName\"");
                return "undefinedName";
            }
            // https://stackoverflow.com/questions/14080164/how-to-replace-a-string-in-java-which-contains-dot/14080194
            // https://stackoverflow.com/questions/2932392/java-how-to-replace-2-or-more-spaces-with-single-space-in-string-and-delete-lead
            String newName = name.trim().replaceAll(",", " ").replaceAll("—"," ").replaceAll("-", " ").replaceAll("\\."," ").replaceAll("&"," ")
                                        .replaceAll("/"," ").replaceAll("\"", " ").replaceAll("\'", " ").replaceAll("( )+"," ").replaceAll(" ", "_");
            newName = newName.replaceAll("_",""); // no underscore divider
            if (newName.contains("__"))
            {
                System.out.flush();
                System.err.println("fixname: " + newName);
                newName = newName.replaceAll("__", "_");
            }
            return newName;
        }
        public final static String htmlize(String s)
        {
            return s.replace("&","and").replace("&","and");
        }

    /**
     * https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#format-java.lang.String-java.lang.Object...-
     * https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax
     */
    private void loadEnumTemplates()
    {
        try {
            enumTemplate1               = loadOneTemplate("disenumpart1.txt");
            enumTemplate1WithFootnote   = loadOneTemplate("disenumpart1withfootnote.txt");
            enumCommentTemplate         = loadOneTemplate("disenumcomment.txt");
            enumFootnoteCommentTemplate = loadOneTemplate("disenumfootnotecomment.txt");
            enumTemplate2               = loadOneTemplate("disenumpart2.txt");
            enumTemplate21              = loadOneTemplate("disenumpart21.txt");
            enumTemplate25              = loadOneTemplate("disenumpart25.txt");
            enumTemplate3_32            = loadOneTemplate("disenumpart3_32.txt");
            enumTemplate3_16            = loadOneTemplate("disenumpart3_16.txt");
            enumTemplate3_8             = loadOneTemplate("disenumpart3_8.txt");
            dictEnumTemplate1           = loadOneTemplate("disdictenumpart1.txt");
            dictEnumTemplate2           = loadOneTemplate("disdictenumpart2.txt");
            dictEnumTemplate3           = loadOneTemplate("disdictenumpart3.txt");
            bitsetTemplate1             = loadOneTemplate("disbitset1.txt");
            bitsetTemplate15            = loadOneTemplate("disbitset15.txt");
            bitsetTemplate16            = loadOneTemplate("disbitset16.txt");
            bitsetTemplate2             = loadOneTemplate("disbitset2.txt");
            bitsetXrefCommentTemplate   = loadOneTemplate("disbitsetcommentxref.txt");
            bitsetCommentTemplate       = loadOneTemplate("disbitsetcomment.txt");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String loadOneTemplate(String s) throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(getClass().getResource(s).toURI())));
    }

    class EnumElem
    {
        String uid;
        String name;
        String size;
        String footnote;
        List<EnumRowElem> elems                 = new ArrayList<>();
    } 

    class EnumRowElem
    {
        String value;
        String description;
        String footnote;
        String xrefclassuid;
    }

    class DictionaryElem
    {
        String name;
        String uid;
        List<DictionaryRowElem> elems = new ArrayList<>();
    }

    class DictionaryRowElem
    {
        String value;
        String description;
    }

    class BitfieldElem
    {
        String name;
        String size;
        String uid;
        List<BitfieldRowElem> elems = new ArrayList<>();
    }

    class BitfieldRowElem
    {
        String name;
        String bitposition;
        String length = "1"; // default
        String description;
        String xrefclassuid;
    }

    public class UidCollector extends DefaultHandler
    {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            switch (qName) {
                case "enum":
                case "bitfield":
                case "dict":
                    String uid = attributes.getValue("uid");
                    if (uid != null) {
                        String name = fixName(attributes.getValue("name")); // name canonicalization C14N
                        String name2 = GenerateEnumerations.this.uid2ClassName.getProperty(uid);
                        if(name2 != null)
                          uidClassNames.put(uid, name2);
                        else
                          uidClassNames.put(uid,name);
                    }
                default:
            }
        }
        private String makeLegalClassName(String s)
        {
            s = s.replace(" ","");
            s = s.replace(".", "");  // Check for other routines
            return s;
        }
    }

  /** XML handler for recursively reading information and autogenerating code, namely an
     * inner class that handles the SAX parsing of the XML file. This is relatively simple, if
     * a little verbose. Basically we just create the appropriate objects as we come across the
     * XML elements in the file.
     */
    public class MyHandler extends DefaultHandler
    {
        List<EnumElem> enums = new ArrayList<>();
        EnumElem currentEnum;
        EnumRowElem currentEnumRow;

        List<DictionaryElem> dictionaries = new ArrayList<>();
        DictionaryElem currentDict;
        DictionaryRowElem currentDictRow;

        List<BitfieldElem> bitfields = new ArrayList<>();
        BitfieldElem currentBitfield;
        BitfieldRowElem currentBitfieldRow;

        Set<String> testElems = new HashSet<>();
/*
        private void maybeSysOut(String xref, String msg)
        {
            if (xref != null)
                System.out.println(msg + " xref=" + xref);
        }
*/
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            if (qName.equals("category") && "true".equals(attributes.getValue("deprecated")))
                return;

            switch (qName) {
                case "enum":
                    currentEnum = new EnumElem();
                    currentEnum.name = fixName(attributes.getValue("name")); // name canonicalization C14N
                    currentEnum.uid  = attributes.getValue("uid");
                    currentEnum.size = attributes.getValue("size");
                    currentEnum.footnote = attributes.getValue("footnote");
                    if (currentEnum.footnote != null)
                        currentEnum.footnote = currentEnum.footnote.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", ""); // mdash
                    enums.add(currentEnum);
                    //maybeSysOut(attributes.getValue("xref"), "enum uid " + currentEnum.uid + " " + currentEnum.name);
                    break;

                case "meta":
                    if (currentEnum == null)
                        break;
                    if ((currentEnumRow.description == null) || currentEnumRow.description.isEmpty())
                    {
                        String   key = attributes.getValue("key");
                        String value = attributes.getValue("value");
                        if (key != null)
                            currentEnumRow.description = key.toUpperCase() + "_";
                        if (value != null)
                            currentEnumRow.description += value;
                        if (currentEnumRow.description != null)
                            currentEnumRow.description = currentEnumRow.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "")
                                                                                   .replaceAll(" "," ").replaceAll("/","");
                    }
                    break;

                case "enumrow":
                    if (currentEnum == null)
                        break;
                    currentEnumRow = new EnumRowElem();
                    // TODO if description is empty, get meta key-value
                    currentEnumRow.description = attributes.getValue("description");
                    if (currentEnumRow.description != null)
                        currentEnumRow.description = currentEnumRow.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
                    currentEnumRow.value = attributes.getValue("value");
                    currentEnumRow.footnote = attributes.getValue("footnote");
                    if (currentEnum.footnote != null)
                        currentEnum.footnote = currentEnum.footnote.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
                    currentEnumRow.xrefclassuid = attributes.getValue("xref");
                    currentEnum.elems.add(currentEnumRow);
                    //maybeSysOut(attributes.getValue("xref"), "enumrow uid " + currentEnum.uid + " " + currentEnum.name + " " + currentEnumRow.value + " " + currentEnumRow.description);
                    break;

                case "bitfield":
                    currentBitfield = new BitfieldElem();
                    currentBitfield.name = fixName(attributes.getValue("name")); // name canonicalization C14N
                    currentBitfield.size = attributes.getValue("size");
                    currentBitfield.uid = attributes.getValue("uid");
                    bitfields.add(currentBitfield);
                    //maybeSysOut(attributes.getValue("xref"), "bitfieldrow uid " + currentBitfield.uid + " " + currentBitfield.name);
                    break;

                case "bitfieldrow":
                    if (currentBitfield == null)
                        break;
                    currentBitfieldRow = new BitfieldRowElem();
                    currentBitfieldRow.name = fixName(attributes.getValue("name")); // name canonicalization C14N
                    currentBitfieldRow.description = attributes.getValue("description");
                    if (currentBitfieldRow.description != null)
                        currentBitfieldRow.description = currentBitfieldRow.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
                    currentBitfieldRow.bitposition = attributes.getValue("bit_position");
                    String len = attributes.getValue("length");
                    if (len != null)
                        currentBitfieldRow.length = len;
                    currentBitfieldRow.xrefclassuid = attributes.getValue("xref");
                    currentBitfield.elems.add(currentBitfieldRow);
                    //maybeSysOut(attributes.getValue("xref"), "bitfieldrow uid " + currentBitfield.uid + " " + currentBitfield.name + " " + currentBitfieldRow.name + " " + currentBitfieldRow.description);
                    break;

                case "dict":
                    currentDict = new DictionaryElem();
                    currentDict.name = fixName(attributes.getValue("name")); // name canonicalization C14N
                    currentDict.uid = attributes.getValue("uid");
                    dictionaries.add(currentDict);
                    //maybeSysOut(attributes.getValue("xref"), "dict uid " + currentDict.uid + " " + currentDict.name);
                    break;

                case "dictrow":
                    if (currentDict == null)
                        break;
                    currentDictRow = new DictionaryRowElem();
                    currentDictRow.value = attributes.getValue("value");
                    currentDictRow.description = attributes.getValue("description");
                    if (currentDictRow.description != null)
                        currentDictRow.description = currentDictRow.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
                    currentDict.elems.add(currentDictRow);
                    //maybeSysOut(attributes.getValue("xref"), "dictrow uid" + currentDict.uid + " " + currentDict.name + " " + currentDictRow.value + " " + currentDictRow.description);
                    break;

               case "revision":
                    if (specTitleDate == null) // assume first encountered is latest
                        specTitleDate = attributes.getValue("title") + ", " + attributes.getValue("date");
                    break;

                // fall throughs
                case "cot":
                case "cet":
                case "copyright":
                case "revisions":
                case "ebv":
                case "jammer_technique":
                case "jammer_kind":
                case "jammer_category":
                case "jammer_subcategory":
                case "jammer_specific":
                default:
                    testElems.add(qName);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
        {
            switch (qName) {
                case "enum":
                    if (currentEnum != null)
                       writeOutEnum(currentEnum);
                    currentEnum = null;
                    break;

                case "enumrow":
                    currentEnumRow = null;
                    break;

                case "bitfield":
                    if (currentBitfield != null)
                        writeOutBitfield(currentBitfield);
                    currentBitfield = null;
                    break;

                case "bitfieldrow":
                    currentBitfieldRow = null;
                    break;

                case "dict":
                    if (currentDict != null)
                        writeOutDict(currentDict);
                    currentDict = null;
                    break;

                case "dictrow":
                    currentDictRow = null;
                    break;
            }
        }

        Set<String> dictNames = new HashSet<>();

        private void writeOutDict(DictionaryElem el)
        {
            String clsName = uidClassNames.get(el.uid); //Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null)
            {
                System.out.flush();
                System.err.println("*** Didn't find a class name for uid = " + el.uid + ", ignored");
                return;
            }
            String classNameCorrected = clsName;
            if (classNameCorrected.contains("Link11/11"))
            {
                System.out.flush();
                System.err.print  ( "original classNameCorrected=" + classNameCorrected);
                classNameCorrected = classNameCorrected.replace("Link11/11B", "Link11_11B"); // Fix slash in entry
                System.err.println(", revised classNameCorrected=" + classNameCorrected);
            }
            StringBuilder sb = new StringBuilder();

            // Header section
            String additionalInterface = "";
            String otherIf = interfaceInjection.getProperty(clsName);
            if (otherIf != null)
                additionalInterface = ", " + otherIf;

            sb.append(String.format(dictEnumTemplate1, specTitleDate, packageName, "UID " + el.uid, classNameCorrected, additionalInterface));

            // enumerations section
            dictNames.clear();
            if (el.elems.size() > MAX_ENUMERATIONS)
            {
                System.out.println ("*** Enumerations class " + packageName + classNameCorrected + " has " + el.elems.size() +
                    ", possibly too large?");
            }
            el.elems.forEach((row) -> {
                String name = row.value.replaceAll("[^a-zA-Z0-9]", ""); // only chars and numbers
                if (!dictNames.contains(name)) {
                    sb.append(String.format(dictEnumTemplate2, name, row.description.replaceAll("\"", "").replaceAll("\'", "")));
                    dictNames.add(name);
                }
                else
                    System.out.println("   Duplicate dictionary entry for " + name + " in " + clsName);
            });

            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");

            // footer section
            sb.append(String.format(dictEnumTemplate3, classNameCorrected, classNameCorrected));

            // save file
            File targetFile = new File(outputDirectory, classNameCorrected + ".java");
            targetFile.getParentFile().mkdirs();
            FileWriter targetFileWriter;
            try {
                targetFile.createNewFile();
                targetFileWriter = new FileWriter(targetFile, StandardCharsets.UTF_8);
                targetFileWriter.write(sb.toString());
                targetFileWriter.flush();
                targetFileWriter.close();
            }
            catch (IOException ex) {
                System.out.flush();
                System.err.println (ex.getMessage()
                // + " targetFile.getAbsolutePath()=" 
                   + targetFile.getAbsolutePath()
                // + ", classNameCorrected=" + classNameCorrected
                );
                ex.printStackTrace(System.err);
            }
        }

        private void writeOutBitfield(BitfieldElem el)
        {
            String clsName = uidClassNames.get(el.uid); //Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null)
            {
                System.out.flush();
                System.err.println("Didn't find a class name for uid = " + el.uid);
                return;
            }
            String classNameCorrected = clsName;
            if (classNameCorrected.contains("Link11/11"))
            {
                System.out.flush();
                System.err.print  ( "original classNameCorrected=" + classNameCorrected);
                classNameCorrected = classNameCorrected.replace("Link11/11B", "Link11_11B"); // Fix slash in entry
                System.err.println(", revised classNameCorrected=" + classNameCorrected);
            }
            StringBuilder sb = new StringBuilder();
      
            String otherInf = uid2ExtraInterface.get(el.uid);

            sb.append(String.format(bitsetTemplate1, packageName, specTitleDate, "UID " + el.uid, el.size, el.name, classNameCorrected, (otherInf==null?"":"implements "+otherInf)));
            enumNames.clear();
            if (el.elems.size() > MAX_ENUMERATIONS)
            {
                System.out.println ("Enumerations class " + packageName + classNameCorrected + " has " + el.elems.size() +
                    ", possibly too large, limiting size to " + MAX_ENUMERATIONS);
            }
            el.elems.forEach((row) -> {
                String xrefName = null;
                if (row.xrefclassuid != null)
                    xrefName = uidClassNames.get(row.xrefclassuid); //Main.this.uid2ClassName.getProperty(row.xrefclassuid);
                if (xrefName != null) {
                    sb.append(String.format(bitsetXrefCommentTemplate, htmlize((row.description==null?"":row.description.replaceAll("\"", "").replaceAll("\'", "")+", ")),xrefName));
                    sb.append(String.format(bitsetTemplate16, createEnumName(row.name), row.bitposition, row.length, xrefName));
                }
                else {
                    if(row.description != null)
                        sb.append(String.format(bitsetCommentTemplate, (htmlize(row.description.replaceAll("\"", "").replaceAll("\'", "")))));
                    sb.append(String.format(bitsetTemplate15, createEnumName(row.name), row.bitposition, row.length));
                }
            });
            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");

            sb.append(String.format(bitsetTemplate2, classNameCorrected, el.size, classNameCorrected, classNameCorrected, classNameCorrected, classNameCorrected, classNameCorrected));

            // save file
            File targetFile = new File(outputDirectory, classNameCorrected + ".java");
            FileWriter targetFileWriter;
            try {
                targetFile.createNewFile();
                targetFileWriter = new FileWriter(targetFile, StandardCharsets.UTF_8);
                targetFileWriter.write(sb.toString());
                targetFileWriter.flush();
                targetFileWriter.close();
            }
            catch (IOException ex) {
                System.out.flush();
                System.err.println (ex.getMessage() + " targetFile.getAbsolutePath()=" + targetFile.getAbsolutePath()
                      + ", classNameCorrected=" + classNameCorrected);
                ex.printStackTrace(System.err);
            }
        }

        Set<String> enumNames = new HashSet<>();
        Properties aliases = null; // previously aliasNames

        private void writeOutEnum(EnumElem el)
        {
            // be careful here, concurrency scoping
            // overflow buffer matching el.elems to later avoid dreaded "code too large" error
            List<EnumRowElem> additionalRowElements = new ArrayList<>();

            String clsName = uidClassNames.get(el.uid); //Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null)
            {
                System.out.flush();
                System.err.println("*** Didn't find a class name for uid = " + el.uid);
                return;
            }
            String classNameCorrected = clsName;
            if (!classNameCorrected.isEmpty() && classNameCorrected.contains("Link 11/11")) // special case
            {
                System.out.flush();
                System.err.print  ( "original classNameCorrected=" + classNameCorrected);
                classNameCorrected = classNameCorrected.replace("Link 11/11B", "Link11_11B"); // Fix slash in entry
                System.err.println(", revised classNameCorrected=" + classNameCorrected);
            }
            
        /*    if(GenerateEnumerations.this.uidDoNotGenerate.contains(el.uid)) {
                System.out.println("Not generating "+clsName);
                return;
            }
        */
            if(el.uid.equals("4"))
              aliases = uid4aliases;
            
            StringBuilder sb = new StringBuilder();
            StringBuilder additionalRowStringBuilder = new StringBuilder();
            // change additional class name to match similarly
            final String ADDITIONAL_ENUMERATION_FILE_SUFFIX = "Additional";

            // Header section
            String additionalInterface = "";
            String otherIf = interfaceInjection.getProperty(clsName);
            String otherIf2 = uid2ExtraInterface.get(el.uid);
            
            if(otherIf != null | otherIf2 != null) {
                StringBuilder ifsb = new StringBuilder("implements ");
                if(otherIf != null)
                    ifsb.append(otherIf);
                if(otherIf2 != null){
                    ifsb.append(",");
                    ifsb.append(otherIf2);
                }
                additionalInterface = ifsb.toString();
            }
            /*
            if (otherIf != null)
                additionalInterface = "implements " + otherIf;
            
            otherIf = uid2ExtraInterface.get(el.uid);
            if (otherIf != null)
                additionalInterface = "implements "+otherIf;
            */
            /* enumeration initial template, de-spacify name */
            if(el.footnote == null)
              sb.append(String.format(enumTemplate1,             packageName, specTitleDate,  "UID " + el.uid, el.size, el.name, classNameCorrected, additionalInterface));
            else
              sb.append(String.format(enumTemplate1WithFootnote, packageName, specTitleDate,  "UID " + el.uid, el.size, el.name, el.footnote, classNameCorrected, additionalInterface));

            enumNames.clear();
            // enum section
            if (el.elems.isEmpty())
            {
                String elementName = "(undefined element)";
                if (el.name != null)
                       elementName = el.name;
                sb.append(String.format(enumTemplate2, "SELF", "0", elementName + " details not found in SISO spec"));
                // TODO resolve
                System.err.println("*** " + elementName + " uid='" + el.uid + "' has no child element (further SELF details not found in SISO reference)");
            }
            else // here we go
            {
                if (el.elems.size() > MAX_ENUMERATIONS)
                {
                    System.out.flush();
                    System.err.println ("=================================");
                    System.err.println ("*** Enumerations class " + packageName + "." + classNameCorrected + " <enum name=\"" + el.name + "\" uid=\"" + el.uid + "\" etc.>" +
                                        " has " + el.elems.size() + " enumerations, possibly too large to compile.");
                    System.err.println ("*** Dropping enumerations above MAX_ENUMERATIONS=" + MAX_ENUMERATIONS + " for this class..." +
                                        " next, create auxiliary class with remainder.");
                    // https://stackoverflow.com/questions/1184636/shrinking-an-arraylist-to-a-new-size
                    // save the rest
                    System.err.println ("    last element=" + el.elems.get(MAX_ENUMERATIONS - 1).value + ", " + el.elems.get(MAX_ENUMERATIONS - 1).description);
                    // make copy (not reference) available  for further processing
                    additionalRowElements = new ArrayList<>(el.elems.subList(MAX_ENUMERATIONS, el.elems.size()));
                    // save what was created so far for later reuse
                    additionalRowStringBuilder.append(sb.toString().replace("public enum " + classNameCorrected,
                                                                            "public enum " + classNameCorrected + ADDITIONAL_ENUMERATION_FILE_SUFFIX));
                    el.elems.subList(MAX_ENUMERATIONS, el.elems.size()).clear(); // clear elements after this
                }
                // continue with original or reduced list
                el.elems.forEach((row) -> {                    
                    // Check for aliases
                    if(aliases != null && aliases.getProperty(row.value)!=null)
                      writeOneEnum(sb,row,aliases.getProperty(row.value));
                    else {
                      String enumName = createEnumName(row.description.replaceAll("\"", "").replaceAll("\'", ""));
                      writeOneEnum(sb, row, enumName);
                    }
                  /*  if(row.xrefclassuid != null)
                        xrefName=uidClassNames.get(row.xrefclassuid);
                    
                    if(xrefName == null) {
                        String nm=null;
                        sb.append(String.format(enumFootnoteCommentTemplate, htmlize(row.descriptionrow.description.replaceAll("\"", "").replaceAll("\'", ""))+( row.footnote==null?"":", "+htmlize(row.footnote))));
                        sb.append(String.format(enumTemplate2, enumName, row.value, row.description.replaceAll("\"", "").replaceAll("\'", "")));
                        if(aliases != null && aliases.get(row.value) != null) {
                           sb.append(String.format(enumFootnoteCommentTemplate, htmlize(row.description)+( row.footnote==null?"":", "+htmlize(row.footnote))));
                           sb.append(String.format(enumTemplate2, nm = enumName, row.value, row.description.replaceAll("\"", "").replaceAll("\'", "")));
                        }
                    }
                    else {
                        sb.append(String.format(enumCommentTemplate,xrefName));
                        sb.append(String.format(enumTemplate21, createEnumName(row.descriptionrow.description.replaceAll("\"", "").replaceAll("\'", "")), row.value, row.description.replaceAll("\"", "").replaceAll("\'", ""),xrefName));
                    }*/

                });
//                if (additionalRowElements.size() > 0)
//                {
//                    additionalRowElements.forEach((row) -> {                    
//                        // Check for aliases
//                        if(aliases != null && aliases.getProperty(row.value)!=null)
//                          writeOneEnum(sb_additional,row,aliases.getProperty(row.value));
//                        else {
//                          String enumName = createEnumName(row.description.replaceAll("\"", "").replaceAll("\'", ""));
//                          writeOneEnum(sb_additional, row, enumName);
//                        }
//                    });
//                    sb_additional.setLength(sb.length() - 2);
//                    sb_additional.append(";\n");
//                    additionalRowElements.clear();
//                }
            }
            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");
            
            if (el.size == null)
                el.size = "8";

            sb.append(String.format(enumTemplate25, classNameCorrected, el.size, classNameCorrected, classNameCorrected, classNameCorrected, classNameCorrected));

            // footer section
            // Many enums come in with smaller bit widths or in-between bitwidths;  Leave handling the odd balls up to the user 
            // but figure out the smallest primitive size needed to hold it.
            int sz = Integer.parseInt(el.size);
            if(sz <= 8)
               sb.append(String.format(enumTemplate3_8, classNameCorrected, classNameCorrected, classNameCorrected));
            else if(sz <= 16)
               sb.append(String.format(enumTemplate3_16, classNameCorrected, classNameCorrected, classNameCorrected));
            else
               sb.append(String.format(enumTemplate3_32, classNameCorrected, classNameCorrected, classNameCorrected));

            // save file
            File targetFile = new File(outputDirectory, classNameCorrected + ".java");
            FileWriter targetFileWriter;
            try {
                targetFile.createNewFile();
                targetFileWriter = new FileWriter(targetFile, StandardCharsets.UTF_8);
                targetFileWriter.write(sb.toString());
                targetFileWriter.flush();
                targetFileWriter.close();
            }
            catch (IOException ex) {
                System.out.flush();
                System.err.println (ex.getMessage() + " targetFile.getAbsolutePath()=" + targetFile.getAbsolutePath()
                      + ", classNameCorrected=" + classNameCorrected);
                ex.printStackTrace(System.err);
            }
        //  now handle additionalRowElements similarly, if any, creating another file...
        if ((additionalRowElements.size() > 0) && !additionalRowStringBuilder.toString().isEmpty())
        {
            classNameCorrected = classNameCorrected + ADDITIONAL_ENUMERATION_FILE_SUFFIX;
            for (EnumRowElem row : additionalRowElements)
            {
//            additionalRowElements.elems.forEach((row) -> {

                // Check for aliases
                if(aliases != null && aliases.getProperty(row.value)!=null)
                  writeOneEnum(additionalRowStringBuilder,row,aliases.getProperty(row.value));
                else {
                  String enumName = createEnumName(row.description.replaceAll("\"", "").replaceAll("\'", ""));
                  writeOneEnum(additionalRowStringBuilder, row, enumName);
                }
            } /* ); */
            additionalRowStringBuilder.setLength(additionalRowStringBuilder.length() - 2);
            additionalRowStringBuilder.append("; /*here*/\n");

            additionalRowStringBuilder.append(String.format(enumTemplate25, classNameCorrected, el.size, classNameCorrected, classNameCorrected, classNameCorrected, classNameCorrected));

            // footer section
            // Many enums come in with smaller bit widths or in-between bitwidths;  Leave handling the odd balls up to the user
            // but figure out the smallest primitive size needed to hold it.
            sz = Integer.parseInt(el.size);
            if(sz <= 8)
               additionalRowStringBuilder.append(String.format(enumTemplate3_8, classNameCorrected, classNameCorrected, classNameCorrected));
            else if(sz <= 16)
               additionalRowStringBuilder.append(String.format(enumTemplate3_16, classNameCorrected, classNameCorrected, classNameCorrected));
            else
               additionalRowStringBuilder.append(String.format(enumTemplate3_32, classNameCorrected, classNameCorrected, classNameCorrected));

            // save file
            targetFile = new File(outputDirectory, classNameCorrected + ".java"); // already appended ADDITIONAL_ENUMERATION_FILE_SUFFIX
            try {
                targetFile.createNewFile();
                targetFileWriter = new FileWriter(targetFile, StandardCharsets.UTF_8);
                targetFileWriter.write(additionalRowStringBuilder.toString());
                targetFileWriter.flush();
                targetFileWriter.close();
                System.out.flush();
                System.err.println ("*** Created additional-enumerations file, "
                                    + "classNameCorrected=" + classNameCorrected
                                    + ",\n    "
                            //      + "targetFile.getAbsolutePath()="
                                    + targetFile.getAbsolutePath()
                );
                System.err.println ("    first element=" + additionalRowElements.get(0).value + ", " + additionalRowElements.get(0).description);
                System.err.println ("=================================");
                // reset this special case
                additionalRowElements.clear();
                additionalRowStringBuilder.setLength(0);
                additionalEnumClassesCreated++;
            }
            catch (IOException ex) {
                System.out.flush();
                System.err.println (ex.getMessage() + " targetFile.getAbsolutePath()=" + targetFile.getAbsolutePath()
                      + ", classNameCorrected=" + classNameCorrected);
                ex.printStackTrace(System.err);
            }
        }
    }
        
      private void writeOneEnum(StringBuilder sb, EnumRowElem row, String enumName)
      {
        String xrefName = null;
        if (row.xrefclassuid != null)
          xrefName = uidClassNames.get(row.xrefclassuid);

        if (xrefName == null) {
          sb.append(String.format(enumFootnoteCommentTemplate, htmlize(row.description.replaceAll("\"", "").replaceAll("\'", "")) + (row.footnote == null ? "" : ", " + htmlize(row.footnote))));
          sb.append(String.format(enumTemplate2, enumName, row.value, row.description.replaceAll("\"", "").replaceAll("\'", "")));
        }
        else {
          sb.append(String.format(enumCommentTemplate, xrefName));
          sb.append(String.format(enumTemplate21, createEnumName(row.description.replaceAll("\"", "").replaceAll("\'", "")), row.value, row.description.replaceAll("\"", "").replaceAll("\'", ""), xrefName));
        }
      }
        /**
         * Naming conventions for enumeration names
         * @param s enumeration string from XML data file
         * @return normalized name
         */
        private String createEnumName(String s)
        {
            String r = s.toUpperCase();
            // Convert any of these chars to underbar (u2013 is a hyphen observed in source XML):
            r = r.replaceAll("[\\h-/,\";:\\u2013]", "_");

            // Remove any of these chars (u2019 is an apostrophe observed in source XML):
            r = r.replaceAll("[()}{}'.#&\\u2019]", "");

            // Special case the plus character:
            r = r.replace("+", "PLUS");

            // Collapse all contiguous underbars:
            r = r.replaceAll("_{2,}", "_");

            // If there's nothing there, put in something:
            if (r.isEmpty() || r.equals("_"))
                r = "undef";

            // Java identifier can't start with digit
            if (Character.isDigit(r.charAt(0)))
                r = "_" + r; // originally "$"

            // Handle multiply defined entries in the XML by appending a digit:
            String origR = r;
            int count = 2;
            while (enumNames.contains(r)) {
                r = origR + "_" + Integer.toString(count++);
            }
            enumNames.add(r);
            return r;
        }

        private String firstCharUpper(String s)
        {
            String ret = s.toLowerCase();
            char[] ca = ret.toCharArray();
            ca[0] = Character.toUpperCase(ca[0]);
            return new String(ca);
        }

        String maybeSpecialCase(String s, String dflt)
        {
            String lc = s.toLowerCase();
            if (lc.equals("united states"))
                return "USA";
            if (lc.equals("not_used"))
                return "";
            return dflt;
        }

        String smallCountryName(String s, String integ)
        {
            if (integ.equals("0"))
                return "";  // "other

            if (s.length() <= 3)
                return s;
            try {
                s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
                if (s.length() > 3) {
                    return maybeSpecialCase(s, integ);
                }
                return s;
            }
            catch (Exception ex) {
                return integ;
            }
        }
    }

    public static void main(String[] args)
    {
        try {
            if  (args.length == 0)
                 new GenerateEnumerations("",      "",      ""     ).run(); // use defaults
            else new GenerateEnumerations(args[0], args[1], args[2]).run();
        }
        catch (SAXException | IOException | ParserConfigurationException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
