Free open source framework for targeted search of mathematical scholarly papers

## Features ##

  * Indexing mathematical papers in Latex as [LOD compliant](http://en.wikipedia.org/wiki/Linked_data) RDF data
  * Mining the document logical structure (see details in [our WIMS'11 slides](http://www.slideshare.net/NikitaZhiltsov/logical-structure-analysis-of-scientific-publication-in-mathematics))
  * Resolving combinations of structured queries and full text queries:
    * Metadata search
    * Semantic search of mathematical entities such as theorems, proofs, lemmas, definitions etc. connected with relations
    * Combine keyword search and search over the document logical structure
  * Performing inference during query resolution using [our ontology](http://cll.niimm.ksu.ru/ontologies/mocassin)
  * Enhanced preview of the search results; service for dereferencing URIs of the extracted elements

## Demo ##

**UI:** http://cll.niimm.ksu.ru/mocassin

Feel free to give us feedback using the widget on the right.

**SPARQL endpoint:** http://cll.niimm.ksu.ru:8890/sparql

Use 'http://cll.niimm.ksu.ru/mocassinfortest' as 'Default Graph URI' and execute SPARQL queries. Some examples are given in the slides below.

## Details ##

[Mocassin Indexing Process Workflow](http://code.google.com/p/mocassin/wiki/MocassinIndexingProcessWorkflow)

See the [presentation](http://speakerdeck.com/u/nzhiltsov/p/mocassin-search-of-mathematical-scholarly-papers) for more details

## Known Issues ##

[Issues & Bugs](http://code.google.com/p/mocassin/issues/list?can=2&q=type=Defect)

## Future Work ##

[Road Map](http://code.google.com/p/mocassin/wiki/RoadMap)