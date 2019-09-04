# open-dis7-source-generator
This is aroject to generate a type-safe java implementation of the DIS Protocol v. 7, IEE 1278.1-2012 from SISO and IEEE specifications.  This project is written in Java.

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

The SISO specification is issued in several file formats.  One of these is XML and that file is used directly by this project.  The IEEE specification is textual and preliminary work was required to describe its contents in several XML files.  Both the SISO file and the IEEE-based XML files used as input to this project are found in the **`XML`** directory.

A **`SAX`** ("Simple API for XML") Java implementation is used to process the XML input files.  String templates for the various output classes are used to define the basic structure of the generated code, and these files are found in the **`resources/edu/moves/dis7/source/generator`** directory.

<H3>Development Environment</H3>

The Java language is inherently cross-platform and any OS on any hardware for which a Java run-time is available should *theoretically* support running of this project.  However, the configuration used by the initial developer is the following:

1. Apache **Netbeans 11** Integrated Development Environment ("IDE")
2. Apache **Ant** Java build tool (integrated in Netbeans)
3. **Git** version control system (for downloading project; supported in Netbeans)
4. OpenJdk Java version 12.0.2

The project is hosted on **github.com** and the support files which are used to define the project structure are also included.  Following the procedure below, a simple download, then a small number of additional steps are all that are required to build the source files for a DIS distribution.

The project does not automatically download runtime dependencies like a **Maven**-based project.  Only one external dependency is used, and that is the Apache **Commons-IO** library.  The jar for that is found in the `libs/` directory of the project.

<H3>Generation Procedure</H3>

(The following steps are ripe for automation using a custom Ant/Netbeans task.)

There are five separate class groups that are generated separately.  Some need prior groups to be built and compiled before they can be compiled with no errors.  There are several defined source folder in the project.  One is `./src-specialcase` . This small group of class files requires the presence of the generated pdus before properly compiling.  Similarly the pdus require the presence of the enumerations before they can be built.

To handle this complexity, 5 separate run configurations have been defined and numbered:

* 1 make enums
* 2 make pdus
* 3 make jammers
* 4 make object types
* 5 make entities

The instructions below reference them in order.

1. **Clone project**
	(You may alternatively use the built-in Git support in Netbeans.)  The command-line execution is:
	
	`git clone https://github.com/open-dis/open-dis7-source-generator.git`

2. **Open Netbeans 11, navigate to clone directory**

	`File->Open Project->open-dis7-source-generator`<hr/>

3. **Choose "1 make enums"** from the run configurations list
4. **Run Project** from the `Run` menu
5. **Build Project** from the `Run` menu (not needed if "compile on save")<hr/>
6. **Choose "2 make pdus"** from the run configurations list
7. **Run Project** from the `Run` menu
8. **In Project Properties/Sources** add the `src-specialcase/java` folder to the "Source Package Folders"
9. **Build Project** from the `Run` menu (not needed if "compile on save")<hr/>
10. **Choose "3 make jammers"** from the run configurations list
11. **Run Project** from the `Run` menu
12. **Choose "4 make object types"** from the run configurations list
13. **Run Project** from the `Run` menu

14. **Choose "5 make entities"** from the run configurations list
15. **Run Project** from the `Run` menu
16. **Clean and Build Project** from the `Run` menu

	This step takes a while since there are over 20000 entity classes.

	A jar file named `open-dis7-source-generator.jar` is created, but it is not used.<hr/>

17. **From the File window, right-click** `build.xml`

18. **Select** `Run Target->Other Targets->package-all-jars`

19. **Select** `Run Target->Other Targets->javadoc entities`

	These steps take a while and produce the products below.
	
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

<h3>Project Directory Structure</h3>

The initial project directory looks like:

```
|-- ./libs
|-- ./nbproject
|-- ./resources
|   `-- ./edu
|       `-- ./nps
|           `-- ./moves
|               `-- ./dis7
|                   `-- ./source
|                       `-- ./generator
|                           |-- ./entitytypes
|                           |-- ./enumerations
|                           |-- ./pdus
|-- ./src
|   `-- ./edu
|       `-- ./nps
|           `-- ./moves
|               `-- ./dis7
|                   `-- ./source
|                       `-- ./generator
|                           |-- ./entitytypes
|                           |-- ./enumerations
|                           |-- ./generator/pdus
|-- ./src-generated
|-- ./src-specialcase
|-- ./src-supporting
`-- ./xml
    |-- ./xml/SISO
    `-- ./xml/dis_7_2012
```
After project execution, the directory tree will also contain:

```
|-- ./build
|-- ./dist
|-- ./test
```

