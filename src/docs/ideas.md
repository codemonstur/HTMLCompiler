
- Add validation features of HTML
  - WIA
  - table without th children
  - Warn on use of tables, structured divs are better: https://divtable.com/table-styler/
  - spelling mistakes
  - missing seo meta tags
  - input field without a label
  - unencoded HTML entities
  - Body tags in head
  - Head tags in body
  - block elements inside inline elements
  - Implement checks for accessibility rules: https://a11y-automation.dev/violations
- Make validation checks work for neko compilers
- Allow for check hints inside HTML pages
- Run lighthouse on some code and turn all its warnings into checks
- Adaptive compiling
  Keep track of which file get touched by which root file
  When a dependent file gets changed compile the root file
- Implement tag merging with neko compilers
- Implement HtmlCompiler based on codemonstur htmlparser
- Fix html namespace addition in neko compilers
- Run checks post processing
- Fix bug with wrong output of script tag in warning on integrity
- Create html tags for including common components
  - From material
  - From gentelella
  - From coreui
- A library meta tag (supported by Jsoup)
- Support more library tags
  Look at CDNs and how they store their stuff, should be able to extract a bunch of libraries
- Add support for stylus: http://stylus-lang.com/, https://github.com/stylus/stylus
- Add option to select 'latest' library
- Add version testing for external binaries
- Implementeer alle a11y checks in htmlcompiler https://a11y-automation.dev/violations

- Implement JWebAssemply to allow for Java client side code
- Add a way of including a Java project as inline JavaScript by converting it
  <script type="application/java" src="path/to/root" main-class="app.Main" 
  dependencies="
  dep/path/first.jar;
  dep/path/second.jar;
  groupId:artifactId:version;
  "></script>
