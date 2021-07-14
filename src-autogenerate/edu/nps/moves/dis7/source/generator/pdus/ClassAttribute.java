/**
 * Copyright (c) 2008-2021, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
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
    public enum ClassAttributeType { 
        /** attribute property */
        UNSET,
        /** attribute property */
        PRIMITIVE,
        /** attribute property */
        CLASSREF,
        /** attribute property */
        PRIMITIVE_LIST,
        /** attribute property */
        OBJECT_LIST,
        /** attribute property */
        SISO_ENUM,
        /** attribute property */
        SISO_BITFIELD,
        /** attribute property */
        PADTO16,
        /** attribute property */
        PADTO32,
        /** attribute property */
        PADTO64,
        /** attribute property */
        STATIC_IVAR };
    
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
    
    /** Used only if this is a list attribute */
    protected int listLength = 0;

    /** Whether or not attribute has fixed bit length */
    protected boolean fixedLength = false;
    
    /** If this is a variable list length field, when unmarshalling we need to know how many list items
     * to unmarshal. This is the name of the field that contains that count.
     */
    protected String countFieldName;
    
    /** 
     * Which of list or array is it
     */
    protected boolean isPrimitiveListLengthField = false;
    /** Whether or not list length is dynamic */
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

    /** List of bit fields. */
    protected List<BitField> bitFieldList = new ArrayList<>();
    
    /** Should we serialize this attribute to the message or not? By default yes, but
     * this can be overridden by the attribute serialize="false" in the xml
     */
    protected boolean shouldSerialize = true;

    /** Default enumeration size 9 */
    protected String enumMarshalSize = "8";
    
    /** Get the name of the class attribute/iname
     * @return name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Set the name of the class attribute/iname
     * @param pName new name
     */
    public void setName(String pName)
    {
        name = pName;
    }
   
    /** get the kind of the attribute (primitive, list, array, etc.)
     * @return attribute kind of interest */
    public ClassAttributeType getAttributeKind()
    {
        return attributeKind;
    }
    
    /**
     * Set the kind of the class attribute/iname
     * @param pKind new kind
     */
    public void setAttributeKind(ClassAttributeType pKind)
    {
        attributeKind = pKind;
    }
    
    /** Get the type of the field
     * @return type 
     */
    public String getType()
    {
        return type;
    }

    /**
     * Set the type of the class attribute/iname
     * @param pType new type
     */
    public void setType(String pType)
    {
        type = pType;
    }
    
    /**
     * Accessor method
     * @return initialClass name
     */
    public String getInitialClass()
    {
        return initialClass;
    }
    
    /**
     * Accessor method
     * @param initialClassName name
     */
    public void setInitialClass(String initialClassName)
    {
        initialClass = initialClassName;
    }
    
    /** get comment value (description)
     * @return comment value 
     */
    public String getComment()
    {
        return comment;
    }

    /** set comment value (description)
     * @param pComment comment value
     */
    public void setComment(String pComment)
    {
        comment = pComment;
    }
    
    /**
     * set whether attribute is hidden
     * @param flag whether attribute is hidden
     */
    public void setHidden(boolean flag)
    {
        hidden = flag;
    }
    
    /**
     * get whether attribute is hidden
     * @return whether attribute is hidden
     */
    public boolean isHidden()
    {
        return hidden;
    }
    
    /**
     * Set list length
     * @param pListLength  list length
     */
    public void setListLength(int pListLength)
    {
        listLength = pListLength;
    }
    
    /**
     * Get list length
     * @return list length
     */
    public int getListLength()
    {
        return listLength;
    }
 
    /**
     * get whether attribute has fixed length in bits
     * @return whether attribute has fixed length in bits
     */
    public boolean isFixedLength()
    {
        return fixedLength;
    }
    
    /**
     * Set whether attribute has fixed length in bits
     * @param flag whether attribute has fixed length in bits
     */
    public void setFixedLength(boolean flag)
    {
        fixedLength = flag;
    }
    
    /**
     * TODO
     * @return TODO
     */
    public String getCountFieldName()
    {
        return countFieldName;
    }
    
    /**
     * TODO
     * @param pFieldName TODO
     */
    public void setCountFieldName(String pFieldName)
    {
        countFieldName = pFieldName;
    }
    
    /** 
     * Returns true if 1) this is a list,  either fixed or variable, and 2) contains a class
     * @return whether list contains a class
     */
    public boolean listIsClass()
    {
        if(! ((attributeKind == ClassAttributeType.PRIMITIVE_LIST) || (attributeKind == ClassAttributeType.OBJECT_LIST)))
            return false;
        
        return !underlyingTypeIsPrimitive;
    }
    
    /**
     * Get the default value for a primitive type 
     * @return default value
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }
    
    /** 
     * Set the default value for a primitive type
     * @param pValue value of interest
     */
    public void setDefaultValue(String pValue)
    {
        defaultValue = pValue;
    }
    
    /**
     * sets true if the underlying type of a list is a primitive, false if it is a class
     * @param newValue of interest
     */
    public void setUnderlyingTypeIsPrimitive(boolean newValue)
    {
        underlyingTypeIsPrimitive = newValue;
    }
    
    /**
     * returns true if this is a list and the underlying type is a primitive
     * @return value of interest
     */
    public boolean getUnderlyingTypeIsPrimitive()
    {
        return underlyingTypeIsPrimitive;
    }
    
   /**
     * sets true if the underlying type of a list is a enum
     * @param newValue of interest
     */
    public void setUnderlyingTypeIsEnum(boolean newValue)
    {
        underlyingTypeIsEnum= newValue;
    }
    
    /**
     * returns true if this is a list and the underlying type is a primitive
     * @return value of interest
     */
    public boolean getUnderlyingTypeIsEnum()
    {
        return underlyingTypeIsEnum;
    }
    
    /**
     * whether attribute could be string
     * @return whether attribute could be string
     */
    public boolean getCouldBeString()
    {
        return couldBeString;
    }

    /**
     * set whether attribute could be string
     * @param couldBeString whether attribute could be string
     */
    public void setCouldBeString(boolean couldBeString)
    {
        this.couldBeString = couldBeString;
    }
    
    /**
     * whether attribute has dynamic list length
     * @param flag whether attribute has dynamic list length
     */
    public void setIsDynamicListLengthField(boolean flag)
    {
        isDynamicListLengthField = flag;
    }
    
    /**
     * whether attribute has dynamic list length
     * @return whether attribute has dynamic list length
     */
    public boolean getIsDynamicListLengthField()
    {
        return isDynamicListLengthField;
    }
    
    /**
     * set whether attribute has primitive list length
     * @param flag whether attribute has primitive list length
     */
    public void setIsPrimitiveListLengthField(boolean flag)
    {
        isPrimitiveListLengthField = flag;
    }
    /**
     * whether attribute has primitive list length
     * @return whether attribute has primitive list length
     */
    public boolean getIsPrimitiveListLengthField()
    {
        return isPrimitiveListLengthField;
    }

    /**
     * set special attribute
     * @param attr special attribute
     */
    public void setDynamicListClassAttribute(ClassAttribute attr)
    {
        dynamicListClassAttribute = attr;
    }

    /**
     * get special attribute
     * @return special attribute
     */
    public ClassAttribute getDynamicListClassAttribute()
    {
        return dynamicListClassAttribute;
    }

    /**
     * set whether attribute is a bit field
     * @param isBitField whether attribute is a bit field
     */
    public void setIsBitField(boolean isBitField)
    {
        this.isBitField = isBitField;
    }

    /**
     * whether attribute is a bit field
     * @return whether attribute is a bit field
     */
    public boolean getIsBitField()
    {
        return this.isBitField;
    }
    
    /**
     * Add a bit field
     * @param aBitField BitField to add
     */
    public void addBitField(BitField aBitField)
    {
        bitFieldList.add(aBitField);
    }

    /**
     * get enumeration marshal size
     * @return enumeration marshal size value (note String type)
     */
    public String getEnumMarshalSize()
    {
        return enumMarshalSize;
    }

    /**
     * set enumeration marshal size
     * @param enumMarshalSize size value (note String type)
     */
    public void setEnumMarshalSize(String enumMarshalSize)
    {
        this.enumMarshalSize = enumMarshalSize;
    }
    
    
}
