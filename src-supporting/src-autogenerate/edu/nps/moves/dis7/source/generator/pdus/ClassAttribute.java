/**
 * Copyright (c) 2008-2020, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */
package edu.nps.moves.dis7.source.generator.pdus;

import java.util.*;

/**
 * Represents one attribute of a class, ie an instance variable. this may be a 
 * primitive type, a class defined elsewhere in the document, a list, or an
 * array.
 *
 * @author DMcG
 */

public class ClassAttribute 
{
    /**
     * The various things an attribute can be: a primitive type (int, short, byte, etc), 
     * a reference to another class defined in this document, a list of primitives, aka
     * an array, or a list of objects of variable length.  An array could hold objects,
     * but we'll enforce this distinction for easy understanding.
     */
    public enum ClassAttributeType { UNSET, PRIMITIVE, CLASSREF, PRIMITIVE_LIST, OBJECT_LIST, SISO_ENUM, SISO_BITFIELD, PADTO16, PADTO32, PADTO64, STATIC_IVAR };
    
    /** Name of this attribute, winds up as the ivar name */
    protected String name;
    
    /** What attribute class this is: primitive, list, array, etc */
    protected ClassAttributeType attributeKind = ClassAttributeType.UNSET;
    
    /** the type of the attribute: a short, class, etc. */
    protected String type;
    
    /** if a class and we want to initialize a subtype: */
    protected String initialClass;
    
    /** Comment, can be carried over to the source code generated */
    String comment;
    
    /** if it specifies a field which we want to have no getter or setter */
    protected boolean hidden = false;
    
    /** Used only if this is a list attibute */
    protected int listLength = 0;
    
    protected boolean fixedLength = false;
    
    /** If this is a variable list length field, when unmarshalling we need to know how many
     * to unmarshal. This is the name of the filed that contains that count.
     */
    protected String countFieldName;
    
    /** 
     * Which of list or array is it
     */
    protected boolean isPrimitiveListLengthField = false;
    protected boolean isDynamicListLengthField = false;
    
    /** If this is a dynamic length list field or primitive list, we also need the field that this tells the 
     * length for.
     */
    protected ClassAttribute dynamicListClassAttribute = null;
    
    /**
     * The default value for this attribute if it is a primitive.
     */
    protected String defaultValue = null;
    
    /** If this is a list of some sort, this is true if the list consists of primitives, false if the list 
     * consists of classes
     */
    protected boolean underlyingTypeIsPrimitive = false;
    
    /** If this is a list of some sort, this is true if the list consists of class references, false if the list 
     * consists of primitives
     */
    protected boolean underlyingTypeIsClass = false;
    
    /** If this is a list of some sort, this is true if the list consists of class references, false if the list 
     * consists of primitives
     */    
    protected boolean underlyingTypeIsEnum = false;
    
    
    /** Some fields, such as Marking, could have arrays that are treated a C strings. At least on the set
     *  method, if we pass in an array we can have an alternate method that treats the input string as
     * a c-style string, with a terminating null character. This is not strictly compliant with the DIS
     * standard, which makes no assumptions about null-terminated strings, but it happens often enough
     * in the C world to special case it.
     */
    protected boolean couldBeString = false;
    
    /** Some fields are really bit fields, with flags that constitute subranges. */
    protected boolean isBitField = false;
    
    protected List<BitField> bitFieldList = new ArrayList<>();
    
    /** Should we serialize this attribute to the message or not? By default yes, but
     * this can be overridden by the attribute serialize="false" in the xml
     */
    protected boolean shouldSerialize = true;
    
    protected String enumMarshalSize = "8";
    
    /** Get the name of the class attribute/iname
     * @return 
     */
    public String getName()
    {
        return name;
    }
    
    public void setName(String pName)
    {
        name = pName;
    }
   
    /** get the kind of the attribute (primitive, list, array, etc.)
     * @return  */
    public ClassAttributeType getAttributeKind()
    {
        return attributeKind;
    }
    
    public void setAttributeKind(ClassAttributeType pKind)
    {
        attributeKind = pKind;
    }
    
