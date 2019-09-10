/**
 * Copyright (c) 2008-2019, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 */
package edu.nps.moves.dis7.source.generator.pdus;

import edu.nps.moves.dis7.source.generator.pdus.ClassAttribute.ClassAttributeType;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * A class that reads an XML file in a specific format, and spits out a Java, C#,
 * Objective-C, or C++ classes that do <i>most</i> of the work of the protocol.<p>
 *
 * This can rely on properties set in the XML file for the language. For example,
 * the Java element in the XML file can specify whether Hibernate or JAXB support
 * is included in the generated code.<p>
 * 
 * There is a huge risk of using variable names that have ambiguous meaning
 * here, as many of the terms such as "class" are also used
 * by java or c++. <p>
 *
 * In effect this is reading an XML file and creating an abstract description of
 * the protocol data units. That abstract description is written out as source
 * code in various languages, such as C++, Java, etc.
 *
 * @author DMcG and Mike Bailey
 */
public class Main 
{
    // Elements and attributes we look for in our XML pdu description files:
    public static final String INHERITSFROM = "inheritsFrom";
    public static final String ALIASFOR = "aliasFor";
    public static final String IMPLEMENTS = "implements";
    public static final String XMLROOTELEMENT = "xmlRootElement";
    public static final String SISOENUM = "sisoenum";
    public static final String SISOBITFIELD = "sisobitfield";
    public static final String CLASS = "class";
    public static final String ATTRIBUTE = "attribute";
    public static final String COMMENT = "comment";
    public static final String INITIALVALUE = "initialvalue";
    public static final String NAME = "name";
    public static final String CLASSREF = "classref";
    public static final String COUNTFIELDNAME = "countfieldname";
    public static final String TYPE = "type";
    public static final String DEFAULTVALUE = "defaultvalue";
    public static final String PRIMITIVE = "primitive";
    public static final String PRIMITIVELIST = "primitivelist";
    public static final String OBJECTLIST = "objectlist";
    public static final String LENGTH = "length";
    public static final String FIXEDLENGTH = "fixedlength";
    public static final String COULDBESTRING = "couldbestring";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String VALUE = "value";
    public static final String SERIALIZE = "serialize";
    public static final String HIDDEN = "hidden";
    public static final String SPECIALCASE = "specialCase";
    //public static final String DOMAINHOLDER = "domainHolder";
    public static final String PADTOBOUNDARY = "padtoboundary";
    public static final String ABSTRACT = "abstract";
    
    public static final String JAVA = "java";
    public static final String CPP = "cpp";
    public static final String OBJC = "objc"; 
    public static final String CSHARP = "csharp";
    public static final String JAVASCRIPT = "javascript";
    public static final String PYTHON = "python";
    
    // Pending to investigate:
    public static final String FLAG = "flag";
    public static final String MASK = "mask";
    public static final String STATICIVAR = "staticivar";
    
    /** Contains the database of all the classes described by the XML document */
    protected HashMap<String,GeneratedClass> generatedClassNames = new HashMap();
    
    /** The language types we generate */
    public enum LanguageType {CPP, JAVA, CSHARP, OBJECTIVEC, JAVASCRIPT, PYTHON }
    
    /** As we parse the XML document, this is the class we are currently working on */
    private GeneratedClass currentGeneratedClass = null;
    
    /** As we parse the XML document, this is the current attribute */
    private ClassAttribute currentClassAttribute = null;
    
    // The languages may have language-specific properties, such as libraries that they
    // depend on. Each language has its own set of properties.
    
    /** Java properties--imports, packages, etc. */
    Properties javaProperties = new Properties();
    
    /** C++ properties--includes, etc. */
    Properties cppProperties = new Properties();
    
    /** C# properties--using, namespace, etc. */
    Properties csharpProperties = new Properties();

    /** Objective-C properties */
    Properties objcProperties = new Properties();
    
    /** Javascript properties */
    Properties javascriptProperties = new Properties();

    /** source code generation options */
    Properties sourceGenerationOptions;
    
