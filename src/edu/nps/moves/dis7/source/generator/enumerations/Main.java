/**
 * Copyright (c) 2008-2020, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */

package edu.nps.moves.dis7.source.generator.enumerations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import org.apache.commons.io.FileUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Main.java created on Apr 16, 2019 MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Main
{
    private File outputDirectory;
    private Properties uid2ClassName;
    private Properties uid4aliases;
    private Properties interfaceInjection;
    private Map<String,String> uidClassNames;
    private Set<String> uidDoNotGenerate;
    private Map<String,String> uid2ExtraInterface;
    private String packageName;
    private String xmlPath;

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

    public Main(String xmlPath, String outputDir, String packageName)
    {
        this.packageName = packageName;
        this.xmlPath = xmlPath;
        outputDirectory = new File(outputDir);
    }

    private void run() throws SAXException, IOException, ParserConfigurationException
    {
        outputDirectory.mkdirs();
        FileUtils.cleanDirectory(outputDirectory);
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
        File xmlFile = new File(xmlPath);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        System.out.println("Begin uid preprocess...");
        factory.newSAXParser().parse(xmlFile,new UidCollector());

        System.out.println("Begin enum generation ...");
        MyHandler handler = new MyHandler();
        factory.newSAXParser().parse(new File(xmlPath), handler);

        System.out.println("Complete. " + handler.enums.size() + " enums created.");
    }

    /**
     * https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#format-java.lang.String-java.lang.Object...-
     * https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax
     */
    private void loadEnumTemplates()
    {
        try {
            enumTemplate1 = loadOneTemplate("disenumpart1.txt");
            enumTemplate1WithFootnote = loadOneTemplate("disenumpart1withfootnote.txt");
            enumCommentTemplate = loadOneTemplate("disenumcomment.txt");
            enumFootnoteCommentTemplate = loadOneTemplate("disenumfootnotecomment.txt");
            enumTemplate2 = loadOneTemplate("disenumpart2.txt");
            enumTemplate21 = loadOneTemplate("disenumpart21.txt");
            enumTemplate25 = loadOneTemplate("disenumpart25.txt");
            enumTemplate3_32 = loadOneTemplate("disenumpart3_32.txt");
            enumTemplate3_16 = loadOneTemplate("disenumpart3_16.txt");
            enumTemplate3_8 = loadOneTemplate("disenumpart3_8.txt");
            dictEnumTemplate1 = loadOneTemplate("disdictenumpart1.txt");
            dictEnumTemplate2 = loadOneTemplate("disdictenumpart2.txt");
            dictEnumTemplate3 = loadOneTemplate("disdictenumpart3.txt");
            bitsetTemplate1 = loadOneTemplate("disbitset1.txt");
            bitsetTemplate15 = loadOneTemplate("disbitset15.txt");
            bitsetTemplate16 = loadOneTemplate("disbitset16.txt");
            bitsetTemplate2 = loadOneTemplate("disbitset2.txt");
            bitsetXrefCommentTemplate = loadOneTemplate("disbitsetcommentxref.txt");
            bitsetCommentTemplate = loadOneTemplate("disbitsetcomment.txt");
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
        List<EnumRowElem> elems = new ArrayList<>();
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
                        String name = attributes.getValue("name");
                        String name2 = Main.this.uid2ClassName.getProperty(uid);
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
                    currentEnum.name = attributes.getValue("name");
                    currentEnum.uid = attributes.getValue("uid");
                    currentEnum.size = attributes.getValue("size");
                    currentEnum.footnote = attributes.getValue("footnote");
                    enums.add(currentEnum);
                    //maybeSysOut(attributes.getValue("xref"), "enum uid " + currentEnum.uid + " " + currentEnum.name);
                    break;

                case "enumrow":
                    if (currentEnum == null)
                        break;
                    currentEnumRow = new EnumRowElem();
                    currentEnumRow.description = attributes.getValue("description");
                    currentEnumRow.value = attributes.getValue("value");
                    currentEnumRow.footnote = attributes.getValue("footnote");
                    currentEnumRow.xrefclassuid = attributes.getValue("xref");
                    currentEnum.elems.add(currentEnumRow);
                    //maybeSysOut(attributes.getValue("xref"), "enumrow uid " + currentEnum.uid + " " + currentEnum.name + " " + currentEnumRow.value + " " + currentEnumRow.description);
                    break;

                case "bitfield":
                    currentBitfield = new BitfieldElem();
                    currentBitfield.name = attributes.getValue("name");
                    currentBitfield.size = attributes.getValue("size");
                    currentBitfield.uid = attributes.getValue("uid");
                    bitfields.add(currentBitfield);
                    //maybeSysOut(attributes.getValue("xref"), "bitfieldrow uid " + currentBitfield.uid + " " + currentBitfield.name);
                    break;

                case "bitfieldrow":
                    if (currentBitfield == null)
                        break;
                    currentBitfieldRow = new BitfieldRowElem();
                    currentBitfieldRow.name = attributes.getValue("name");
                    currentBitfieldRow.description = attributes.getValue("description");
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
                    currentDict.name = attributes.getValue("name");
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
                    currentDict.elems.add(currentDictRow);
                    //maybeSysOut(attributes.getValue("xref"), "dictrow uid" + currentDict.uid + " " + currentDict.name + " " + currentDictRow.value + " " + currentDictRow.description);
                    break;

               case "revision":
                    if (specTitleDate == null) // assume first encountered is latest
                        specTitleDate = attributes.getValue("title") + ", " + attributes.getValue("date");
                    break;

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
            if (clsName == null) {
                System.err.println("Didn't find a class name for uid = " + el.uid);
                return;
            }
            StringBuilder sb = new StringBuilder();

            // Header section
            String additionalInterface = "";
            String otherIf = interfaceInjection.getProperty(clsName);
            if (otherIf != null)
                additionalInterface = ", " + otherIf;

            sb.append(String.format(dictEnumTemplate1, specTitleDate, packageName, "UID " + el.uid, clsName, additionalInterface));

            dictNames.clear();
            // enum section
            el.elems.forEach((row) -> {
                String name = row.value.replaceAll("[^a-zA-Z0-9]", ""); // only chars and numbers
                if (!dictNames.contains(name)) {
                    sb.append(String.format(dictEnumTemplate2, name, row.description.replace('"', '\'')));
                    dictNames.add(name);
                }
                else
                    System.out.println("Duplicate dictionary entry for " + name + " in " + clsName);
            });

            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");

            // footer section
            sb.append(String.format(dictEnumTemplate3, clsName, clsName));

            // save file
            File target = new File(outputDirectory, clsName + ".java");
            target.getParentFile().mkdirs();
            FileWriter fw;
            try {
                target.createNewFile();
                fw = new FileWriter(target);
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }

        private void writeOutBitfield(BitfieldElem el)
        {
            String clsName = uidClassNames.get(el.uid); //Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null)
                return;
            StringBuilder sb = new StringBuilder();
      
            String otherInf = uid2ExtraInterface.get(el.uid);

            sb.append(String.format(bitsetTemplate1, packageName, specTitleDate, "UID " + el.uid, el.size, el.name, clsName, (otherInf==null?"":"implements "+otherInf)));
            enumNames.clear();
            el.elems.forEach((row) -> {
                String xrefName = null;
                if (row.xrefclassuid != null)
                    xrefName = uidClassNames.get(row.xrefclassuid); //Main.this.uid2ClassName.getProperty(row.xrefclassuid);
                if (xrefName != null) {
                    sb.append(String.format(bitsetXrefCommentTemplate, htmlize((row.description==null?"":row.description+", ")),xrefName));
                    sb.append(String.format(bitsetTemplate16, createEnumName(row.name), row.bitposition, row.length, xrefName));
                }
                else {
                    if(row.description != null)
                        sb.append(String.format(bitsetCommentTemplate, (htmlize(row.description))));
                    sb.append(String.format(bitsetTemplate15, createEnumName(row.name), row.bitposition, row.length));
                }
            });
            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");

            sb.append(String.format(bitsetTemplate2, clsName, el.size, clsName, clsName, clsName, clsName, clsName));

            // save file
            File target = new File(outputDirectory, clsName + ".java");
            FileWriter fw;
            try {
                target.createNewFile();
                fw = new FileWriter(target);
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }

        Set<String> enumNames = new HashSet<>();

        private void writeOutEnum(EnumElem el)
        {
            String clsName = uidClassNames.get(el.uid); //Main.this.uid2ClassName.getProperty(el.uid);
            if (clsName == null)
                return;
            
        /*    if(Main.this.uidDoNotGenerate.contains(el.uid)) {
                System.out.println("Not generating "+clsName);
                return;
            }
        */
            Properties aliasNames = null;
            if(el.uid.equals("4"))
              aliasNames = uid4aliases;
            
            StringBuilder sb = new StringBuilder();

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
            if(el.footnote == null)
              sb.append(String.format(enumTemplate1,             packageName, specTitleDate,  "UID " + el.uid, el.size, el.name, clsName, additionalInterface));
            else
              sb.append(String.format(enumTemplate1WithFootnote, packageName, specTitleDate,  "UID " + el.uid, el.size, el.name, el.footnote, clsName, additionalInterface));

            enumNames.clear();
            // enum section
            if (el.elems.isEmpty())
                sb.append(String.format(enumTemplate2, "NOT_SPECIFIED", "0", "undefined by SISO spec"));
            else{
                Properties aliases = aliasNames;
                el.elems.forEach((row) -> {                    
                    // Check for aliases
                    if(aliases != null && aliases.getProperty(row.value)!=null)
                      writeOneEnum(sb,row,aliases.getProperty(row.value));
                    else {
                      String enumName = createEnumName(row.description);
                      writeOneEnum(sb, row, enumName);
                    }
                  /*  if(row.xrefclassuid != null)
                        xrefName=uidClassNames.get(row.xrefclassuid);
                    
                    if(xrefName == null) {
                        String nm=null;
                        sb.append(String.format(enumFootnoteCommentTemplate, htmlize(row.description)+( row.footnote==null?"":", "+htmlize(row.footnote))));
                        sb.append(String.format(enumTemplate2, enumName, row.value, row.description.replace('"', '\'')));
                        if(aliases != null && aliases.get(row.value) != null) {
                           sb.append(String.format(enumFootnoteCommentTemplate, htmlize(row.description)+( row.footnote==null?"":", "+htmlize(row.footnote))));
                           sb.append(String.format(enumTemplate2, nm = enumName, row.value, row.description.replace('"', '\'')));
                        }
                    }
                    else {
                        sb.append(String.format(enumCommentTemplate,xrefName));
                        sb.append(String.format(enumTemplate21, createEnumName(row.description), row.value, row.description.replace('"', '\''),xrefName));
                    }*/

                });
            }
            if (el.elems.size() > 0)
                sb.setLength(sb.length() - 2);
            sb.append(";\n");
            
            if (el.size == null)
                el.size = "8";

            sb.append(String.format(enumTemplate25, clsName, el.size, clsName, clsName, clsName, clsName));

            // footer section
            // Many enums come in with smaller bit widths or in-between bitwidths;  Leave handling the odd balls up to the user 
            // but figure out the smallest primitive size needed to hold it.
            int sz = Integer.parseInt(el.size);
            if(sz <= 8)
               sb.append(String.format(enumTemplate3_8, clsName, clsName, clsName));
            else if(sz <= 16)
               sb.append(String.format(enumTemplate3_16, clsName, clsName, clsName));
            else
               sb.append(String.format(enumTemplate3_32, clsName, clsName, clsName));

            // save file
            File target = new File(outputDirectory, clsName + ".java");
            FileWriter fw;
            try {
                target.createNewFile();
                fw = new FileWriter(target);
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            }
            catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
        private String htmlize(String s)
        {
            return s.replace("&","and");
        }
        
      private void writeOneEnum(StringBuilder sb, EnumRowElem row, String enumName)
      {
        String xrefName = null;
        if (row.xrefclassuid != null)
          xrefName = uidClassNames.get(row.xrefclassuid);

        if (xrefName == null) {
          sb.append(String.format(enumFootnoteCommentTemplate, htmlize(row.description) + (row.footnote == null ? "" : ", " + htmlize(row.footnote))));
          sb.append(String.format(enumTemplate2, enumName, row.value, row.description.replace('"', '\'')));

        }
        else {
          sb.append(String.format(enumCommentTemplate, xrefName));
          sb.append(String.format(enumTemplate21, createEnumName(row.description), row.value, row.description.replace('"', '\''), xrefName));
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
                r = "$" + r;

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
            new Main(args[0], args[1], args[2]).run();
        }
        catch (SAXException | IOException | ParserConfigurationException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
