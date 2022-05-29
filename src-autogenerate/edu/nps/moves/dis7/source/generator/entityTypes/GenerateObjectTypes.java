/**
 * Copyright (c) 2008-2022, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */

package edu.nps.moves.dis7.source.generator.entityTypes;

import edu.nps.moves.dis7.source.generator.enumerations.GenerateEnumerations;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * GenerateOpenDis7JavaPackages creates source code from SISO enumeration definitions.
 * Created on Aug 6, 2019 by MOVES Institute, Naval Postgraduate School (NPS), Monterey California USA https://www.nps.edu
 *
 * @author Don McGregor, Mike Bailey and Don Brutzman
 * @version $Id$by 
 */
public class GenerateObjectTypes
{
    // set defaults to allow direct run
    private        File   outputDirectory;
    private static String outputDirectoryPath = "src-generated/java/edu/nps/moves/dis7/objectTypes"; // default
    private static String         packageName =                    "edu.nps.moves.dis7.objectTypes"; // default
    private static String            language = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_LANGUAGE;
    private static String         sisoXmlFile = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_SISO_XML_FILE;
    private String sisoSpecificationTitleDate = "";

    String objecttypeTemplate;

  class TypeClassData
  {
    String pkg;
    File directory;
    StringBuilder sb;
    String className;
  }
    
    private String        packageInfoPath;
    private File          packageInfoFile;
    private StringBuilder packageInfoBuilder;

