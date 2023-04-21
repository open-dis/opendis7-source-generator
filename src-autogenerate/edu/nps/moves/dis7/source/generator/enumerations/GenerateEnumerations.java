/**
 * Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
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
 * GenerateEnumerations creates source code from SISO enumeration definitions.
 * Created on Apr 16, 2019 by MOVES Institute, Naval Postgraduate School (NPS), Monterey California USA https://www.nps.edu
 *
 * @author Don McGregor, Mike Bailey and Don Brutzman
 * @version $Id$
 */
public class GenerateEnumerations
{
    // set defaults to allow direct run
    private        File   outputDirectory;
    private static String outputDirectoryPath = "src-generated/java/edu/nps/moves/dis7/enumerations"; // default
    private static String         packageName =                    "edu.nps.moves.dis7.enumerations"; // default
    private static String            language = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_PROGRAMMING_LANGUAGE;
    private static String         sisoXmlFile = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_SISO_XML_FILE;
    
    private Properties uid2ClassName;
    private Properties uid4aliases;
    private Properties interfaceInjection;
    private Map<String,String> uidClassNames;
    private Set<String> uidDoNotGenerate;
    private Map<String,String> uid2ExtraInterface;

    private String disenumpart1Template;
    private String disenumpart1withfootnoteTemplate;
    private String disenumcommentTemplate;
    private String disenumfootnotecommentTemplate;
    private String disenumpart2Template;
    private String disenumpart21Template;
    private String disenumpart25Template;
    private String disenumpart3_8Template;
    private String disenumpart3_16Template;
    private String disenumpart3_32Template;
    private String disdictenumpart1Template;
    private String disdictenumpart2Template;
    private String disdictenumpart3Template;
    private String disbitset1Template;
    private String disbitset15Template;
    private String disbitset16Template;
    private String disbitset2Template;
    private String disbitsetcommentxrefTemplate;
    private String disbitsetcommentTemplate;
    private String licenseTemplate;

    private static String       sisoSpecificationTitleDate = "";

    // https://stackoverflow.com/questions/11883043/does-an-enum-class-containing-20001-enum-constants-hit-any-limit
    final int MAX_ENUMERATIONS = 2000;

    private int additionalEnumClassesCreated = 0;
    
    private String        packageInfoPath;
    private File          packageInfoFile;
    private StringBuilder packageInfoBuilder;

    /** Constructor
     * @param xmlFile input sisoXmlFile
     * @param outputDir output directory path
     * @param packageName package name for this set of enumerations
     */
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
        outputDirectory.mkdirs();
    //  FileUtils.cleanDirectory(outputDirectory); // do NOT clean directory, results can co-exist with other classes
        System.out.println ("actual directory path=" + outputDirectory.getAbsolutePath());
        
        packageInfoPath = outputDirectoryPath + "/" + "package-info.java";
        packageInfoFile = new File(packageInfoPath);
        
