/**
 * Copyright (c) 2008-2021, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */

package edu.nps.moves.dis7.source.generator.entityTypes;

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
import org.apache.commons.io.FileUtils;

/**
 * GenerateOpenDis7JavaPackages.java created on Aug 6, 2019 
 MOVES Institute, Naval Postgraduate School (NPS), Monterey California USA https://www.nps.edu
 *
 * @author Don McGregor, Mike Bailey and Don Brutzman
 * @version $Id$
 */
public class GenerateJammers
{
    // set defaults to allow direct run
    private        File   outputDirectory;
    private static String outputDirectoryPath = "src-generated/java/edu/nps/moves/dis7/jammers"; // default
    private static String         packageName =                    "edu.nps.moves.dis7.jammers";
    private static String            language = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_LANGUAGE;
    private static String         sisoXmlFile = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_SISO_XML_FILE;
    private static String       sisoSpecificationTitleDate = "";

    String jammertechniqueTemplate;

    class DataPkt
    {
      String pkg;
      File directory;
      StringBuilder sb;
      String clsNm;
    }
    
    private String        packageInfoPath;
    private File          packageInfoFile;
    private StringBuilder packageInfoBuilder;

  /** Constructor for GenerateJammers
     * @param xmlFile sisoXmlFile
     * @param outputDir outputDirectoryPath
     * @param packageName key to package name for jammer */
  public GenerateJammers(String xmlFile, String outputDir, String packageName)
  {
        if (!xmlFile.isEmpty())
             sisoXmlFile = xmlFile;
        if (!outputDir.isEmpty())
            outputDirectoryPath = outputDir;
        if (!packageName.isEmpty())
           GenerateJammers.packageName = packageName;
        System.out.println (GenerateJammers.class.getName());
        System.out.println ("              xmlFile=" + sisoXmlFile);
        System.out.println ("          packageName=" + GenerateJammers.packageName);
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
            packageInfoBuilder.append(" * Infrastructure classes for ").append(sisoSpecificationTitleDate).append(" enumerations supporting <a href=\"https://github.com/open-dis/open-dis7-java\" target=\"open-dis7-java\">open-dis7-java</a> library.\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append(" * <p> Online: NPS <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500\" target=\"MV3500\">MV3500 Networked Simulation course</a> \n");
            packageInfoBuilder.append(" * links to <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/specifications/README.md\" target=\"README.MV3500\">IEEE and SISO specification references</a> of interest. </p>\n");
            packageInfoBuilder.append(" * <ul> <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&EntryId=46172\" target=\"SISO-REF-010\" >SISO-REF-010-2020 Reference for Enumerations for Simulation Interoperability</a> </li> \n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&EntryId=47284\" target=\"SISO-REF-10.1\">SISO-REF-10.1-2019 Reference for Enumerations for Simulation, Operations Manual</a></li> </ul>\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append(" * @see java.lang.Package\n");
            packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful\">https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful</a>\n");
            packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java\">https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java</a>\n");
            packageInfoBuilder.append(" *").append("/\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append("package edu.nps.moves.dis7.jammers;\n");

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

    //System.out.println("Generating jammers: ");
    MyHandler handler = new MyHandler();
    factory.newSAXParser().parse(new File(sisoXmlFile), handler);
    System.out.println (GenerateJammers.class.getName() + " complete."); // TODO  + handler.enums.size() + " enums created.");
  }

