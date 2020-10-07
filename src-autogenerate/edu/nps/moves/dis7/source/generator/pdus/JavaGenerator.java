/**
 * Copyright (c) 2008-2020, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */
package edu.nps.moves.dis7.source.generator.pdus;

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.nps.moves.dis7.source.generator.pdus.ClassAttribute.ClassAttributeType;

/**
 * Given the input object, something of an abstract syntax tree, this generates a source code file in the java language. It has ivars, getters, setters, and serialization/deserialization methods.
 *
 * @author DMcG
 */
public class JavaGenerator extends Generator
{
    /**
     * Some Java (or Java-like) distributions don't have the full Java JDK 1.6 stack, such as Android. That means no JAXB, and no Hibernate/JPA. These booleans flip on or off the generation of the
     * annotations that use these features in the generated Java code.
     */

    /**
     * Maps the primitive types listed in the XML file to the java types
     */
    Properties types = new Properties();

    /**
     * What primitive types should be marshalled as. This may be different from the Java get/set methods, i.e. an unsigned short might have ints as the getter/setter, but is marshalled as a short.
     */
    Properties marshalTypes = new Properties();

    /**
     * Similar to above, but used on unmarshalling. There are some special cases (unsigned types) to be handled here.
     */
    Properties unmarshalTypes = new Properties();

    /**
     * sizes of various primitive types
     */
    Properties primitiveSizes = new Properties();
    Map<String,Integer> primitiveSizesMap = new HashMap<>();
    
    /**
     * A property list that contains java-specific code generation information, such as package names, imports, etc.
     */
    Properties javaProperties;

    public JavaGenerator(Map<String, GeneratedClass> pClassDescriptions, Properties pJavaProperties)
    {
        super(pClassDescriptions, pJavaProperties);

        try {
            Properties systemProperties = System.getProperties();

            // The command line (passed in as -D system properties to the java invocation)
            // may override some settings in the XML file. If these are non-null, they
            // take precedence over
            String clDirectory = systemProperties.getProperty("xmlpg.generatedSourceDir");
            //System.out.println("clDirectory=" + clDirectory);
            String clPackage = systemProperties.getProperty("xmlpg.package");

            //System.out.println("System properties: " + systemProperties);
            if (clDirectory != null)
                pJavaProperties.setProperty("directory", clDirectory);

            if (clPackage != null)
                pJavaProperties.setProperty("package", clPackage);

            super.setDirectory(clDirectory);

            System.out.println("Source code directory set to " + clDirectory);
        }
        catch (Exception e) {
            System.err.println("Required property not set. Modify the XML file to include the missing property");
            System.err.println(e);
            System.exit(-1);
        }

        final String UNSIGNED_INT8 = "uint8"; // TODO generalize this approach
        
        // Set up a mapping between the strings used in the Open-DIS XML file and the strings used
        // in the generated java file, specifically the data types. This could be externalized to
        // a properties file, but there's only a dozen or so and an external props file
        // would just add some complexity.
        // dont quite get this.  looks in error
        types.setProperty("uint8",   "byte");
        types.setProperty("uint16",  "short");
        types.setProperty("uint32",  "int");
        types.setProperty("uint64",  "long");
        types.setProperty("int8",    "byte");
        types.setProperty("int16",   "short");
        types.setProperty("int32",   "int");
        types.setProperty("int64",   "long");
        types.setProperty("float32", "float");
        types.setProperty("float64", "double");
    
        // Set up the mapping between Open-DIS primitive types and marshal types.       
        marshalTypes.setProperty("uint8",   "byte");
        marshalTypes.setProperty("uint16",  "short");
        marshalTypes.setProperty("uint32",  "int");
        marshalTypes.setProperty("uint64",  "long");
        marshalTypes.setProperty("int8",    "byte");
        marshalTypes.setProperty("int16",   "short");
        marshalTypes.setProperty("int32",   "int");
        marshalTypes.setProperty("int64",   "long");
        marshalTypes.setProperty("float32", "float");
        marshalTypes.setProperty("float64", "double");

        // Unmarshalling types
        unmarshalTypes.setProperty("uint8",   "UnsignedByte");
        unmarshalTypes.setProperty("uint16",  "UnsignedShort");
        unmarshalTypes.setProperty("uint32",  "int");
        unmarshalTypes.setProperty("uint64",  "long");
        unmarshalTypes.setProperty("int8",    "byte");
        unmarshalTypes.setProperty("int16",   "short");
        unmarshalTypes.setProperty("int32",   "int");
        unmarshalTypes.setProperty("int64",   "long");
        unmarshalTypes.setProperty("float32", "float");
        unmarshalTypes.setProperty("float64", "double");

        // How big various primitive types are
        primitiveSizes.setProperty("uint8",   "1");
        primitiveSizes.setProperty("uint16",  "2");
        primitiveSizes.setProperty("uint32",  "4");
        primitiveSizes.setProperty("uint64",  "8");
        primitiveSizes.setProperty("int8",    "1");
        primitiveSizes.setProperty("int16",   "2");
        primitiveSizes.setProperty("int32",   "4");
        primitiveSizes.setProperty("int64",   "8");
        primitiveSizes.setProperty("float32", "4");
        primitiveSizes.setProperty("float64", "8");
        
        primitiveSizesMap.put("uint8",   1);
        primitiveSizesMap.put("uint16",  2);
        primitiveSizesMap.put("uint32",  4);
        primitiveSizesMap.put("uint64",  8);
        primitiveSizesMap.put("int8",    1);
        primitiveSizesMap.put("int16",   2);
        primitiveSizesMap.put("int32",   4);
        primitiveSizesMap.put("int64",   8);
        primitiveSizesMap.put("float32", 4);
        primitiveSizesMap.put("float64", 8); 
    }

    /**
     * Generate the classes and write them to a directory
     */
    @Override
    public void writeClasses()
    {
        int classCount = 0;
        
        readTemplates();  // get the license
        
        this.createDirectory();

        Iterator it = classDescriptions.values().iterator();

        while (it.hasNext()) {
            try {
                GeneratedClass aClass     = (GeneratedClass) it.next();
                String         aClassName = aClass.getName();
//                String  pduSubpackageName = "pdus";

                // Create package structure, if any
                String aClassPackageName = languageProperties.getProperty("package");
                String aClassFullPath;

                // If we have a package specified, replace the dots in the package name (edu.nps.moves.dis)
                // with slashes (edu/nps/moves/dis and create that directory
                if (aClassPackageName != null) {
                    aClassPackageName = aClassPackageName.replace(".", "/");
                    aClassFullPath  = getDirectory() + "/" + aClassPackageName + "/" ;
//                    if (aClassName.endsWith("Pdu") || aClassPackageName.endsWith("dis7")) //  is there a better way to distinguish?
//                       aClassFullPath += pduSubpackageName + "/";
                    aClassFullPath += aClassName + ".java";
                    //System.out.println("full path is " + fullPath);
                }
                else {
                    aClassFullPath = getDirectory() + "/" + aClassName + ".java";
                }
                //System.out.println("Creating Java source code file for " + fullPath);

                // Create the new, empty file, and create printwriter object for output to it
                File outputFile = new File(aClassFullPath);
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
                PrintWriter pw = new PrintWriter(outputFile, StandardCharsets.UTF_8.name());

                // print the source code of the class to the file
                // System.out.println("trying to make class "+name);
                this.writeClass(pw, aClass);
                classCount++;
            }
            catch (IOException e) {
                e.printStackTrace(System.err);
                System.err.println("error creating source code " + e);
            }

        } // End while
        
        System.out.println (JavaGenerator.class.getName() + " complete, " + classCount + " classes written.");

    } // End write classes

