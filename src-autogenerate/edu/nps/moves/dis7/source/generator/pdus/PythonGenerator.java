/**
 * Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */
package edu.nps.moves.dis7.source.generator.pdus;

import edu.nps.moves.dis7.source.generator.pdus.GeneratedClassAttribute.ClassAttributeType;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class autogenerates Python source code from XML PDU definitions, specifically
 * producing a modular Python package for the open-dis7-python distribution.
 * Each class is written to its own .py file under src-generated/python/opendis7/pdus/.
 * @see AbstractGenerator
 * @see JavaGenerator
 * @author DMcG
 */
public class PythonGenerator extends AbstractGenerator
{
    /** Standard python indent is four spaces */
    public static final String INDENT = "    ";

    /** Properties of interest */
    public Properties marshalTypes = new Properties();

    /** Properties of interest */
    public Properties unmarshalTypes = new Properties();

    /** Enum marshal size mappings (enum size in bytes -> read/write method suffix) */
    public Properties enumMarshalReadTypes = new Properties();
    public Properties enumMarshalWriteTypes = new Properties();

    /** Sizes of primitive types in bytes for padding calculations */
    public Properties primitiveSizes = new Properties();

    /** Base output directory for generated Python package */
    private String pythonPackageDir;

    /**
     * Given the input object, something of an abstract syntax tree, this generates
     * modular Python source code files with one class per file.
     * @param pClassDescriptions String Map of class descriptions
     * @param languagePropertiesPython special language properties
     */
    public PythonGenerator(Map<String, GeneratedClass> pClassDescriptions, Properties languagePropertiesPython)
    {
        super(pClassDescriptions, languagePropertiesPython);

        // Marshal types - must match DataOutputStream.py method names (snake_case)
        marshalTypes.setProperty("uint8",   "unsigned_byte");
        marshalTypes.setProperty("uint16",  "unsigned_short");
        marshalTypes.setProperty("uint32",  "int");
        marshalTypes.setProperty("uint64",  "long");
        marshalTypes.setProperty("int8",    "byte");
        marshalTypes.setProperty("int16",   "short");
        marshalTypes.setProperty("int32",   "int");
        marshalTypes.setProperty("int64",   "long");
        marshalTypes.setProperty("float32", "float");
        marshalTypes.setProperty("float64", "double");

        // Unmarshalling types - must match DataInputStream.py method names (snake_case)
        unmarshalTypes.setProperty("uint8",   "unsigned_byte");
        unmarshalTypes.setProperty("uint16",  "unsigned_short");
        unmarshalTypes.setProperty("uint32",  "int");
        unmarshalTypes.setProperty("uint64",  "long");
        unmarshalTypes.setProperty("int8",    "byte");
        unmarshalTypes.setProperty("int16",   "short");
        unmarshalTypes.setProperty("int32",   "int");
        unmarshalTypes.setProperty("int64",   "long");
        unmarshalTypes.setProperty("float32", "float");
        unmarshalTypes.setProperty("float64", "double");

        // Enum marshal: size in bits -> read method suffix
        enumMarshalReadTypes.setProperty("8",  "unsigned_byte");
        enumMarshalReadTypes.setProperty("16", "unsigned_short");
        enumMarshalReadTypes.setProperty("32", "unsigned_int");

        // Enum marshal: size in bits -> write method suffix
        enumMarshalWriteTypes.setProperty("8",  "unsigned_byte");
        enumMarshalWriteTypes.setProperty("16", "unsigned_short");
        enumMarshalWriteTypes.setProperty("32", "unsigned_int");

        // Primitive sizes in bytes
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
    }

