
[![GitHub Release](https://img.shields.io/github/release/codemonstur/htmlcompiler.svg)](https://github.com/codemonstur/htmlcompiler/releases) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.codemonstur/htmlcompiler/badge.svg)](http://mvnrepository.com/artifact/com.github.codemonstur/htmlcompiler)
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

- Scaling images to a given size
- Remove unnecessary meta tag in output
- Inline remotely hosted JavaScript and CSS
  - Check validity using integrity tag
- Add support for [Autoprefixer](https://github.com/postcss/autoprefixer), or [autoprefixer4j](https://github.com/mwanji/autoprefixer4j)

### How to use as maven plugin

1. Add this code to the pom:
```
<plugin>
    <groupId>com.github.codemonstur</groupId>
    <artifactId>htmlcompiler</artifactId>
    <version>5.3.0</version>
    <executions>
        <execution><goals><goal>compile</goal></goals></execution>
    </executions>
</plugin>
```
2. Run `mvn package`

The code will compile all HTML files in `src/main/websrc` to `target/classes/webbin` while mirroring the directory structure.

For an example how to set this up see here: in the `example` subdirectory.