    /** source code generation for python */
    Properties pythonProperties = new Properties();
    
    /** Hash table of all the primitive types we can use (short, long, byte, etc.)*/
    private HashSet primitiveTypes = new HashSet();
    
    /** Directory in which the java class package is created */
    private String javaDirectory = null;
    
    /** Directory in which the C++ classes are created */
    private String cppDirectory = null;
    
    //PES
	/** Directory in which the C# classes are created */
	private String csharpDirectory = null;

    /** Director in which the objc classes are created */
    private String objcDirectory = null;
    
    private int classCount = 0;
   
    /*
     * Create a new collection of Java objects by reading an XML file; these
     * java objects can be used to generate code templates of any language,
     * once you write the translator.
     */
    public Main(String xmlDescriptionFileName, String languageToGenerate)
    {
        System.out.println(xmlDescriptionFileName+" "+languageToGenerate);
        try {
            DefaultHandler handler = new MyHandler();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);
            factory.setXIncludeAware(true);
            factory.newSAXParser().parse(new File(xmlDescriptionFileName), handler);
        }
        catch (IOException | ParserConfigurationException | SAXException e) {
            System.out.println(e);
        }

        // This does at least a cursory santity check on the data that has been read in from XML
        // It is far from complete.
        if (!this.astIsPlausible()) {
            System.out.println("The generated XML file is not internally consistent according to astIsPlausible()");
            System.out.println("There are one or more errors in the XML file. See output for details.");
            System.exit(1);
        }