    /** Get the type of the field
     * @return  
     */
    public String getType()
    {
        return type;
    }
    
    public void setType(String pType)
    {
        type = pType;
    }
    
    public String getInitialClass()
    {
        return initialClass;
    }
    
    public void setInitialClass(String s)
    {
        initialClass = s;
    }
    
    /** Comment
     * @return  
     */
    public String getComment()
    {
        return comment;
    }
    
    public void setComment(String pComment)
    {
        comment = pComment;
    }
    
    public void setHidden(boolean tf)
    {
        hidden = tf;
    }
    
    public boolean isHidden()
    {
        return hidden;
    }
    
    public void setListLength(int pListLength)
    {
        listLength = pListLength;
    }
    
    public int getListLength()
    {
        return listLength;
    }
 
    public boolean isFixedLength()
    {
        return fixedLength;
    }
    
    public void setFixedLength(boolean tf)
    {
        fixedLength = tf;
    }
    
    public String getCountFieldName()
    {
        return countFieldName;
    }
    
    public void setCountFieldName(String pFieldName)
    {
        countFieldName = pFieldName;
    }
    
    /** 
     * Returns true if 1) this is a list, either fixed or variable, and 2) contains a class
     * @return 
     */
    public boolean listIsClass()
    {
        if(! ((attributeKind == ClassAttributeType.PRIMITIVE_LIST) || (attributeKind == ClassAttributeType.OBJECT_LIST)))
            return false;
        
        return !underlyingTypeIsPrimitive;
    }
    
    /**
     * Set the default value for a primitive type 
     * @return 
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }
    
    /** 
     * Return the default value for a primitive type
     * @param pValue
     */
    public void setDefaultValue(String pValue)
    {
        defaultValue = pValue;
    }
    
    /**
     * sets true if the underlying type of a list is a primitive, false if it is a class
     * @param newValue
     */
    public void setUnderlyingTypeIsPrimitive(boolean newValue)
    {
        underlyingTypeIsPrimitive = newValue;
    }
    
    /**
     * returns true if this is a list and the underlying type is a primitive
     * @return 
     */
    public boolean getUnderlyingTypeIsPrimitive()
    {
        return underlyingTypeIsPrimitive;
    }
    
   /**
     * sets true if the underlying type of a list is a enum
     * @param newValue
     */
    public void setUnderlyingTypeIsEnum(boolean newValue)
    {
        underlyingTypeIsEnum= newValue;
    }
    
    /**
     * returns true if this is a list and the underlying type is a primitive
     * @return 
     */
    public boolean getUnderlyingTypeIsEnum()
    {
        return underlyingTypeIsEnum;
    }
    
    public boolean getCouldBeString()
    {
        return couldBeString;
    }
    
    public void setCouldBeString(boolean couldBeString)
    {
        this.couldBeString = couldBeString;
    }
    
    public void setIsDynamicListLengthField(boolean flag)
    {
        isDynamicListLengthField = flag;
    }
    
    public boolean getIsDynamicListLengthField()
    {
        return isDynamicListLengthField;
    }
    
    public void setIsPrimitiveListLengthField(boolean b)
    {
        isPrimitiveListLengthField = b;
    }
    public boolean getIsPrimitiveListLengthField()
    {
        return isPrimitiveListLengthField;
    }

    public void setDynamicListClassAttribute(ClassAttribute attr)
    {
        dynamicListClassAttribute = attr;
    }
    
    public ClassAttribute getDynamicListClassAttribute()
    {
        return dynamicListClassAttribute;
    }
    
    public void setIsBitField(boolean isBitField)
    {
        this.isBitField = isBitField;
    }
    
    public boolean getIsBitField()
    {
        return this.isBitField;
    }
    
    public void addBitField(BitField aBitField)
    {
        bitFieldList.add(aBitField);
    }

    public String getEnumMarshalSize()
    {
        return enumMarshalSize;
    }

    public void setEnumMarshalSize(String enumMarshalSize)
    {
        this.enumMarshalSize = enumMarshalSize;
    }
    
    
}
