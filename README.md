Protomapper Search
=======

## Description
Designed to search biological sequences for specific patterns. Uses Lucene as backend. Under production.

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

