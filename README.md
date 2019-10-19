
[![Build Status](https://travis-ci.org/codemonstur/htmlcompiler.svg?branch=master)](https://travis-ci.org/codemonstur/htmlcompiler)
[![GitHub Release](https://img.shields.io/github/release/codemonstur/htmlcompiler.svg)](https://github.com/codemonstur/htmlcompiler/releases) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.codemonstur/htmlcompiler/badge.svg)](http://mvnrepository.com/artifact/com.github.codemonstur/htmlcompiler)
[![Maintainability](https://api.codeclimate.com/v1/badges/63924c44946973cb37f8/maintainability)](https://codeclimate.com/github/codemonstur/htmlcompiler/maintainability)
[![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/dwyl/esta/issues)
[![Coverage Status](https://coveralls.io/repos/github/codemonstur/htmlcompiler/badge.svg?branch=master)](https://coveralls.io/github/codemonstur/htmlcompiler?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/813d8482256b4ed88e2ff1018d53f06e)](https://www.codacy.com/app/codemonstur/htmlcompiler)
[![Sputnik](https://sputnik.ci/conf/badge)](https://sputnik.ci/app#/builds/codemonstur/htmlcompiler)
[![MIT Licence](https://badges.frapsoft.com/os/mit/mit.svg?v=103)](https://opensource.org/licenses/mit-license.php)

## HtmlCompiler

The HTML compiler is a maven plugin.
Its purpose is to provide compile time features for generating static HTML resources for a frontend project.

The idea is to make it easier to write webapps by allowing the developer to write source code and have the build system generate resources for deployment.
No more minified files everywhere. No more manually compressing resources.

### Dependencies

- To compile typescript the code calls `tsc`, have it in the PATH.
- To compile js++ the code calls `js++`, have it in the PATH.

### Supported features

- Parses HTML as Pebble templates using the Pebble template engine
- Compresses HTML (remove comments, remove whitespace)
- Inlines javascript
- Minifies javascript
- Inlines images
- Inlines stylesheets
- Minifies stylesheets (removing comments, removing whitespace)
- Generates integrity tag for remote resources
- Adds an include statement to JavaScript
- Also compresses HTML inside script tag with type text/x-jquery-tmpl or text/html
- Allow for calling htmlcompiler features through the command line
- Inlining of link tags (favico)
- SASS and LESS compiling
- Inline link tag to stylesheet into style tag

### Planned features (bugfixes)

- Add an error reporter for when JavaScript or CSS has errors
- Add combining of multiple inlined script and style tags into a single tag
- Scaling images to a given size
- Remove unnecessary meta tag in output
- Removing unused JavaScript functions
  - Integrate https://github.com/google/closure-compiler
- Obfuscate css and javascript by rearranging code
- Inline remotely hosted JavaScript and CSS
  - Check validity using integrity tag

### How to use as maven plugin

1. Add this code to the pom:
```
<plugin>
    <groupId>com.github.codemonstur</groupId>
    <artifactId>htmlcompiler</artifactId>
    <version>2.4.0</version>
    <executions>
        <execution><goals><goal>compile</goal></goals></execution>
    </executions>
</plugin>
```
2. Run `mvn package`

The code will compile all HTML files in `src/main/websrc` to `target/classes/webbin` while mirroring the directory structure.

For an example how to set this up see here: in the `example` subdirectory.