  private void loadTemplates()
  {
    try {
      jammertechniqueTemplate = loadOneTemplate("jammertechnique.txt");
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private String loadOneTemplate(String s) throws Exception
  {
    return new String(Files.readAllBytes(Paths.get(getClass().getResource(s).toURI())));
  }

  class DescriptionElem
  {
    String description;
    String value;

    String pkgFromDescription;
    String enumFromDescription;
    List<DescriptionElem> children = new ArrayList<>();
  }

  class JammerKindElem extends DescriptionElem
  {
    List<DescriptionElem> categories = new ArrayList<>();
  }

  class JammerCategoryElem extends DescriptionElem
  {
    JammerKindElem parent;
  }

  class JammerSubCategoryElem extends DescriptionElem
  {
    JammerCategoryElem parent;
  }

  class JammerSpecificElem extends DescriptionElem
  {
    JammerSubCategoryElem parent;
  }

  /** XML handler for recursively reading information and autogenerating code, namely an
     * inner class that handles the SAX parsing of the XML file. This is relatively simple, if
     * a little verbose. Basically we just create the appropriate objects as we come across the
     * XML elements in the file.
     */
  public class MyHandler extends DefaultHandler
  {
    JammerKindElem currentKind;
    JammerCategoryElem currentCategory;
    JammerSubCategoryElem currentSubCategory;
    JammerSpecificElem currentSpecific;
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

        case "jammer_kind":
          currentKind = new JammerKindElem();
          currentKind.value = attributes.getValue("value");
          currentKind.description = attributes.getValue("description");
          if (currentKind.description != null)
              currentKind.description = currentKind.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
          break;

        case "jammer_category":
          if (currentKind == null)
            break;

          currentCategory = new JammerCategoryElem();
          currentCategory.value = attributes.getValue("value");
          currentCategory.description = attributes.getValue("description");
          if (currentCategory.description != null)
              currentCategory.description = currentCategory.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
          currentCategory.parent = currentKind;
          setUniquePkgAndEmnum(currentCategory, currentKind.categories);
          currentKind.categories.add(currentCategory);
          break;

        case "jammer_subcategory":
          if (currentCategory == null)
            break;
          currentSubCategory = new JammerSubCategoryElem();
          currentSubCategory.value = attributes.getValue("value");
          currentSubCategory.description = attributes.getValue("description");
          if (currentSubCategory.description != null)
              currentSubCategory.description = currentSubCategory.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
          currentSubCategory.parent = currentCategory;
          setUniquePkgAndEmnum(currentSubCategory, currentCategory.children);
          currentCategory.children.add(currentSubCategory);
          break;

        case "jammer_specific":
          if (currentSubCategory == null)
            break;
          currentSpecific = new JammerSpecificElem();
          currentSpecific.value = attributes.getValue("value");
          currentSpecific.description = attributes.getValue("description");
          if (currentSpecific.description != null)
              currentSpecific.description = currentSpecific.description.replaceAll("—","-").replaceAll("–","-").replaceAll("\"", "").replaceAll("\'", "");
          currentSpecific.parent = currentSubCategory;
          setUniquePkgAndEmnum(currentSpecific, currentSubCategory.children);
          currentSubCategory.children.add(currentSpecific);
          break;

        default:
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {
      try {
        switch (qName) {

          case "jammer_kind":
            if (currentKind != null)
              writeKindFile(null);
            currentKind = null;
            break;

          case "jammer_category":
            if (currentCategory != null) //might have been deprecated
              writeCategoryFile(null);
            currentCategory = null;
            break;

          case "jammer_subcategory":
            if (currentSubCategory != null) // might have been deprecated
              writeSubCategoryFile(null);
            currentSubCategory = null;
            break;

          case "jammer_specific":
            if (currentSpecific != null) // might have been deprecated)
              writeSpecificFile(null);
            currentSpecific = null;
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

    private void saveJammerFile(DataPkt data)
    {
        data.sb.append("    }\n}\n");
        saveFile(data.directory, data.clsNm + ".java", data.sb.toString());
      
        packageInfoPath = data.directory + "/" + "package-info.java";
        packageInfoFile = new File(packageInfoPath);

        if (!packageInfoFile.exists()) // write package-info.java during first time through
        {
            FileWriter packageInfoFileWriter;
            try {
                packageInfoFile.createNewFile();
                packageInfoFileWriter = new FileWriter(packageInfoFile, StandardCharsets.UTF_8);
                packageInfoBuilder = new StringBuilder();
                packageInfoBuilder.append("/**\n");
                packageInfoBuilder.append(" * Infrastructure classes for ").append(sisoSpecificationTitleDate).append(" enumerations supporting <a href=\"https://github.com/open-dis/open-dis7-java\" target=\"open-dis7-java\">open-dis7-java</a> library.\n");
                packageInfoBuilder.append("\n");
                packageInfoBuilder.append(" * <p> Online: NPS <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500\" target=\"MV3500\">MV3500 Networked Simulation course</a> \n");
                packageInfoBuilder.append(" * links to <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/specifications/README.md\" target=\"README.MV3500\">IEEE and SISO specification references</a> of interest. </p>\n");
                packageInfoBuilder.append(" * <ul> <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&EntryId=46172\" target=\"SISO-REF-010\" >SISO-REF-010-2020 Reference for Enumerations for Simulation Interoperability</a> </li> \n");
                packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostds.org/DigitalLibrary.aspx?Command=Core_Download&EntryId=47284\" target=\"SISO-REF-10.1\">SISO-REF-10.1-2019 Reference for Enumerations for Simulation, Operations Manual</a></li> </ul>\n");
                packageInfoBuilder.append("\n");
                packageInfoBuilder.append(" * @see java.lang.Package\n");
                packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful\">https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful</a>\n");
                packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java\">https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java</a>\n");
                packageInfoBuilder.append(" */\n");
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

    private void appendCommonStatements(DataPkt data)
    {
      String contents = String.format(jammertechniqueTemplate, data.pkg,
        sisoSpecificationTitleDate, "284",data.clsNm,data.clsNm);
      data.sb.append(contents);
    }

    private void appendStatement(DescriptionElem elem, String typ, StringBuilder sb)
    {
      String template = "        set" + typ + "((byte)%s); // %s\n";
      sb.append(String.format(template, elem.value, elem.description));
    }

    private void writeKindFile(DataPkt d)
    {
      DataPkt data = d;
      if (data == null) {
        data = buildJammerCommon(fixName(currentKind), currentKind);
        appendStatement(currentKind, "Kind", data.sb);
        saveJammerFile(data);
      }
    }

    private void writeCategoryFile(DataPkt d)
    {
      DataPkt data = d;
      if (data == null) {
        data = buildJammerCommon(fixName(currentCategory), currentCategory);
      }
      appendStatement(currentKind, "Kind", data.sb);
      appendStatement(currentCategory, "Category", data.sb);

      if (d == null) {
        saveJammerFile(data);
      }
    }

    private void writeSubCategoryFile(DataPkt d)
    {
      DataPkt data = d;
      if (data == null) {
        data = buildJammerCommon(fixName(currentSubCategory), currentSubCategory);
      }
      appendStatement(currentKind, "Kind", data.sb);
      appendStatement(currentCategory, "Category", data.sb);
      appendStatement(currentSubCategory, "SubCategory", data.sb);

      if (d == null)
        saveJammerFile(data);
    }

    private void writeSpecificFile(DataPkt d) throws Exception
    {
      DataPkt data = d;
      if (data == null) {
        data = buildJammerCommon(fixName(currentSpecific), currentSpecific);
      }
      appendStatement(currentKind, "Kind", data.sb);
      appendStatement(currentCategory, "Category", data.sb);
      appendStatement(currentSubCategory, "SubCategory", data.sb);
      appendStatement(currentSpecific, "Specific", data.sb);

      if (d == null)
        saveJammerFile(data);
    }

    private DataPkt buildJammerCommon(String fixedName, DescriptionElem elem)
    {
      try {
        DataPkt data = new DataPkt();
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
        data.clsNm = fixedName;
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

  private void setUniquePkgAndEmnum(DescriptionElem elem, List<DescriptionElem> lis)
  {
    String mangledDescription = fixName(elem);
    mangledDescription = makeUnique(mangledDescription, lis);
    elem.pkgFromDescription = mangledDescription;
    elem.enumFromDescription = mangledDescription.toUpperCase();
  }

  private String makeUnique(String s, List<DescriptionElem> lis)
  {
    String news = s;
    for (int i = 1; i < 1000; i++) {
      outer:
      {
        for (DescriptionElem hd : lis) {
          if (hd.pkgFromDescription.equalsIgnoreCase(news))
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
    if (elem instanceof JammerKindElem)
      buildPackagePath((JammerKindElem) elem, sb);
    else if (elem instanceof JammerCategoryElem)
      buildPackagePath((JammerCategoryElem) elem, sb);
    else if (elem instanceof JammerSubCategoryElem)
      buildPackagePath((JammerSubCategoryElem) elem, sb);
    else if (elem instanceof JammerSpecificElem)
      buildPackagePath((JammerSpecificElem) elem, sb);
  }

  private void buildPackagePath(JammerKindElem kind, StringBuilder sb) throws Exception
  {
    sb.append(fixName(kind.description));
    sb.append("/");
  }

  private void buildPackagePath(JammerCategoryElem cat, StringBuilder sb) throws Exception
  {
    buildPackagePath(cat.parent, sb);
    sb.append(fixName(cat.description));
    sb.append("/");
  }

  private void buildPackagePath(JammerSubCategoryElem sub, StringBuilder sb) throws Exception
  {
    buildPackagePath(sub.parent, sb);
    sb.append(fixName(sub.description));
    sb.append("/");
  }

  private void buildPackagePath(JammerSpecificElem spec, StringBuilder sb) throws Exception
  {
    buildPackagePath(spec.parent, sb);
    sb.append(fixName(spec.description));
    sb.append("/");
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

  private String legalJavaDoc(String s)
  {
        s = s.replace("<", "&lt;");
        s = s.replace(">", "&gt;");
        s = s.replace("&", "&amp;");
    return s;
  }

  private String tryParent(DescriptionElem elem)
  {
    if (elem instanceof JammerSpecificElem)
      return fixName(((JammerSpecificElem) elem).parent.description);

    if (elem instanceof JammerSubCategoryElem)
      return fixName(((JammerSubCategoryElem) elem).parent.description);

    if (elem instanceof JammerCategoryElem)
      return fixName(((JammerCategoryElem) elem).parent.description);

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

  /** Command-line invocation (CLI)
    * @param args command-line arguments */
  public static void main(String[] args)
  {
    try {
        if  (args.length == 0)
             new GenerateJammers("",      "",      ""     ).run(); // use defaults
        else new GenerateJammers(args[0], args[1], args[2]).run();
    }
    catch (SAXException | IOException | ParserConfigurationException ex) {
      System.err.println(ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
    }
  }
}
