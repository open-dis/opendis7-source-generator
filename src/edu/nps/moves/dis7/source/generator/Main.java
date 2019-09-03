/**
 * Copyright (c) 2008-2019, MOVES Institute, Naval Postgraduate School. All rights reserved.
 * This work is licensed under the BSD open source license, available at https://www.movesinstitute.org/licenses/bsd.html
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

  static String enumOutputPath       = "src-generated/java/edu/nps/moves/dis7/enumerations";
  static String pduOutputPath        = "src-generated/java";
  static String jammerOutputPath     = "src-generated/java/edu/nps/moves/dis7/jammers";
  static String objectTypeOutputPath = "src-generated/java/edu/nps/moves/dis7/objecttypes";
  static String entitiesOutputPath   = "src-generated/java/edu/nps/moves/dis7/entities";

  static String enumPackage       = "edu.nps.moves.dis7.enumerations";
  static String pduPackage        = "edu.nps.moves.dis7";
  static String jammerPackage     = "edu.nps.moves.dis7.jammers";
  static String objectTypePackage = "edu.nps.moves.dis7.objecttypes";
  static String entitiesPackage   = "edu.nps.moves.dis7.entities";
  // @formatter:on

  public static void main(String[] args)
  {
    // ENUMERATIONS
    System.out.println("------------- Generating enumerations in "+enumPackage+" -------------");
    edu.nps.moves.dis7.source.generator.enumerations.Main.main(new String[]{sisoXmlFile, enumOutputPath, enumPackage});

    // PDUS and associated objects, legacy classes
    System.out.println("------------- Generating pdus in "+pduPackage+" -------------");
    System.getProperties().setProperty("xmlpg.generatedSourceDir", pduOutputPath); // legacy parameter passing
    System.getProperties().setProperty("xmlpg.package", pduPackage);
    edu.nps.moves.dis7.source.generator.pdus.Main.main(new String[]{pduXmlFile, "java"});

    // JAMMERS
    System.out.println("------------- Generating jammers in "+jammerPackage+" -------------");
    edu.nps.moves.dis7.source.generator.entitytypes.JammerMain.main(new String[]{sisoXmlFile, jammerOutputPath, jammerPackage});

    // Object types
    System.out.println("------------- Generating object types in "+objectTypePackage+" -------------");
    edu.nps.moves.dis7.source.generator.entitytypes.ObjectTypeMain.main(new String[]{sisoXmlFile, objectTypeOutputPath, objectTypePackage});

    //ENTITIES
    System.out.println("------------- Generating entity types in "+entitiesPackage+" -------------");
    edu.nps.moves.dis7.source.generator.entitytypes.Main.main(new String[]{sisoXmlFile, entitiesOutputPath, entitiesPackage});

    System.out.println("------------- DIS7 source generation complete -------------");

  }
}
