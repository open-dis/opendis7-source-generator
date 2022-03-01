# opendis7-source-generator

This is a project to generate a type-safe java implementation of the DIS Protocol v. 7, IEEE Standard 1278.1-2012 from SISO and IEEE specifications.  This project is written in Java.

This project provides a complete implementation of the IEEE DIS Protocol and
associated enumerations that are used in the
[opendis7-java](https://github.com/open-dis/opendis7-java) and related projects.
In preparation: [opendis7-python](https://github.com/open-dis/opendis7-python).

Recent project products include full Java support for all 72 DISv7 Protocol Data Units (PDUs)
and over 22,000 SISO-REF-010 enumerations.

Additional testing and experimentation is performed in the NPS MOVES
[Networked Graphics MV3500](https://gitlab.nps.edu/Savage/NetworkedGraphicsMV3500) course.

### Background

This work is an update/continuation of the **`open-dis/xmlpg`** project created by the late Don McGregor of the Naval Postgraduate School (NPS).

Goals are twofold:

1. To provide reference implementations of the DIS protocol network messages in several programming languages.
2. To do so by means of single XML descriptions of the protocol which are then referenced by individual language generators.

While there exists code in the project to generate source in JavaScript, Python and other languages, that code is legacy, and so far only the Java implementation is complete.

This work is driven by two specifications.

* *IEEE Std 1278.1-2012, IEEE Standard for Distributed Interactive Simulation—Application Protocols*
* *SISO-REF-010-v25, 2018-08-29, Simulation Interoperability Standards Organization, Inc.*

The first describes the DIS protocol in detail -- specifying application algorithms as well as the precise format of network data.  The second enumerates specific values for fields within the network data which correspond to actual entities in the real world.

The SISO specification is issued in several file formats.  One of these is XML and that file is used directly by this project.  The IEEE specification is textual and preliminary work was required to describe its contents in several XML files.  Both the SISO file and the IEEE-based XML files used as input to this project are found in the **`XML`** directory.

A **`SAX`** ("Simple API for XML") Java implementation is used to process the XML input files.  String templates for the various output classes are used to define the basic structure of the generated code, and these files are found in the **`stringTemplates/edu/moves/dis7/source/generator`** directory.

<h3>Development Environment</h3>

The Ant build.xml is greatly improved and build tasks are now simply performed.

The Java language is inherently cross-platform and any OS on any hardware for which a Java run-time is available should *theoretically* support running of this project.  However, the configuration used by the initial developer is the following:

1. Apache **Netbeans 12.1** Integrated Development Environment ("IDE")
2. Apache **Ant** Java build tool version 1.10.9
3. **Git** version control system (for downloading project; supported in Netbeans)
4. OpenJdk Java version 15.0.1

Please see [Savage Developers Guide](https://savage.nps.edu/Savage/developers.html) for our recommended development settings.

The project is hosted on **github.com** and the support files which are used to define the project structure are also included.  Following the procedure below, a simple download, then a small number of additional steps are all that are required to build the source files for a DIS distribution.

The project does not automatically download run-time dependencies like a **Maven**-based project.  Only one external dependency is used, and that is the Apache **Commons-IO** library.  The jar for that is found in the `libs/` directory of the project.

1. The generated source         resides in the `src-generated` directory.
2. The generated entity jars    reside  in the `dist` directory.
3. The generated entity javadoc resides in the `dist` directory.

<h3>Project Directory Structure</h3>

The initial project directory looks like:

```
|-- images
|-- lib
|-- nbproject
|-- src-autogenerate
|   `-- edu
|       `-- nps
|           `-- moves
|               `-- dis7
|                   `-- source
|                       `-- generator
|                           |-- entityTypes
|                           |-- enumerations
|                           +-- pdus
|-- src-generated
|-- src-specialcase
|-- src-supporting
|-- stringTemplates
|   `-- edu
|       `-- nps
|           `-- moves
|               `-- dis7
|                   `-- source
|                       `-- generator
|                           |-- entitytypes
|                           |-- enumerations
|                           |-- pdus
`-- xml
    |-- xml/SISO
    `-- xml/dis_7_2012
```
After project execution, the directory tree will also contain:

```
|-- build
|-- dist
|   |-- javadoc
|-- test
```

1. **libs** -- third-party Java libraries used by this project
2. **nbproject** -- files supporting the Netbeans project structure
3. **stringTemplates** -- supporting files, such as string templates
4. **src-autogenerate** -- generator Java source files
5. **src-generated** -- Java source file output from the source generator
6. **src-specialcase** -- required DIS class files which could not be described by XML
7. **src-supporting** -- class files satisfying generated source dependencies
8. **xml** -- SISO and IEEE-based XML files which serve as the input to the generator
9. **build** -- generated directory holding products of the Java compiler
10. **dist** -- generated jar files and javadoc, the products of the project
11. **test** -- an empty directory created by Netbeans

Products are then copied to the opendis7-java projects for further integration, testing
and publication.

<h3>Project Internals</h3>

There are several logical output types described separately in the specifications.  This project processes them independently, i.e., the input XML is re-read for each type.  Those types are:

1. Protocol Data Units (PDUs)
2. Enumerations
3. Object types
4. Radio Jammer types
5. Entity types

*PDUs* and *Enumerations* are the most commonly used.  The number of distinct *entity types* is large, which translates to a large number of Java classes.  The use of the generated entity types is optional in a DIS application, since a DIS programmer managing a small number of entities may choose to manually insert the appropriate values into his/her data structures.  For that reason, plus the fact that the entity type classes simply implement an abstract class by supplying 1-4 integer values, the source is not intended to be included in a distribution.  The completed class jar files are available in the `dist/` directory.

When the project is "run", as described above, the class which serves as the entry point, or "main", is `src/edu/nps/moves/dis7/source/generator/Main`.  As mentioned above, the 5 types of Java classes which are generated are done so independently.  To that end, the main entry just listed simply calls similar Java "main" methods in 5 separate classes:

1. `edu.nps.moves.dis7.source.generator.enumerations.GenerateEnumerations` -- produces enumerations from the SISO specification
2. `edu.nps.moves.dis7.source.generator.pdus.Main` -- produces Pdus and assorted sub-object classes from the IEEE-derived XML inputs
3. `edu.nps.moves.dis7.source.generator.entitytypes.GenerateJammers` -- produces radio jammer classes from the SISO specification
4. `edu.nps.moves.dis7.source.generator.entitytypes.GenerateObjectTypes` -- produces miscellaneous object classes from the SISO specification
5. `edu.nps.moves.dis7.source.generator.entitytypes.GenerateEntityTypes` -- produces entity type classes from the SISO specification

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

Further work:

Refactor Java generator classes -- reasonably stable, minor refactoring occurs occasionally
Implement other language outputs -- Python work is in progress
Improve descriptions / javadoc in XML -- looking pretty good now!
Implement information "toString()" methods for classes like EulerAngles, EntityID, EntityKind