    @Override
    public void writeClasses()
    {
        pythonPackageDir = "./src-generated/python/opendis7";
        generatedSourceDirectoryName = pythonPackageDir + "/pdus";

        // Create directory structure
        new File(pythonPackageDir).mkdirs();
        new File(pythonPackageDir + "/pdus").mkdirs();
        new File(pythonPackageDir + "/io").mkdirs();

        int classCount = 0;
        List<String> classNames = new ArrayList<>();

        Iterator<GeneratedClass> classDescriptionsIterator = classDescriptions.values().iterator();
        while (classDescriptionsIterator.hasNext())
        {
            try
            {
                GeneratedClass aClass = classDescriptionsIterator.next();
                String aClassName = aClass.getName();
                classNames.add(aClassName);

                // Create one .py file per class
                String classFilePath = pythonPackageDir + "/pdus/" + aClassName + ".py";
                File outputFile = new File(classFilePath);
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
                PrintWriter pw = new PrintWriter(outputFile, StandardCharsets.UTF_8.name());

                writeClassFile(pw, aClass);
                classCount++;
                System.out.println("Created python class " + aClassName);
            }
            catch (IOException e)
            {
                System.err.println("Problem creating python class: " + e);
                e.printStackTrace(System.err);
            }
        }

        // Generate __init__.py files
        try
        {
            writePdusInitPy(classNames);
            writeTopLevelInitPy(classNames);
            writeIoPackage();
            writePduFactory(classNames);
        }
        catch (IOException e)
        {
            System.err.println("Problem creating python package files: " + e);
            e.printStackTrace(System.err);
        }

        System.out.println(PythonGenerator.class.getName() + " complete, " + classCount + " python classes written.");
    }

    /**
     * Write a complete single-class Python file with imports, class definition,
     * __init__, serialize, parse, and flag methods.
     * @param pw PrintWriter for the output file
     * @param aClass the class to generate
     */
    private void writeClassFile(PrintWriter pw, GeneratedClass aClass)
    {
        try (pw)
        {
            // License header
            writeLicense(pw);
            pw.println();

            // Future annotations for forward references
            pw.println("from __future__ import annotations");
            pw.println();

            // Imports
            writeImports(pw, aClass);
            pw.println();

            // Class declaration
            String parentClassName = aClass.getParentClass();
            String pythonParent;
            if (parentClassName.equalsIgnoreCase("root"))
            {
                pythonParent = "object";
            }
            else
            {
                pythonParent = parentClassName;
            }

            pw.println("class " + aClass.getName() + "(" + pythonParent + "):");

            // Class docstring
            String classComment = aClass.getClassComments();
            if (classComment != null && !classComment.trim().isEmpty())
            {
                pw.println(INDENT + "\"\"\"" + classComment + "\"\"\"");
            }
            else
            {
                pw.println(INDENT + "\"\"\"" + aClass.getName() + " PDU class.\"\"\"");
            }
            pw.println();

            // Handle alias classes (empty subclasses)
            if (aClass.getAliasFor() != null)
            {
                pw.println(INDENT + "pass");
                pw.flush();
                return;
            }

            // __init__ method
            writeInit(pw, aClass);

            // serialize method
            writeMarshal(pw, aClass);

            // parse method
            writeUnmarshal(pw, aClass);

            // Bit flag accessor methods
            writeFlagMethods(pw, aClass);

            pw.println();
            pw.flush();
        }
    }