        //System.out.println("putting java files in " + javaDirectory);
        switch (languageToGenerate.toLowerCase()) {
            case JAVA:
                JavaGenerator javaGenerator = new JavaGenerator(generatedClassNames, javaProperties);
                javaGenerator.writeClasses();
                break;

            // Use the same information to generate classes in C++
            case CPP:

                CppGenerator cppGenerator = new CppGenerator(generatedClassNames, cppProperties);
                cppGenerator.writeClasses();
                break;

            case CSHARP:
                // Create a new generator object to write out the source code for all the classes in csharp
                CsharpGenerator csharpGenerator = new CsharpGenerator(generatedClassNames, csharpProperties);
                csharpGenerator.writeClasses();
                break;

            case OBJC:

                // create a new generator object for objc
                ObjcGenerator objcGenerator = new ObjcGenerator(generatedClassNames, objcProperties);
                objcGenerator.writeClasses();
                break;

            case JAVASCRIPT:

                // create a new generator object for javascript
                JavascriptGenerator javascriptGenerator = new JavascriptGenerator(generatedClassNames, javascriptProperties);
                javascriptGenerator.writeClasses();
                break;

            case PYTHON:
                // create a new generator object for Python
                PythonGenerator pythonGenerator = new PythonGenerator(generatedClassNames, pythonProperties);
                pythonGenerator.writeClasses();
                break;
        }
    }

    /*
     * entry point. Pass in two arguments, the language you want to generate for and the XML file
     * that describes the classes
     */
    public static void main(String args[])
    {
        for(String s : args) System.out.println(s);
        if(args.length < 2 || args.length > 2)
        {
            System.out.println("Usage: Xmlpg xmlFile language"); 
            System.out.println("Allowable languages are java, cpp, objc, python, and csharp");
            System.exit(0);
        }
        
        Main.preflightArgs(args[0], args[1]);
        
	    new Main(args[0], args[1]);   
    }
    
    /*
     * Does a sanity check on the args passed in: does the XML file exist, and is
     * the language valid.
     */
    public static void preflightArgs(String xmlFile, String language)
    {
        try 
        {
            FileInputStream fis = new FileInputStream(xmlFile);
            fis.close();
            
            if(!(language.equalsIgnoreCase(JAVA) || language.equalsIgnoreCase(CPP) ||
               language.equalsIgnoreCase(OBJC) || language.equalsIgnoreCase(CSHARP) ||
               language.equalsIgnoreCase(JAVASCRIPT) || language.equalsIgnoreCase(PYTHON) ))
            {
                System.out.println("Not a valid language to generate. The options are java, cpp, objc, javascript, python and csharp");
                System.out.println("Usage: Xmlpg xmlFile language"); 
                System.exit(0);
            }
        }
        catch (FileNotFoundException fnfe) 
        {
            System.out.println("XML file " + xmlFile + " not found. Please check the path and try again");
            System.out.println("Usage: Xmlpg xmlFile language"); 
            System.exit(0);
        }
        catch(IOException e)
        {
            System.out.println("Problem with arguments to Xmlpg. Please check them.");
            System.out.println("Usage: Xmlpg xmlFile language"); 

            System.exit(0);
        }
    }
    
    /*
     * Returns true if the information parsed from the protocol description XML file
     * is "plausible" in addition to being syntactically correct. This means that:
     * <ul>
     * <li>references to other classes in the file are correct; if a class attribute
     * refers to a "EntityID", there's a class by that name elsewhere in the document;
     * <li> The primitive types belong to a list of known correct primitive types,
     * eg short, unsigned short, etc
     *
     * AST is a reference to "abstract syntax tree", which this really isn't, but
     * sort of is.
     * 10 Sep 2019, I think this method can be removed.  If a class is missing, it will
     * show up soon enough when the products are compiled.  It's had to be hacked to
     * get around some of the special cases.
     */
    private boolean astIsPlausible()
    { 
        // Create a list of primitive types we can use to check against

        primitiveTypes.add("uint8");
        primitiveTypes.add("uint16");
        primitiveTypes.add("uint32");
        primitiveTypes.add("uint64");
        primitiveTypes.add("int8");
        primitiveTypes.add("int16");
        primitiveTypes.add("int32");
        primitiveTypes.add("int64");
        primitiveTypes.add("float32");
        primitiveTypes.add("float64");
        
        // A temporary hack to get past the tests below
        generatedClassNames.put("JammerKind",new GeneratedClass());
        generatedClassNames.put("JammerCategory",new GeneratedClass());
        generatedClassNames.put("JammerSubCategory",new GeneratedClass());
        generatedClassNames.put("JammerSpecific",new GeneratedClass());
        generatedClassNames.put("EntityCapabilities", new GeneratedClass());
        generatedClassNames.put("PduStatus", new GeneratedClass());
        generatedClassNames.put("Domain", new GeneratedClass());
        
        // trip through every class specified
        for(GeneratedClass aClass : generatedClassNames.values()) {

            // Trip through every class attribute in this class and confirm that the type is either a primitive or references
            // another class defined in the document.
            for(ClassAttribute anAttribute : aClass.getClassAttributes())
            {
                ClassAttribute.ClassAttributeType kindOfNode = anAttribute.getAttributeKind();
                
                // The primitive type is on the known list of primitives.
                if(kindOfNode == ClassAttribute.ClassAttributeType.PRIMITIVE)
                {
                    if(primitiveTypes.contains(anAttribute.getType()) == false)
                    {
                        System.out.println("Cannot find a primitive type of " + anAttribute.getType() + " in class " + aClass.getName());
                        return false;
                    }
                }
            
                // The class referenced is available elsewehere in the document
                if(kindOfNode == ClassAttribute.ClassAttributeType.CLASSREF)
                {
                    if(generatedClassNames.get(anAttribute.getType()) == null)
                    {
                        if(!anAttribute.getType().equals("Object")) {
                            System.out.println("Makes reference to a class of name " + anAttribute.getType() + " in class " + aClass.getName() + " but no user-defined class of that type can be found in the document");
                            return false;
                        }
                    }
                    
                }
            } // end of trip through one class' attributes
            
            // Run through the list of initial values, ensuring that the initial values mentioned actually exist as attributes
            // somewhere up the inheritance chain.
            
            for(InitialValue anInitialValue : aClass.getInitialValues()) {          
                GeneratedClass currentClass = aClass;
                boolean found = false;
                
                //System.out.println("----Looking for matches of inital value " + anInitialValue.getVariable());
                while(currentClass != null)
                {
                    List thisClassesAttributes = currentClass.getClassAttributes();
                    for(ClassAttribute anAttribute : currentClass.getClassAttributes()) {
                        //System.out.println("--checking " + anAttribute.getName() + " against inital value " + anInitialValue.getVariable());
                        if(anInitialValue.getVariable().equals(anAttribute.getName()))
                        {
                            found = true;
                            break;
                        }
                    }
                    currentClass = (GeneratedClass)generatedClassNames.get(currentClass.getParentClass());
                }
                if(!found)
                {
                    System.out.println("Could not find initial value matching attribute name for " + anInitialValue.getVariable() + " in class " + aClass.getName());
                }
                    
                    
                    
            } // end of for loop thorugh initial values

        } // End of trip through classes
        generatedClassNames.remove("JammerKind");
        generatedClassNames.remove("JammerCategory");
        generatedClassNames.remove("JammerSubCategory");
        generatedClassNames.remove("JammerSpecific");
        generatedClassNames.remove("EntityCapabilities");
        generatedClassNames.remove("PduStatus");
        generatedClassNames.remove("Domain");

        return true;
    } // end of astIsPlausible
    
    /*
     * inner class that handles the SAX parsing of the XML file. This is relatively simnple, if
     * a little verbose. Basically we just create the appropriate objects as we come across the
     * XML elements in the file.
     */
    public class MyHandler extends DefaultHandler
    {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
        {
            // Lanaguage-specific elements. All the properties needed to generate code specific
            // to a language should be included in the properties list for that language.
            Properties props=null;
            switch(qName) {
                case JAVA:
                    props = javaProperties;
                    break;
                case CPP:
                    props = cppProperties;
                    break;
                case CSHARP:
                    props = csharpProperties;
                    break;
                case JAVASCRIPT:
                    props = javascriptProperties;
                    break;
                case OBJC:
                    props = objcProperties;
                    break;
                case PYTHON:
                    props = pythonProperties;
                    break;      
            }
            if(props != null)
              for(int idx = 0; idx < attributes.getLength(); idx++) {
                  props.setProperty(attributes.getQName(idx), attributes.getValue(idx));
              }

            switch (qName.toLowerCase()) {
                case CLASS:                  
                    handleClass(attributes);
                    break;
                    
                case INITIALVALUE:
                    handleInitialValue(attributes);
                    break;
                    
                case ATTRIBUTE:
                    handleAttribute(attributes);
                    break;

                case FLAG:
                    handleFlag(attributes);
                    break;

                case PRIMITIVE:
                    handlePrimitive(attributes);
                    break;
                    
                case CLASSREF:
                    handleClassRef(attributes);
                    break;
                    
                case SISOENUM:
                    handleSisoEnum(attributes);
                    break;
 
                case SISOBITFIELD:
                    handleSisoBitfield(attributes);
                    break;
                    
                case PADTOBOUNDARY:
                    handlePadToBoundary(attributes);
                    break;
                    
                case STATICIVAR:
                    handleStaticIvar(attributes);
                    break;
                    
                case OBJECTLIST:
                    handleObjectList(attributes);
                    break;

                case PRIMITIVELIST:
                    handlePrimitiveList(attributes);
                    break;
            }
        } // end of startElement
        
        @Override
        public void endElement(String uri, String localName, String qName)
        {
            // We've reached the end of a class element. The class should be complete; add it to the hash table.
            switch (qName.toLowerCase()) {
                case CLASS:
                    classCount--;
                    //System.out.println("classCount is " + classCount);
                    //System.out.println("---#End of class" + currentGeneratedClass.getName());
                    generatedClassNames.put(currentGeneratedClass.getName(), currentGeneratedClass);
                    break;
                case ATTRIBUTE:
                    //System.out.println("     end attribute");
                    currentGeneratedClass.addClassAttribute(currentClassAttribute);
                    break;
            }

        }

        private void handleAttribute(Attributes attributes)
        {
            currentClassAttribute = new ClassAttribute();

            // Attributes on the attribute tag.
            for (int idx = 0; idx < attributes.getLength(); idx++) {
                switch (attributes.getQName(idx).toLowerCase()) {
                    case NAME:
                        currentClassAttribute.setName(attributes.getValue(idx));
                        break;
                    case COMMENT:
                        currentClassAttribute.setComment(attributes.getValue(idx));
                        break;
                    case SERIALIZE:
                        if (attributes.getValue(idx).toLowerCase().equals(FALSE))
                            currentClassAttribute.shouldSerialize = false;
                        break;
                    case HIDDEN:
                        String tf = attributes.getValue((idx));
                        currentClassAttribute.setHidden(Boolean.parseBoolean(tf));
                        break;
                }

            }
        }
       
        private void handleInitialValue(Attributes attributes)
        {
            String anAttributeName = null;
            String anInitialValue = null;

            // Attributes on the initial value tag
            for (int idx = 0; idx < attributes.getLength(); idx++) {
                switch (attributes.getQName(idx).toLowerCase()) {
                    case NAME:
                        anAttributeName = attributes.getValue(idx);
                        break;
                    case VALUE:
                        anInitialValue = attributes.getValue(idx);
                        break;
                }
            }
            if ((anAttributeName != null) && (anInitialValue != null)) {
                InitialValue aValue = new InitialValue(anAttributeName, anInitialValue);
                currentGeneratedClass.addInitialValue(aValue);
            }
        }
        
        private void handleClass(Attributes attributes)
        {
            classCount++;
            //System.out.println("classCount is" + classCount);

            currentGeneratedClass = new GeneratedClass();

            // The default is that this inherits from Object
            currentGeneratedClass.setParentClass("root");

            // Trip through all the attributes of the class tag
            for (int idx = 0; idx < attributes.getLength(); idx++) {
                switch (attributes.getQName(idx)) {

                    case NAME:// class name
                        //System.out.println("--->Processing class named " + attributes.getValue(idx));
                        currentGeneratedClass.setName(attributes.getValue(idx));
                        break;

                    case COMMENT:// Class comment

                        //System.out.println("comment is " + attributes.getValue(idx));
                        currentGeneratedClass.setComment(attributes.getValue(idx));
                        break;

                    case INHERITSFROM:// Inherits from
                        //System.out.println("inherits from " + attributes.getValue(idx));
                        currentGeneratedClass.setParentClass(attributes.getValue(idx));
                        break;
                        
                    case ALIASFOR: // write empty subclass
                        currentGeneratedClass.setAliasFor(attributes.getValue(idx));
                        break;
                        
                    case IMPLEMENTS:
                        currentGeneratedClass.setInterfaces(attributes.getValue(idx));
                        break;
                        
                    case XMLROOTELEMENT:// XML root element--used for marshalling to XML with JAXB

                        //System.out.println("is root element " + attributes.getValue(idx));
                        if (attributes.getValue(idx).equalsIgnoreCase(TRUE)) {
                            currentGeneratedClass.setXmlRootElement(true);
                        }
                    // by default it is false unless specified otherwise
                        break;
                        
                    case SPECIALCASE:
                        currentGeneratedClass.setSpecialCase(attributes.getValue(idx));
                        break;
                        
                    case ABSTRACT:
                        currentGeneratedClass.setAbstract(attributes.getValue(idx));
                        break;
                }
            }
        }
        
        
        private void handleStaticIvar(Attributes attributes)
        {
            currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.STATIC_IVAR);
            for (int idx = 0; idx < attributes.getLength(); idx++) {
                switch (attributes.getQName(idx).toLowerCase()) {
                    case TYPE:
                        currentClassAttribute.setType(attributes.getValue(idx));
                        break;
                    case VALUE:
                        currentClassAttribute.setDefaultValue(attributes.getValue(idx));
                        break;
                }
            }
        }
        
        private void handleFlag(Attributes attributes)
        {
            String flagName = null;
            String flagComment = null;
            String flagMask = "0";

            for (int idx = 0; idx < attributes.getLength(); idx++) {
                // Name of class attribute
                switch (attributes.getQName(idx).toLowerCase()) {
                    case NAME:
                        flagName = attributes.getValue(idx);
                        break;
                    case COMMENT:
                        flagComment = attributes.getValue(idx);
                        break;
                    case MASK:
                        // Should parse "0x80" or "31" equally well.
                        String text = attributes.getValue(idx);
                        flagMask = text;
                        break;
                }
            }
            BitField bitField = new BitField(flagName, flagMask, flagComment, currentClassAttribute);
            currentClassAttribute.bitFieldList.add(bitField);
        }
        
        private void handlePrimitive(Attributes attributes)
        {
            if (currentClassAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.UNSET) 
                currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.PRIMITIVE);
         
            currentClassAttribute.setUnderlyingTypeIsPrimitive(true);

            for (int idx = 0; idx < attributes.getLength(); idx++) {
                switch (attributes.getQName(idx).toLowerCase()) {
                    case TYPE:
                        currentClassAttribute.setType(attributes.getValue(idx));
                        break;
                    case DEFAULTVALUE:
                        currentClassAttribute.setDefaultValue(attributes.getValue(idx));
                        break;
                }
            }
        }
        
        private void handleClassRef(Attributes attributes)
        {
            // The classref may occur inside a List element; if that's the case, we want to 
            // respect the existing list type.
            if (currentClassAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.UNSET) {
                currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.CLASSREF);
                currentClassAttribute.setUnderlyingTypeIsPrimitive(false);
            }

            for (int idx = 0; idx < attributes.getLength(); idx++) {
                switch (attributes.getQName(idx).toLowerCase()) {
                    case NAME:
                        currentClassAttribute.setType(attributes.getValue(idx));
                        break;
                    case "initialclass":
                        currentClassAttribute.setInitialClass(attributes.getValue(idx));
                        break;
                    case DEFAULTVALUE:
                        currentClassAttribute.setDefaultValue(attributes.getValue(idx));
                        break;                        
                }
            }
        }
        
        private void handleSisoEnum(Attributes attributes)
        {
            if(currentClassAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.UNSET) {
                currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.SISO_ENUM);
                currentClassAttribute.setUnderlyingTypeIsPrimitive(false);
            }
            else
                currentClassAttribute.setUnderlyingTypeIsEnum(true);

            for (int idx = 0; idx < attributes.getLength(); idx++) {
                String nm = attributes.getQName(idx);
                switch (attributes.getQName(idx).toLowerCase()) {
                    case TYPE:
                        currentClassAttribute.setType(attributes.getValue(idx));
                        break;
                    case COMMENT:
                        String s = currentClassAttribute.getComment();
                        currentClassAttribute.setComment((s==null?"":s)+" "+attributes.getValue(idx));
                        break;
                    case DEFAULTVALUE:
                        currentClassAttribute.setDefaultValue(attributes.getValue(idx));
                        break;
                }
            }
        }

       private void handleSisoBitfield(Attributes attributes)
        {
            if(currentClassAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.UNSET) {
                currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.SISO_BITFIELD);
                currentClassAttribute.setUnderlyingTypeIsPrimitive(false);
            }

            for (int idx = 0; idx < attributes.getLength(); idx++) {
                String nm = attributes.getQName(idx);
                switch (nm.toLowerCase()) {
                    case TYPE:
                        currentClassAttribute.setType(attributes.getValue(idx));
                        break;
                    case COMMENT:
                        String s = currentClassAttribute.getComment();
                        currentClassAttribute.setComment((s==null?"":s)+" "+attributes.getValue(idx));
                        break;
                    case DEFAULTVALUE:
                        currentClassAttribute.setDefaultValue(attributes.getValue(idx));
                        break;
                }
            }
        }
        private void handlePadToBoundary(Attributes attributes)
        {
            for (int idx = 0; idx < attributes.getLength(); idx++) {
                String nm = attributes.getQName(idx);
                switch (nm.toLowerCase()) {
                    case LENGTH:
                        switch (attributes.getValue(idx)) {
                            case "16":
                                currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.PADTO16);
                                break;
                            case "32":
                                currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.PADTO32);
                                break;
                            case "64":
                                currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.PADTO64);
                                break;
                            default:
                                System.err.println("Unrecognized value for padtoboundary length attribute: "+attributes.getValue(idx));
                                break;
                            }
                        break;
                    default:
                        System.err.println("Unrecognized attribute to padtoboundary element: "+nm);
                }
            }
        }
       
        private void handleObjectList(Attributes attributes)
        {
            currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.OBJECT_LIST);
            for (int idx = 0; idx < attributes.getLength(); idx++) {
                // Variable list length fields require a name of another field that contains how many
                // there are. This is used in unmarshalling.
                if (attributes.getQName(idx).equalsIgnoreCase(COUNTFIELDNAME)) {
                    currentClassAttribute.setCountFieldName(attributes.getValue(idx));

                    // We also want to inform the attribute associated with countFieldName that
                    // it is keeping track of a list--this modifies the getter method and
                    // eliminates the setter method. This code assumes that the count field
                    // attribute has already been processed.
                    List ats = currentGeneratedClass.getClassAttributes();
                    backReferenceCountField(attributes, ats, idx, currentClassAttribute.getAttributeKind());/*
                    boolean atFound = false;

                    for (int jdx = 0; jdx < ats.size(); jdx++) {
                        ClassAttribute at = (ClassAttribute) ats.get(jdx);
                        if (at.getName().equals(attributes.getValue(idx))) {
                            at.setIsDynamicListLengthField(true);
                            at.setDynamicListClassAttribute(currentClassAttribute);
                            atFound = true;
                            break;
                        }
                    }
                    if (atFound == false) {
                        System.out.println("Could not find a matching attribute for the length field for " + attributes.getValue(idx));
                    } */
                }
            }
        }
      private void backReferenceCountField(Attributes attributes, List ats, int idx, ClassAttributeType lstType)
      {
        boolean atFound = false;

        for (int jdx = 0; jdx < ats.size(); jdx++) {
          ClassAttribute at = (ClassAttribute) ats.get(jdx);
          if (at.getName().equals(attributes.getValue(idx))) {
            at.setIsDynamicListLengthField(lstType == ClassAttributeType.OBJECT_LIST);
            at.setIsPrimitiveListLengthField(lstType == ClassAttributeType.PRIMITIVE_LIST);
            at.setDynamicListClassAttribute(currentClassAttribute);
            atFound = true;

            break;
          }
        }
        if (atFound == false) {
          System.out.println("Could not find a matching attribute for the length field for " + attributes.getValue(idx));
        }
      }
      
      private void handlePrimitiveList(Attributes attributes)
      {
        currentClassAttribute.setAttributeKind(ClassAttribute.ClassAttributeType.PRIMITIVE_LIST);
        
        for (int idx = 0; idx < attributes.getLength(); idx++) {
          String nm = attributes.getQName(idx).toLowerCase();
          switch (attributes.getQName(idx).toLowerCase()) {
            case COULDBESTRING:
              if (attributes.getValue(idx).equalsIgnoreCase(TRUE))
                currentClassAttribute.setCouldBeString(true);
              break;

            case LENGTH:
              String length = attributes.getValue(idx);
              try {
                int listLen = Integer.parseInt(length);
                currentClassAttribute.setListLength(listLen);
              }
              catch (NumberFormatException e) {
                System.out.println("Invalid list length found. Bad format for integer " + length);
                currentClassAttribute.setListLength(0);
              }
              break;
              
            case FIXEDLENGTH:
              currentClassAttribute.setFixedLength(Boolean.parseBoolean(attributes.getValue(idx)));
              break;
              
            case COUNTFIELDNAME:
              currentClassAttribute.setCountFieldName(attributes.getValue(idx));
              backReferenceCountField(attributes, currentGeneratedClass.getClassAttributes(), idx, currentClassAttribute.getAttributeKind());
              break;
              
            default:
              currentClassAttribute.setListLength(0); //Apr8
              break;
          }
        }
      }
    }
}
