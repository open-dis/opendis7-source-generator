/**
 * Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */
package edu.nps.moves.dis7.source.generator.pdus;

/** PDU autogeneration supporting class.
 */
public class GeneratedBitField // TODO consider refactor renaming as GeneratedBitField
{
    String mask = "0";
    String name;
    String description;
    GeneratedClassAttribute parentAttribute;

    /**
     * Constructor
     * @param name name of this BitField
     * @param mask bit mask for corresponding bits
     * @param description description of this BitField
     * @param parentAttribute parentAttribute of this BitField
     */
    public GeneratedBitField(String name, String mask, String description, GeneratedClassAttribute parentAttribute)
    {
        this.mask = mask;
        this.name = name;
        this.description = description;
        this.parentAttribute = parentAttribute;
    }
}