    /**
     * Write import statements for all referenced classes.
     * Scans attributes for CLASSREF, OBJECT_LIST, SISO_ENUM, SISO_BITFIELD references.
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    private void writeImports(PrintWriter pw, GeneratedClass aClass)
    {
        Set<String> imports = new HashSet<>();

        // Import parent class if not root
        String parentClass = aClass.getParentClass();
        if (!parentClass.equalsIgnoreCase("root"))
        {
            imports.add(parentClass);
        }

        // Scan attributes for class references
        List<GeneratedClassAttribute> attributes = aClass.getClassAttributes();
        for (GeneratedClassAttribute attr : attributes)
        {
            switch (attr.getAttributeKind())
            {
                case CLASSREF:
                    imports.add(attr.getType());
                    break;

                case OBJECT_LIST:
                    // If the list element type is a class (not primitive), import it
                    String marshalType = marshalTypes.getProperty(attr.getType());
                    if (marshalType == null)
                    {
                        imports.add(attr.getType());
                    }
                    break;

                case PRIMITIVE_LIST:
                    // If the list element is a class, import it
                    if (!attr.getUnderlyingTypeIsPrimitive() && attr.listIsClass())
                    {
                        imports.add(attr.getType());
                    }
                    break;

                case SISO_ENUM:
                    imports.add(attr.getType());
                    break;

                case SISO_BITFIELD:
                    imports.add(attr.getType());
                    break;

                default:
                    break;
            }
        }

        // Remove self-reference
        imports.remove(aClass.getName());

        // Write import statements - PDU classes from pdus package, enum/bitfield from enumerations
        for (String importName : imports)
        {
            // Check if the imported name is another PDU class or an enumeration
            if (classDescriptions.containsKey(importName))
            {
                pw.println("from opendis7.pdus." + importName + " import " + importName);
            }
            else
            {
                // It's likely an enumeration or bitfield from enumerations package
                pw.println("from opendis7.enumerations." + importName + " import " + importName);
            }
        }
    }

    /**
     * Write the __init__ method with all instance variables.
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    private void writeInit(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println(INDENT + "def __init__(self):");
        pw.println(INDENT + INDENT + "\"\"\"Initializer for " + aClass.getName() + ".\"\"\"");

        // Call super().__init__() if subclass
        if (!aClass.getParentClass().equalsIgnoreCase("root"))
        {
            pw.println(INDENT + INDENT + "super().__init__()");
        }

        // Write class attributes
        List<GeneratedClassAttribute> ivars = aClass.getClassAttributes();
        boolean hasAttributes = false;
        for (int idx = 0; idx < ivars.size(); idx++)
        {
            GeneratedClassAttribute anAttribute = ivars.get(idx);
            hasAttributes = true;

            switch (anAttribute.getAttributeKind())
            {
                case PRIMITIVE:
                {
                    String defaultValue = anAttribute.getDefaultValue();
                    if (defaultValue == null)
                        defaultValue = "0";
                    pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = " + defaultValue);
                    writeAttributeComment(pw, anAttribute);
                    break;
                }

                case CLASSREF:
                {
                    String attributeType = anAttribute.getType();
                    String initClass = anAttribute.getInitialClass();
                    if (initClass != null)
                        pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = " + initClass + "()");
                    else
                        pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = " + attributeType + "()");
                    writeAttributeComment(pw, anAttribute);
                    break;
                }

                case PRIMITIVE_LIST:
                {
                    if (anAttribute.getUnderlyingTypeIsPrimitive())
                    {
                        pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = [0] * " + anAttribute.getListLength());
                    }
                    else
                    {
                        String attributeType = anAttribute.getType();
                        pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = [" + attributeType + "() for _ in range(" + anAttribute.getListLength() + ")]");
                    }
                    writeAttributeComment(pw, anAttribute);
                    break;
                }

                case OBJECT_LIST:
                {
                    pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = []");
                    writeAttributeComment(pw, anAttribute);
                    break;
                }

                case SISO_ENUM:
                {
                    String enumType = anAttribute.getType();
                    String defaultValue = anAttribute.getDefaultValue();
                    if (defaultValue != null)
                        pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = " + defaultValue);
                    else
                        pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = " + enumType + "(0)");
                    writeAttributeComment(pw, anAttribute);
                    break;
                }

                case SISO_BITFIELD:
                {
                    String bfType = anAttribute.getType();
                    String defaultValue = anAttribute.getDefaultValue();
                    if (defaultValue != null)
                        pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = " + defaultValue);
                    else
                        pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = " + bfType + "(0)");
                    writeAttributeComment(pw, anAttribute);
                    break;
                }

                case PADTO16:
                case PADTO32:
                case PADTO64:
                    pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = b''");
                    pw.println(INDENT + INDENT + "\"\"\"Padding to boundary.\"\"\"");
                    break;

                case STATIC_IVAR:
                    // Static class variables are handled at class level, not instance
                    break;

                default:
                    break;
            }
        }

        // If no attributes and no parent, add pass
        if (!hasAttributes && aClass.getParentClass().equalsIgnoreCase("root"))
        {
            pw.println(INDENT + INDENT + "pass");
        }

        // Initial values
        List<GeneratedInitialValue> inits = aClass.getInitialValues();
        for (int idx = 0; idx < inits.size(); idx++)
        {
            GeneratedInitialValue anInit = inits.get(idx);
            GeneratedClass currentClass = aClass;
            boolean found = false;

            while (currentClass != null)
            {
                List<GeneratedClassAttribute> thisClassAttributes = currentClass.getClassAttributes();
                for (GeneratedClassAttribute anAttribute : thisClassAttributes)
                {
                    if (anInit.getVariable().equals(anAttribute.getName()))
                    {
                        found = true;
                        break;
                    }
                }
                if (found) break;
                currentClass = classDescriptions.get(currentClass.getParentClass());
            }
            if (!found)
            {
                System.out.println("Could not find initial value matching attribute name for " + anInit.getVariable() + " in class " + aClass.getName());
            }
            else
            {
                pw.println(INDENT + INDENT + "self." + anInit.getVariable() + " = " + anInit.getVariableValue());
            }
        }

        pw.println();
    }

    /**
     * Write attribute comment as inline docstring
     */
    private void writeAttributeComment(PrintWriter pw, GeneratedClassAttribute anAttribute)
    {
        if (anAttribute.getComment() != null && !anAttribute.getComment().trim().isEmpty())
        {
            pw.println(INDENT + INDENT + "\"\"\"" + anAttribute.getComment() + "\"\"\"");
        }
    }

