Protomapper Search
=======

## Description
Designed to search biological sequences for specific patterns. Uses Lucene as backend. 
This was a project undertaken during graduate school. It indexes biological sequences
in an inverted trigram index for rapid searching. It performs quite well for short pattern searches,
but performance degrades quickly for complex patterns due to the limitations of the indexing strategy.
I keep it here primarily for reference, as it is an example of my early Scala work.
This code is for indexing of the sequences and contains a basic command line application for searching. The API/data access
layer is stored in the Protomapper-Serve repository. 

## Supported Patterns
Because of the complexity of arbitrary regular expressions (eg .*A.*),
I've developed a subset of patterns that should work well for a fulltext search:

| Regex         | Description           | Implimentation   |
| ------------- |:---------------------:| ----------------:|
| .             | Match any char        | MultiphraseQuery |
| .{2,4}        | 2 - 4 occurences      | BooleanQuery     |
| [^X]          | not X                 | MultiphraseQuery |
| [XY]          | X or y                | MultiphraseQuery |
| ^             | beginning (N-Termini) | Add ^ to docs    |
| $             | end (C-Termini)       | Add $ to docs    |
