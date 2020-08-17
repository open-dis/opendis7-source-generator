/**
 * Copyright (c) 2008-2020, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */

package edu.nps.moves.dis7.source.generator.entitytypes;

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
 * Main.java created on Aug 6, 2019 MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class ObjectTypeMain
{
  private final File outputDirectory;
  private final String basePackageName;
  private final String xmlPath;

  String objectTypeTemplate;

  class DataPkt
  {
    String pkg;
    File directory;
    StringBuilder sb;
    String clsNm;
  }

  public ObjectTypeMain(String xmlPath, String outputDir, String packageName)
  {
    this.basePackageName = packageName;
    this.xmlPath = xmlPath;
    this.outputDirectory = new File(outputDir);
  }

  private void run() throws SAXException, IOException, ParserConfigurationException
  {
    outputDirectory.mkdirs();
    FileUtils.cleanDirectory(outputDirectory);

    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(false);
    factory.setNamespaceAware(true);
    factory.setXIncludeAware(true);

    loadTemplates();

    //System.out.println("Generating jammers: ");
    factory.newSAXParser().parse(new File(xmlPath), new MyHandler());
  }

  private void loadTemplates()
  {
    try {
      objectTypeTemplate = loadOneTemplate("objecttype.txt");
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

    String pkgFromDescription;
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

  public class MyHandler extends DefaultHandler
  {
    CotElem currentCot;
    ObjectElem currentObject;
    CategoryElem currentCategory;
    SubCategoryElem currentSubCategory;
    String specTitleDate = "";

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

        case "cot":
          String uid = attributes.getValue("uid");
          if(uid.equals("226") || uid.equals("227") || uid.equals("228")) {
            currentCot = new CotElem();
            currentCot.uid = uid;
            currentCot.description = specialCaseObjectTypeName(attributes.getValue("name"));  // not an error
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
    
    private void saveFile(DataPkt data)
    {
      data.sb.append("    }\n}\n");
      ObjectTypeMain.this.saveFile(data.directory, data.clsNm + ".java", data.sb.toString());
    }

    private void appendCommonStatements(DataPkt data)
    {
      String contents = String.format(objectTypeTemplate, data.pkg,
        specTitleDate, currentCot.uid,data.clsNm,data.clsNm);
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
  
    private void writeCotFile(DataPkt d)
    {
      DataPkt data = d;
      if (data == null) {
        data = buildObjectTypeCommon(fixName(currentCot), currentCot);
        appendDomainStatement(currentCot, "Domain", data.sb); // not an error
        saveFile(data);
      }
    }

    private void writeObjectFile(DataPkt d)
    {
      DataPkt data = d;
      if (data == null) {
        data = buildObjectTypeCommon(fixName(currentObject), currentObject);
      }
      appendDomainStatement(currentCot, "Domain", data.sb);  // not an error
      appendKindStatement(currentObject, "ObjectKind", data.sb);

      if (d == null) {
        saveFile(data);
      }
    }

    private void writeCategoryFile(DataPkt d)
    {
      DataPkt data = d;
      if (data == null) {
        data = buildObjectTypeCommon(fixName(currentCategory), currentCategory);
      }
      appendDomainStatement(currentCot, "Domain", data.sb); // not an error
      appendKindStatement(currentObject, "ObjectKind", data.sb);
      appendCategoryValueStatement(currentCategory, "Category", data.sb);

      if (d == null)
        saveFile(data);
    }

    private void writeSubCategoryFile(DataPkt d) throws Exception
    {
      DataPkt data = d;
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

    private DataPkt buildObjectTypeCommon(String fixedName, DescriptionElem elem)
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

        String pkg = basePackageName + "." + pathToPackage(data.sb.toString());
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

  private void setUniquePkgAndEmail(DescriptionElem elem, List<DescriptionElem> lis)
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
    sb.append(fixName(sub.description));
    sb.append("/");
    //return sb.toString();
  }

  private String pathToPackage(String s)
  {
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
    String r = fixName(elem.description);
    if (isNumeric(r) | isNumeric(r.substring(1))) {
      r = makeNonNumeric(elem, r);
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
    r = r.replaceAll(" ", "");

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

    // If there's nothing there, put in something:
    if (r.isEmpty() || r.equals("_"))
      r = "undef";

    // Java identifier can't start with digit
    if (Character.isDigit(r.charAt(0)))
      r = "_" + r;
    //System.out.println("In: "+s+" out: "+r);
    return r;
  }

  public static void main(String[] args)
  {
    try {
      if(args == null || args.length != 3)
        new ObjectTypeMain(
          "xml/SISO/SISO-REF-010.xml",
          "src-generated/java/edu/nps/moves/dis7/objecttypes",
          "edu.nps.moves.dis7.objecttypes"
          ).run();
      else
        new ObjectTypeMain(args[0], args[1], args[2]).run();
    }
    catch (SAXException | IOException | ParserConfigurationException ex) {
      System.err.println(ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
    }
  }
}