    /**
     * Write the serialize method that marshals the class to a DataOutputStream.
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    public void writeMarshal(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println(INDENT + "def serialize(self, outputStream):");
        pw.println(INDENT + INDENT + "\"\"\"Serialize the class to a DataOutputStream.\"\"\"");

        // Call super().serialize() if subclass
        if (!aClass.getParentClass().equalsIgnoreCase("root"))
        {
            pw.println(INDENT + INDENT + "super().serialize(outputStream)");
        }

        List<GeneratedClassAttribute> attributes = aClass.getClassAttributes();
        boolean hasContent = false;

        for (int idx = 0; idx < attributes.size(); idx++)
        {
            GeneratedClassAttribute anAttribute = attributes.get(idx);

            if (!anAttribute.shouldSerialize)
            {
                pw.println(INDENT + INDENT + "# attribute " + anAttribute.getName() + " marked as do not serialize");
                hasContent = true;
                continue;
            }

            hasContent = true;
            switch (anAttribute.getAttributeKind())
            {
                case PRIMITIVE:
                {
                    String marshalType = marshalTypes.getProperty(anAttribute.getType());
                    if (anAttribute.getIsDynamicListLengthField())
                    {
                        GeneratedClassAttribute listAttribute = anAttribute.getDynamicListClassAttribute();
                        pw.println(INDENT + INDENT + "outputStream.write_" + marshalType + "(len(self." + listAttribute.getName() + "))");
                    }
                    else
                    {
                        pw.println(INDENT + INDENT + "outputStream.write_" + marshalType + "(self." + anAttribute.getName() + ")");
                    }
                    break;
                }

                case CLASSREF:
                    pw.println(INDENT + INDENT + "self." + anAttribute.getName() + ".serialize(outputStream)");
                    break;

                case PRIMITIVE_LIST:
                {
                    pw.println(INDENT + INDENT + "for idx in range(0, " + anAttribute.getListLength() + "):");
                    if (anAttribute.getUnderlyingTypeIsPrimitive())
                    {
                        String marshalType = marshalTypes.getProperty(anAttribute.getType());
                        pw.println(INDENT + INDENT + INDENT + "outputStream.write_" + marshalType + "(self." + anAttribute.getName() + "[idx])");
                    }
                    else if (anAttribute.listIsClass())
                    {
                        pw.println(INDENT + INDENT + INDENT + "self." + anAttribute.getName() + "[idx].serialize(outputStream)");
                    }
                    break;
                }

                case OBJECT_LIST:
                {
                    pw.println(INDENT + INDENT + "for anObj in self." + anAttribute.getName() + ":");
                    String marshalType = marshalTypes.getProperty(anAttribute.getType());
                    if (marshalType == null) // It's a class
                    {
                        pw.println(INDENT + INDENT + INDENT + "anObj.serialize(outputStream)");
                    }
                    else // It's a primitive
                    {
                        pw.println(INDENT + INDENT + INDENT + "outputStream.write_" + marshalType + "(anObj)");
                    }
                    break;
                }

                case SISO_ENUM:
                {
                    String enumSize = anAttribute.getEnumMarshalSize();
                    String writeMethod = enumMarshalWriteTypes.getProperty(enumSize);
                    if (writeMethod == null) writeMethod = "unsigned_byte";
                    pw.println(INDENT + INDENT + "outputStream.write_" + writeMethod + "(self." + anAttribute.getName() + ".value)");
                    break;
                }

                case SISO_BITFIELD:
                {
                    String enumSize = anAttribute.getEnumMarshalSize();
                    String writeMethod = enumMarshalWriteTypes.getProperty(enumSize);
                    if (writeMethod == null) writeMethod = "unsigned_byte";
                    pw.println(INDENT + INDENT + "outputStream.write_" + writeMethod + "(self." + anAttribute.getName() + ".value)");
                    break;
                }

                case PADTO16:
                    pw.println(INDENT + INDENT + "# pad to 16-bit boundary");
                    pw.println(INDENT + INDENT + "# TODO: implement padding logic");
                    break;

                case PADTO32:
                    pw.println(INDENT + INDENT + "# pad to 32-bit boundary");
                    pw.println(INDENT + INDENT + "# TODO: implement padding logic");
                    break;

                case PADTO64:
                    pw.println(INDENT + INDENT + "# pad to 64-bit boundary");
                    pw.println(INDENT + INDENT + "# TODO: implement padding logic");
                    break;

                default:
                    break;
            }
        }

        // If no content, add pass
        if (!hasContent && aClass.getParentClass().equalsIgnoreCase("root"))
        {
            pw.println(INDENT + INDENT + "pass");
        }
        pw.println();
    }

    /**
     * Write the parse method that unmarshals the class from a DataInputStream.
     * @param pw PrintWriter
     * @param aClass class of interest
     */
    public void writeUnmarshal(PrintWriter pw, GeneratedClass aClass)
    {
        pw.println(INDENT + "def parse(self, inputStream):");
        pw.println(INDENT + INDENT + "\"\"\"Parse a message. This may recursively call embedded objects.\"\"\"");

        // Call super().parse() if subclass
        if (!aClass.getParentClass().equalsIgnoreCase("root"))
        {
            pw.println(INDENT + INDENT + "super().parse(inputStream)");
        }

        List<GeneratedClassAttribute> attributes = aClass.getClassAttributes();
        boolean hasContent = false;

        for (int idx = 0; idx < attributes.size(); idx++)
        {
            GeneratedClassAttribute anAttribute = attributes.get(idx);

            if (!anAttribute.shouldSerialize)
            {
                pw.println(INDENT + INDENT + "# attribute " + anAttribute.getName() + " marked as do not serialize");
                hasContent = true;
                continue;
            }

            hasContent = true;
            switch (anAttribute.getAttributeKind())
            {
                case PRIMITIVE:
                {
                    String marshalType = marshalTypes.getProperty(anAttribute.getType());
                    pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = inputStream.read_" + marshalType + "()");
                    break;
                }

                case CLASSREF:
                    pw.println(INDENT + INDENT + "self." + anAttribute.getName() + ".parse(inputStream)");
                    break;

                case PRIMITIVE_LIST:
                {
                    if (anAttribute.getUnderlyingTypeIsPrimitive())
                    {
                        pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = [0] * " + anAttribute.getListLength());
                        pw.println(INDENT + INDENT + "for idx in range(0, " + anAttribute.getListLength() + "):");
                        String marshalType = unmarshalTypes.getProperty(anAttribute.getType());
                        pw.println(INDENT + INDENT + INDENT + "self." + anAttribute.getName() + "[idx] = inputStream.read_" + marshalType + "()");
                    }
                    else
                    {
                        pw.println(INDENT + INDENT + "for idx in range(0, " + anAttribute.getListLength() + "):");
                        pw.println(INDENT + INDENT + INDENT + "self." + anAttribute.getName() + "[idx].parse(inputStream)");
                    }
                    break;
                }

                case OBJECT_LIST:
                {
                    pw.println(INDENT + INDENT + "for idx in range(0, self." + anAttribute.getCountFieldName() + "):");
                    String marshalType = marshalTypes.getProperty(anAttribute.getType());
                    if (marshalType == null) // It's a class
                    {
                        pw.println(INDENT + INDENT + INDENT + "element = " + anAttribute.getType() + "()");
                        pw.println(INDENT + INDENT + INDENT + "element.parse(inputStream)");
                        pw.println(INDENT + INDENT + INDENT + "self." + anAttribute.getName() + ".append(element)");
                    }
                    else // It's a primitive
                    {
                        pw.println(INDENT + INDENT + INDENT + "self." + anAttribute.getName() + ".append(inputStream.read_" + marshalType + "())");
                    }
                    break;
                }

                case SISO_ENUM:
                {
                    String enumSize = anAttribute.getEnumMarshalSize();
                    String readMethod = enumMarshalReadTypes.getProperty(enumSize);
                    if (readMethod == null) readMethod = "unsigned_byte";
                    pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = " + anAttribute.getType() + "(inputStream.read_" + readMethod + "())");
                    break;
                }

                case SISO_BITFIELD:
                {
                    String enumSize = anAttribute.getEnumMarshalSize();
                    String readMethod = enumMarshalReadTypes.getProperty(enumSize);
                    if (readMethod == null) readMethod = "unsigned_byte";
                    pw.println(INDENT + INDENT + "self." + anAttribute.getName() + " = " + anAttribute.getType() + "(inputStream.read_" + readMethod + "())");
                    break;
                }

                case PADTO16:
                    pw.println(INDENT + INDENT + "# pad to 16-bit boundary");
                    pw.println(INDENT + INDENT + "# TODO: implement padding parse logic");
                    break;

                case PADTO32:
                    pw.println(INDENT + INDENT + "# pad to 32-bit boundary");
                    pw.println(INDENT + INDENT + "# TODO: implement padding parse logic");
                    break;

                case PADTO64:
                    pw.println(INDENT + INDENT + "# pad to 64-bit boundary");
                    pw.println(INDENT + INDENT + "# TODO: implement padding parse logic");
                    break;

                default:
                    break;
            }
        }

        // If no content, add pass
        if (!hasContent && aClass.getParentClass().equalsIgnoreCase("root"))
        {
            pw.println(INDENT + INDENT + "pass");
        }
        pw.println();
    }

