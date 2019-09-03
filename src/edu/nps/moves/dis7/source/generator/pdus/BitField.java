/**
 * Copyright (c) 2008-2019, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
 */
package edu.nps.moves.dis7.source.generator.pdus;

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
