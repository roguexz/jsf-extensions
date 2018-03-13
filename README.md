[![License](https://img.shields.io/:license-Apache2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)


* [JSF Resource Framework Extensions](#jsf-resource-framework-extensions)
  * [About](#about)
  * [Improved Resource Handling](#improved-resource-handling)
    * [Dependencies](#dependencies)
    * [Maven Coordinates](#maven-coordinates)
    * [Build Status](#build-status)

# JSF Resource Framework Extensions

Portable extensions that make JSF nicer to use.

## About
Sometime back I had [blogged](http://roguexz.blogspot.com/2013/10/jsf-2-returning-resource-url-that-is.html) about how
you can render a pretty URL for JSF resources which are local to the web-app. I have successfully used this technique
across various pet projects of mine, but the repetition was definitely annoying me. So I decided to put together a
portable JSF extension that can be easily included in to any project.

Additionally, I also realized that when consuming an open-source CSS library, I find myself rewriting the relative URL
references to fit the JSF resource handling format, i.e., something on the below lines,

    url("#{resource['bootstrap:fonts/glyphicons-halflings-regular.eot']}");

This task can make upgrades quite tedious, so I figured I should be able to leverage existing constructs to solve this
problem instead of defining new ones.

Consider the following project layout,

    ├── app
    │   └── src
    │       └── main
    │           └── webapp
    │               ├── index.xhtml
    │               └── resources
    │                   └── popper
    │                       └── js
    │                           ├── popper.js        ∖ Resources local
    │                           └── popper.min.js    / to the web-app
    ├── resource-library
    │   └── src
    │       └── main/resources/
    │           └── META-INF/resources
    │               ├── bootstrap
    │               │   ├── css
    │               │   │   ├── bootstrap.css
    │               │   │   ├── bootstrap.min.css
    │               │   ├── js
    │               │   │   ├── bootstrap.js
    │               │   │   ├── bootstrap.min.js
    │               │   └── v_4.0.0
    │               └── open-iconic
    │                   ├── css
    │                   │   ├── open-iconic-bootstrap.css      ∖__
    │                   │   ├── open-iconic-bootstrap.min.css  /  |
    │                   ├── fonts                                 |
    │                   │   ├── open-iconic.eot   ∖               |
    │                   │   ├── open-iconic.otf    |      Uses relative
    │                   │   ├── open-iconic.svg    |<---- URL references
    │                   │   ├── open-iconic.ttf    |      to point to these
    │                   │   └── open-iconic.woff  /       files
    │                   └── v_1.1.0

, and let's say your `index.xhtml` has the following resource declarations,

    ...
    <h:outputStylesheet library="bootstrap" name="css/bootstrap.min.css"/>
    <h:outputStylesheet library="open-iconic" name="css/open-iconic-bootstrap.min.css"/>
    ...
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" crossorigin="anonymous"/>
    <h:outputScript library="popper" name="js/popper.min.js" crossorigin="anonymous"/>
    <h:outputScript library="bootstrap" name="js/bootstrap.min.js" crossorigin="anonymous"/>
    ...

, the final rendered output might look like the below,

    <link type="text/css" rel="stylesheet" href="/javax.faces.resource/css/bootstrap.min.css.xhtml?ln=bootstrap"/>
    <link type="text/css" rel="stylesheet" href="/javax.faces.resource/css/open-iconic-bootstrap.min.css.xhtml?ln=open-iconic"/>
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" crossorigin="anonymous"/>
    <script type="text/javascript" src="/javax.faces.resource/js/popper.min.js.xhtml?ln=popper"/>
    <script type="text/javascript" src="/javax.faces.resource/js/bootstrap.min.js.xhtml?ln=bootstrap"/>

The problem with the above rendering is that the Open-Iconic file would request for the CSS files that translate to,

    <link type="text/css" rel="stylesheet" href="/javax.faces.resource/fonts/open-iconic.eot?"/>

Now, the JSF resource servlet (which would handle the above request) will not know how to handle this request .. and it
returns a 404!

## Improved Resource Handling
And this is where my library will help you. Here is what it does,

1. If a resource is local to your web-app, then do not bother rendering it via the JSF servlet.
2. If a request comes in for a resource that does not have any library name associated (e.g., the 404 case noted above),
   then look for the referrer URL and determine the library details from it and send a redirect to the correct resource
   URL.

Once you include this project as a dependency, then you will notice that rendered URLs look like the following,

    <link type="text/css" rel="stylesheet" href="/javax.faces.resource/css/bootstrap.min.css.xhtml?ln=bootstrap"/>
    <link type="text/css" rel="stylesheet" href="/javax.faces.resource/css/open-iconic-bootstrap.min.css.xhtml?ln=open-iconic"/>
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" crossorigin="anonymous"/>
    <script type="text/javascript" src="/resources/popper/js/popper.min.js"/>
    <script type="text/javascript" src="/javax.faces.resource/js/bootstrap.min.js.xhtml?ln=bootstrap"/>

And the [RelativeResourceHandlingFilter](src/main/java/io/rogue/faces/filter/RelativeResourceHandlingFilter.java) will
take care of sending the appropriate redirect to the browser.

### Dependencies

* Java 8
* JavaEE 7 - specifically JSF2

### Maven Coordinates

This module is published to OSS Central and can be access at the following coordinates:

    io.rogue.ee:jsf-extensions:<version>
    
    Check the following link for locating the latest version.
    https://oss.sonatype.org/#nexus-search;gav~io.rogue.ee~jsf-extensions~~~
    
    The very initial functioning release is version 0.0.0

### Build Status

|Branch|Status|
|------|------|
|master| [![Build Status](https://travis-ci.org/roguexz/jsf-extensions.svg?branch=master)](https://travis-ci.org/roguexz/jsf-extensions) |
|develop| [![Build Status](https://travis-ci.org/roguexz/jsf-extensions.svg?branch=develop)](https://travis-ci.org/roguexz/jsf-extensions) |