    /**
     * Write bit flag accessor/mutator methods.
     * @param pw PrintWriter
     * @param aClass of interest
     */
    public void writeFlagMethods(PrintWriter pw, GeneratedClass aClass)
    {
        List<GeneratedClassAttribute> attributes = aClass.getClassAttributes();

        for (int idx = 0; idx < attributes.size(); idx++)
        {
            GeneratedClassAttribute anAttribute = attributes.get(idx);

            switch (anAttribute.getAttributeKind())
            {
                case PRIMITIVE:
                {
                    List<GeneratedBitField> bitfields = anAttribute.bitFieldList;

                    for (int jdx = 0; jdx < bitfields.size(); jdx++)
                    {
                        GeneratedBitField bitfield = bitfields.get(jdx);
                        String capped = this.initialCapital(bitfield.name);
                        int shiftBits = this.getBitsToShift(anAttribute, bitfield.mask);

                        // Getter
                        pw.println(INDENT + "def get" + capped + "(self):");
                        if (bitfield.description != null)
                        {
                            pw.println(INDENT + INDENT + "\"\"\"" + bitfield.description + "\"\"\"");
                        }
                        pw.println(INDENT + INDENT + "val = self." + bitfield.parentAttribute.getName() + " & " + bitfield.mask);
                        pw.println(INDENT + INDENT + "return val >> " + shiftBits);
                        pw.println();

                        // Setter
                        pw.println(INDENT + "def set" + capped + "(self, val):");
                        if (bitfield.description != null)
                        {
                            pw.println(INDENT + INDENT + "\"\"\"" + bitfield.description + "\"\"\"");
                        }
                        pw.println(INDENT + INDENT + "self." + bitfield.parentAttribute.getName() + " &= ~" + bitfield.mask);
                        pw.println(INDENT + INDENT + "val = val << " + shiftBits);
                        pw.println(INDENT + INDENT + "self." + bitfield.parentAttribute.getName() + " = self." + bitfield.parentAttribute.getName() + " | val");
                        pw.println();
                    }
                    break;
                }

                default:
                {
                    List<GeneratedBitField> bitfields = anAttribute.bitFieldList;
                    if (!bitfields.isEmpty())
                    {
                        System.out.println("Attempted to use bit flags on a non-primitive field");
                        System.out.println("Field: " + anAttribute.getName());
                    }
                    break;
                }
            }
        }
    }