    /**
     * Generate a source code file with accessor methods (getters and setters), ivars, and marshal/unmarshal methods for one class.
     */
    private void writeClass(PrintWriter pw, GeneratedClass aClass)
    {
        writeLicense(pw,aClass);
        pw.flush();
        /*
        if(aClass.getSpecialCase() != null) {
            writeSpecialCase(pw, aClass);
            return;
        }
        */
        writeImports(pw, aClass);
        pw.flush();
        writeClassComments(pw, aClass);
        pw.flush();
        writeClassDeclaration(pw, aClass);
        
        if(aClass.getAliasFor()!= null) {
            pw.flush();
            pw.close();
            return;
        }
        
        pw.flush();
        writeIvars(pw, aClass);
        pw.flush();
        writeConstructor(pw, aClass);
        pw.flush();
        writeGetMarshalledSizeMethod(pw, aClass);
        pw.flush();
        writeGettersAndSetters(pw, aClass);
        pw.flush();
        writeBitflagMethods(pw, aClass);
        pw.flush();
        writeMarshalMethod(pw, aClass);
        pw.flush();
        writeUnmarshallMethod(pw, aClass);
        pw.flush();
        writeMarshalMethodWithByteBuffer(pw, aClass);
        pw.flush();
        writeUnmarshallMethodWithByteBuffer(pw, aClass);
        pw.flush();

        if (aClass.getName().equals("Pdu"))
            writeMarshalMethodToByteArray(pw, aClass);
        pw.flush();

        //this.writeXmlMarshallMethod(pw, aClass);
        writeEqualityMethod(pw, aClass);

        writeToStringMethod(pw, aClass);
        
        pw.println("} // end of class");
        pw.flush();
        pw.close();
    }