  /** Constructor for GenerateEntityTypes
     * @param xmlFile sisoXmlFile
     * @param outputDir outputDirectoryPath
     * @param packageName key to package name for object types */
  public GenerateObjectTypes(String xmlFile, String outputDir, String packageName)
  {
        if (!xmlFile.isEmpty())
             sisoXmlFile = xmlFile;
        if (!outputDir.isEmpty())
            outputDirectoryPath = outputDir;
        if (!packageName.isEmpty())
           this.packageName = packageName;
        System.out.println (GenerateObjectTypes.class.getName());
        System.out.println ("              xmlFile=" + sisoXmlFile);
        System.out.println ("          packageName=" + this.packageName);
        System.out.println ("  outputDirectoryPath=" + outputDirectoryPath);
        
        outputDirectory  = new File(outputDirectoryPath);
        outputDirectory.mkdirs();
//      FileUtils.cleanDirectory(outputDirectory); // do NOT clean directory, results can co-exist with other classes

        System.out.println ("actual directory path=" + outputDirectory.getAbsolutePath());
        
        packageInfoPath = outputDirectoryPath + "/" + "package-info.java";
        packageInfoFile = new File(packageInfoPath);
        
        FileWriter packageInfoFileWriter;
        try {
            packageInfoFile.createNewFile();
            packageInfoFileWriter = new FileWriter(packageInfoFile, StandardCharsets.UTF_8);
            packageInfoBuilder = new StringBuilder();
            packageInfoBuilder.append("/**\n");
            packageInfoBuilder.append(" * Object type infrastructure classes for ").append(sisoSpecificationTitleDate).append(" enumerations.\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append(" * <p> Online references: </p>\n");
            packageInfoBuilder.append(" * <ul>\n");
            packageInfoBuilder.append(" *      <li> GitHub <a href=\"https://github.com/open-dis/open-dis7-java\" target=\"_blank\">open-dis7-java library</a> </li> \n");
            packageInfoBuilder.append(" *      <li> NPS <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/examples/src/OpenDis7Examples\" target=\"MV3500\">MV3500 Distributed Simulation Fundamentals course examples</a> </li> \n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/specifications/README.md\" target=\"README.MV3500\">IEEE and SISO specification references</a> of interest</li> \n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&amp;EntryId=46172\" target=\"SISO-REF-010\" >SISO-REF-010-2022 Reference for Enumerations for Simulation Interoperability</a> </li> \n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&amp;EntryId=47284\" target=\"SISO-REF-10.1\">SISO-REF-10.1-2019 Reference for Enumerations for Simulation, Operations Manual</a></li>\n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://savage.nps.edu/open-dis7-java/javadoc\" target=\"_blank\">open-dis7 Javadoc</a>, <a href=\"https://savage.nps.edu/open-dis7-java/xml/DIS_7_2012.autogenerated.xsd\" target=\"_blank\">open-dis7 XML Schema</a>and <a href=\"https://savage.nps.edu/open-dis7-java/xml/SchemaDocumentation\" target=\"_blank\">open-dis7 XML Schema documentation</a></li> </ul>\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append(" * @see java.lang.Package\n");
            packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful\">https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful</a>\n");
            packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java\">https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java</a>\n");
            packageInfoBuilder.append(" */\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append("package edu.nps.moves.dis7.objectTypes;\n");

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
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    factory.setXIncludeAware(true);

    loadTemplates();

    System.out.println("Generating object types: ");
    MyHandler handler = new MyHandler();
    factory.newSAXParser().parse(new File(sisoXmlFile), handler);
    System.out.println (GenerateObjectTypes.class.getName() + " complete."); // TODO  + handler.enums.size() + " enums created.");
  }

  private void loadTemplates()
  {
    try {
      objecttypeTemplate = loadOneTemplate("objecttype.txt");
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private String loadOneTemplate(String s) throws Exception
  {
    return new String(Files.readAllBytes(Paths.get(getClass().getResource(s).toURI())), StandardCharsets.UTF_8.name());
  }

  class DescriptionElem
  {
    String description;

    String packageFromDescription;
    String enumFromDescription;
    ArrayList<DescriptionElem> children = new ArrayList<>();
  }

  class CotElem extends DescriptionElem
  {
    List<DescriptionElem> objects = new ArrayList<>();
    String uid;
    String domain;
  }

  class ObjectElem extends DescriptionElem
  {
    CotElem parent;
    String kind;
    String domain;
  }

  class CategoryElem extends DescriptionElem
  {
    ObjectElem parent;
    String value;
  }

  class SubCategoryElem extends DescriptionElem
  {
    CategoryElem parent;
    String value;
  }

  /** XML handler for recursively reading information and autogenerating code, namely an
     * inner class that handles the SAX parsing of the XML file. This is relatively simple, if
     * a little verbose. Basically we just create the appropriate objects as we come across the
     * XML elements in the file.
     */
  public class MyHandler extends DefaultHandler
  {
    CotElem currentCot;
    ObjectElem currentObject;
    CategoryElem currentCategory;
    SubCategoryElem currentSubCategory;
    int filesWrittenCount = 0;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {
      if (qName.equalsIgnoreCase("revision")) {
        if (sisoSpecificationTitleDate.length() <= 0) // only want the first/latest
            sisoSpecificationTitleDate = legalJavaDoc(attributes.getValue("title") + " (" + attributes.getValue("date") + ")");
        return;
      }

      if (attributes.getValue("deprecated") != null)
        return;

      switch (qName) {

        case "cot":
          String uid = attributes.getValue("uid");
          if(uid.equals("226") || uid.equals("227") || uid.equals("228")) {
            currentCot = new CotElem();
            currentCot.uid = uid;
            currentCot.description = fixName(specialCaseObjectTypeName(attributes.getValue("name")));  // not an error
          }
          else
            currentCot = null;
          break;

        case "object":
          if (currentCot == null)
            break;

          currentObject = new ObjectElem();
          currentObject.kind = attributes.getValue("kind");
          currentObject.domain = attributes.getValue("domain");
          currentObject.description = attributes.getValue("description");
          if (currentObject.description != null)
              currentObject.description = currentObject.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
          currentObject.parent = currentCot;
          currentCot.domain = currentObject.domain;
          setUniquePkgAndEmail(currentObject, currentCot.objects);
          currentCot.objects.add(currentObject);
          break;

        case "category":
          if (currentObject == null)
            break;
          currentCategory = new CategoryElem();
          currentCategory.value = attributes.getValue("value");
          currentCategory.description = attributes.getValue("description");
          if (currentCategory.description != null)
              currentCategory.description = currentCategory.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
          currentCategory.parent = currentObject;
          setUniquePkgAndEmail(currentCategory, currentObject.children);
          currentObject.children.add(currentCategory);
          break;

        case "subcategory":
          if (currentCategory == null)
            break;
          currentSubCategory = new SubCategoryElem();
          currentSubCategory.value = attributes.getValue("value");
          currentSubCategory.description = attributes.getValue("description");
          if (currentSubCategory.description != null)
              currentSubCategory.description = currentSubCategory.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
          currentSubCategory.parent = currentCategory;
          setUniquePkgAndEmail(currentSubCategory, currentCategory.children);
          currentCategory.children.add(currentSubCategory);
          break;

        default:
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
      try {
        switch (qName) {

          case "cot":
            if (currentCot != null)
              writeCotFile(null);
            currentCot = null;
            break;

          case "object":
            if (currentObject != null) //might have been deprecated
              writeObjectFile(null);
            currentObject = null;
            break;

          case "category":
            if (currentCategory != null) // might have been deprecated
              writeCategoryFile(null);
            currentCategory = null;
            break;

          case "subcategory":
            if (currentSubCategory != null) // might have been deprecated)
              writeSubCategoryFile(null);
            currentSubCategory = null;
            break;

          default:
        }
      }
      catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }

    @Override
    public void endDocument() throws SAXException
    {
    }
    
    private String specialCaseObjectTypeName(String s)
    {
        switch(s.toLowerCase()) {
            case "object types-areal object":
                return "ArealObject";
            case "object types-linear object":
                return "LinearObject";
            case "object types-point object":
                return "PointObject";
            default:
                return s;
        }
    }
    
    private void saveFile(TypeClassData data)
    {
        data.sb.append("    }\n}\n");
        GenerateObjectTypes.this.saveFile(data.directory, data.className + ".java", data.sb.toString());

        packageInfoPath = data.directory + "/" + "package-info.java";
        File   packageInfoFile = new File(packageInfoPath);
      
        if (!packageInfoFile.exists()) // write package-info.java during first time through
        {
            FileWriter packageInfoFileWriter;
            try {
                packageInfoFile.createNewFile();
                packageInfoFileWriter = new FileWriter(packageInfoFile, StandardCharsets.UTF_8);
                packageInfoBuilder = new StringBuilder();
                packageInfoBuilder.append("/**\n");
                packageInfoBuilder.append(" * Object type infrastructure classes for ").append(sisoSpecificationTitleDate).append(" enumerations.\n");
                packageInfoBuilder.append("\n");
                packageInfoBuilder.append(" * <p> Online references: </p>\n");
                packageInfoBuilder.append(" * <ul>\n");
                packageInfoBuilder.append(" *      <li> GitHub <a href=\"https://github.com/open-dis/open-dis7-java\" target=\"_blank\">open-dis7-java library</a> </li> \n");
                packageInfoBuilder.append(" *      <li> NPS <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/examples/src/OpenDis7Examples\" target=\"MV3500\">MV3500 Distributed Simulation Fundamentals course examples</a> </li> \n");
                packageInfoBuilder.append(" *      <li> <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/specifications/README.md\" target=\"README.MV3500\">IEEE and SISO specification references</a> of interest</li> \n");
                packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&amp;EntryId=46172\" target=\"SISO-REF-010\" >SISO-REF-010-2022 Reference for Enumerations for Simulation Interoperability</a> </li> \n");
                packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&amp;EntryId=47284\" target=\"SISO-REF-10.1\">SISO-REF-10.1-2019 Reference for Enumerations for Simulation, Operations Manual</a></li>\n");
                packageInfoBuilder.append(" *      <li> <a href=\"https://savage.nps.edu/open-dis7-java/javadoc\" target=\"_blank\">open-dis7 Javadoc</a>, <a href=\"https://savage.nps.edu/open-dis7-java/xml/DIS_7_2012.autogenerated.xsd\" target=\"_blank\">open-dis7 XML Schema</a>and <a href=\"https://savage.nps.edu/open-dis7-java/xml/SchemaDocumentation\" target=\"_blank\">open-dis7 XML Schema documentation</a></li> </ul>\n");
                packageInfoBuilder.append("\n");
                packageInfoBuilder.append(" * @see java.lang.Package\n");
                packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful\">https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful</a>\n");
                packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java\">https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java</a>\n");
                packageInfoBuilder.append(" */\n");
                packageInfoBuilder.append("// created by edu/nps/moves/dis7/source/generator/entityTypes/GenerateObjectTypes.java\n");
                packageInfoBuilder.append("\n");
                packageInfoBuilder.append("package ").append(data.pkg).append(";\n");

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
    }

    private void appendCommonStatements(TypeClassData data)
    {
      String contents = String.format(objecttypeTemplate, data.pkg,
        sisoSpecificationTitleDate, currentCot.uid,data.className,data.className);
      data.sb.append(contents);
    }

    private void appendCategoryValueStatement(CategoryElem elem, String typ, StringBuilder sb)
    {
      String template = "        set" + typ + "((byte)%s); // %s\n";
      sb.append(String.format(template, elem.value, elem.description));
    }
    private void appendSubCategoryValueStatement(SubCategoryElem elem, String typ, StringBuilder sb)
    {
      String template = "        set" + typ + "((byte)%s); // %s\n";
      sb.append(String.format(template, elem.value, elem.description));
    }
   
    private void appendKindStatement(ObjectElem elem, String typ, StringBuilder sb)
    {
       String template = "        set" + typ + "(ObjectKind.getEnumForValue(%s)); // %s\n";
       sb.append(String.format(template, elem.kind, elem.description));
    }
    
     private void appendDomainStatement(CotElem cot, String typ, StringBuilder sb)
    {
       String template = "        set" + typ + "(PlatformDomain.getEnumForValue(%s));\n";
       sb.append(String.format(template, cot.domain));
    }
  
    private void writeCotFile(TypeClassData d)
    {
      TypeClassData data = d;
      if (data == null) {
        data = buildObjectTypeCommon(fixName(currentCot), currentCot);
        appendDomainStatement(currentCot, "Domain", data.sb); // not an error
        saveFile(data);
      }
    }

    private void writeObjectFile(TypeClassData d)
    {
      TypeClassData data = d;
      if (data == null) {
        data = buildObjectTypeCommon(fixName(currentObject), currentObject);
      }
      appendDomainStatement(currentCot, "Domain", data.sb);  // not an error
      appendKindStatement(currentObject, "ObjectKind", data.sb);

      if (d == null) {
        saveFile(data);
      }
    }

    private void writeCategoryFile(TypeClassData d)
    {
      TypeClassData data = d;
      if (data == null) {
        data = buildObjectTypeCommon(fixName(currentCategory), currentCategory);
      }
      appendDomainStatement(currentCot, "Domain", data.sb); // not an error
      appendKindStatement(currentObject, "ObjectKind", data.sb);
      appendCategoryValueStatement(currentCategory, "Category", data.sb);

      if (d == null)
        saveFile(data);
    }

    private void writeSubCategoryFile(TypeClassData d) throws Exception
    {
      TypeClassData data = d;
      if (data == null) {
        data = buildObjectTypeCommon(fixName(currentSubCategory), currentSubCategory);
      }
      appendDomainStatement(currentCot, "Domain", data.sb); // not an error
      appendKindStatement(currentObject, "ObjectKind", data.sb);
      appendCategoryValueStatement(currentCategory, "Category", data.sb);
      appendSubCategoryValueStatement(currentSubCategory, "SubCategory", data.sb);

      if (d == null)
        saveFile(data);
    }

    private TypeClassData buildObjectTypeCommon(String fixedName, DescriptionElem elem)
    {
      try {
        TypeClassData data = new TypeClassData();
        data.sb = new StringBuilder();
        buildPackagePathAbstract(elem, data.sb);

        data.directory = new File(outputDirectory, data.sb.toString());
        data.directory.mkdirs();

        // Protect against duplicate class names
        int i = 1;
        while (new File(data.directory, fixedName + ".java").exists()) {
          fixedName = fixedName + i++;
        }

        String pkg = packageName + "." + pathToPackage(data.sb.toString());
        data.pkg = pkg;
        data.className = fixedName;
        data.sb.setLength(0);

        appendCommonStatements(data);
        return data;
      }
      catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  private void saveFile(File parentDir, String name, String contents)
  {
    // save file
    File target = new File(parentDir, name);
    try {
      target.createNewFile();
      try (FileWriter fw = new FileWriter(target, StandardCharsets.UTF_8)) {
        fw.write(contents);
        fw.flush();
      }
    }
    catch (IOException ex) {
      throw new RuntimeException("Error saving " + name + ": " + ex.getLocalizedMessage(), ex);
    }
  }

  private void setUniquePkgAndEmail(DescriptionElem elem, List<DescriptionElem> lis)
  {
    String mangledDescription = fixName(elem);
    mangledDescription = makeUnique(mangledDescription, lis);
    elem.packageFromDescription = mangledDescription;
    elem.enumFromDescription = mangledDescription.toUpperCase();
  }

  private String makeUnique(String s, List<DescriptionElem> lis)
  {
    String news = s;
    for (int i = 1; i < 1000; i++) {
      outer:
      {
        for (DescriptionElem hd : lis) {
          if (hd.packageFromDescription.equalsIgnoreCase(news))
            break outer;
        }
        return news;
      }
      news = s + i;
    }
    throw new RuntimeException("Problem generating unique name for " + s);
  }

  private void buildPackagePathAbstract(DescriptionElem elem, StringBuilder sb) throws Exception
  {
    if (elem instanceof CotElem)
      buildPackagePath((CotElem) elem, sb);
    else if (elem instanceof ObjectElem)
      buildPackagePath((ObjectElem) elem, sb);
    else if (elem instanceof CategoryElem)
      buildPackagePath((CategoryElem) elem, sb);
    else if (elem instanceof SubCategoryElem)
      buildPackagePath((SubCategoryElem) elem, sb);
  }

  private void buildPackagePath(CotElem cot, StringBuilder sb) throws Exception
  {
    sb.append(fixName(cot.description));
    sb.append("/");
  }

  private void buildPackagePath(ObjectElem obj, StringBuilder sb) throws Exception
  {
    buildPackagePath(obj.parent, sb);
    sb.append(fixName(obj.description));
    sb.append("/");
  }

  private void buildPackagePath(CategoryElem cat, StringBuilder sb) throws Exception
  {
    buildPackagePath(cat.parent, sb);
    sb.append(fixName(cat.description));
    sb.append("/");
  }

  private void buildPackagePath(SubCategoryElem sub, StringBuilder sb) throws Exception
  {
    buildPackagePath(sub.parent, sb);
//    sb.append(fixName(sub.description)); // TODO isn't this the name of the enumeration itself
//    sb.append("/");
    //return sb.toString();
  }

  private String pathToPackage(String s)
  {
    s = s.replace("_", "");
    s = s.replace("/", ".");
    if (s.endsWith("."))
      s = s.substring(0, s.length() - 1);
    return s;
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
/*
  private void printUnsupportedMessage(String elname, String eldesc, JammerCategoryElem cat)
  {
    StringBuilder bldr = new StringBuilder();
    bldr.append(cat.description);

    System.out.println("XML element " + elname + " {" + eldesc + "in " + bldr.toString() + " not supported");
  }

  private void printUnsupportedMessage(String elname, String eldesc, JammerSubCategoryElem sub)
  {
    StringBuilder bldr = new StringBuilder();
    bldr.append(sub.description);
    bldr.append("/");
    bldr.append(sub.parent.description);

    System.out.println("XML element " + elname + " {" + eldesc + "in " + bldr.toString() + " not supported");
  }
*/
  private String legalJavaDoc(String s)
  {
    s = s.replace("<", "&lt;");
    s = s.replace(">", "&gt;");
    s = s.replace("&", "&amp;");
    return s;
  }

  private String tryParent(DescriptionElem elem)
  {
    if (elem instanceof SubCategoryElem)
      return fixName(((SubCategoryElem) elem).parent.description);

    if (elem instanceof CategoryElem)
      return fixName(((CategoryElem) elem).parent.description);

    if (elem instanceof ObjectElem)
      return fixName(((ObjectElem) elem).parent.description);

    return null;
  }

  private String makeNonNumeric(DescriptionElem elem, String s)
  {
    if (s.startsWith("_"))
      s = s.substring(1);

    while (isNumeric(s)) {
      String p = tryParent(elem);
      if (p == null)
        return "_" + s;
      s = p + "_" + s;
    }
    return s;
  }

  private boolean isNumeric(String s)
  {
    try {
      int i = Integer.parseInt(s);
      return true;
    }
    catch (NumberFormatException t) {
      return false;
    }
  }

  private String fixName(DescriptionElem elem)
  {
    String r = new String();
    if ((elem != null) && (elem.description != null))
    {
        r = fixName(elem.description);
        if(!r.isEmpty() && (isNumeric(r) | isNumeric(r.substring(1))))
        {
          r = makeNonNumeric(elem,r);    
        }
        r = r.substring(0,1) + r.substring(1).replaceAll("_",""); // no underscore divider after first character
    }
    return r;
  }

/**
 * Naming conventions for cleaning up provided names
 * @param s enumeration string from XML data file
 * @return normalized name
 */
  private String fixName(String s)
  {
    String r = s.trim();
    
    if (r.isEmpty())
        return r;

    // Convert any of these chars to underbar (u2013 is a hyphen observed in source XML):
    r = r.trim().replaceAll(",", " ").replaceAll("—"," ").replaceAll("-", " ").replaceAll("\\."," ").replaceAll("&"," ")
                                     .replaceAll("/"," ").replaceAll("\"", " ").replaceAll("\'", " ").replaceAll("( )+"," ").replaceAll(" ", "_");
    r = r.substring(0,1) + r.substring(1).replaceAll("_",""); // no underscore divider after first character
            
    r = r.replaceAll("[\\h-/,\";:\\u2013]", "_");

    // Remove any of these chars (u2019 is an apostrophe observed in source XML):
    r = r.replaceAll("[\\[\\]()}{}'.#&\\u2019]", "");

    // Special case the plus character:
    r = r.replace("+", "PLUS");

    // Collapse all contiguous underbars:
    r = r.replaceAll("_{2,}", "_");

    r = r.replace("<=", "LTE");
    r = r.replace("<", "LT");
    r = r.replace(">=", "GTE");
    r = r.replace(">", "GT");
    r = r.replace("=", "EQ");
    r = r.replace("%", "pct");
    r = r.replaceAll("—","_").replaceAll("–","_").replaceAll("\"", "").replaceAll("\'", "");

    // Java identifier can't start with digit
    if (Character.isDigit(r.charAt(0)))
        r = "_" + r;
    
    if (r.contains("__"))
    {
        System.err.println("fixname contains multiple underscores: " + r);
        r = r.replaceAll("__", "_");
    }
    // If there's nothing there, put in something:
    if (r.trim().isEmpty() || r.equals("_"))
    {
      System.err.print("fixname: erroneous name \"" + s + "\"");
      r = "undefinedName";
      if (!s.equals(r))
           System.err.print( " converted to \"" + r + "\"");
      System.err.println();
    }
    //System.out.println("In: "+s+" out: "+r);
    return r;
  }    
  /**
     * Normalize string characters to create valid description
     * @param value of interest
     * @return normalized value
     */
    private String normalizeDescription(String value)
    {
        return GenerateEnumerations.normalizeDescription(value);
    }
    /**
     * Normalize string characters to create valid Java name.  Note that unmangled name typically remains available in the description
     * @param value of interest
     * @return normalized value
     */
    private String normalizeToken(String value)
    {
        return GenerateEnumerations.normalizeToken(value);
    }

  /** GenerateObjectTypes invocation, passing run-time arguments (if any)
     * @param args three configuration arguments, if defaults not used
     */
  public static void main(String[] args)
  {
    try {
        if  (args.length == 0)
             new GenerateObjectTypes("",      "",      ""     ).run(); // use defaults
        else new GenerateObjectTypes(args[0], args[1], args[2]).run();
    }
    catch (SAXException | IOException | ParserConfigurationException ex) {
      System.err.println(ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
    }
  }
}