    /**
     * Write the pdus/__init__.py file with imports of all PDU classes.
     */
    private void writePdusInitPy(List<String> classNames) throws IOException
    {
        File initFile = new File(pythonPackageDir + "/pdus/__init__.py");
        initFile.createNewFile();
        PrintWriter pw = new PrintWriter(initFile, StandardCharsets.UTF_8.name());

        writeLicense(pw);
        pw.println();
        pw.println("# Auto-generated __init__.py for opendis7.pdus package");
        pw.println();

        for (String className : classNames)
        {
            pw.println("from opendis7.pdus." + className + " import " + className);
        }
        pw.println();
        pw.flush();
        pw.close();
    }

    /**
     * Write the top-level opendis7/__init__.py file.
     */
    private void writeTopLevelInitPy(List<String> classNames) throws IOException
    {
        File initFile = new File(pythonPackageDir + "/__init__.py");
        initFile.createNewFile();
        PrintWriter pw = new PrintWriter(initFile, StandardCharsets.UTF_8.name());

        writeLicense(pw);
        pw.println();
        pw.println("# Auto-generated __init__.py for opendis7 package");
        pw.println("# Import subpackages for convenience");
        pw.println();
        pw.println("from opendis7 import pdus");
        pw.println("from opendis7 import io");
        pw.println();
        pw.flush();
        pw.close();
    }