  /*  String domainTemplate1;
    String domainTemplate2;
    String domainTemplate3;*/
    String specSource;
    String license;
    /*
    private void writeSpecialCase(PrintWriter pw, GeneratedClass aClass)
    {
        if(aClass.getSpecialCase().equals(Main.DOMAINHOLDER)) {
            writeDomain(pw,aClass);
        }
    }
    private void writeDomain(PrintWriter pw, GeneratedClass aClass)
    {
        if(domainTemplate1 == null)
            readTemplates();
        
        pw.println(String.format(domainTemplate1));
        for(ClassAttribute attr : aClass.getClassAttributes()) {
            if(attr.getAttributeKind().equals(ClassAttribute.ClassAttributeType.SISO_ENUM)) {
                pw.println(String.format(domainTemplate2,attr.getType(),attr.getComment()));
            }            
        }
        pw.println(String.format(domainTemplate3));
        pw.flush();
        pw.close();
    }
    */
    private void readTemplates()
    {
        try {
         //   domainTemplate1 = loadOneTemplate("domainpart1.txt");
         //   domainTemplate2 = loadOneTemplate("domainpart2.txt");
         //   domainTemplate3 = loadOneTemplate("domainpart3.txt");
            specSource      = loadOneTemplate("dis7spec.txt");
            license         = loadOneTemplate("dis7javalicense.txt");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private String loadOneTemplate(String s) throws Exception
    {
        return new String(Files.readAllBytes(Paths.get(getClass().getResource(s).toURI())));
    }
    
    /**
     * Write the license text as a java comment at the top of the file.
     */
    
    private void writeLicense(PrintWriter pw, GeneratedClass aClass)
    {
      if(license == null)
        System.out.println("bp");
      pw.println(license);
      pw.println();
    }
    /**
     * Writes the package and package import code at the top of the Java source file
     *
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    private void writeImports(PrintWriter pw, GeneratedClass aClass)
    {
        // Write the package name
        String packageName = languageProperties.getProperty("package");
        if (packageName != null) {
            pw.println("package " + packageName + ";");
        }

        pw.println();

        // Write the various import statements
        String imports = languageProperties.getProperty("imports");
        StringTokenizer tokenizer = new StringTokenizer(imports, ", ");
        String aPackage;
        while (tokenizer.hasMoreTokens()) {
            aPackage = tokenizer.nextToken();
            pw.println("import " + aPackage + ";");
        }
        pw.println();
    }

    /**
     * Write the class comments block
     *
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    private void writeClassComments(PrintWriter pw, GeneratedClass aClass)
    {
        // Print class comments header
        pw.println("/**");
        if (aClass.getClassComments() != null)
            pw.println(" * " + aClass.getClassComments());
        
        pw.println(" * "+specSource);
        pw.println(" */");
    }

    /**
     * Writes the class declaration, including any inheritance and interfaces
     *
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    private void writeClassDeclaration(PrintWriter pw, GeneratedClass aClass)
    {
        // Class declaration
        if(aClass.getAliasFor() != null) {
          pw.println("public class "+aClass.getName()+" extends "+aClass.getAliasFor());
          pw.println("{}");
          return;
        }
        
        String parentClass = aClass.getParentClass();
        String interfaces = aClass.getInterfaces();

        String abstractcls = aClass.isAbstract() ? "abstract " : "";
        
        if (parentClass.equalsIgnoreCase("root"))
            parentClass = "Object";

        pw.print("public "+ abstractcls + "class " + aClass.getName() + " extends " + parentClass + " implements Serializable");
        if(interfaces != null)
            pw.println(","+interfaces);
        else
            pw.println();
        pw.println("{");
    }

    /**
     * Write instance variables (ivars)
     * @param pw PrintWriter
     * @param aClass class of interest 
     */
    private void writeIvars(PrintWriter pw, GeneratedClass aClass)
    {
        List ivars = aClass.getClassAttributes();
        //System.out.println("Ivars for class: " + aClass.getName());
        for (ClassAttribute anAttribute : aClass.getClassAttributes()) {
            if (anAttribute.shouldSerialize == false) {
                pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                continue;
            }
            String attributeType;
            int listLength;
            String clsnm;
            
            String fieldaccess = "protected"; // allow subclassing anAttribute.isHidden()? "private":"protected";
            
            switch (anAttribute.attributeKind) {
                case STATIC_IVAR:
                    //if (anAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.STATIC_IVAR) {
                    attributeType = types.getProperty(anAttribute.getType());
                    String value = anAttribute.getDefaultValue();
                    pw.print("   public static " + attributeType + "  " + anAttribute.getName());
                    pw.print(" = " + value + ";\n");
                    break;

                    // This attribute is a primitive. 
                case PRIMITIVE:
                    // The primitive type--we need to do a lookup from the abstract type in the
                    // xml to the java-specific type. The output should look something like
                    //
                    // /** This is a comment */
                    // protected int foo;
                    //
                    attributeType = types.getProperty(anAttribute.getType());
                    if (anAttribute.getComment() != null) {
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    }

                    String defaultValue = anAttribute.getDefaultValue();

                    pw.print("   " + fieldaccess + " " + attributeType + "  " + anAttribute.getName());
                    if (defaultValue != null)
                        pw.print(" = (" + attributeType + ")" + defaultValue); // Needs cast to primitive type for float/double issues
                    pw.println(";\n");
                    break; // end of primitive attribute type

                // this attribute is a reference to another class defined in the XML document, The output should look like
                //
                // /** This is a comment */
                // protected AClass foo = new AClass();
                //
                case CLASSREF:
                    attributeType = anAttribute.getType();
                    String initialClass = anAttribute.getInitialClass(); // most often null

                    if (anAttribute.getComment() != null) {
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    }
                    if(anAttribute.getDefaultValue() == null)
                        pw.println("   " + fieldaccess + " " + attributeType + "  " + anAttribute.getName() + " = new " + (initialClass == null ? attributeType : initialClass) + "(); \n");
                    else
                        pw.println("   " + fieldaccess + " " + attributeType + "  " + anAttribute.getName() + " =  " + anAttribute.getDefaultValue() + "; \n");
                    break;

                // The attribute is a fixed list, ie an array of some type--maybe primitve, maybe a class.
                case PRIMITIVE_LIST:
                    attributeType = anAttribute.getType();
                    listLength = anAttribute.getListLength();

                    if (anAttribute.getComment() != null) {
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    }

                    pw.println("   " + fieldaccess + " " + types.getProperty(attributeType) + "[]  " + anAttribute.getName() + " = new "
                        + types.getProperty(attributeType) + "[" + listLength + "]" + "; \n");
                    break;

                // The attribute is a variable list of some kind. 
                case OBJECT_LIST:
                    attributeType = anAttribute.getType();
                    listLength = anAttribute.getListLength();

                    if (anAttribute.getComment() != null)
                        pw.println("   /** " + anAttribute.getComment() + " */");

                    pw.println("   " + fieldaccess + " List< " + attributeType + " > " + anAttribute.getName() + " = new ArrayList< " + attributeType + " >();\n ");
                    break;

//            if((anAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.SISO_ENUM)) {
//                String clsnm = anAttribute.getType();
//                if(anAttribute.getComment() != null)
//                    pw.println("   /** " + anAttribute.getComment() + " */");
//                if(anAttribute.getDefaultValue() == null) 
//                    pw.println("   protected "+ clsnm + " "+ anAttribute.getName() + ";\n");
//                else
//                    pw.println("   protected "+ clsnm + " "+ anAttribute.getName() + " = " + anAttribute.getDefaultValue() + ";\n");
//
//            }
                case SISO_ENUM:
                    clsnm = anAttribute.getType();
                    if (anAttribute.getComment() != null)
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    if (anAttribute.getDefaultValue() == null)
                        pw.println("   " + fieldaccess + " " + clsnm + " " + anAttribute.getName() + " = " + clsnm + ".values()[0];\n");
                    else
                        pw.println("   " + fieldaccess + " " + clsnm + " " + anAttribute.getName() + " = " + anAttribute.getDefaultValue() + ";\n");
                    break;

                case SISO_BITFIELD:
                    clsnm = anAttribute.getType();
                    if (anAttribute.getComment() != null)
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    if (anAttribute.getDefaultValue() == null)
                        pw.println("   " + fieldaccess + " " + clsnm + " " + anAttribute.getName() + " = new " + clsnm + "();\n");
                    else
                        pw.println("   " + fieldaccess + " " + clsnm + " " + anAttribute.getName() + " = " + anAttribute.getDefaultValue() + ";\n");
                    break;
                    
                case PADTO16:                
                    pw.println("   private byte[] "+anAttribute.getName()+" = new byte[0]; // pad to 16-bit boundary\n");
                    break;
                case PADTO32:
                    pw.println("   private byte[] "+anAttribute.getName()+" = new byte[0]; // pad to 32-bit boundary\n");
                    break;
                case PADTO64:
                    pw.println("   private byte[] "+anAttribute.getName()+" = new byte[0]; // pad to 64-bit boundary\n");
                    break;
                    
            }
        } // End of loop through ivars
    }
  
    private void writeConstructor(PrintWriter pw, GeneratedClass aClass)
    {
        // Write a constructor
        pw.println();
        pw.println("/** Constructor */");
        pw.println(" public " + aClass.getName() + "()");
        pw.println(" {");

        // Set primitive types with initial values
        for (InitialValue anInit : aClass.getInitialValues()) {

            // This is irritating. we have to match up the attribute name with the type,
            // so we can do a cast. Otherwise java pukes because it wants to interpret all
            // numeric strings as ints or doubles, and the attribute may be a short.
            boolean found = false;
            GeneratedClass currentClass = aClass;
            String aType = null;
            ClassAttributeType aKind=null;
            
            String attName=null;
            while (currentClass != null) {
                for (ClassAttribute anAttribute : currentClass.classAttributes) {
                    attName = anAttribute.getName();
                    //System.out.println("--checking " + anAttribute.getName() + " against inital value " + anInitialValue.getVariable());
                    if (anInit.getVariable().equals(anAttribute.getName())) {
                        found = true;
                        aType = anAttribute.getType();
                        aKind = anAttribute.attributeKind;
                        break;
                    }

                }
                currentClass = classDescriptions.get(currentClass.getParentClass());
            }
            if (!found) {
                System.out.println("Could not find initial value matching attribute name ("+attName+" for " + anInit.getVariable() + " in class " + aClass.getName());
            }
            else {
                if(aKind == ClassAttributeType.SISO_ENUM)
                    pw.println("    " + anInit.getSetterMethodName() + "( " + anInit.getVariableValue() + " );");
                else
                    pw.println("    " + anInit.getSetterMethodName() + "( (" + types.getProperty(aType) + ")" + anInit.getVariableValue() + " );");
            }
        } // End initialize initial values
        
        pw.println(" }");    
    }
    
    public void writeGetMarshalledSizeMethod(PrintWriter pw, GeneratedClass aClass)
    {
        // Create a getMarshalledSize() method
        pw.println();
        // Not all object are setup to implement Marshaller; should be done
        //pw.println("@Override");
        pw.println(
"  /**\n" +
"   * Returns size of this serialized (marshalled) object in bytes\n" +
"   * See <a href=\"https://en.wikipedia.org/wiki/Marshalling_(computer_science)\" target=\"_blank\">https://en.wikipedia.org/wiki/Marshalling_(computer_science)</a>\n" +
"   * @return serialized size in bytes\n" +
"   */");
        pw.println("public int getMarshalledSize()");
        pw.println("{");
        pw.println("   int marshalSize = 0; ");
        pw.println();

        // Size of superclass is the starting point
        if (!aClass.getParentClass().equalsIgnoreCase("root")) {
            pw.println("   marshalSize = super.getMarshalledSize();");
        }

        for (ClassAttribute anAttribute : aClass.getClassAttributes()) {
            switch (anAttribute.getAttributeKind()) {
                case PRIMITIVE:
                    pw.print("   marshalSize += ");
                    pw.println(primitiveSizes.get(anAttribute.getType()) + ";  // " + anAttribute.getName());
                    break;
                case CLASSREF:
                case SISO_ENUM:
                case SISO_BITFIELD:
                    pw.print("   marshalSize += ");
                    pw.println(anAttribute.getName() + ".getMarshalledSize();");
                    break;
                case PRIMITIVE_LIST:
                    //System.out.println("Generating fixed list for " + anAttribute.getName() + " listIsClass:" + anAttribute.listIsClass());
                    // If this is a fixed list of primitives, it's the list size times the size of the primitive.
                    //pw.println("   marshalSize = marshalSize + " +  anAttribute.getListLength() + " * " + primitiveSizes.get(anAttribute.getType()) + ";  // " + anAttribute.getName());
                    pw.println("   marshalSize += " + anAttribute.getName()+".length * "  + primitiveSizes.get(anAttribute.getType()) + ";");
                    break;
                case OBJECT_LIST:
                    // If this is a dynamic list of primitives, it's the list size times the size of the primitive.
                    pw.println("   for(int idx=0; idx < " + anAttribute.getName() + ".size(); idx++)");
                    pw.println("   {");
                    //pw.println( anAttribute.getName() + ".size() " + " * " +  " new " + anAttribute.getType() + "().getMarshalledSize()"  + ";  // " + anAttribute.getName());
                    pw.println("        " + anAttribute.getType() + " listElement = " + anAttribute.getName() + ".get(idx);");
                    pw.println("        marshalSize += listElement.getMarshalledSize();");
                    pw.println("   }");
                    break;
                case PADTO16:
                case PADTO32:
                case PADTO64:
                    pw.println("   marshalSize += "+anAttribute.getName()+".length;");
                    break;
            }          
        }

        pw.println();
        pw.println("   return marshalSize;");
        pw.println("}");
        pw.println();
    }
 
    private void writeGettersAndSetters(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println();

        for (ClassAttribute anAttribute : aClass.classAttributes) {

            // The setter method should be of the form
            //
            // public void setName(AType pName)
            // { name = pName;
            // }
            //
            if(anAttribute.isHidden())
                continue;
            
            switch (anAttribute.getAttributeKind()) {

                case PRIMITIVE:
                    if (anAttribute.getIsDynamicListLengthField() == false) {
                        String beanType = types.getProperty(anAttribute.getType());
                        pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                        pw.println("  * @param p" + this.initialCap(anAttribute.getName()) + " new value of interest");
                        pw.println("  * @return same object to permit progressive setters */");
                        pw.print("public ");
                        pw.print(aClass.getName());
                        pw.println(" set" + this.initialCap(anAttribute.getName()) + "(" + beanType + " p" + this.initialCap(anAttribute.getName()) + ")");
                        pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCap(anAttribute.getName()) + ";");
                        pw.println("    return this;");
                        pw.println("}");
                        
                        // utility setter to allow int types
                        if (beanType.equals("byte") || beanType.equals("short") || beanType.equals("long"))
                        {
                        pw.println("/** Utility setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                        pw.println("  * @param p" + this.initialCap(anAttribute.getName()) + " new value of interest");
                        pw.println("  * @return same object to permit progressive setters */");
                        pw.print("public ");
                        pw.print(aClass.getName());
                        pw.print(" set" + this.initialCap(anAttribute.getName()) + "(" );
                        pw.print("int"); // allow int, will then coerce downcasting to beantype when setting
                        pw.print(" p" + this.initialCap(anAttribute.getName()) + ")");
                        // TODO overflow checks when downcasting int to given beantype
                        pw.println("{\n    " + anAttribute.getName() + " = (" + beanType + ") p" + this.initialCap(anAttribute.getName()) + ";");
                        pw.println("    return this;");
                        pw.println("}");
                        }

                        pw.println();
                        pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                        pw.println("  * @return value of interest */");
                        pw.println("public " + beanType + " get" + this.initialCap(anAttribute.getName()) + "()");
                        pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                        pw.println();
                    }
                    else //todo, now obsolete with new definition of PRIMITIVE_LIST  // This is the count field for a dynamic list
                    {
                        String beanType = types.getProperty(anAttribute.getType());
                        ClassAttribute listAttribute = anAttribute.getDynamicListClassAttribute();

                        pw.println("public " + beanType + " get" + this.initialCap(anAttribute.getName()) + "()");
                        pw.println("{\n    return (" + beanType + ")" + listAttribute.getName() + ".size(); \n}");

                        pw.println();

                        pw.println("/* Note that setting this value will not change the marshalled value. The list whose length this describes is used for that purpose.");
                        pw.println(" * The get" + anAttribute.getName() + " method will also be based on the actual list length rather than this value. ");
                        pw.println(" * The method is simply here for java bean completeness.");
                        pw.println(" */");
                        pw.print("public ");
                        pw.print(aClass.getName());
                        pw.println(" set" + this.initialCap(anAttribute.getName()) + "(" + beanType + " p" + this.initialCap(anAttribute.getName()) + ")");
                        pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCap(anAttribute.getName()) + ";");
                        pw.println("    return this;");
                        pw.println("}");

                        pw.println();

                    }
                    break; // End is primitive

                // The attribute is a class of some sort. Generate getters and setters.
                case CLASSREF:
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCap(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCap(anAttribute.getName()) + "(" + anAttribute.getType() + " p" + this.initialCap(anAttribute.getName()) + ")");
                    pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCap(anAttribute.getName()) + ";");
                    pw.println("    return this;");
                    pw.println("}");
                    pw.println();
                    
                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public " + anAttribute.getType() + " get" + this.initialCap(anAttribute.getName()) + "()");
                    pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                    pw.println();
                    break;
                    
                // The attribute is an array of some sort. Generate getters and setters.
                case PRIMITIVE_LIST:
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCap(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCap(anAttribute.getName()) + "(" + types.getProperty(anAttribute.getType()) + "[] p" + this.initialCap(anAttribute.getName()) + ")");

                    if (!anAttribute.isFixedLength())
                        pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCap(anAttribute.getName()) + ";");
                    else
                        pw.println("{\n    " + anAttribute.getName() + " = Arrays.copyOf(p" + this.initialCap(anAttribute.getName()) + ", " + anAttribute.getName() + ".length);");

                    pw.println("    return this;");
                    pw.println("}");
                    pw.println();
                    
                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public " + types.getProperty(anAttribute.getType()) + "[] get" + this.initialCap(anAttribute.getName()) + "()");
                    pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                    pw.println();
                    break;

                case OBJECT_LIST:
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCap(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCap(anAttribute.getName()) + "(List<" + anAttribute.getType() + ">" + " p" + this.initialCap(anAttribute.getName()) + ")");
                    pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCap(anAttribute.getName()) + ";");
                    pw.println("    return this;");
                    pw.println("}");

