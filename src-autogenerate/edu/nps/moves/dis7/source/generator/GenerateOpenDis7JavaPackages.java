/**
 * Copyright (c) 2008-2023, MOVES Institute, Naval Postgraduate School (NPS). All rights reserved.
 * This work is provided under a BSD open-source license, see project license.html and license.txt
 */

package edu.nps.moves.dis7.source.generator;

import edu.nps.moves.dis7.source.generator.pdus.GeneratePdusForGivenLanguage;

/**
 * GenerateOpenDis7JavaPackages.java created on Jul 17, 2019
 * MOVES Institute, Naval Postgraduate School (NPS), Monterey California USA https://www.nps.edu
 *
 * @author Don McGregor, Mike Bailey and Don Brutzman
 * @version $Id$
 */
public class GenerateOpenDis7JavaPackages
{
        /** default constructor */
        public GenerateOpenDis7JavaPackages()
        {
        }
  // @formatter:off
  // shared static defaults
  
  /** Java is a supported programming language for this generator */
  public static final String      JAVA_LANGUAGE    = "java";   // tested sat
  
  /** Python is undergoing testing to be a supported programming language for this generator */
  public static final String      PYTHON_LANGUAGE  = "python"; // TODO python testing
    
  /** Java is default programming language for this generator */
  public static final String      DEFAULT_PROGRAMMING_LANGUAGE = JAVA_LANGUAGE;
  
  /** xml/SISO/SISO-REF-010.xml */
  public static final String DEFAULT_SISO_XML_FILE = "xml/SISO/SISO-REF-010.xml";
  
  /** xml/dis_7_2012/DIS_7_2012.xml */
  public static final String  DEFAULT_PDU_XML_FILE = "xml/dis_7_2012/DIS_7_2012.xml";

  static String enumOutputPath       = "src-generated/java/edu/nps/moves/dis7/enumerations";
  static String pduOutputPath        = "src-generated/java/edu/nps/moves/dis7/pdus";
  static String jammerOutputPath     = "src-generated/java/edu/nps/moves/dis7/jammers";
  static String objectTypeOutputPath = "src-generated/java/edu/nps/moves/dis7/objectTypes";
  static String entitiesOutputPath   = "src-generated/java/edu/nps/moves/dis7/entities";

  static String enumPackage       = "edu.nps.moves.dis7.enumerations";
  static String pduPackage        = "edu.nps.moves.dis7.pdus";
  static String jammerPackage     = "edu.nps.moves.dis7.jammers";
  static String objectTypePackage = "edu.nps.moves.dis7.objectTypes";
  static String entitiesPackage   = "edu.nps.moves.dis7.entities";
  // @formatter:on

    /** Command-line or solo invocation to run this object
     * @param args not used
     */
    public static void main(String[] args)
    {
        String whichLanguage = DEFAULT_PROGRAMMING_LANGUAGE; // DEFAULT_PROGRAMMING_LANGUAGE JAVA_LANGUAGE PYTHON_LANGUAGE
        
        System.out.println (GenerateOpenDis7JavaPackages.class.getName() + "commencing...");
        if (whichLanguage.equalsIgnoreCase("java"))
        {
            System.out.println("------------- opendis7-java generation commence -------------");
            System.out.println();
            // ENUMERATIONS
            System.out.println("------------- Generating enumerations in "+enumPackage+" -------------");
            edu.nps.moves.dis7.source.generator.enumerations.GenerateEnumerations.main(new String[]{DEFAULT_SISO_XML_FILE, enumOutputPath, enumPackage});

            // PDUS and associated objects, legacy classes
            System.out.println("------------- Generating pdus in "+pduPackage+" -------------");
            System.getProperties().setProperty("xmlpg.generatedSourceDir", pduOutputPath); // legacy parameter passing
            System.getProperties().setProperty("xmlpg.package", pduPackage);
            edu.nps.moves.dis7.source.generator.pdus.GeneratePdusForGivenLanguage.main(new String[]{DEFAULT_PDU_XML_FILE, "java"});

            // JAMMERS
            System.out.println("------------- Generating jammers in "+jammerPackage+" -------------");
            edu.nps.moves.dis7.source.generator.entityTypes.GenerateJammers.main(new String[]{DEFAULT_SISO_XML_FILE, jammerOutputPath, jammerPackage});

            // Object types
            System.out.println("------------- Generating object types in "+objectTypePackage+" -------------");
            edu.nps.moves.dis7.source.generator.entityTypes.GenerateObjectTypes.main(new String[]{DEFAULT_SISO_XML_FILE, objectTypeOutputPath, objectTypePackage});

            //ENTITIES
            System.out.println("------------- Generating entity types in "+entitiesPackage+" -------------");
            edu.nps.moves.dis7.source.generator.entityTypes.GenerateEntityTypes.main(new String[]{DEFAULT_SISO_XML_FILE, entitiesOutputPath, entitiesPackage});

            System.out.println("------------- opendis7-java generation complete -------------");
        }
        else if (whichLanguage.equalsIgnoreCase("python"))
        {
            System.out.println("------------- opendis7-python generation commence -------------");
            
//            GeneratePdusForGivenLanguage generatePdusForGivenLanguage = new GeneratePdusForGivenLanguage(DEFAULT_SISO_XML_FILE, whichLanguage);
            
            GeneratePdusForGivenLanguage.main(new String[]{DEFAULT_SISO_XML_FILE, whichLanguage});
            
            System.out.println("------------- opendis7-python generation complete -------------");
        }
        else System.out.println (GenerateOpenDis7JavaPackages.class.getName() + " unsupported language: " + whichLanguage);

        System.out.println (GenerateOpenDis7JavaPackages.class.getName() + "complete.");
    }
}