    /**
     * Write the io/ package with DataInputStream.py and DataOutputStream.py.
     * Copies from existing src/python/ files.
     */
    private void writeIoPackage() throws IOException
    {
        // __init__.py for io package
        File ioInit = new File(pythonPackageDir + "/io/__init__.py");
        ioInit.createNewFile();
        PrintWriter pw = new PrintWriter(ioInit, StandardCharsets.UTF_8.name());
        pw.println("from opendis7.io.DataInputStream import DataInputStream");
        pw.println("from opendis7.io.DataOutputStream import DataOutputStream");
        pw.flush();
        pw.close();

        // Copy DataInputStream.py
        copyPythonSourceFile("./src/python/DataInputStream.py", pythonPackageDir + "/io/DataInputStream.py");

        // Copy DataOutputStream.py
        copyPythonSourceFile("./src/python/DataOutputStream.py", pythonPackageDir + "/io/DataOutputStream.py");
    }

    /**
     * Copy a Python source file to the generated package directory.
     */
    private void copyPythonSourceFile(String sourcePath, String destPath) throws IOException
    {
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        if (sourceFile.exists())
        {
            String content = new String(Files.readAllBytes(Paths.get(sourcePath)), StandardCharsets.UTF_8);
            destFile.createNewFile();
            PrintWriter pw = new PrintWriter(destFile, StandardCharsets.UTF_8.name());
            pw.print(content);
            pw.flush();
            pw.close();
            System.out.println("Copied " + sourcePath + " to " + destPath);
        }
        else
        {
            System.out.println("Warning: source file not found: " + sourcePath);
        }
    }