                    pw.println();
                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public List<" + anAttribute.getType() + ">" + " get" + this.initialCap(anAttribute.getName()) + "()");
                    pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                    pw.println();
                    break;

                case SISO_ENUM:
                    String enumtype = anAttribute.getType();
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCap(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCap(anAttribute.getName()) + "(" + enumtype + " p" + this.initialCap(anAttribute.getName()) + ")");
                    pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCap(anAttribute.getName()) + ";");
                    pw.println("    return this;");
                    pw.println("}");

                    pw.println();
                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public " + enumtype + " get" + this.initialCap(anAttribute.getName()) + "()");
                    pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                    pw.println();
                    break;
                    
                case SISO_BITFIELD:
                    String bitfieldtype = anAttribute.getType();
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCap(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCap(anAttribute.getName()) + "(" + bitfieldtype + " p" + this.initialCap(anAttribute.getName()) + ")");
                    pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCap(anAttribute.getName()) + ";");
                    pw.println("    return this;");
                    pw.println("}");

                    pw.println();
                    
                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public " + bitfieldtype + " get" + this.initialCap(anAttribute.getName()) + "()");
                    pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                    pw.println();
                    break;
            }
        } // End of loop trough writing getter/setter methods
    }

    /**
     * Some fields have integers with bit fields defined, eg an integer where bits 0-2 represent some value, while bits 3-4 represent another value, and so on. This writes accessor and mutator methods
     * for those fields.
     *
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    private void writeBitflagMethods(PrintWriter pw, GeneratedClass aClass)
    {
        List attributes = aClass.getClassAttributes();

        for (int idx = 0; idx < attributes.size(); idx++) {
            ClassAttribute anAttribute = (ClassAttribute) attributes.get(idx);

            switch (anAttribute.getAttributeKind()) {

                // Anything with bitfields must be a primitive type
                case PRIMITIVE:
                    List bitfields = anAttribute.bitFieldList;
                    String attributeType = types.getProperty(anAttribute.getType());
                    String bitfieldIvarName = anAttribute.getName();

                    for (int jdx = 0; jdx < bitfields.size(); jdx++) {
                        BitField bitfield = (BitField) bitfields.get(jdx);
                        String capped = this.initialCap(bitfield.name);
                        String cappedIvar = this.initialCap(bitfieldIvarName);
                        int shiftBits = super.getBitsToShift(anAttribute, bitfield.mask);

                        // write getter
                        pw.println();
                        if (bitfield.comment != null) {
                            pw.println("/**\n * " + bitfield.comment + "\n */");
                        }

                        pw.println("public int get" + cappedIvar + "_" + bitfield.name + "()");
                        pw.println("{");

                        pw.println("    " + attributeType + " val = (" + attributeType + ")(this." + bitfield.parentAttribute.getName() + "   & " + "(" + attributeType + ")" + bitfield.mask + ");");
                        pw.println("    return (int)(val >> " + shiftBits + ");");
                        pw.println("}\n");

                        // Write the setter/mutator
                        pw.println();
                        if (bitfield.comment != null) {
                            pw.println("/** \n * " + bitfield.comment + "\n */");
                        }
                        pw.println("public void set" + cappedIvar + "_" + bitfield.name + "(int val)");
                        pw.println("{");
                        pw.println("    " + attributeType + " " + " aVal = 0;");
                        pw.println("    this." + bitfield.parentAttribute.getName() + " &= (" + attributeType + ")(~" + bitfield.mask + "); // clear bits");
                        pw.println("    aVal = (" + attributeType + ")(val << " + shiftBits + ");");
                        pw.println("    this." + bitfield.parentAttribute.getName() + " = (" + attributeType + ")(this." + bitfield.parentAttribute.getName() + " | aVal);");
                        pw.println("}\n");
                    }

                    break;

                default:
                    bitfields = anAttribute.bitFieldList;
                    if (!bitfields.isEmpty()) {
                        System.out.println("Attempted to use bit flags on a non-primitive field");
                        System.out.println("Field: " + anAttribute.getName());
                    }
            }

        }
    }

    private void writeMarshalMethod(PrintWriter pw, GeneratedClass aClass)
    {
        //pw.println();
        pw.println("/**");
        pw.println(" * Serializes an object to a DataOutputStream.");
        pw.println(" * @throws java.lang.Exception if something goes wrong");
        pw.println(" * @see java.io.DataOutputStream");
        pw.println(" * @param dos the OutputStream");
        pw.println(" */");
 
        pw.println("public void marshal(DataOutputStream dos) throws Exception");
        pw.println("{");

        // If we're a sublcass of another class, we should first call super
        // to make sure the superclass's ivars are marshaled out.
        if (!(aClass.getParentClass().equalsIgnoreCase("root")))
            pw.println("    super.marshal(dos);");

        pw.println("    try \n    {");

        // Loop through the class attributes, generating the output for each.
        for (ClassAttribute anAttribute : aClass.getClassAttributes()) {

            if (anAttribute.shouldSerialize == false) {
                pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                continue;
            }

            String marshalType;
            String capped;
            switch (anAttribute.getAttributeKind()) {

            // Write out a method call to serialize a primitive type
              case PRIMITIVE:
                marshalType = marshalTypes.getProperty(anAttribute.getType());
                capped = this.initialCap(marshalType);

                // If we're a normal primitivetype, marshal out directly; otherwise, marshall out
                // the list length.
                if (anAttribute.getIsDynamicListLengthField()) {
                  ClassAttribute listAttribute = anAttribute.getDynamicListClassAttribute();
                  pw.println("       dos.write" + capped + "(" + listAttribute.getName() + ".size());");
                }
                
                else if (anAttribute.getIsPrimitiveListLengthField()) {
                  ClassAttribute listAttribute = anAttribute.getDynamicListClassAttribute();
                  pw.println("       dos.write" + capped + "(" + listAttribute.getName() + ".length);");
                }

                else {
                  pw.println("       dos.write" + capped + "(" + anAttribute.getName() + ");");
                }
                break;

                case SISO_ENUM:
                    /* if(anAttribute.enumMarshalSize.equals("1"))
                    pw.println("       dos.write( (byte)" + anAttribute.getName() + ".getValue() );");
                else if(anAttribute.enumMarshalSize.equals("2"))
                    pw.println("       dos.write( (unsigned short)" + anAttribute.getName() + ".getValue() );");
                     */
                    pw.println("       " + anAttribute.getName() + ".marshal(dos);");

                    break;
                    
                case SISO_BITFIELD:
                    pw.println("       " + anAttribute.getName() + ".marshal(dos);"); 
                    break;
                    
                // Write out a method call to serialize a class.
                case CLASSREF:
                    marshalType = anAttribute.getType();

                    pw.println("       " + anAttribute.getName() + ".marshal(dos);");
                    break;

                // Write out the method call to marshal a fixed length list, aka an array.
                case PRIMITIVE_LIST:
                    pw.println();
                    pw.println("       for(int idx = 0; idx < " + anAttribute.getName() + ".length; idx++)");

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    capped = this.initialCap(marshalType);
                    pw.println("           dos.write" + capped + "(" + anAttribute.getName() + "[idx]);");

                    pw.println();
                    break;

                // Write out a section of code to marshal a variable length list. The code should look like
                //
                // for(int idx = 0; idx < attrName.size(); idx++)
                // { anAttribute.marshal(dos);
                // }
                //    
                case OBJECT_LIST:
                    pw.println();
                    pw.println("       for(int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");
                    pw.println("       {");

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    pw.println("            " + anAttribute.getType() + " a" + initialCap(anAttribute.getType() + " = "
                        + anAttribute.getName() + ".get(idx);"));
                    pw.println("            a" + initialCap(anAttribute.getType()) + ".marshal(dos);");

                    pw.println("       }");
                    pw.println();
                    break;
                
                case PADTO16:
                    pw.println("       "+anAttribute.getName()+" = new byte[Align.to16bits(dos)];");
                    break;
                case PADTO32:
                    pw.println("       "+anAttribute.getName()+" = new byte[Align.to32bits(dos)];");
                    break;
                case PADTO64:
                    pw.println("       "+anAttribute.getName()+" = new byte[Align.to64bits(dos)];");
                    break;
                    
            }

        } // End of loop through the ivars for a marshal method

        pw.println("    }\n    catch(Exception e)");
        pw.println("    {\n      System.err.println(e);\n    }");

        pw.println("}");
    }

    private void writeUnmarshallMethod(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println();
        pw.println("/**");
        pw.println(" * Deserializes an object from a DataInputStream.");
        pw.println(" * @throws java.lang.Exception if something goes wrong");
        pw.println(" * @see java.io.DataInputStream");
        pw.println(" * See <a href=\"https://en.wikipedia.org/wiki/Marshalling_(computer_science)\" target=\"_blank\">https://en.wikipedia.org/wiki/Marshalling_(computer_science)</a>");
        pw.println(" * @param dis the InputStream");
        pw.println(" * @return marshalled serialized size in bytes");
        pw.println(" */");

        pw.println("public int unmarshal(DataInputStream dis) throws Exception");
        pw.println("{");
        pw.flush();
        pw.println("    int uPosition = 0;");
        
        if (!(aClass.getParentClass().equalsIgnoreCase("root")))
            pw.println("    uPosition += super.unmarshal(dis);\n");

        pw.println("    try \n    {");

        // Loop through the class attributes, generating the output for each.
        for (ClassAttribute anAttribute : aClass.getClassAttributes()) {

            if (anAttribute.shouldSerialize == false) {
                pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                continue;
            }
            String attributeName= anAttribute.getName();
            switch(anAttribute.getAttributeKind()) {
                case PRIMITIVE:            
                    String marshalType = unmarshalTypes.getProperty(anAttribute.getType());
                    String capped = this.initialCap(marshalType);
                
                    if (marshalType.equalsIgnoreCase("UnsignedByte")) {// || marshalType.equalsIgnoreCase("uint8"))
                        pw.println("        " + attributeName + " = (byte)dis.read" + capped + "();");
                        pw.println("        uPosition += 1;");
                    }
                    else if (marshalType.equalsIgnoreCase("UnsignedShort")) {/// || marshalType.equalsIgnoreCase("uint16"))
                        pw.println("        " + attributeName + " = (short)dis.read" + capped + "();");
                        pw.println("        uPosition += 2;");
                    }
                    else if (marshalType.equalsIgnoreCase("UnsignedLong")) { // || marshalType.equalsIgnoreCase("uint64"))
                        pw.println("        " + attributeName + " = (long)dis.readLong" + "();"); // ^^^This is wrong; need to read unsigned here
                        pw.println("        uPosition += 8;");
                    }
                    else {
                        pw.println("        " + attributeName + " = dis.read" + capped + "();");
                        pw.println("        uPosition += 4;");
                    }
                    pw.flush();
                    break;
                
                case SISO_ENUM:
                    pw.println("        " + attributeName + " = "+anAttribute.getType()+".unmarshalEnum(dis);");
                    pw.println("        uPosition += " + attributeName + ".getMarshalledSize();");
                    break;
                    
                case SISO_BITFIELD:
                case CLASSREF:           
                    pw.println("        uPosition += " + attributeName + ".unmarshal(dis);");
                    break;
                    
                case PRIMITIVE_LIST:
                    pw.println("        for(int idx = 0; idx < " + attributeName + ".length; idx++)");

                    // This is some sleaze. We're an array, but an array of what? We could be either a
                    // primitive or a class. We need to figure out which. This is done via the expedient
                    // but not very reliable way of trying to do a lookup on the type. If we don't find
                    // it in our map of primitives to marshal types, we assume it is a class.
                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    if (marshalType == null) { // It's a class
                        pw.println("            uPosition += " + attributeName + "[idx].unmarshal(dis);");
                    }
                    else { // It's a primitive
                        int primitiveByteSize = primitiveSizesMap.get(anAttribute.getType());
                        capped = this.initialCap(marshalType);
                        pw.println("            " + anAttribute.getName() + "[idx] = dis.read" + capped + "();");
                        pw.println("        uPosition += ("+attributeName + ".length * "+primitiveByteSize+");");
                    }
                    break;
                    
                case OBJECT_LIST:
                    if (anAttribute.getCountFieldName() != null)
                        pw.println("        for(int idx = 0; idx < " + anAttribute.getCountFieldName() + "; idx++)");
                    else
                        pw.println("        for(int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");

                    pw.println("        {");

                    if(anAttribute.getUnderlyingTypeIsEnum()) {
                        pw.println("            " +anAttribute.getType() + " anX = "+anAttribute.getType() + ".unmarshalEnum(dis);");
                        pw.println("            " + anAttribute.getName() + ".add(anX);");
                        pw.println("            uPosition += anX.getMarshalledSize();");
                    }
                    else {
                        marshalType = marshalTypes.getProperty(anAttribute.getType());

                        if (marshalType == null) { // It's a class
                            pw.println("            " + anAttribute.getType() + " anX = new " + anAttribute.getType() + "();");
                            pw.println("            uPosition += anX.unmarshal(dis);");
                            pw.println("            " + anAttribute.getName() + ".add(anX);");
                        }
                        else  { // It's a primitive
                            capped = this.initialCap(marshalType);
                            pw.println("            dis.read" + capped + "(" + anAttribute.getName() + ");");
                            pw.println("            uPosition += 4; // mike check");
                        }
                    }
                    pw.println("        }");
                    pw.println();
                    break;
                    
                case PADTO16:
                    pw.println("        "+anAttribute.getName() + " = new byte[Align.from16bits(uPosition,dis)];");
                    pw.println("        uPosition += " + anAttribute.getName() + ".length;");
                    break;
                case PADTO32:
                    pw.println("        "+anAttribute.getName() + " = new byte[Align.from32bits(uPosition,dis)];");
                    pw.println("        uPosition += " + anAttribute.getName() + ".length;");
                    break;
                case PADTO64:
                    pw.println("        "+anAttribute.getName() + " = new byte[Align.from64bits(uPosition,dis)];");
                    pw.println("        uPosition += " + anAttribute.getName() + ".length;");
                    break;                   
            }
        } // End of loop through ivars for writing the unmarshal method

        pw.println("    }\n    catch(Exception e)");
        pw.println("    { \n      System.err.println(e); \n    }");
        
        pw.println("    return getMarshalledSize();");
        pw.println("}\n");
    }

    private void writeMarshalMethodWithByteBuffer(PrintWriter pw, GeneratedClass aClass)
    {
        //pw.println();
        pw.println("/**");
        pw.println(" * Packs an object into the ByteBuffer.");
        pw.println(" * @throws java.nio.BufferOverflowException if byteBuffer is too small");
        pw.println(" * @throws java.nio.ReadOnlyBufferException if byteBuffer is read only");
        pw.println(" * @see java.nio.ByteBuffer");
        pw.println(" * @param byteBuffer The ByteBuffer at the position to begin writing");
        pw.println(" * @throws Exception ByteBuffer-generated exception");
        pw.println(" */");
        pw.println("public void marshal(java.nio.ByteBuffer byteBuffer) throws Exception");
        pw.println("{");

        // If we're a sublcass of another class, we should first call super
        // to make sure the superclass's ivars are marshaled out.

        if(!(aClass.getParentClass().equalsIgnoreCase("root")))
            pw.println("   super.marshal(byteBuffer);");

        //pw.println("    try \n    {");

        // Loop through the class attributes, generating the output for each.
        for(ClassAttribute anAttribute: aClass.getClassAttributes()) {
            if(anAttribute.shouldSerialize == false) {
                 pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                 continue;
            }
            
            String marshalType;
            String capped;
            
            switch(anAttribute.getAttributeKind()) {
                case PRIMITIVE:
                    marshalType = marshalTypes.getProperty(anAttribute.getType());
                    capped = this.initialCap(marshalType);
                    if( capped.equals("Byte") )
                        capped = "";    // ByteBuffer just has put() for bytes

                    // If we're a normal primitivetype, marshal out directly; otherwise, marshall out
                    // the list length.
                    if(anAttribute.getIsDynamicListLengthField())
                       pw.println("   byteBuffer.put" + capped + "( (" + marshalType + ")" + anAttribute.getDynamicListClassAttribute().getName() + ".size());");
                    else if(anAttribute.getIsPrimitiveListLengthField())
                       pw.println("   byteBuffer.put" + capped + "( (" + marshalType + ")" + anAttribute.getDynamicListClassAttribute().getName() + ".length);");
                    else
                       pw.println("   byteBuffer.put" + capped + "( (" + marshalType + ")" + anAttribute.getName() + ");");

                    break;
                    
                case SISO_ENUM:
                case SISO_BITFIELD:
                case CLASSREF:
                    pw.println("   " + anAttribute.getName() + ".marshal(byteBuffer);" );
                    break;
                    
                case PRIMITIVE_LIST:
                    pw.println();
                    pw.println("   for(int idx = 0; idx < " + anAttribute.getName() + ".length; idx++)");

                    // This is some sleaze. We're an array, but an array of what? We could be either a
                    // primitive or a class. We need to figure out which. This is done via the expedient
                    // but not very reliable way of trying to do a lookup on the type. If we don't find
                    // it in our map of primitives to marshal types, we assume it is a class.

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    if(anAttribute.getUnderlyingTypeIsPrimitive())
                    {
                        capped = this.initialCap(marshalType);
                        if( capped.equals("Byte") )
                            capped = "";    // ByteBuffer just has put() for bytes
                        pw.println("       byteBuffer.put" + capped + "((" + marshalType + ")" + anAttribute.getName() + "[idx]);");
                    }
                    else
                        pw.println("       " + anAttribute.getName() + "[idx].marshal(byteBuffer);" ); //"[idx].marshal(dos);" )

                    pw.println();
                    break;
                    
                case OBJECT_LIST:
                    pw.println();
                    pw.println("   for(int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");
                    pw.println("   {");

                    // This is some sleaze. We're an array, but an array of what? We could be either a
                    // primitive or a class. We need to figure out which. This is done via the expedient
                    // but not very reliable way of trying to do a lookup on the type. If we don't find
                    // it in our map of primitives to marshal types, we assume it is a class.

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    if(anAttribute.getUnderlyingTypeIsPrimitive())
                    {
                        capped = this.initialCap(marshalType);
                        if( capped.equals("Byte") ){
                            capped = "";    // ByteBuffer just uses put() for bytes
                        }
                        //pw.println("           dos.write" + capped + "(" + anAttribute.getName() + ");");
                        pw.println("       byteBuffer.put" + capped + "(" + anAttribute.getName() + ");");
                    }
                    else
                    {
                        pw.println("        " + anAttribute.getType() + " a" + initialCap(anAttribute.getType() + " = " + anAttribute.getName() + ".get(idx);"));
                        pw.println("        a" + initialCap(anAttribute.getType()) + ".marshal(byteBuffer);" );
                    }

                    pw.println("   }");
                    pw.println();
                    break;
                                  
                case PADTO16:
                    pw.println("   "+anAttribute.getName()+" = new byte[Align.to16bits(byteBuffer)];");
                    break;
                case PADTO32:
                    pw.println("   "+anAttribute.getName()+" = new byte[Align.to32bits(byteBuffer)];");
                    break;
                case PADTO64:
                    pw.println("   "+anAttribute.getName()+" = new byte[Align.to64bits(byteBuffer)];");
                    break;
            }   
        } // End of loop through the ivars for a marshal method

        pw.println("}");
    }

    private void writeUnmarshallMethodWithByteBuffer(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println();
        pw.println("/**");
        pw.println(" * Unpacks a Pdu from the underlying data.");
        pw.println(" * @throws java.nio.BufferUnderflowException if byteBuffer is too small");
        pw.println(" * @see java.nio.ByteBuffer");
        pw.println(" * See <a href=\"https://en.wikipedia.org/wiki/Marshalling_(computer_science)\" target=\"_blank\">https://en.wikipedia.org/wiki/Marshalling_(computer_science)</a>");
        pw.println(" * @param byteBuffer The ByteBuffer at the position to begin reading");
        pw.println(" * @return marshalled serialized size in bytes");
        pw.println(" * @throws Exception ByteBuffer-generated exception");
        pw.println(" */");
        pw.println("public int unmarshal(java.nio.ByteBuffer byteBuffer) throws Exception"); // throws EnumNotFoundException");
        pw.println("{");

        if(!(aClass.getParentClass().equalsIgnoreCase("root")))
            pw.println("    super.unmarshal(byteBuffer);\n");

        // Loop through the class attributes, generating the output for each.
        for(ClassAttribute anAttribute : aClass.getClassAttributes()) { 

            if(anAttribute.shouldSerialize == false) {
                 pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                 continue;
            }
            String marshalType;
            String capped;
            switch(anAttribute.getAttributeKind()) {
                case PRIMITIVE:
                    marshalType = unmarshalTypes.getProperty(anAttribute.getType());
                    capped = this.initialCap(marshalType);
                    if( capped.equals("Byte") )
                        capped = "";
                
                    if(marshalType.equalsIgnoreCase("UnsignedByte"))
                        pw.println("    " + anAttribute.getName() + " = (byte)(byteBuffer.get() & 0xFF);");               
                    else if (marshalType.equalsIgnoreCase("UnsignedShort"))
                        pw.println("    " + anAttribute.getName() + " = (short)(byteBuffer.getShort() & 0xFFFF);");               
                    else
                        pw.println("    " + anAttribute.getName() + " = byteBuffer.get" + capped + "();");
                    break;
                    
                case SISO_ENUM:
                    pw.println("    " + anAttribute.getName() + " = "+anAttribute.getType()+".unmarshalEnum(byteBuffer);");
                    break;
                    
                case SISO_BITFIELD:
                case CLASSREF:         
                    pw.println("    " + anAttribute.getName() + ".unmarshal(byteBuffer);" );
                    break;

                case PRIMITIVE_LIST:
                    pw.println("    for(int idx = 0; idx < " + anAttribute.getName() + ".length; idx++)");

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    if(marshalType == null) // It's a class  // should be unnecessary w/ refactor
                        pw.println("        " + anAttribute.getName() + "[idx].unmarshal(byteBuffer);" );
                    else { // It's a primitive
                        capped = this.initialCap(marshalType);
                        if( capped.equals("Byte") )
                             capped = "";
                        pw.println("        " +  anAttribute.getName() + "[idx] = byteBuffer.get" + capped + "();");
                    }
                    break;
                    
                case OBJECT_LIST:
                    if(anAttribute.getCountFieldName() != null)
                        pw.println("    for(int idx = 0; idx < " + anAttribute.getCountFieldName() + "; idx++)");
                    else
                        pw.println("    for(int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");
                
                    pw.println("    {");

                    if(anAttribute.getUnderlyingTypeIsEnum()) {
                        pw.println("    " +anAttribute.getType() + " anX = "+anAttribute.getType() + ".unmarshalEnum(byteBuffer);");
                        pw.println("    " + anAttribute.getName() + ".add(anX);");
                    }
                    else {
                        marshalType = marshalTypes.getProperty(anAttribute.getType());

                        if(marshalType == null) { // It's a class
                            pw.println("    " + anAttribute.getType() + " anX = new " + anAttribute.getType() + "();");                           
                            pw.println("    anX.unmarshal(byteBuffer);");
                            pw.println("    " + anAttribute.getName() + ".add(anX);");
                        }
                        else { // It's a primitive  // should be unnecessary now w/ refactor
                            capped = this.initialCap(marshalType);
                            if( capped.equals("Byte") )
                                capped = "";
                            pw.println("    byteBuffer.get" + capped + "(" + anAttribute.getName() + ");");
                        }
                    }
                    pw.println("    }");
                    pw.println();
                    break;
                    
                                    
                case PADTO16:
                    pw.println("    "+anAttribute.getName() + " = new byte[Align.from16bits(byteBuffer)];");
                    break;
                case PADTO32:
                    pw.println("    "+anAttribute.getName() + " = new byte[Align.from32bits(byteBuffer)];");
                    break;
                case PADTO64:
                    pw.println("    "+anAttribute.getName() + " = new byte[Align.from64bits(byteBuffer)];");
                    break;
            }

        } // End of loop through ivars for writing the unmarshal method
        pw.println("    return getMarshalledSize();");
        pw.println("}\n");
    }

    /**
     * Placed in the {@link Pdu} class, this method provides a convenient,
     * though inefficient way to marshal a Pdu. Better is to reuse a
     * ByteBuffer and pass it along to the similarly-named method, but
     * still, there's something to be said for convenience.
     *
     * <pre>public byte[] marshal(){
     *     byte[] data = new byte[getMarshalledSize()];
     *     java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.wrap(data);
     *     marshal(byteBuffer);
     *     return data;
     * }</pre>
     *
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    private void writeMarshalMethodToByteArray(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println();
        pw.println("/**");
        pw.println(" * A convenience method for marshalling to a byte array.");
        pw.println(" * This is not as efficient as reusing a ByteBuffer, but it <em>is</em> easy.");
        pw.println(" * @return a byte array with the marshalled {@link Pdu}");
        pw.println(" * @throws Exception ByteBuffer-generated exception");
        pw.println(" */");
        pw.println("public byte[] marshal() throws Exception");
        pw.println("{");
        pw.println("    byte[] data = new byte[getMarshalledSize()];");
        pw.println("    java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.wrap(data);");
        pw.println("    marshal(byteBuffer);");
        pw.println("    return data;");
        pw.println("}");

    }
    
  
    /**
     * Generate method to write out data in XML format.
     */
