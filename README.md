# open-dis7-source-generator
Project to generate a type-safe java implementation of the DIS Protocol v. 7, IEE 1278.1-2012 from SISO and IEEE specifications.  This project is written in Java.

**IMPORTANT!**  This project is not complete and is in a testing phase.  Until this notice is removed, use the DIS libraries under the **open-dis-java** and **Enumerations** projects.

<H3>Background</H3>
This work is an update/continuation of the **`open-dis/xmlpg`** project created by the late Don McGregor of the Naval Postgraduate School.  Its goal is twofold:

1. To provide reference implementations of the DIS protocol network messages in several languages.
2. To do so by means of single XML descriptions of the protocol which are then referenced by individual language generators.

While there exists code in the project to generate source in javascript, python and other languages, that code is legacy, and to-date, only the Java implementation is complete.

This work is driven by two specifications.

* *IEEE Std 1278.1-2012, IEEE Standard for Distributed Interactive Simulation—Application Protocols*
* *SISO-REF-010-v25, 2018-08-29, Simulation Interoperability Standards Organization, Inc.*

The first describes the DIS protocol in detail -- specifying application algorithms as well as the precise format of network data.  The second enumerates specific values for fields within the network data which correspond to actual entities in the real world.

The SISO specification is issued in several formats.  One of these is XML and that file is used directly by this project.  The IEEE specification is textual and preliminary work was required to describe its contents in several XML files.  The XML files used as input to this project are found in the **`XML`** directory.

A **`SAX`** ("Simple API for XML") Java implementation is used to process the XML input files.  String templates for the various output classes are used to define the basic structure of the generated code, and these files are found in the **`resources/edu/moves/dis7/source/generator`** directory.

There are several logical output types described separately in the specifications.  This project processes them independently, i.e., the input XML is re-read for each type.  Those types are:

1. Protocol Data Units (PDUs)
2. Enumerations
3. Object types
4. Radio Jammer types
5. Entity types

*PDUs* and *Enumerations* are the most commonly used.  The number of distinct *entity types* is large, which translates into a large number of Java classes.  The use of the generated entity types is optional in a DIS application, since a DIS programmer managing a small number of entities may choose to manually insert the appropriate values into his/her data structures.  For that reason, plus the fact that the entity type classes simply implement an abstract class by supplying 1-4 integer values, the source is not intended to be included in a distribution.  The completed class jar files are available.  See below.

<H3>Generation Procedure</H3>
1. **Clone project**

	`git clone https://github.com/open-dis/open-dis7-source-generator.git`

2. **Open Netbeans 11, navigate to clone directory**

3. **`File->Open Project->open-dis7-source-generator`**

4. **Run Project**

	First run will end in error such as<br/>
`Exception in thread "main" java.lang.RuntimeException: java.lang.ClassNotFoundException: edu.nps.moves.dis7.enumerations.PlatformDomain`

5. **Run Project again**

	Should be no errors.

6. **`Run->Build Project`**

	This step takes a while since there are over 20000 entity classes.

	A jar file named `open-dis7-source-generator.jar` is created, but it is not used.

7. **From the File window, right-click `build.xml`**

8. **Select `Run Target->Other Targets->package-all-jars`**

9. **Select `Run Target->Other Targets->javadoc entities`**

	This step takes a while.
	
<H3>Products</H3>
1. The generated source resides in the `src-generated` directory.
2. The generated entity jars reside in the `dist` directory.
3. The generated entity javadoc resides in the `dist` directory.

If you desire to update the `open-dis7-java` project with any or all of the products from this project, clone that project locally, then follow this procedure:

1. Copy the following files **from**<br/>`open-dis7-source-generator/dist` **to**<br/>`open-dis7-java/entityjars`:
  * open-dis7-entities-all.jar
  * open-dis7-entities-chn.jar
  * open-dis7-entities-deu.jar
  * open-dis7-entities-nato.jar
  * open-dis7-entities-rus.jar
  * open-dis7-entities-usa-air.jar
  * open-dis7-entities-usa-all.jar
  * open-dis7-entities-usa-land.jar
  * open-dis7-entities-usa-munitions.jar
  * open-dis7-entities-usa-surface.jar
  * **open-dis7-entities-javadoc.jar**
2. **Omitting the `entities` directory**, copy the tree of java source files (*.java) **from**<br/>`open-dis7-source-generator/src-generated/java/edu/nps/moves/dis7` **to**<br/>`open-dis7-java/src-generated/edu/nps/moves/dis7`.
	