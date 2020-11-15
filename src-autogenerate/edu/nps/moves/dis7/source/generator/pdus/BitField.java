/**
 * Copyright (c) 2008-2020, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */
package edu.nps.moves.dis7.source.generator.pdus;

/** PDU autogeneration supporting class. */
public class BitField 
{
    String mask = "0";
    String name;
    String comment;
    ClassAttribute parentAttribute;

    public BitField(String name, String mask, String comment, ClassAttribute parentAttribute)
    {
        this.mask = mask;
        this.name = name;
        this.comment = comment;
        this.parentAttribute = parentAttribute;
    }
}