        FileWriter packageInfoFileWriter;
        try {
            packageInfoFile.createNewFile();
            packageInfoFileWriter = new FileWriter(packageInfoFile, StandardCharsets.UTF_8);
            packageInfoBuilder = new StringBuilder();
            packageInfoBuilder.append("/**\n");
            packageInfoBuilder.append(" * Enumeration type infrastructure classes for ").append(sisoSpecificationTitleDate).append(" enumerations supporting <a href=\"https://github.com/open-dis/open-dis7-java\" target=\"open-dis7-java\">open-dis7-java</a> library.\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append(" * <p> Online: NPS <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/examples/src/OpenDis7Examples\" target=\"MV3500\">MV3500 Distributed Simulation Fundamentals course examples</a> \n");
            packageInfoBuilder.append(" * links to <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/specifications/README.md\" target=\"README.MV3500\">IEEE and SISO specification references</a> of interest. </p>\n");
            packageInfoBuilder.append(" * <ul>\n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&amp;EntryId=46172\" target=\"SISO-REF-010\" >SISO-REF-010-2023 Reference for Enumerations for Simulation Interoperability</a> </li> \n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&amp;EntryId=47284\" target=\"SISO-REF-10.1\">SISO-REF-10.1-2019 Reference for Enumerations for Simulation, Operations Manual</a></li> </ul>\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append(" * @see java.lang.Package\n");
            packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful\">https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful</a>\n");
            packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java\">https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java</a>\n");
            packageInfoBuilder.append(" */\n");
            packageInfoBuilder.append("// created by edu/nps/moves/dis7/source/generator/entityTypes/GenerateEnumerations.java\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append("package edu.nps.moves.dis7.enumerations;\n");

            packageInfoFileWriter.write(packageInfoBuilder.toString());
            packageInfoFileWriter.flush();
            packageInfoFileWriter.close();
            System.out.println("Created " + packageInfoPath);
        }
        catch (IOException ex) {
            System.out.flush(); // avoid intermingled output
            System.err.println (ex.getMessage()
               + packageInfoFile.getAbsolutePath()
            );
            ex.printStackTrace(System.err);
        }
    }

    private void run() throws SAXException, IOException, ParserConfigurationException
    {
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

        System.out.println("Begin enumeration generation...");
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
        /** cleanup special characters in string
         *  @param s input string
         *  @return output string */
        public final static String htmlize(String s)
        {
            return s.replace("&","and").replace("&","and");
        }

    /**
     * https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#format-java.lang.String-java.lang.Object...-
     * https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax
     * @see java.util.Formatter
     * @see java.util.logging.Formatter
     */
    private void loadEnumTemplates()
    {
        try {
            disenumpart1Template               = loadOneTemplate("disenumpart1.txt");
            disenumpart1withfootnoteTemplate   = loadOneTemplate("disenumpart1withfootnote.txt");
            disenumcommentTemplate             = loadOneTemplate("disenumcomment.txt");
            disenumfootnotecommentTemplate     = loadOneTemplate("disenumfootnotecomment.txt");
            disenumpart2Template               = loadOneTemplate("disenumpart2.txt");
            disenumpart21Template              = loadOneTemplate("disenumpart21.txt");
            disenumpart25Template              = loadOneTemplate("disenumpart25.txt");
            disenumpart3_32Template            = loadOneTemplate("disenumpart3_32.txt");
            disenumpart3_16Template            = loadOneTemplate("disenumpart3_16.txt");
            disenumpart3_8Template             = loadOneTemplate("disenumpart3_8.txt");
            disdictenumpart1Template           = loadOneTemplate("disdictenumpart1.txt");
            disdictenumpart2Template           = loadOneTemplate("disdictenumpart2.txt");
            disdictenumpart3Template           = loadOneTemplate("disdictenumpart3.txt");
            disbitset1Template                 = loadOneTemplate("disbitset1.txt");
            disbitset15Template                = loadOneTemplate("disbitset15.txt");
            disbitset16Template                = loadOneTemplate("disbitset16.txt");
            disbitset2Template                 = loadOneTemplate("disbitset2.txt");
            disbitsetcommentxrefTemplate       = loadOneTemplate("disbitsetcommentxref.txt");
            disbitsetcommentTemplate           = loadOneTemplate("disbitsetcomment.txt");
            licenseTemplate                    = loadOneTemplate("../pdus/dis7javalicense.txt");
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

    /** Utility class */
    public class UidCollector extends DefaultHandler
    {
        /** default constructor */
        public UidCollector()
        {
            super();
        }
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
        /** default constructor */
        public MyHandler()
        {
            super();
        }
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
                        currentEnum.footnote = normalizeDescription(currentEnum.footnote);
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
                            currentEnumRow.description = normalizeDescription(currentEnumRow.description);
                    }
                    break;

                case "enumrow":
                    if (currentEnum == null)
                        break;
                    currentEnumRow = new EnumRowElem();
                    // TODO if description is empty, get meta key-value
                    currentEnumRow.description = attributes.getValue("description");
                    if (currentEnumRow.description != null)
                        currentEnumRow.description = normalizeDescription(currentEnumRow.description);
                    currentEnumRow.value = attributes.getValue("value");

/* special case, 2147483648 is one greater than max Java integer, reported 30 JAN 2022
    <enumrow value="2147483648" description="Rectangular Volume Record 4" group="1" status="hold" uuid="fdccf8e0-e73c-4137-b140-f7d0882b0778">
      <cr value="1913" />
    </enumrow>
*/            
            if (currentEnumRow.value.equals("2147483648"))
            {
                System.out.println ("*** Special case 'Rectangular Volume Record 4' value 2147483648 reset to 2147483647" +
                                    " in order to avoid exceeding max integer value");
                currentEnumRow.value = "2147483647";
            }
                    currentEnumRow.footnote = attributes.getValue("footnote");
                    if (currentEnum.footnote != null)
                        currentEnum.footnote = normalizeDescription(currentEnum.footnote);
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
                        currentBitfieldRow.description = normalizeDescription(currentBitfieldRow.description);
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
                        currentDictRow.description = normalizeDescription(currentDictRow.description);
                    currentDict.elems.add(currentDictRow);
                    //maybeSysOut(attributes.getValue("xref"), "dictrow uid" + currentDict.uid + " " + currentDict.name + " " + currentDictRow.value + " " + currentDictRow.description);
                    break;

               case "revision":
                    if (sisoSpecificationTitleDate == null) // assume first encountered is latest
                        sisoSpecificationTitleDate = attributes.getValue("title") + ", " + attributes.getValue("date");
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

            sb.append(String.format(disdictenumpart1Template, sisoSpecificationTitleDate, packageName, "UID " + el.uid, classNameCorrected, additionalInterface));

            // enumerations section
            dictNames.clear();
            if (el.elems.size() > MAX_ENUMERATIONS)
            {
                System.out.println ("*** Enumerations class " + packageName + classNameCorrected + " has " + el.elems.size() +
                    ", possibly too large?");
            }
            el.elems.forEach((row) -> {
                String name = row.value.replaceAll("[^a-zA-Z0-9]", ""); // only chars and numbers
                if (!dictNames.contains(name))
                {
                     String fullName = normalizeDescription(row.description);
                     sb.append(String.format(disdictenumpart2Template, name, fullName, name, fullName)); // first Javadoc then enumeration pair
                     dictNames.add(name);
                }
                else System.out.println("   Duplicate dictionary entry for " + name + " in " + clsName);
            });

            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");

            // footer section
            sb.append(String.format(disdictenumpart3Template, classNameCorrected, classNameCorrected));

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

            sb.append(String.format(disbitset1Template, 
                packageName, sisoSpecificationTitleDate, 
                "UID " + el.uid, el.size, 
                el.name, classNameCorrected, 
                (otherInf==null?"":"implements "+otherInf)));
            enumNames.clear();
            if (el.elems.size() > MAX_ENUMERATIONS)
            {
                System.out.println ("*** Enumerations class " + packageName + classNameCorrected + " has " + el.elems.size() +
                    ", possibly too large, limiting size to " + MAX_ENUMERATIONS);
            }
            el.elems.forEach((row) -> {
                String xrefName = null;
                if (row.xrefclassuid != null)
                    xrefName = uidClassNames.get(row.xrefclassuid); //Main.this.uid2ClassName.getProperty(row.xrefclassuid);
                String bitsType = new String();
                if  (Integer.valueOf(row.length) == 1)
                     bitsType = "boolean";
                else bitsType = "length=" + row.length;
                if (xrefName != null) {
                    sb.append(String.format(disbitsetcommentxrefTemplate, 
                        "bit position " + row.bitposition + ", " + bitsType,
                        htmlize((row.description==null?"":normalizeDescription(row.description)+", ")),xrefName));
                    sb.append(String.format(disbitset16Template, 
                        createEnumName(row.name), row.bitposition, row.length, xrefName));
                }
                else {
                    if(row.description != null)
                        sb.append(String.format(disbitsetcommentTemplate,
                            "bit position " + row.bitposition + ", " + bitsType, 
                            (htmlize(normalizeDescription(row.description)))));
                    sb.append(String.format(disbitset15Template, createEnumName(row.name), row.bitposition, row.length));
                }
            });
            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");

            sb.append(String.format(disbitset2Template, classNameCorrected, el.size, classNameCorrected, classNameCorrected, classNameCorrected, classNameCorrected, classNameCorrected));

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
            sb.append(licenseTemplate);
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
            int numberOfEnumerations = el.elems.size();
            if(el.footnote == null)
              sb.append(String.format(disenumpart1Template,             packageName, sisoSpecificationTitleDate,  "UID " + el.uid, el.size, el.name,
                                      numberOfEnumerations,              classNameCorrected, additionalInterface));
            else
              sb.append(String.format(disenumpart1withfootnoteTemplate, packageName, sisoSpecificationTitleDate,  "UID " + el.uid, el.size, el.name,
                                      numberOfEnumerations, el.footnote, classNameCorrected, additionalInterface));

            enumNames.clear();
            // enum section
            if (el.elems.isEmpty())
            {
                String elementName = "(undefined element)";
                if (el.name != null)
                       elementName = el.name;
                sb.append("   /** Constructor initialization */");
                sb.append(String.format(disenumpart2Template, "SELF", "0", elementName + " details not found in SISO spec"));
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
                      String enumName = createEnumName(normalizeDescription(row.description));
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

            sb.append(String.format(disenumpart25Template, classNameCorrected, el.size, classNameCorrected, classNameCorrected, classNameCorrected, classNameCorrected));

            // footer section
            // Many enums come in with smaller bit widths or in-between bitwidths;  Leave handling the odd balls up to the user 
            // but figure out the smallest primitive size needed to hold it.
            int sz = Integer.parseInt(el.size);
            if(sz <= 8)
               sb.append(String.format(disenumpart3_8Template, classNameCorrected, classNameCorrected, classNameCorrected));
            else if(sz <= 16)
               sb.append(String.format(disenumpart3_16Template, classNameCorrected, classNameCorrected, classNameCorrected));
            else
               sb.append(String.format(disenumpart3_32Template, classNameCorrected, classNameCorrected, classNameCorrected));

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
                  String enumName = createEnumName(normalizeDescription(row.description));
                  writeOneEnum(additionalRowStringBuilder, row, enumName);
                }
            } /* ); */
            additionalRowStringBuilder.setLength(additionalRowStringBuilder.length() - 2);
            additionalRowStringBuilder.append("; /*here*/\n");

            additionalRowStringBuilder.append(String.format(disenumpart25Template, classNameCorrected, el.size, classNameCorrected, classNameCorrected, classNameCorrected, classNameCorrected));

            // footer section
            // Many enums come in with smaller bit widths or in-between bitwidths;  Leave handling the odd balls up to the user
            // but figure out the smallest primitive size needed to hold it.
            sz = Integer.parseInt(el.size);
            if(sz <= 8)
               additionalRowStringBuilder.append(String.format(disenumpart3_8Template,  classNameCorrected, classNameCorrected, classNameCorrected));
            else if(sz <= 16)
               additionalRowStringBuilder.append(String.format(disenumpart3_16Template, classNameCorrected, classNameCorrected, classNameCorrected));
            else
               additionalRowStringBuilder.append(String.format(disenumpart3_32Template, classNameCorrected, classNameCorrected, classNameCorrected));

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
          sb.append(String.format(disenumfootnotecommentTemplate, htmlize(normalizeDescription(row.description)) + (row.footnote == null ? "" : ", " + htmlize(normalizeDescription(row.footnote)))));
          sb.append(String.format(disenumpart2Template, normalizeToken(enumName), row.value, normalizeDescription(row.description)));
        }
        else {
          sb.append(String.format(disenumcommentTemplate, xrefName));
          sb.append(String.format(disenumpart21Template, createEnumName(normalizeDescription(row.description)), row.value, normalizeDescription(row.description), xrefName));
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

    /** Command-line or solo invocation to run this object
     * @param args three configuration arguments, if defaults not used
     */
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
        /**
         * Normalize string characters to create valid description
         * @param value of interest
         * @return normalized value
         */
        public static String normalizeDescription(String value)
        {
            String normalizedEntry = value.trim()
                                          .replaceAll("&", "&amp;").replaceAll("&amp;amp;", "&amp;")
                                          .replaceAll("\"", "'");
            // note that no solitary & characters appear in valid XML document, so this is restoring escape characters for HTML Javadoc
//            if (!value.equals(normalizedEntry))
//                System.out.println ("*** normalizeDescription: " + 
//                                    "'" + value + "' to " + 
//                                    "'" + normalizedEntry + "'");
            return normalizedEntry;
        }
        /**
         * Normalize string characters to create valid Java name.  Note that unmangled name typically remains available in the description
         * @param value of interest
         * @return normalized value
         */
        public static String normalizeToken(String value)
        {
            String normalizedEntry = value.trim()
                                          .replaceAll("\"", "").replaceAll("\'", "")
                                          .replaceAll("—","-").replaceAll("–","-") // mdash
                    // 
                                          .replaceAll("\\*","x").replaceAll("/","") // escaped regex for multiply, divide
                                          .replaceAll("&", "&amp;").replaceAll("&amp;amp;", "&amp;");
            if (!normalizedEntry.isEmpty() && Character.isDigit(normalizedEntry.toCharArray()[0]))
                    normalizedEntry = '_' + normalizedEntry;
            if (!value.equals(normalizedEntry) && !normalizedEntry.equals(value.trim()))
                System.out.println ("*** normalize " + "\n" + 
                                    "'" + value + "' to\n" + 
                                    "'" + normalizedEntry + "'");
            return normalizedEntry;
        }
}
