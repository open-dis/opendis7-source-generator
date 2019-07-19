/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nps.moves.dis7.source.generator;

/**
 * Main.java created on Jul 17, 2019
 * MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Main
{
  // @formatter:off
  static String sisoXmlFile = "xml/SISO/SISO_REF_010_v25/SISO_REF_010.xml";
  static String pduXmlFile  = "xml/dis_7_2012/DIS_7_2012.xml";

  static String enumOutputPath       = "src-generated/java/edu/nps/moves/dis/enumerations";
  static String pduOutputPath        = "src-generated/java";
  static String jammerOutputPath     = "src-generated/java/edu/nps/moves/dis/jammers";
  static String objectTypeOutputPath = "src-generated/java/edu/nps/moves/dis/objecttypes";
  static String entitiesOutputPath   = "src-generated/java/edu/nps/moves/dis/entities";

  static String enumPackage       = "edu.nps.moves.dis.enumerations";
  static String pduPackage        = "edu.nps.moves.dis";
  static String jammerPackage     = "edu.nps.moves.dis.jammers";
  static String objectTypePackage = "edu.nps.moves.dis.objecttypes";
  static String entitiesPackage   = "edu.nps.moves.dis.entities";
  // @formatter:on

  public static void main(String[] args)
  {
    // ENUMERATIONS
    System.out.println("------------- Generating enumerations in edu.nps.moves.dis.enumerations -------------");
    edu.nps.moves.dis7.source.generator.enumerations.Main.main(new String[]{sisoXmlFile, enumOutputPath, enumPackage});

    // PDUS and associated objects, legacy classes
    System.out.println("------------- Generating pdus in edu.nps.moves.dis -------------");
    System.getProperties().setProperty("xmlpg.generatedSourceDir", pduOutputPath); // legacy parameter passing
    System.getProperties().setProperty("xmlpg.package", pduPackage);
    edu.nps.moves.dis7.source.generator.pdus.Main.main(new String[]{pduXmlFile, "java"});

    // JAMMERS
    System.out.println("------------- Generating jammers in edu.nps.moves.dis.jammers -------------");
    edu.nps.moves.dis7.source.generator.entitytypes.JammerMain.main(new String[]{sisoXmlFile, jammerOutputPath, jammerPackage});

    // Object types
    System.out.println("------------- Generating object types in edu.nps.moves.dis.objecttypes -------------");
    edu.nps.moves.dis7.source.generator.entitytypes.ObjectTypeMain.main(new String[]{sisoXmlFile, objectTypeOutputPath, objectTypePackage});

    //ENTITIES
    System.out.println("------------- Generating entity types in edu.nps.moves.dis.entities -------------");
    edu.nps.moves.dis7.source.generator.entitytypes.Main.main(new String[]{sisoXmlFile, entitiesOutputPath, entitiesPackage});

    System.out.println("------------- DIS7 source generation complete -------------");

  }
}
