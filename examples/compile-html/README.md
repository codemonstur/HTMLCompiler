
# HTML example

The base functionality of HtmlCompiler is to take source HTML files 
and convert them to compiled HTML files.

This means certain tags will be replaced, CSS and JavaScript can be 
inlined and compressed and other features.

This example project shows how to configure the plugin in maven and 
compile the source HTML located in `src/main/websrc`. 
The result of the compilation can be found in `target/classes/webbin`.

Just run `mvn clean package` to trigger the process.

## Webserver

This project also contains some Java code that will create an HTTP 
server on port 8080 that serves the HTML code.

It is not necessary to use this approach, you can use whatever means 
you like to host the HTML files. A sibling project uses Spring to host 
the files.