/*
    private void writeXmlMarshallMethod(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println();
        pw.println("public void marshalXml(PrintWriter textWriter)");
        pw.println("{");
         
        // If we're a sublcass of another class, we should first call super
        // to make sure the superclass's ivars are marshaled out. after we
        // marshall all of our stuff out, we need to call the superclass again
        // to get the close tag.
        
        String superclassName = aClass.getParentClass();
        if(!(superclassName.equalsIgnoreCase("root")))
        {
            pw.println("    super.marshalXml(textWriter);");
            pw.println();
        }
        
       
        pw.println("    try \n    {");
        
        // Loop through the class attributes, generating the output for each.
        
        List ivars = aClass.getClassAttributes();
        
        // write the tag for this class, eg <header
        System.out.println("        textWriter.print(\"<" + aClass.getName());
        
        // First, we need to write out all the primitive values in this class as attributes. We
        // have to loop through all the attributes, selecting only the primitive types. If we
        // want to be official, we should short the names alphabetically as well to conform
        // to canonical XML.
        for(int idx = 0; idx < ivars.size(); idx++)
        {
            ClassAttribute anAttribute = (ClassAttribute)ivars.get(idx);
    
            if(anAttribute.shouldSerialize == false)
            {
                 pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                 continue;
            }
        
            // Write out a method call to serialize a primitive type
            if(anAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.PRIMITIVE)
            {
                // If we're a normal primitive type, marshal out directly; otherwise, marshall out
                // the list length.
                if(anAttribute.getIsDynamicListLengthField() == false)
                {
                     pw.print( "      textWriter.print(" "" + anAttribute.getName() + "" + "="" " + "this.get" + this.initialCap(anAttribute.getName()) + "();");
                }
               else
               {
                   ClassAttribute listAttribute = anAttribute.getDynamicListClassAttribute();
                   //pw.println("       dos.write" + capped + "( (" + marshalType + ")" + listAttribute.getName() + ".size());");
               }
                
            }
        } // End of loop through primitive types
 */      
        /*
            // Write out a method call to serialize a class.
            if( anAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.CLASSREF )
            {
                String marshalType = anAttribute.getType();
            
                pw.println("       " + anAttribute.getName() + ".marshal(dos);" );
            }
            
            // Write out the method call to marshal a fixed length list, aka an array.
            if( (anAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.FIXED_LIST) )
            {
                pw.println();
                pw.println("       for(int idx = 0; idx < " + anAttribute.getName() + ".length; idx++)");
                pw.println("       {");
                
                // This is some sleaze. We're an array, but an array of what? We could be either a
                // primitive or a class. We need to figure out which. This is done via the expedient
                // but not very reliable way of trying to do a lookup on the type. If we don't find
                // it in our map of primitives to marshal types, we assume it is a class.
                
                String marshalType = marshalTypes.getProperty(anAttribute.getType());
                
                if(anAttribute.getUnderlyingTypeIsPrimitive())
                {
                    String capped = this.initialCap(marshalType);
                    pw.println("           dos.write" + capped + "(" + anAttribute.getName() + "[idx]);");
                }
                else
                {
                     pw.println("           " + anAttribute.getName() + "[idx].marshal(dos);" );
                }
            
                pw.println("       } // end of array marshaling");
                pw.println();
            }
            
            // Write out a section of code to marshal a variable length list. The code should look like
            //
            // for(int idx = 0; idx < attrName.size(); idx++)
            // { anAttribute.marshal(dos);
            // }
            //    
            
            if( (anAttribute.getAttributeKind() == ClassAttribute.ClassAttributeType.VARIABLE_LIST) )
            {
                pw.println();
                pw.println("       for(int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");
                pw.println("       {");
                
                // This is some sleaze. We're an array, but an array of what? We could be either a
                // primitive or a class. We need to figure out which. This is done via the expedient
                // but not very reliable way of trying to do a lookup on the type. If we don't find
                // it in our map of primitives to marshal types, we assume it is a class.
                
                String marshalType = marshalTypes.getProperty(anAttribute.getType());
                
                if(anAttribute.getUnderlyingTypeIsPrimitive())
                {
                    String capped = this.initialCap(marshalType);
                    pw.println("           dos.write" + capped + "(" + anAttribute.getName() + ");");
                }
                else
                {
                    pw.println("            " + anAttribute.getType() + " a" + initialCap(anAttribute.getType() + " = (" + anAttribute.getType() + ")" +
                                                                                     anAttribute.getName() + ".get(idx);"));
                    pw.println("            a" + initialCap(anAttribute.getType()) + ".marshal(dos);" );
                }
                
                pw.println("       } // end of list marshalling");
                pw.println();
            }   
        } // End of loop through the ivars for a marshal method
        */
       /* 
        pw.println("    } // end try \n    catch(IOException e)");
        pw.println("    { \n      System.err.println(e);}");
        
        pw.println("    } // end of marshalXml method");
        
        
    }
   */
 
    /**
     * Write the code for an equality operator. This allows you to compare two
     * objects for equality.The code should look like
     *
     * bool operator ==(const ClassName&amp; rhs) return (_ivar1==rhs._ivar1 &amp;&amp;
     * _var2 == rhs._ivar2 ...)
     *
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    public void writeEqualityMethod(PrintWriter pw, GeneratedClass aClass) {
        try {
            //pw.println();
            pw.println(" /*");
            pw.println("  * Override of default equals method.  Calls equalsImpl() for content comparison.");
            pw.println("  */");
            pw.println("@Override");
            pw.println(" public boolean equals(Object obj)");
            pw.println(" {");
            pw.println("    if(this == obj)");
            pw.println("      return true;");
            pw.println();
            pw.println("    if(obj == null)");
            pw.println("       return false;");
            pw.println();
            pw.println("    if(!getClass().isAssignableFrom(obj.getClass())) //if(getClass() != obj.getClass())");
            pw.println("        return false;");
            pw.println();
            pw.println("    return equalsImpl(obj);");
            pw.println(" }");
        } catch (Exception e) {
            System.err.println(e);
        }

        writeEqualityImplMethod(pw, aClass); // Write impl for establishing content equality
    }

  /**
     * write equalsImpl(...) method to this class to parent or subclasses
     *
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    public void writeEqualityImplMethod(PrintWriter pw, GeneratedClass aClass)
    {
        try {
            pw.println();

            if (aClass.getParentClass().equalsIgnoreCase("root")) {
                pw.println(" /**");
                pw.println("  * Compare all fields that contribute to the state, ignoring");
                pw.println("  * transient and static fields, for <code>this</code> and the supplied object");
                pw.println("  * @param obj the object to compare to");
                pw.println("  * @return true if the objects are equal, false otherwise.");
                pw.println("  */");
            }
            else {
                pw.println("@Override");
            }
            pw.println(" public boolean equalsImpl(Object obj)");
            pw.println(" {");
            pw.println("     boolean ivarsEqual = true;");
            pw.println();
            /*
            redundant with equals method above
            pw.println("    if(!(obj instanceof " + aClass.getName() + "))");
            pw.println("        return false;");
            pw.println();
            */
            pw.println("     final " + aClass.getName() + " rhs = ("
                + aClass.getName() + ")obj;");
            pw.println();

          for (int idx = 0; idx < aClass.getClassAttributes().size(); idx++) {
            ClassAttribute anAttribute = aClass.getClassAttributes().get(idx);
            if (anAttribute.isHidden())
              continue;
            String attname = anAttribute.getName();
            
            switch (anAttribute.getAttributeKind()) {
              case PRIMITIVE:
                pw.println("     if( ! (" + attname + " == rhs." + attname + ")) ivarsEqual = false;");
                break;

              case SISO_ENUM:
                pw.println("     if( ! (" + attname + " == rhs." + attname + ")) ivarsEqual = false;");
                break;

              case SISO_BITFIELD:
              case CLASSREF:
                pw.println("     if( ! (" + attname + ".equals( rhs." + attname + ") )) ivarsEqual = false;");
                break;
                
              case PRIMITIVE_LIST:
                pw.println();
                pw.println("     for(int idx = 0; idx < "+ anAttribute.getListLength() + "; idx++)");
                pw.println("     {");
                pw.println("          if(!(" + attname + "[idx] == rhs." + attname + "[idx])) ivarsEqual = false;");
                pw.println("     }");
                pw.println();
                break;

              case OBJECT_LIST:
                pw.println();
                pw.println("     for(int idx = 0; idx < " + attname + ".size(); idx++)");
                pw.println("        if( ! ( " + attname + ".get(idx).equals(rhs." + attname + ".get(idx)))) ivarsEqual = false;");
                pw.println();
                break;
            }
          }

            //pw.println();
            if (aClass.getParentClass().equalsIgnoreCase("root")) {
                pw.println("    return ivarsEqual;");
            }
            else {
                pw.println("    return ivarsEqual && super.equalsImpl(rhs);");
            }
            pw.println(" }");
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
 
    /**
     * Build the toString() method for this class, using the toString() methods of the
     * fields of the object
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    public void writeToStringMethod(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println();
        pw.println(" @Override");
        pw.println(" public String toString()");
        pw.println(" {");
        pw.println("    StringBuilder sb = new StringBuilder();");
        pw.println("    sb.append(getClass().getSimpleName()).append(\":\\n\");\n");

        ArrayList<ClassAttribute> objlists = new ArrayList<>();

        aClass.getClassAttributes().forEach(attr -> {
            if (!attr.isHidden()) {
                switch(attr.getAttributeKind()) {
                    case PRIMITIVE_LIST:
                        writePrimitiveList(pw,attr);
                        break;
                    case OBJECT_LIST:
                        objlists.add(attr);
                        break;
                    default:
                        writeOneToString(pw,attr);
                }
            }
        });

        if (!objlists.isEmpty())
            objlists.forEach(attr -> writeList(pw, attr));
    
        pw.println();
        pw.println("   return sb.toString();");
        pw.println(" }");
    }
  
    private void writePrimitiveList(PrintWriter pw, ClassAttribute attr)
    {
        pw.print("    sb.append(\" ");
        pw.print(attr.getName());
        pw.println(": \").append(\"\\n\");");
        pw.print("    sb.append(Arrays.toString(");
        pw.print(attr.getName());
        pw.println(")).append(\"\\n\");");
    }
  
    private void writeList(PrintWriter pw, ClassAttribute attr)
    {
        pw.print("    sb.append(\" ");
        pw.print(attr.getName());
        pw.println(": \").append(\"\\n\");");
        pw.print("    ");
        pw.print(attr.getName());
        pw.println(".forEach(r->{ sb.append(r.getClass().getSimpleName()).append(\": \").append(r).append(\"\\n\");});");
    
    }
    
    private void writeOneToString(PrintWriter pw, ClassAttribute attr)
    {
        pw.print("    sb.append(\" ");
        pw.print(attr.getName());
        pw.print(": \").append(");
        pw.print(attr.getName());
        pw.println(").append(\"\\n\");");
    }
   
    /** 
     * returns a string with the first letter capitalized.
     * @param aString of interest
     * @return same string with first letter capitalized
     */
    @Override
    public String initialCap(String aString)
    {
      if(aString == null)   //test test!
        return "";
      
      StringBuffer stb = new StringBuffer(aString);
      stb.setCharAt(0, Character.toUpperCase(aString.charAt(0)));

      return new String(stb);
    }
    
    /**
     * returns a string with the first letter lower case.
     */
    @Override
    public String initialLower(String aString)
    {
        StringBuffer stb = new StringBuffer(aString);
        stb.setCharAt(0, Character.toLowerCase(aString.charAt(0)));

        return new String(stb);
    }

}
