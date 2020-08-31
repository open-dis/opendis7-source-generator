/**
 * Copyright (c) 2008-2020, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
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
    private static String outputDirectoryPath = "src-generated/java/edu/nps/moves/dis7/entityTypes";
    private static String         packageName =                    "edu.nps.moves.dis7.entityTypes";
    private static String            language = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_LANGUAGE;
    private static String         sisoXmlFile = edu.nps.moves.dis7.source.generator.GenerateOpenDis7JavaPackages.DEFAULT_SISO_XML_FILE;

    String jammerTechniqueTemplate;

  class DataPkt
  {
    String pkg;
    File directory;
    StringBuilder sb;
    String clsNm;
  }

  public GenerateJammers(String xmlFile, String outputDir, String packageName)
  {
        if (!xmlFile.isEmpty())
             sisoXmlFile = xmlFile;
        if (!outputDir.isEmpty())
            outputDirectoryPath = outputDir;
        if (!packageName.isEmpty())
           this.packageName = packageName;
        System.out.println (GenerateJammers.class.getName());
        System.out.println ("              xmlFile=" + sisoXmlFile);
        System.out.println ("          packageName=" + this.packageName);
        System.out.println ("  outputDirectoryPath=" + outputDirectoryPath);
        
        outputDirectory  = new File(outputDirectoryPath);
        System.out.println ("actual directory path=" + outputDirectory.getAbsolutePath());
  }

  private void run() throws SAXException, IOException, ParserConfigurationException
  {
    outputDirectory.mkdirs();
//  FileUtils.cleanDirectory(outputDirectory); // do NOT clean directory, results can co-exist with other classes

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
      jammerTechniqueTemplate = loadOneTemplate("jammertechnique.txt");
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

  public class MyHandler extends DefaultHandler
  {
    JammerKindElem currentKind;
    JammerCategoryElem currentCategory;
    JammerSubCategoryElem currentSubCategory;
    JammerSpecificElem currentSpecific;
    String specTitleDate = "";
    int filesWrittenCount = 0;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {
      if (qName.equalsIgnoreCase("revision")) {
        if (specTitleDate.length() <= 0) // only want the first/latest
          specTitleDate = legalJavaDoc(attributes.getValue("title") + ", " + attributes.getValue("date"));
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
    }

    private void appendCommonStatements(DataPkt data)
    {
      String contents = String.format(jammerTechniqueTemplate, data.pkg,
        specTitleDate, "284",data.clsNm,data.clsNm);
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