    /**
     * Generate PduFactory.py that maps PDU type IDs to PDU classes.
     */
    private void writePduFactory(List<String> classNames) throws IOException
    {
        File factoryFile = new File(pythonPackageDir + "/PduFactory.py");
        factoryFile.createNewFile();
        PrintWriter pw = new PrintWriter(factoryFile, StandardCharsets.UTF_8.name());

        writeLicense(pw);
        pw.println();
        pw.println("from __future__ import annotations");
        pw.println();
        pw.println("from opendis7.io.DataInputStream import DataInputStream");
        pw.println("from io import BytesIO");
        pw.println();

        // Import all PDU classes
        pw.println("# Import all PDU classes");
        for (String className : classNames)
        {
            pw.println("from opendis7.pdus." + className + " import " + className);
        }
        pw.println();

        // Build the decoder map - find classes with pduType initial values
        pw.println("# Map of PDU type values to PDU classes");
        pw.println("PduTypeDecoders = {");
        for (String className : classNames)
        {
            GeneratedClass aClass = classDescriptions.get(className);
            if (aClass != null)
            {
                List<GeneratedInitialValue> inits = aClass.getInitialValues();
                for (GeneratedInitialValue init : inits)
                {
                    if (init.getVariable().equals("pduType"))
                    {
                        pw.println("    " + init.getVariableValue() + ": " + className + ",");
                    }
                }
            }
        }
        pw.println("}");
        pw.println();

        // getPdu function
        pw.println();
        pw.println("def getPdu(inputStream):");
        pw.println("    \"\"\"Decode a PDU from a DataInputStream based on the PDU type field.\"\"\"");
        pw.println("    inputStream.stream.seek(2, 0)  # Skip to PDU type enum field");
        pw.println("    pduType = inputStream.read_unsigned_byte()");
        pw.println("    inputStream.stream.seek(0, 0)  # Rewind to start");
        pw.println();
        pw.println("    if pduType in PduTypeDecoders:");
        pw.println("        Decoder = PduTypeDecoders[pduType]");
        pw.println("        pdu = Decoder()");
        pw.println("        pdu.parse(inputStream)");
        pw.println("        return pdu");
        pw.println();
        pw.println("    return None");
        pw.println();

        // createPdu function
        pw.println();
        pw.println("def createPdu(data):");
        pw.println("    \"\"\"Create a PDU from binary data bytes.\"\"\"");
        pw.println("    memoryStream = BytesIO(data)");
        pw.println("    inputStream = DataInputStream(memoryStream)");
        pw.println("    return getPdu(inputStream)");
        pw.println();

        pw.flush();
        pw.close();
        System.out.println("Created PduFactory.py");
    }

    /**
     * Write BSD license header as Python comment.
     * @param printWriter output
     */
    public void writeLicense(PrintWriter printWriter)
    {
        printWriter.println("#");
        printWriter.println("# Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS).");
        printWriter.println("# All rights reserved.");
        printWriter.println("# This work is provided under a BSD open-source license, see project");
        printWriter.println("# license.html and license.txt");
        printWriter.println("#");
    }

    /**
     * Python doesn't like forward-declaring classes, so a subclass must be
     * declared after its superclass. This reorders the list of classes so that
     * this is the case.
     * Note: with modular output (one file per class), this ordering is no longer
     * strictly necessary since each file imports its dependencies. However, we
     * keep it for generating __init__.py in the correct order.
     * @return sorted List of classes
     */
    public List<GeneratedClass> sortClasses()
    {
        List<GeneratedClass> allClasses = new ArrayList<>(classDescriptions.values());
        List<GeneratedClass> sortedClasses = new ArrayList<>();

        TreeNode root = new TreeNode(null);

        while (allClasses.size() > 0)
        {
            Iterator<GeneratedClass> li = allClasses.listIterator();
            while (li.hasNext())
            {
                GeneratedClass aClass = li.next();
                if (aClass.getParentClass().equalsIgnoreCase("root"))
                {
                    root.addClass(aClass);
                    li.remove();
                }
            }

            li = allClasses.listIterator();
            while (li.hasNext())
            {
                GeneratedClass aClass = li.next();
                TreeNode aNode = root.findClass(aClass.getParentClass());
                if (aNode != null)
                {
                    aNode.addClass(aClass);
                    li.remove();
                }
            }
        }

        // Get a sorted list
        List<TreeNode> nodeList = new ArrayList<>();
        root.getList(nodeList);

        Iterator<TreeNode> it = nodeList.iterator();
        while (it.hasNext())
        {
            TreeNode node = it.next();
            if (node.aClass != null)
                sortedClasses.add(node.aClass);
        }

        return sortedClasses;
    }
}
