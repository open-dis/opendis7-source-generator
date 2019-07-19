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
