jsdelivr is a CDN used mostly by NPM
it has an API that can be queried for versions of given libraries:

https://github.com/jsdelivr/data.jsdelivr.com#list-package-versions

Getting the versions available for jquery:
curl https://data.jsdelivr.com/v1/package/npm/jquery

An included file will look like this:

// load any project hosted on npm
https://cdn.jsdelivr.net/npm/package@version/file

// load jQuery v3.2.1
https://cdn.jsdelivr.net/npm/jquery@3.2.1/dist/jquery.min.js

// use a version range instead of a specific version
https://cdn.jsdelivr.net/npm/jquery@3.2/dist/jquery.min.js
https://cdn.jsdelivr.net/npm/jquery@3/dist/jquery.min.js

// omit the version completely to get the latest one
// you should NOT use this in production
https://cdn.jsdelivr.net/npm/jquery/dist/jquery.min.js

// add ".min" to any JS/CSS file to get a minified version
// if one doesn't exist, we'll generate it for you
https://cdn.jsdelivr.net/npm/jquery@3.2.1/src/core.min.js

// omit the file path to get the default file
https://cdn.jsdelivr.net/npm/jquery@3.2

// add / at the end to get a directory listing
https://cdn.jsdelivr.net/npm/jquery/