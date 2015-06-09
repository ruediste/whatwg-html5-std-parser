whatwg-html5-std-parser
=======================

[![Build Status](https://travis-ci.org/ruediste/whatwg-html5-std-parser.svg)](https://travis-ci.org/ruediste/whatwg-html5-std-parser)

A Parser for the WHATWG HTML5 standard.

The WHATWG HTML5 is provided as HTML text document. This is a parser which extracts machine readable information from that document.

The document can be downloaded from <https://html.spec.whatwg.org/> and has to be provided to the parser.

The result is a object describing all HTML elements and the available attributes. The parser only extracts a very small subset of the information contained in the standard. Feel free to extend.

Code Sample:

    
    // load the spec from the classpath, parse it and print the result
    new Parser().parse(getClass().getResourceAsStream(...)).print();
    
    