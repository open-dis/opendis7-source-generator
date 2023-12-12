/**
 * Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD-style open-source license, see project license.html and license.txt
 */
package edu.nps.moves.dis7.source.generator.pdus;

import edu.nps.moves.dis7.source.generator.pdus.GeneratedClassAttribute.ClassAttributeType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class autogenerates Java source code from XML PDU definitions, specifically 
 * producing most source code needed for the opendis7-java distribution.
 * Given the input object, something of an abstract syntax tree, this generates a source code file in the Java language.
 * It has ivars, getters, setters, and serialization/deserialization methods.
 * Fully implemented.
 * @see AbstractGenerator
 * @see PythonGenerator
 * @author DMcG
 */
public class JavaGenerator extends AbstractGenerator
{
    /**
     * Some Java (or Java-like) distributions don't have the full Java JDK 1.6 stack, such as Android. That means no JAXB, and no Hibernate/JPA. These booleans flip on or off the generation of the
     * annotations that use these features in the generated Java code.
     */

    /**
     * Maps the primitive types listed in the XML file (key) to the java types (value)
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

    /**
     * Constructor
     * @param pClassDescriptions String map of generated classes
     * @param pJavaProperties language properties
     */
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

            super.setGeneratedSourceDirectoryName(clDirectory);

            System.out.println("Source code directory set to " + clDirectory);
        }
        catch (Exception e) {
            System.err.println("Required property not set. Modify the XML file to include the missing property");
            System.err.println(e);
            System.exit(-1);
        }

        final String UNSIGNED_INT8 = "uint8"; // TODO generalize this approach
        
        // Set up a mapping between the strings used in the Open-DIS XML file (key) and the strings used
        // in the generated java file (value), specifically the data types. This could be externalized to
        // a properties file, but there's only a dozen or so and an external props file
        // would just add some complexity.
        
        // don't quite get this....  looks in error, duplicating marshallTypes.  
        // TODO rename all occurrences to marshallTypes since they exactly match (DRY principle)
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
    
    private String        packageInfoPath;
    private File          packageInfoFile;
    private StringBuilder packageInfoBuilder;

    /**
     * Generate the classes and write them to a directory
     */
    @Override
    public void writeClasses()
    {
        int classCount = 0;
        
        readTemplates();  // get the license
        
        createGeneratedSourceDirectory(true); // boolean: whether to clean out prior files, if any exist in that directory

        Iterator classDescriptionsIterator = classDescriptions.values().iterator();

        while (classDescriptionsIterator.hasNext()) {
            try {
                GeneratedClass aClass     = (GeneratedClass) classDescriptionsIterator.next();
                String         aClassName = aClass.getName();
//                String  pduSubpackageName = "pdus";

                // Create package structure, if any
                String aClassPackageName = languageProperties.getProperty("package");
                String aClassFullPath;

                // If we have a package specified, replace the dots in the package name (edu.nps.moves.dis)
                // with slashes (edu/nps/moves/dis and create that directory
                if (aClassPackageName != null) {
                    aClassPackageName = aClassPackageName.replace(".", "/");
                    aClassFullPath  = getGeneratedSourceDirectoryName() + "/" + aClassPackageName + "/" ;
//                    if (aClassName.endsWith("Pdu") || aClassPackageName.endsWith("dis7")) //  is there a better way to distinguish?
//                       aClassFullPath += pduSubpackageName + "/";
                    aClassFullPath += aClassName + ".java";
                    //System.out.println("full path is " + fullPath);
                }
                else {
                    aClassFullPath = getGeneratedSourceDirectoryName() + "/" + aClassName + ".java";
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
        
        packageInfoPath = getGeneratedSourceDirectoryName() + "/edu/nps/moves/dis7/pdus/" + "package-info.java";
        packageInfoFile = new File(packageInfoPath);
        
        FileWriter packageInfoFileWriter;
        try 
        {
            packageInfoFile.createNewFile();
            packageInfoFileWriter = new FileWriter(packageInfoFile, StandardCharsets.UTF_8);
            packageInfoBuilder = new StringBuilder();
            packageInfoBuilder.append("/**\n");
            packageInfoBuilder.append(" * IEEE DIS Protocol Data Unit (PDU) packet definition classes.\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append(" * <p> Online references: </p>\n");
            packageInfoBuilder.append(" * <ul>\n");
            packageInfoBuilder.append(" *      <li> GitHub <a href=\"https://github.com/open-dis/opendis7-java\" target=\"_blank\">opendis7-java library</a> </li> \n");
            packageInfoBuilder.append(" *      <li> NPS <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/examples/src/OpenDis7Examples\" target=\"MV3500\">MV3500 Distributed Simulation Fundamentals course examples</a> </li> \n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500/-/tree/master/specifications/README.md\" target=\"README.MV3500\">IEEE and SISO specification references</a> of interest</li> \n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostandards.org/DigitalLibrary.aspx?Command=Core_Download&amp;EntryId=46172\" target=\"SISO-REF-010\" >SISO-REF-010-2023 Reference for Enumerations for Simulation Interoperability</a> </li> \n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://www.sisostandards.org/DigitalLibrary.aspx?Command=Core_Download&amp;EntryId=47284\" target=\"SISO-REF-10.1\">SISO-REF-10.1-2019 Reference for Enumerations for Simulation, Operations Manual</a></li>\n");
            packageInfoBuilder.append(" *      <li> <a href=\"https://savage.nps.edu/opendis7-java/javadoc\" target=\"_blank\">open-dis7 Javadoc</a>, <a href=\"https://savage.nps.edu/opendis7-java/xml/DIS_7_2012.autogenerated.xsd\" target=\"_blank\">open-dis7 XML Schema</a>and <a href=\"https://savage.nps.edu/opendis7-java/xml/SchemaDocumentation\" target=\"_blank\">open-dis7 XML Schema documentation</a></li> </ul>\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append(" * @see java.lang.Package\n");
            packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful\">https://stackoverflow.com/questions/22095487/why-is-package-info-java-useful</a>\n");
            packageInfoBuilder.append(" * @see <a href=\"https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java\">https://stackoverflow.com/questions/624422/how-do-i-document-packages-in-java</a>\n");
            packageInfoBuilder.append(" */\n");
            packageInfoBuilder.append("\n");
            packageInfoBuilder.append("package edu.nps.moves.dis7.pdus;\n");

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
        writeCopyMethods(pw, aClass);
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
        
        if      (aClass.getName().equals("Pdu"))
                 writePduUtilityMethods(pw, aClass);
        else if (aClass.getName().startsWith("EntityStatePdu"))
                 writeEntityStateUtilityMethods(pw, aClass);
        
        pw.println("} // end of class");
        pw.flush();
        pw.close();
    }
    /** Additional methods of interest for Pdu class */
    private void writePduUtilityMethods(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println();
        pw.println("// autogenerated by JavaGenerator.writePduUtilityMethods()");
        pw.println();
        
        StringBuilder utilitySourceCodeBlock = new StringBuilder()
            .append("/** Utility setter for {@link Pdu#timestamp} converting double (or float) to\n")
            .append("  * Timestamp in seconds at 2^31 - 1 units past top of hour\n")
            .append("  * @see setTimestamp\n")
            .append("  * @see edu.nps.moves.dis7.utilities.DisTime\n")
            .append("  * @param newTimestamp new timestamp in seconds\n")
            .append("  * @return same object to permit progressive setters */\n")
            .append("public synchronized Pdu setTimestampSeconds(double newTimestamp)\n")
            .append("{\n")
            .append("    timestamp = (int) ((newTimestamp * 3600.0) / Integer.MAX_VALUE);\n")
            .append("    return this;\n")
            .append("}      \n")
            .append("/** Utility getter for {@link Pdu#timestamp} converting \n")
            .append("  * integer timestamp at 2^31 - 1 units past top of hour to double (or float)\n")
            .append("  * @see getTimestamp\n")
            .append("  * @see edu.nps.moves.dis7.utilities.DisTime\n")
            .append("  * @return fractional timestamp past hour */\n")
            .append("public double getTimestampSeconds()\n")
            .append("{\n")
            .append("    return timestamp * Integer.MAX_VALUE / 3600.0;\n")
            .append("}\n")
            .append(" /**\n")
            .append("  * Whether or not timestamp for this Pdu occurs after timestamp as another Pdu.\n")
            .append("  * @param pdu2 second Pdu for comparison\n")
            .append("  * @return whether timestamp for this Pdu occurs later\n")
            .append("  */\n")
            .append("  public boolean occursAfter(Pdu pdu2)\n")
            .append("  {\n")
            .append("     return (getTimestamp() < pdu2.getTimestamp());\n")
            .append("  }\n")
            .append(" /**\n")
            .append("  * Whether or not timestamp for this Pdu occurs before timestamp as another Pdu.\n")
            .append("  * @param pdu2 second Pdu for comparison\n")
            .append("  * @return whether timestamp for this Pdu occurs earlier\n")
            .append("  */\n")
            .append("  public boolean occursBefore(Pdu pdu2)\n")
            .append("  {\n")
            .append("     return (getTimestamp() < pdu2.getTimestamp());\n")
            .append("  }\n")
            .append(" /**\n")
            .append("  * Whether or not this Pdu occurs at same timestamp as another Pdu.\n")
            .append("  * @param pdu2 second Pdu for comparison\n")
            .append("  * @return whether timestamps are identical for both Pdus\n")
            .append("  */\n")
            .append("  public boolean occursSameTime(Pdu pdu2)\n")
            .append("  {\n")
            .append("     return (getTimestamp() == pdu2.getTimestamp());\n")
            .append("  }\n");
        
        pw.println(utilitySourceCodeBlock.toString());
        pw.println();
    }
    /** Additional methods of interest */
    private void writeEntityStateUtilityMethods(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println("    // writeEntityStateUtilityMethods");
        pw.println();
        
        StringBuilder utilitySourceCodeBlock = new StringBuilder() 
        // """multiline text block""" would be nice but that is JDK 14+
        // https://stackoverflow.com/questions/878573/does-java-have-support-for-multiline-strings
            .append("  /** Direction enumerations */\n")
            .append("  public enum Direction\n")
            .append("  {\n")
            .append("      /** NORTH direction along Y axis */\n")
            .append("      NORTH, \n")
            .append("      /** NORTHEAST direction */\n")
            .append("      NORTHEAST, \n")
            .append("      /** EAST direction along X axis */\n")
            .append("      EAST, \n")
            .append("      /** SOUTHEAST direction */\n")
            .append("      SOUTHEAST, \n")
            .append("      /** SOUTH direction along -Y axis */\n")
            .append("      SOUTH, \n")
            .append("      /** SOUTHWEST direction */\n")
            .append("      SOUTHWEST, \n")
            .append("      /** WEST direction along -X axis */\n")
            .append("      WEST, \n")
            .append("      /** NORTHWEST direction */\n")
            .append("      NORTHWEST\n")
            .append("  }\n")
            .append("\n")
                
            .append("  /** Utility method to set entity linear velocity using speed and direction\n")
            .append("    * @param speed in meters/second\n")
            .append("    * @param direction using Directions enumerations\n")
            .append("    * @see Direction\n")
            .append("    * @return same object to permit progressive setters */\n")
            .append("  public final synchronized EntityStatePdu setEntityLinearVelocity (float speed, Direction direction)\n")
            .append("  {\n")
            .append("      float xFactor = 0.0f;\n")
            .append("      float yFactor = 0.0f;\n")
            .append("      switch (direction)\n")
            .append("      {\n")
            .append("          case NORTH:\n")
            .append("              xFactor =  0.0f;   yFactor =  1.0f;\n")
            .append("              break;\n")
            .append("              \n")
            .append("          case EAST:\n")
            .append("              xFactor =  1.0f;   yFactor =  0.0f;\n")
            .append("              break;\n")
            .append("              \n")
            .append("          case SOUTH:\n")
            .append("              xFactor =  0.0f;   yFactor = -1.0f;\n")
            .append("              break;\n")
            .append("              \n")
            .append("          case WEST:\n")
            .append("              xFactor = -1.0f;   yFactor =  0.0f;\n")
            .append("              break;\n")
            .append("          case NORTHEAST:\n")
            .append("              xFactor =  0.7071f;   yFactor =  0.7071f;\n")
            .append("              break;\n")
            .append("              \n")
            .append("          case SOUTHEAST:\n")
            .append("              xFactor = -0.7071f;   yFactor =  0.7071f;\n")
            .append("              break;\n")
            .append("              \n")
            .append("          case SOUTHWEST:\n")
            .append("              xFactor = -0.7071f;   yFactor = -0.7071f;\n")
            .append("              break;\n")
            .append("              \n")
            .append("          case NORTHWEST:\n")
            .append("              xFactor =  0.7071f;   yFactor = -0.7071f;\n")
            .append("              break;\n")
            .append("          default:\n")
            .append("              System.err.println(\"*** unexpected internal error, encountered illegal EntityStatePdu Direction\");\n")
            .append("              \n")
            .append("      }\n")
            .append("      Vector3Float newVelocity = new Vector3Float().setX(xFactor*speed).setY(yFactor*speed).setZ(getEntityLinearVelocity().z);\n")
            .append("      setEntityLinearVelocity(newVelocity);\n")
            .append("      return this;\n")
            .append("    }\n")
                
            .append("   /** Setter for {@link EntityStatePdu#entityLocation}\n")
            .append("     * @param x location\n")
            .append("     * @param y location\n")
            .append("     * @param z location\n")
            .append("     * @return same object to permit progressive setters */\n")
            .append("   public synchronized EntityStatePdu setEntityLocation(double x, double y, double z)\n")
            .append("   {\n")
            .append("       // TODO autogenerate such utility constructors\n")
            .append("       entityLocation = new Vector3Double().setX(x).setY(y).setZ(z);\n")
            .append("       return this;\n")
            .append("   }\n")
        
            .append("   /** Advance location using linear velocities for a single timestep\n")
            .append("    * @param timestep duration of travel\n")
            .append("    * @return same object to permit progressive setters */\n")
            .append("    public EntityStatePdu advanceEntityLocation(double timestep)\n")
            .append("    {\n")
            .append("        Vector3Double location = getEntityLocation();\n")
            .append("        Vector3Float  velocity = getEntityLinearVelocity();\n")
            .append("        setEntityLocation(location.getX() + velocity.getX() * timestep,\n")
            .append("                          location.getY() + velocity.getY() * timestep,\n")
            .append("                          location.getZ() + velocity.getZ() * timestep);\n")
            .append("       return this;\n")
            .append("    }\n")

            .append("    /** Setter for {@link EntityStatePdu#entityOrientation}\n")
            .append("      * @param phi new value of interest\n")
            .append("      * @param theta new value of interest\n")
            .append("      * @param psi new value of interest\n")
            .append("      * @return same object to permit progressive setters */\n")
            .append("    public synchronized EntityStatePdu setEntityOrientation(float phi, float theta, float psi)\n")
            .append("    {\n")
            .append("        // TODO autogenerate such utility constructors\n")
            .append("        EulerAngles pEntityOrientation = new EulerAngles();\n")
            .append("        pEntityOrientation = pEntityOrientation.setPhi(phi).setTheta(theta).setPsi(psi);\n")
            .append("        entityOrientation = pEntityOrientation;\n")
            .append("        return this;\n")
            .append("    }\n")
                
            .append("   /** Marking utility to clear character values\n")
            .append("    * @return same object to permit progressive setters */\n")
            .append("    public synchronized EntityStatePdu clearMarking()\n")
            .append("   {\n")
            .append("       byte[] emptyByteArray = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};\n")
            .append("       marking.setCharacters(emptyByteArray);\n")
            .append("       return this;\n")
            .append("   }\n")
                
            .append("    /** Marking utility to set character values, 11 characters maximum\n")
            .append("    *@param newMarking new 11-character string to assign as marking value\n")
            .append("    * @return same object to permit progressive setters */\n")
            .append("   public synchronized EntityStatePdu setMarking(String newMarking)\n")
            .append("   {\n")
            .append("       if ((newMarking == null) || newMarking.isEmpty())\n")
            .append("           clearMarking();\n")
            .append("       else if (newMarking.length() > 11)\n")
            .append("           System.err.println (\"*** marking '\" + newMarking + \"' is greater than 11 characters, truncating\");\n")
            .append("       newMarking = String.format(\"%11s\", newMarking);\n")
            .append("       marking.setCharacters(newMarking.getBytes());\n")
            .append("           \n")
            .append("       return this;\n")
            .append("   }\n")
                
            .append("   /** Marking utility to get character values as a string\n")
            .append("    * @return 11-character String value corresponding to marking */\n")
            .append("   public String getMarkingString()\n")
            .append("   {\n")
            .append("       return new String(marking.getCharacters());\n")
            .append("   }\n");
        
        // TODO downcaset Vector3Double to Vector3Float
        
        pw.println(utilitySourceCodeBlock.toString());
        pw.println();
    }

  /*  String domainTemplate1;
    String domainTemplate2;
    String domainTemplate3;*/
    String specSourceTemplate;
    String licenseTemplate;
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
        for (GeneratedClassAttribute attr : aClass.getClassAttributes()) {
            if(attr.getAttributeKind().equals(GeneratedClassAttribute.ClassAttributeType.SISO_ENUM)) {
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
         // domainTemplate1 = loadOneTemplate("domainpart1.txt");
         // domainTemplate2 = loadOneTemplate("domainpart2.txt");
         // domainTemplate3 = loadOneTemplate("domainpart3.txt");
            specSourceTemplate      = loadOneTemplate("dis7spec.txt");
            licenseTemplate         = loadOneTemplate("dis7javalicense.txt");
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
     * Write the license text as a java description at the top of the file.
     */
    
    private void writeLicense(PrintWriter printWriter, GeneratedClass aClass)
    {
      if(licenseTemplate == null)
        System.out.println("*** [bad pointer for licenseTemplate]");
      printWriter.println(licenseTemplate);
      printWriter.println();
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
        if (aClass.getName().equals(("Pdu")))
        {
            pw.println("import edu.nps.moves.dis7.utilities.PduFactory;");
        }
        else if (aClass.getName().endsWith("Pdu") && !aClass.getName().equals(("Pdu"))&& !aClass.isAbstract())
        {
            pw.println("import java.nio.ByteBuffer;"); // used by copy() method
//          pw.println("import edu.nps.moves.dis7.utilities.PduFactory;"); // not available for copy() method, in different package
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
        
        pw.println(" * "+specSourceTemplate);
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
        if(aClass.getAliasFor() != null) 
        {
          // need to avoid javadoc warning "warning: use of default constructor, which does not provide a comment"
          // https://docs.oracle.com/javase/tutorial/java/IandI/super.html
          pw.println("public final class "+aClass.getName()+" extends "+aClass.getAliasFor());
          pw.println("{");
          pw.println("   /** Default constructor for alias invokes superclass");
          pw.println("     * @see <a href=\"https://docs.oracle.com/javase/tutorial/java/IandI/super.html\" target=\"_blank\">https://docs.oracle.com/javase/tutorial/java/IandI/super.html</a>");
          pw.println("     */");
          pw.println("   public "+aClass.getName()+" ()");
          pw.println("   {");
          pw.println("       super();");
          pw.println("   }");
          pw.println("}");
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
        for (GeneratedClassAttribute anAttribute : aClass.getClassAttributes()) {
            if (anAttribute.shouldSerialize == false) {
                pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                continue;
            }
            String attributeType;
            int listLength;
            String className;
            
            String fieldaccess = "protected"; // allow subclassing anAttribute.isHidden()? "private":"protected";
            
            switch (anAttribute.attributeKind) {
                case STATIC_IVAR:
                    //if (anAttribute.getAttributeKind() == GeneratedClassAttribute.ClassAttributeType.STATIC_IVAR) {
                    attributeType = types.getProperty(anAttribute.getType());
                    String value = anAttribute.getDefaultValue();
                    pw.print  ("   /** Default static instance variable */\n");
                    pw.print  ("   public static " + attributeType + "  " + anAttribute.getName());
                    pw.println(" = " + value + ";");
                    break;

                    // This attribute is a primitive. 
                case PRIMITIVE:
                    // The primitive type--we need to do a lookup from the abstract type in the
                    // xml to the java-specific type. The output should look something like
                    //
                    // /** This is a description */
                    // protected int foo;
                    //
                    attributeType = types.getProperty(anAttribute.getType());
                    if ((anAttribute.getComment() != null) && !anAttribute.getComment().trim().isEmpty())
                    {
                         pw.println("   /** " + anAttribute.getComment() + " */");
                    }
                    else pw.println("   /** " + anAttribute.getName() + " is an undescribed parameter... */");

                    String defaultValue = anAttribute.getDefaultValue();

                    pw.print("   " + fieldaccess + " " + attributeType + " " + anAttribute.getName());
                    if (defaultValue != null)
                        pw.print(" = (" + attributeType + ")" + defaultValue); // Needs cast to primitive type for float/double issues
                    pw.println(";\n");
                    break; // end of primitive attribute type

                // this attribute is a reference to another class defined in the XML document, The output should look like
                //
                // /** This is a description */
                // protected AClass foo = new AClass();
                //
                case CLASSREF:
                    attributeType = anAttribute.getType();
                    String initialClass = anAttribute.getInitialClass(); // most often null

                    if ((anAttribute.getComment() != null) && !anAttribute.getComment().trim().isEmpty())
                    {
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    }
                    else pw.println("   /** " + anAttribute.getName() + " is an undescribed parameter... */");

                    if(anAttribute.getDefaultValue() == null)
                        pw.println("   " + fieldaccess + " " + attributeType + "  " + anAttribute.getName() + " = new " + (initialClass == null ? attributeType : initialClass) + "(); \n");
                    else
                        pw.println("   " + fieldaccess + " " + attributeType + "  " + anAttribute.getName() + " =  " + anAttribute.getDefaultValue() + "; \n");
                    break;

                // The attribute is a fixed list, ie an array of some type--maybe primitve, maybe a class.
                case PRIMITIVE_LIST:
                    attributeType = anAttribute.getType();
                    listLength = anAttribute.getListLength();

                    if ((anAttribute.getComment() != null) && !anAttribute.getComment().trim().isEmpty())
                    {
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    }
                    else pw.println("   /** " + anAttribute.getName() + " is an undescribed parameter... */");

                    pw.println("   " + fieldaccess + " " + types.getProperty(attributeType) + "[]  " + anAttribute.getName() + " = new "
                        + types.getProperty(attributeType) + "[" + listLength + "]" + "; \n");
                    break;

                // The attribute is a variable list of some kind. 
                case OBJECT_LIST:
                    attributeType = anAttribute.getType();
                    listLength = anAttribute.getListLength();

                    if ((anAttribute.getComment() != null) && !anAttribute.getComment().trim().isEmpty())
                    {
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    }
                    else pw.println("   /** " + anAttribute.getName() + " is an undescribed parameter... */");

                    pw.println("   " + fieldaccess + " List< " + attributeType + " > " + anAttribute.getName() + " = new ArrayList< " + attributeType + " >();\n ");
                    break;

//            if((anAttribute.getAttributeKind() == GeneratedClassAttribute.ClassAttributeType.SISO_ENUM)) {
//                String className = anAttribute.getType();
//                if(anAttribute.getComment() != null)
//                    pw.println("   /** " + anAttribute.getComment() + " */");
//                if(anAttribute.getDefaultValue() == null) 
//                    pw.println("   protected "+ className + " "+ anAttribute.getName() + ";\n");
//                else
//                    pw.println("   protected "+ className + " "+ anAttribute.getName() + " = " + anAttribute.getDefaultValue() + ";\n");
//
//            }
                case SISO_ENUM:
                    className = anAttribute.getType();
                    if ((anAttribute.getComment() != null) && !anAttribute.getComment().trim().isEmpty())
                    {
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    }
                    else pw.println("   /** " + anAttribute.getName() + " is an undescribed parameter... */");

                    if (anAttribute.getDefaultValue() == null)
                        pw.println("   " + fieldaccess + " " + className + " " + anAttribute.getName() + " = " + className + ".values()[0];\n");
                    else
                        pw.println("   " + fieldaccess + " " + className + " " + anAttribute.getName() + " = " + anAttribute.getDefaultValue() + ";\n");
                    break;

                case SISO_BITFIELD:
                    className = anAttribute.getType();
                    if ((anAttribute.getComment() != null) && !anAttribute.getComment().trim().isEmpty())
                    {
                        pw.println("   /** " + anAttribute.getComment() + " */");
                    }
                    else pw.println("   /** " + anAttribute.getName() + " is an undescribed parameter... */");

                    if (anAttribute.getDefaultValue() == null)
                        pw.println("   " + fieldaccess + " " + className + " " + anAttribute.getName() + " = new " + className + "();\n");
                    else
                        pw.println("   " + fieldaccess + " " + className + " " + anAttribute.getName() + " = " + anAttribute.getDefaultValue() + ";\n");
                    break;
                    
                case PADTO16:
                    pw.println("   /** pad to 16-bit boundary */\n");
                    pw.println("   private byte[] "+anAttribute.getName()+" = new byte[0];\n");
                    break;
                case PADTO32:
                    pw.println("   /** pad to 32-bit boundary */\n");
                    pw.println("   private byte[] "+anAttribute.getName()+" = new byte[0];\n");
                    break;
                case PADTO64:
                    pw.println("   /** pad to 64-bit boundary */\n");
                    pw.println("   private byte[] "+anAttribute.getName()+" = new byte[0];\n");
                    break;
                    
            }
        } // End of loop through ivars
    }
  
    private void writeCopyMethods(PrintWriter pw, GeneratedClass aClass)
    {
        int BYTE_BUFFER_SIZE = 400; // TODO what is expected max buffer size?
        
        if (aClass.getName().equals(("Pdu")))
        {
            pw.println("    /** Create deep copy of current object using PduFactory.");
            pw.println("     * @return deep copy of PDU */");
            pw.println("     public synchronized Pdu copyByPduFactory()");
            pw.println("     {");
            pw.println("         PduFactory pduFactory = new PduFactory();");
            pw.println("         Pdu newPdu = pduFactory.createPdu(pduType); // initialize empty as placeholder");
            pw.println("         try");
            pw.println("         {");
            pw.println("             newPdu = pduFactory.createPdu(marshal());");
            pw.println("         }");
            pw.println("         catch (Exception e)");
            pw.println("         {");
            pw.println("             System.out.println(\"" + aClass.getName() + " copyByPduFactory() Exception: \" + e.getMessage());");
            pw.println("             System.exit(-1);");
            pw.println("         }");
            pw.println("         return newPdu;");
            pw.println("     }");
        }
        else if (aClass.getName().endsWith("Pdu") && !aClass.getName().equals(("Pdu"))&& !aClass.isAbstract())
        {
            pw.println("/** copy method creates a deep copy of current object using preferred marshalling method");
            pw.println(" * @return deep copy of PDU */");
            pw.println(" public synchronized " + aClass.getName() + " copy()");
            pw.println(" {");
            pw.println("     return copyDataOutputStream();");
            pw.println(" }");
            pw.println("/** Creates a \"deep copy\" of current object using ByteBuffer methods.");
            pw.println(" * @return deep copy of PDU */");
            pw.println(" public synchronized " + aClass.getName() + " copyByteBuffer()");
            pw.println(" {");
//          pw.println("     PduFactory pduFactory = new PduFactory();");
//          pw.println("     EntityStatePdu newCopy = pduFactory.make" + aClass.getName() + "();");
            pw.println("     " + aClass.getName() + " newCopy = new " + aClass.getName() + "();");
            pw.println("     ByteBuffer byteBuffer = ByteBuffer.allocate(" + BYTE_BUFFER_SIZE + ");");
            pw.println("     try");
            pw.println("     {");
            pw.println("         this.marshal(byteBuffer);      // working");
            pw.println("         newCopy.unmarshal(byteBuffer); // not working");
            pw.println("     }");
            pw.println("     catch (Exception e)");
            pw.println("     {");
            pw.println("         System.out.println(\"" + aClass.getName() + " deep copy() marshall/unmarshall ByteBuffer exception \" + e.getMessage());");
            pw.println("         e.printStackTrace();");
            pw.println("         System.exit(-1);");
            pw.println("     }");
            pw.println("     return newCopy;");
            pw.println(" }");
            pw.println();
            
            pw.println("/** byteArrayOutputStream (baos) is used for marshal/unmarshal serialization");
            pw.println("   * @see copyDataOutputStream() */");
            pw.println("protected ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();");
            pw.println("/** dataOutputStream (dos) is used for marshal/unmarshal serialization");
            pw.println("   * @see copyDataOutputStream() */");
            pw.println("protected DataOutputStream      dataOutputStream      = new DataOutputStream(byteArrayOutputStream);");
            pw.println();
            pw.println("/** copy method creates a deep copy of current object using DataOutputStream methods.");
            pw.println(" * @return deep copy of PDU */");
            pw.println(" public synchronized " + aClass.getName() + " copyDataOutputStream()");
            pw.println(" {");
//            pw.println("     PduFactory pduFactory = new PduFactory();");
            pw.println("     " + aClass.getName() + " newCopy = new " + aClass.getName() + "();");
//            pw.println("     DataOutputStream dos = new DataOutputStream(new ByteArrayOutputStream());");
            pw.println("     try");
            pw.println("     {");
            pw.println("         this.marshal(dataOutputStream);");
            pw.println("         byte[] byteArrayDOS = byteArrayOutputStream.toByteArray();");
            pw.println("         newCopy.unmarshal(ByteBuffer.wrap(byteArrayDOS));");
            pw.println("     }");
            pw.println("     catch (Exception e)");
            pw.println("     {");
            pw.println("         System.out.println(\"" + aClass.getName() + " deep copy() marshall/unmarshall DataOutputStream exception \" + e.getMessage());");
            pw.println("         e.printStackTrace();");
            pw.println("         System.exit(-1);");
            pw.println("     }");

            pw.println("        try");
            pw.println("        {");
            pw.println("                 dataOutputStream.flush();");
            pw.println("            byteArrayOutputStream.flush();");
            pw.println("            byteArrayOutputStream.reset();");
            pw.println("        }");
            pw.println("        catch (IOException ioe)");
            pw.println("        {");
            pw.println("            System.out.println(\"" + aClass.getName() + " copyDataOutputStream() flush IOException: \" + ioe.getMessage());");
            pw.println("        }");
            pw.println("     return newCopy;");
            pw.println(" }");
        }
    }
  
    private void writeConstructor(PrintWriter pw, GeneratedClass aClass)
    {
        // Write a constructor
        pw.println();
        pw.println("/** Constructor creates and configures a new instance object */");
        pw.println(" public " + aClass.getName() + "()");
        pw.println(" {");

        // Set primitive types with initial values
        for (GeneratedInitialValue anInit : aClass.getInitialValues()) {

            // This is irritating. we have to match up the attribute name with the type,
            // so we can do a cast. Otherwise java pukes because it wants to interpret all
            // numeric strings as ints or doubles, and the attribute may be a short.
            boolean found = false;
            GeneratedClass currentClass = aClass;
            String aType = null;
            ClassAttributeType aKind=null;
            
            String attName=null;
            while (currentClass != null) {
                for (GeneratedClassAttribute anAttribute : currentClass.classAttributes) {
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
    
    /**
     * Produce custom getMarshalledSize() method
     * @param printWriter output
     * @param aClass input class
     */
    public void writeGetMarshalledSizeMethod(PrintWriter printWriter, GeneratedClass aClass)
    {
        printWriter.println();
        // TODO not all object are setup to implement Marshaller; should be done
        // pw.println("@Override");
        printWriter.println(
"  /**\n" +
"   * Returns size of this serialized (marshalled) object in bytes\n" +
"   * @see <a href=\"https://en.wikipedia.org/wiki/Marshalling_(computer_science)\" target=\"_blank\">https://en.wikipedia.org/wiki/Marshalling_(computer_science)</a>\n" +
"   * @return serialized size in bytes\n" +
"   */");
//      printWriter.println("@Override");
        printWriter.println("public int getMarshalledSize()");
        printWriter.println("{");
        printWriter.println("   int marshalSize = 0; ");
        printWriter.println();

        // Size of superclass is the starting point
        if (!aClass.getParentClass().equalsIgnoreCase("root")) {
            printWriter.println("   marshalSize = super.getMarshalledSize();");
        }

        for (GeneratedClassAttribute anAttribute : aClass.getClassAttributes()) {
            switch (anAttribute.getAttributeKind()) {
                case PRIMITIVE:
                    // primitive cannot be null, no checking required
                    printWriter.print("   marshalSize += ");
                    printWriter.println(primitiveSizes.get(anAttribute.getType()) + ";  // " + anAttribute.getName());
                    break;
                case CLASSREF:
                case SISO_ENUM:
                case SISO_BITFIELD:
                    printWriter.println("   if (" + anAttribute.getName() + " != null)");
                    printWriter.print  ("       marshalSize += ");
                    printWriter.println(anAttribute.getName() + ".getMarshalledSize();");
                    break;
                case PRIMITIVE_LIST:
                    //System.out.println("Generating fixed list for " + anAttribute.getName() + " listIsClass:" + anAttribute.listIsClass());
                    // If this is a fixed list of primitives, it's the list size times the size of the primitive.
                    //pw.println("   marshalSize = marshalSize + " +  anAttribute.getListLength() + " * " + primitiveSizes.get(anAttribute.getType()) + ";  // " + anAttribute.getName());
                    printWriter.println("   if (" + anAttribute.getName() + " != null)");
                    printWriter.println("       marshalSize += " + anAttribute.getName()+".length * "  + primitiveSizes.get(anAttribute.getType()) + ";");
                    break;
                case OBJECT_LIST:
                    // If this is a dynamic list of primitives, it's the list size times the size of the primitive.
                    printWriter.println("   if (" + anAttribute.getName() + " != null)");
                    printWriter.println("       for (int idx=0; idx < " + anAttribute.getName() + ".size(); idx++)");
                    printWriter.println("       {");
                    //pw.println( anAttribute.getName() + ".size() " + " * " +  " new " + anAttribute.getType() + "().getMarshalledSize()"  + ";  // " + anAttribute.getName());
                    printWriter.println("            " + anAttribute.getType() + " listElement = " + anAttribute.getName() + ".get(idx);");
                    printWriter.println("            marshalSize += listElement.getMarshalledSize();");
                    printWriter.println("       }");
                    break;
                case PADTO16:
                case PADTO32:
                case PADTO64:
                    printWriter.println("   if (" + anAttribute.getName() + " != null)");
                    printWriter.println("       marshalSize += "+anAttribute.getName()+".length;");
                    break;
            }          
        }
        printWriter.println();
        printWriter.println("   return marshalSize;");
        printWriter.println("}");
        printWriter.println();
    }
 
    private void writeGettersAndSetters(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println();

        for (GeneratedClassAttribute anAttribute : aClass.classAttributes) {

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
                        if (anAttribute.getName().equals("timestamp"))
                        {
                            pw.println("  * Warning: this method sets a DIS bit pattern");
                            pw.println("  * @see setTimestampSeconds");
                            pw.println("  * @see edu.nps.moves.dis7.utilities.DisTime");
                        }
                        pw.println("  * @param p" + this.initialCapital(anAttribute.getName()) + " new value of interest");
                        pw.println("  * @return same object to permit progressive setters */");
                        pw.print("public synchronized ");
                        pw.print(aClass.getName());
                        pw.println(" set" + this.initialCapital(anAttribute.getName()) + "(" + beanType + " p" + this.initialCapital(anAttribute.getName()) + ")");
                        pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCapital(anAttribute.getName()) + ";");
                        pw.println("    return this;");
                        pw.println("}");
                        
                        // utility setter to allow int types
                        if (beanType.equals("byte") || beanType.equals("short") || beanType.equals("long"))
                        {
                        pw.println("/** Utility setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                        if (anAttribute.getName().equals("timestamp"))
                        {
                            pw.println("  * Warning: this method sets a DIS bit pattern");
                            pw.println("  * @see setTimestampSeconds");
                            pw.println("  * @see edu.nps.moves.dis7.utilities.DisTime");
                        }
                        pw.println("  * @param p" + this.initialCapital(anAttribute.getName()) + " new value of interest");
                        pw.println("  * @return same object to permit progressive setters */");
                        pw.print("public synchronized ");
                        pw.print(aClass.getName());
                        pw.print(" set" + this.initialCapital(anAttribute.getName()) + "(" );
                        pw.print("int"); // allow int, will then coerce downcasting to beantype when setting
                        pw.print(" p" + this.initialCapital(anAttribute.getName()) + ")");
                        // TODO overflow checks when downcasting int to given beantype
                        pw.println("{\n    " + anAttribute.getName() + " = (" + beanType + ") p" + this.initialCapital(anAttribute.getName()) + ";");
                        pw.println("    return this;");
                        pw.println("}");
                        }

                        pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                        if (anAttribute.getName().equals("timestamp"))
                        {
                            pw.println("  * Warning: this method gets a DIS bit pattern");
                            pw.println("  * @see getTimestampSeconds");
                            pw.println("  * @see edu.nps.moves.dis7.utilities.DisTime");
                        }
                        pw.println("  * @return value of interest */");
                        pw.println("public " + beanType + " get" + this.initialCapital(anAttribute.getName()) + "()");
                        pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                        pw.println();
                    }
                    else //todo, now obsolete with new definition of PRIMITIVE_LIST  // This is the count field for a dynamic list
                    {
                        String beanType = types.getProperty(anAttribute.getType());
                        GeneratedClassAttribute listAttribute = anAttribute.getDynamicListClassAttribute();

                        pw.println("/** Utility method to get size of field");
                        pw.println(" * @return size of field */");
                        pw.println("public " + beanType + " get" + this.initialCapital(anAttribute.getName()) + "()");
                        pw.println("{\n    return (" + beanType + ")" + listAttribute.getName() + ".size(); \n}");

                        pw.println();

                        pw.println("/** Note that setting this value will not change the marshalled value. The list whose length this describes is used for that purpose.");
                        pw.println(" * The get" + anAttribute.getName() + " method will also be based on the actual list length rather than this value. ");
                        pw.println(" * The method is simply here for java bean completeness.");
                        pw.println(" * @param p" + this.initialCapital(anAttribute.getName()) + " passed parameter");
                        pw.println(" * @return this object");
                        pw.println(" */");
                        pw.print("public synchronized ");
                        pw.print(aClass.getName());
                        pw.println(" set" + this.initialCapital(anAttribute.getName()) + "(" + beanType + " p" + this.initialCapital(anAttribute.getName()) + ")");
                        pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCapital(anAttribute.getName()) + ";");
                        pw.println("    return this;");
                        pw.println("}");

                        pw.println();

                    }
                    break; // End is primitive

                // The attribute is a class of some sort. Generate getters and setters.
                case CLASSREF:
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCapital(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public synchronized ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCapital(anAttribute.getName()) + "(" + anAttribute.getType() + " p" + this.initialCapital(anAttribute.getName()) + ")");
                    pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCapital(anAttribute.getName()) + ";");
                    pw.println("    return this;");
                    pw.println("}");
                    
                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public " + anAttribute.getType() + " get" + this.initialCapital(anAttribute.getName()) + "()");
                    pw.println("{");
                    if (anAttribute.listIsClass())
                    {
                    pw.println("    if (" + anAttribute.getName() + " == null)");
                    pw.println("        " + anAttribute.getName() + " = new " + aClass.getName() + "(); // ensure initial object present");
                    }
                    pw.println("    return " + anAttribute.getName() + ";");
                    pw.println("}\n");
                    pw.println();
                    break;
                    
                // The attribute is an array of some sort. Generate getters and setters.
                case PRIMITIVE_LIST:
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCapital(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public synchronized ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCapital(anAttribute.getName()) + "(" + types.getProperty(anAttribute.getType()) + "[] p" + this.initialCapital(anAttribute.getName()) + ")");

                    if (!anAttribute.isFixedLength())
                        pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCapital(anAttribute.getName()) + ";");
                    else
                        pw.println("{\n    " + anAttribute.getName() + " = Arrays.copyOf(p" + this.initialCapital(anAttribute.getName()) + ", " + anAttribute.getName() + ".length);");

                    pw.println("    return this;");
                    pw.println("}");
                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public " + types.getProperty(anAttribute.getType()) + "[] get" + this.initialCapital(anAttribute.getName()) + "()");
                    pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                    pw.println();
                    break;

                case OBJECT_LIST:
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCapital(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public synchronized ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCapital(anAttribute.getName()) + "(List<" + anAttribute.getType() + ">" + " p" + this.initialCapital(anAttribute.getName()) + ")");
                    pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCapital(anAttribute.getName()) + ";");
                    pw.println("    return this;");
                    pw.println("}");

                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public List<" + anAttribute.getType() + ">" + " get" + this.initialCapital(anAttribute.getName()) + "()");
                    pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                    pw.println();
                    break;

                case SISO_ENUM:
                    String enumtype = anAttribute.getType();
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCapital(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public synchronized ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCapital(anAttribute.getName()) + "(" + enumtype + " p" + this.initialCapital(anAttribute.getName()) + ")");
                    pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCapital(anAttribute.getName()) + ";");
                    pw.println("    return this;");
                    pw.println("}");

                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public " + enumtype + " get" + this.initialCapital(anAttribute.getName()) + "()");
                    pw.println("{\n    return " + anAttribute.getName() + "; \n}");
                    pw.println();
                    break;
                    
                case SISO_BITFIELD:
                    String bitfieldtype = anAttribute.getType();
                    pw.println("/** Setter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @param p" + this.initialCapital(anAttribute.getName()) + " new value of interest");
                    pw.println("  * @return same object to permit progressive setters */");
                    pw.print("public synchronized ");
                    pw.print(aClass.getName());
                    pw.println(" set" + this.initialCapital(anAttribute.getName()) + "(" + bitfieldtype + " p" + this.initialCapital(anAttribute.getName()) + ")");
                    pw.println("{\n    " + anAttribute.getName() + " = p" + this.initialCapital(anAttribute.getName()) + ";");
                    pw.println("    return this;");
                    pw.println("}");
                    
                    pw.println("/** Getter for {@link "+aClass.getName()+"#"+anAttribute.getName()+"}");
                    pw.println("  * @return value of interest */");
                    pw.println("public " + bitfieldtype + " get" + this.initialCapital(anAttribute.getName()) + "()");
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
            GeneratedClassAttribute anAttribute = (GeneratedClassAttribute) attributes.get(idx);

            switch (anAttribute.getAttributeKind()) {

                // Anything with bitfields must be a primitive type
                case PRIMITIVE:
                    List bitfields = anAttribute.bitFieldList;
                    String attributeType = types.getProperty(anAttribute.getType());
                    String bitfieldIvarName = anAttribute.getName();

                    for (int jdx = 0; jdx < bitfields.size(); jdx++) {
                        GeneratedBitField bitfield = (GeneratedBitField) bitfields.get(jdx);
                        String capped = this.initialCapital(bitfield.name);
                        String cappedIvar = this.initialCapital(bitfieldIvarName);
                        int shiftBits = super.getBitsToShift(anAttribute, bitfield.mask);

                        // write getter
                        pw.println();
                        if (bitfield.description != null) {
                            pw.println("/**\n * " + bitfield.description + "\n */");
                        }

                        pw.println("public int get" + cappedIvar + "_" + bitfield.name + "()");
                        pw.println("{");

                        pw.println("    " + attributeType + " val = (" + attributeType + ")(this." + bitfield.parentAttribute.getName() + "   & " + "(" + attributeType + ")" + bitfield.mask + ");");
                        pw.println("    return (int)(val >> " + shiftBits + ");");
                        pw.println("}\n");

                        // Write the setter/mutator
                        pw.println();
                        if (bitfield.description != null) {
                            pw.println("/** \n * " + bitfield.description + "\n */");
                        }
                        pw.println("public synchronized void set" + cappedIvar + "_" + bitfield.name + "(int val)");
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
 
//      pw.println("@Override");
        pw.println("public void marshal(DataOutputStream dos) throws Exception");
        pw.println("{");

        // If we're a sublcass of another class, we should first call super
        // to make sure the superclass's ivars are marshaled out.
        if (!(aClass.getParentClass().equalsIgnoreCase("root")))
            pw.println("    super.marshal(dos);");

        pw.println("    try \n    {");

        // Loop through the class attributes, generating the output for each.
        for (GeneratedClassAttribute anAttribute : aClass.getClassAttributes()) {

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
                capped = this.initialCapital(marshalType);

                // If we're a normal primitivetype, marshal out directly; otherwise, marshall out
                // the list length.
                if (anAttribute.getIsDynamicListLengthField()) {
                  GeneratedClassAttribute listAttribute = anAttribute.getDynamicListClassAttribute();
                  pw.println("       dos.write" + capped + "(" + listAttribute.getName() + ".size());");
                }
                
                else if (anAttribute.getIsPrimitiveListLengthField()) {
                  GeneratedClassAttribute listAttribute = anAttribute.getDynamicListClassAttribute();
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
                    pw.println("       for (int idx = 0; idx < " + anAttribute.getName() + ".length; idx++)");

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    capped = this.initialCapital(marshalType);
                    pw.println("           dos.write" + capped + "(" + anAttribute.getName() + "[idx]);");

                    pw.println();
                    break;

                // Write out a section of code to marshal a variable length list. The code should look like
                //
                // for (int idx = 0; idx < attrName.size(); idx++)
                // { anAttribute.marshal(dos);
                // }
                //    
                case OBJECT_LIST:
                    pw.println();
                    pw.println("       for (int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");
                    pw.println("       {");

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    pw.println("            " + anAttribute.getType() + " a" + initialCapital(anAttribute.getType() + " = "
                        + anAttribute.getName() + ".get(idx);"));
                    pw.println("            a" + initialCapital(anAttribute.getType()) + ".marshal(dos);");

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
        pw.println(" * @see <a href=\"https://en.wikipedia.org/wiki/Marshalling_(computer_science)\" target=\"_blank\">https://en.wikipedia.org/wiki/Marshalling_(computer_science)</a>");
        pw.println(" * @param dis the InputStream");
        pw.println(" * @return marshalled serialized size in bytes");
        pw.println(" */");

//      pw.println("@Override");
        pw.println("public synchronized int unmarshal(DataInputStream dis) throws Exception");
        pw.println("{");
        pw.flush();
        pw.println("    int uPosition = 0;");
        
        if (!(aClass.getParentClass().equalsIgnoreCase("root")))
            pw.println("    uPosition += super.unmarshal(dis);\n");

        pw.println("    try \n    {");

        // Loop through the class attributes, generating the output for each.
        for (GeneratedClassAttribute anAttribute : aClass.getClassAttributes()) {

            if (anAttribute.shouldSerialize == false) {
                pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                continue;
            }
            String attributeName= anAttribute.getName();
            switch(anAttribute.getAttributeKind()) {
                case PRIMITIVE:            
                    String marshalType = unmarshalTypes.getProperty(anAttribute.getType());
                    String capped = this.initialCapital(marshalType);
                
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
                    pw.println("        for (int idx = 0; idx < " + attributeName + ".length; idx++)");

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
                        capped = this.initialCapital(marshalType);
                        pw.println("            " + anAttribute.getName() + "[idx] = dis.read" + capped + "();");
                        pw.println("        uPosition += ("+attributeName + ".length * "+primitiveByteSize+");");
                    }
                    break;
                    
                case OBJECT_LIST:
                    if (anAttribute.getCountFieldName() != null)
                        pw.println("        for (int idx = 0; idx < " + anAttribute.getCountFieldName() + "; idx++)");
                    else
                        pw.println("        for (int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");

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
                            capped = this.initialCapital(marshalType);
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
        for (GeneratedClassAttribute anAttribute: aClass.getClassAttributes())
        {
            if(anAttribute.shouldSerialize == false) {
                 pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                 continue;
            }
            String marshalType;
            String capped;
            
            switch(anAttribute.getAttributeKind()) {
                case PRIMITIVE:
                    marshalType = marshalTypes.getProperty(anAttribute.getType());
                    capped = this.initialCapital(marshalType);
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
                    pw.println("   for (int idx = 0; idx < " + anAttribute.getName() + ".length; idx++)");

                    // This is some sleaze. We're an array, but an array of what? We could be either a
                    // primitive or a class. We need to figure out which. This is done via the expedient
                    // but not very reliable way of trying to do a lookup on the type. If we don't find
                    // it in our map of primitives to marshal types, we assume it is a class.

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    if(anAttribute.getUnderlyingTypeIsPrimitive())
                    {
                        capped = this.initialCapital(marshalType);
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
                    pw.println("   for (int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");
                    pw.println("   {");

                    // This is some sleaze. We're an array, but an array of what? We could be either a
                    // primitive or a class. We need to figure out which. This is done via the expedient
                    // but not very reliable way of trying to do a lookup on the type. If we don't find
                    // it in our map of primitives to marshal types, we assume it is a class.

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    if(anAttribute.getUnderlyingTypeIsPrimitive())
                    {
                        capped = this.initialCapital(marshalType);
                        if( capped.equals("Byte") ){
                            capped = "";    // ByteBuffer just uses put() for bytes
                        }
                        //pw.println("           dos.write" + capped + "(" + anAttribute.getName() + ");");
                        pw.println("       byteBuffer.put" + capped + "(" + anAttribute.getName() + ");");
                    }
                    else
                    {
                        pw.println("        " + anAttribute.getType() + " a" + initialCapital(anAttribute.getType() + " = " + anAttribute.getName() + ".get(idx);"));
                        pw.println("        a" + initialCapital(anAttribute.getType()) + ".marshal(byteBuffer);" );
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
        pw.println(" * @see <a href=\"https://en.wikipedia.org/wiki/Marshalling_(computer_science)\" target=\"_blank\">https://en.wikipedia.org/wiki/Marshalling_(computer_science)</a>");
        pw.println(" * @param byteBuffer The ByteBuffer at the position to begin reading");
        pw.println(" * @return marshalled serialized size in bytes");
        pw.println(" * @throws Exception ByteBuffer-generated exception");
        pw.println(" */");
        pw.println("public synchronized int unmarshal(java.nio.ByteBuffer byteBuffer) throws Exception"); // throws EnumNotFoundException");
        pw.println("{");

        if(!(aClass.getParentClass().equalsIgnoreCase("root")))
            pw.println("    super.unmarshal(byteBuffer);\n");

        pw.println("    try");
        pw.println("    {");
        // Loop through the class attributes, generating the output for each.
        for (GeneratedClassAttribute anAttribute : aClass.getClassAttributes()) { 

            if(anAttribute.shouldSerialize == false) {
                 pw.println("        // attribute " + anAttribute.getName() + " marked as not serialized");
                 continue;
            }
            pw.println("        // attribute " + anAttribute.getName() + " marked as not serialized");
            String marshalType;
            String capped;
            switch(anAttribute.getAttributeKind()) {
                case PRIMITIVE:
                    marshalType = unmarshalTypes.getProperty(anAttribute.getType());
                    capped = this.initialCapital(marshalType);
                    if( capped.equals("Byte") )
                        capped = "";
                
                    if(marshalType.equalsIgnoreCase("UnsignedByte"))
                        pw.println("        " + anAttribute.getName() + " = (byte)(byteBuffer.get() & 0xFF);");               
                    else if (marshalType.equalsIgnoreCase("UnsignedShort"))
                        pw.println("        " + anAttribute.getName() + " = (short)(byteBuffer.getShort() & 0xFFFF);");               
                    else
                        pw.println("        " + anAttribute.getName() + " = byteBuffer.get" + capped + "();");
                    break;
                    
                case SISO_ENUM:
                    pw.println("        " + anAttribute.getName() + " = "+anAttribute.getType()+".unmarshalEnum(byteBuffer);");
                    break;
                    
                case SISO_BITFIELD:
                case CLASSREF:         
                    pw.println("        " + anAttribute.getName() + ".unmarshal(byteBuffer);" );
                    break;

                case PRIMITIVE_LIST:
                    pw.println("        for (int idx = 0; idx < " + anAttribute.getName() + ".length; idx++)");

                    marshalType = marshalTypes.getProperty(anAttribute.getType());

                    if(marshalType == null) // It's a class  // should be unnecessary w/ refactor
                        pw.println("            " + anAttribute.getName() + "[idx].unmarshal(byteBuffer);" );
                    else { // It's a primitive
                        capped = this.initialCapital(marshalType);
                        if( capped.equals("Byte") )
                             capped = "";
                        pw.println("            " +  anAttribute.getName() + "[idx] = byteBuffer.get" + capped + "();");
                    }
                    break;
                    
                case OBJECT_LIST:
                    if(anAttribute.getCountFieldName() != null)
                        pw.println("        for (int idx = 0; idx < " + anAttribute.getCountFieldName() + "; idx++)");
                    else
                        pw.println("        for (int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");
                
                    pw.println("        {");

                    if(anAttribute.getUnderlyingTypeIsEnum()) {
                        pw.println("        " +anAttribute.getType() + " anX = "+anAttribute.getType() + ".unmarshalEnum(byteBuffer);");
                        pw.println("        " + anAttribute.getName() + ".add(anX);");
                    }
                    else {
                        marshalType = marshalTypes.getProperty(anAttribute.getType());

                        if(marshalType == null) { // It's a class
                            pw.println("        " + anAttribute.getType() + " anX = new " + anAttribute.getType() + "();");                           
                            pw.println("        anX.unmarshal(byteBuffer);");
                            pw.println("        " + anAttribute.getName() + ".add(anX);");
                        }
                        else { // It's a primitive  // should be unnecessary now w/ refactor
                            capped = this.initialCapital(marshalType);
                            if( capped.equals("Byte") )
                                capped = "";
                            pw.println("        byteBuffer.get" + capped + "(" + anAttribute.getName() + ");");
                        }
                    }
                    pw.println("        }");
                    pw.println();
                    break;
                    
                                    
                case PADTO16:
                    pw.println("        "+anAttribute.getName() + " = new byte[Align.from16bits(byteBuffer)];");
                    break;
                case PADTO32:
                    pw.println("        "+anAttribute.getName() + " = new byte[Align.from32bits(byteBuffer)];");
                    break;
                case PADTO64:
                    pw.println("        "+anAttribute.getName() + " = new byte[Align.from64bits(byteBuffer)];");
                    break;
            }
        } // End of loop through ivars for writing the unmarshal method
        
        pw.println("    }");
        pw.println("    catch (java.nio.BufferUnderflowException bue)");
        pw.println("    {");
        pw.println("        System.err.println(\"*** buffer underflow error while unmarshalling \" + this.getClass().getName());");
        pw.println("    }");
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
        pw.println("public synchronized byte[] marshal() throws Exception");
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
        for (int idx = 0; idx < ivars.size(); idx++)
        {
            GeneratedClassAttribute anAttribute = (GeneratedClassAttribute)ivars.get(idx);
    
            if(anAttribute.shouldSerialize == false)
            {
                 pw.println("    // attribute " + anAttribute.getName() + " marked as not serialized");
                 continue;
            }
        
            // Write out a method call to serialize a primitive type
            if(anAttribute.getAttributeKind() == GeneratedClassAttribute.ClassAttributeType.PRIMITIVE)
            {
                // If we're a normal primitive type, marshal out directly; otherwise, marshall out
                // the list length.
                if(anAttribute.getIsDynamicListLengthField() == false)
                {
                     pw.print( "      textWriter.print(" "" + anAttribute.getName() + "" + "="" " + "this.get" + this.initialCapital(anAttribute.getName()) + "();");
                }
               else
               {
                   GeneratedClassAttribute listAttribute = anAttribute.getDynamicListClassAttribute();
                   //pw.println("       dos.write" + capped + "( (" + marshalType + ")" + listAttribute.getName() + ".size());");
               }
                
            }
        } // End of loop through primitive types
 */      
        /*
            // Write out a method call to serialize a class.
            if( anAttribute.getAttributeKind() == GeneratedClassAttribute.ClassAttributeType.CLASSREF )
            {
                String marshalType = anAttribute.getType();
            
                pw.println("       " + anAttribute.getName() + ".marshal(dos);" );
            }
            
            // Write out the method call to marshal a fixed length list, aka an array.
            if( (anAttribute.getAttributeKind() == GeneratedClassAttribute.ClassAttributeType.FIXED_LIST) )
            {
                pw.println();
                pw.println("       for (int idx = 0; idx < " + anAttribute.getName() + ".length; idx++)");
                pw.println("       {");
                
                // This is some sleaze. We're an array, but an array of what? We could be either a
                // primitive or a class. We need to figure out which. This is done via the expedient
                // but not very reliable way of trying to do a lookup on the type. If we don't find
                // it in our map of primitives to marshal types, we assume it is a class.
                
                String marshalType = marshalTypes.getProperty(anAttribute.getType());
                
                if(anAttribute.getUnderlyingTypeIsPrimitive())
                {
                    String capped = this.initialCapital(marshalType);
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
            // for (int idx = 0; idx < attrName.size(); idx++)
            // { anAttribute.marshal(dos);
            // }
            //    
            
            if( (anAttribute.getAttributeKind() == GeneratedClassAttribute.ClassAttributeType.VARIABLE_LIST) )
            {
                pw.println();
                pw.println("       for (int idx = 0; idx < " + anAttribute.getName() + ".size(); idx++)");
                pw.println("       {");
                
                // This is some sleaze. We're an array, but an array of what? We could be either a
                // primitive or a class. We need to figure out which. This is done via the expedient
                // but not very reliable way of trying to do a lookup on the type. If we don't find
                // it in our map of primitives to marshal types, we assume it is a class.
                
                String marshalType = marshalTypes.getProperty(anAttribute.getType());
                
                if(anAttribute.getUnderlyingTypeIsPrimitive())
                {
                    String capped = this.initialCapital(marshalType);
                    pw.println("           dos.write" + capped + "(" + anAttribute.getName() + ");");
                }
                else
                {
                    pw.println("            " + anAttribute.getType() + " a" + initialCapital(anAttribute.getType() + " = (" + anAttribute.getType() + ")" +
                                                                                     anAttribute.getName() + ".get(idx);"));
                    pw.println("            a" + initialCapital(anAttribute.getType()) + ".marshal(dos);" );
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
            pw.println(" public synchronized boolean equals(Object obj)");
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
            pw.println(" public synchronized boolean equalsImpl(Object obj)");
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
            GeneratedClassAttribute anAttribute = aClass.getClassAttributes().get(idx);
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
                pw.println("     for (int idx = 0; idx < "+ anAttribute.getListLength() + "; idx++)");
                pw.println("     {");
                pw.println("          if(!(" + attname + "[idx] == rhs." + attname + "[idx])) ivarsEqual = false;");
                pw.println("     }");
                pw.println();
                break;

              case OBJECT_LIST:
                pw.println();
                pw.println("     for (int idx = 0; idx < " + attname + ".size(); idx++)");
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
        pw.println(" public synchronized String toString()");
        pw.println(" {");
        pw.println("    StringBuilder sb  = new StringBuilder();");
        pw.println("    StringBuilder sb2 = new StringBuilder();");
        pw.println("    sb.append(getClass().getSimpleName());");

        ArrayList<GeneratedClassAttribute> objlists = new ArrayList<>();

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
  
    private void writePrimitiveList(PrintWriter pw, GeneratedClassAttribute attr)
    {
        pw.print  ("    sb.append(\" ");
        pw.print  (attr.getName());
        pw.println(":\");");
        pw.print  ("    sb.append(Arrays.toString(");
        pw.print  (attr.getName());
        pw.println(")); // writePrimitiveList");
        
//      pw.print("    sb.append(\" ");
//      pw.print(attr.getName());
//      pw.println(": \").append(\"\\n\");");
//      pw.print("    sb.append(Arrays.toString(");
//      pw.print(attr.getName());
//      pw.println(")).append(\"\\n\");");
    }
  
    private void writeList(PrintWriter pw, GeneratedClassAttribute attr)
    {
        pw.print  ("    sb.append(\" ");
        pw.print  (attr.getName());
        pw.println(": \");");

        pw.print("    ");
        pw.print(attr.getName());
        pw.println(".forEach(r->{ sb2.append(\" \").append(r);}); // writeList");
        pw.println("    sb.append(sb2.toString().trim());");
        pw.println("    // https://stackoverflow.com/questions/2242471/clearing-a-string-buffer-builder-after-loop");
        pw.println("    sb2.setLength(0); // reset");
    
//      pw.print("    sb.append(\" ");
//      pw.print(attr.getName());
//      pw.println(": \").append(\"\\n\");");
//      pw.print("    ");
//      pw.print(attr.getName());
//      pw.println(".forEach(r->{ sb.append(r.getClass().getSimpleName()).append(\": \").append(r).append(\"\\n\");});");
    }
    
    private void writeOneToString(PrintWriter pw, GeneratedClassAttribute attr)
    {
        pw.print  ("    sb.append(\" ");
        pw.print  (attr.getName());
        pw.print(":\").append(");
        pw.print  (attr.getName());
        pw.println("); // writeOneToString");
        
//        pw.print("    sb.append(\" ");
//        pw.print(attr.getName());
//        pw.print(": \").append(");
//        pw.print(attr.getName());
//        pw.println(").append(\"\\n\");");
    }
   
    /** 
     * returns a string with the first letter capitalized.
     * @param aString of interest
     * @return same string with first letter capitalized
     */
    @Override
    public String initialCapital(String aString)
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