1. **libs** -- third-party Java libraries used by this project
2. **nbproject** -- files supporting the Netbeans project structure
3. **resources** -- supporting files, such as string templates
4. **src** -- generator Java source files
5. **src-generated** -- Java source file output from the source generator
6. **src-specialcase** -- required DIS class files which could not be described by XML
7. **src-supporting** -- class files satisfying generated source dependencies
8. **xml** -- SISO and IEEE-based XML files which serve as the input to the generator
9. **build** -- products of the Java compiler
10. **dist** -- jar files which are the final products of the project
11. **test** -- an empty directory created by Netbeans


<h3>Project Internals</h3>

There are several logical output types described separately in the specifications.  This project processes them independently, i.e., the input XML is re-read for each type.  Those types are:

1. Protocol Data Units (PDUs)
2. Enumerations
3. Object types
4. Radio Jammer types
5. Entity types

*PDUs* and *Enumerations* are the most commonly used.  The number of distinct *entity types* is large, which translates to a large number of Java classes.  The use of the generated entity types is optional in a DIS application, since a DIS programmer managing a small number of entities may choose to manually insert the appropriate values into his/her data structures.  For that reason, plus the fact that the entity type classes simply implement an abstract class by supplying 1-4 integer values, the source is not intended to be included in a distribution.  The completed class jar files are available in the `dist/` directory.

When the project is "run", as described above, the class which serves as the entry point, or "main", is `src/edu/nps/moves/dis7/source/generator/Main`.  As mentioned above, the 5 types of Java classes which are generated are done so independently.  To that end, the main entry just listed simply calls similar Java "main" methods in 5 separate classes:

1.  `edu.nps.moves.dis7.source.generator.enumerations.Main` -- produces enumerations from the SISO specification
2. `edu.nps.moves.dis7.source.generator.pdus.Main` -- produces Pdus and assorted sub-object classes from the IEEE-derived XML inputs
3. `edu.nps.moves.dis7.source.generator.entitytypes.JammerMain` -- produces radio jammer classes from the SISO specification
4. `edu.nps.moves.dis7.source.generator.entitytypes.ObjectTypeMain` -- produces miscellaneous object classes from the SISO specification
5. `edu.nps.moves.dis7.source.generator.entitytypes.Main` -- produces entity type classes from the SISO specification

The order of execution of these 5 sections is important: each potentially relies on the existence of classes created by the execution of the prior steps.  Doing a single "Run project" command as described above will first compile all classes found by Netbeans at that moment. Because some later steps require compiled enumerations, the first run will end in error.  Running a second time will cause the just-created enumeration classes to be compiled, and the subsequent steps can then complete.

<h4>Source Generation Method -- Pdus</h4>

This class contains remnants of legacy code which created pdus classes in different languages.  The "JavaGenerator" subclass is the only one used in this project (to date).

`edu.nps.moves.dis7.source.generator.pdus.Main` first reads the IEEE XML files with a SAX parser and produces a map of class names-to-GeneratedClass objects.  The `GeneratedClass` object contains fields which reflect the information contained in the XML for the particular pdu or object.  This map is theoretically language-neutral is then used to produce pdus classes in various languages.

`edu.nps.moves.dis7.source.generator.pdus.JavaGenerator` then processes this map, generating source for each `GeneratedClass` object encountered.  Template files are used so that the standard Java library String class may be used like the following:<br/>
	`String fileContents = String.format(template, value1, value2, value3 ...);`


<h4>Source Generation Method -- Enumerations</h4>

These classes are simpler than Pdus and are created in a simpler way.  The enumerated values in the SISO specification are implemented as either java Enumeration or java Bitset classes.  (The latter uses an invented "BitField" class as a front end.)

The generated uses SAX and operates when the "start" and "end" tags of the following elements are encountered:

1. `enum`
2. `enumrow`
3. `bitfield`
4. `bitfieldrow`
5. `dict`
6. `dictrow`

The `enum, bitfield, and dict` elements map to separate classes.  The `enumrow, bitfieldrow, and dictrow` map to values within each of those classes.

When a SAX "end" element is encountered, the "current" element is written out as a complete Enumeration or BitField class, with enumerated values contained therein. Template files are used as above.
	
<h4>Source Generation Method -- Entity types, Object types, Jammers</h4>

These classes are also simpler than Pdus.  Each of these three is hierarchically defined.  For instance, a single Entity type is defined by entity, category, subcategory, specific and optional parameters, and not all of the sub parameters are required.  Using a SAX parser, the SISO specification is read sequentially.

When a SAX "start" element is encountered, a new Java object is created -- one of `EntityElem, CategoryElem, SubCategoryElem, SpecificElem, or ExtraElem`.  Since it is a hierarchical structure, when, e.g., a SubCategoryElem is encountered, the newly created object is inserted as a child of the previously created EntityElem.

When a SAX "end" element is encountered, the "current" element is written out as a complete EntityType class, with specific category, subcategory, etc., values. Template files are used as above.

*Document end*
