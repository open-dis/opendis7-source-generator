## TODO

The opendis7-source-generator project is a staging area for newly created source code.
New source then gets copied into corresponding target projects (Java, Python, etc.)
for unit testing, documentation generation, packaging and deployment.

Our intent is to move the following current external java-specific directories 
into this directory tree:

* [src-generated](../src-generated]
* [src-specialcase](../src-specialcase]
* [src-supporting](../src-supporting]

Note that the following directory trees will not move, since they include
support for multilingual generators.

* [src-autogenerate](../src-autogenerate]
* [stringTemplates](../stringTemplates]
* [xml](../xml]
