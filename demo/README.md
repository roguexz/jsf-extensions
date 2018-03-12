# Demo App

The demo application included in this project showcases the JSF resource extensions in action.

## Resources Layout

1. The `resource-library` module (bundled as a jar) brings in BootStrap & Open-Iconic web assets. The Open-Iconic CSS
   files refer to font files using relative paths (which we know will break in standard JSF resource handling).
2. The application itself has a single page that is based on a
   [Bootstrap starter template](https://getbootstrap.com/docs/4.0/examples/starter-template/). This page needs access to
   Bootstrap, Open-Iconic & Popper libraries.
3. The Popper.JS files are included in the web-app directly (under the `resources` folder). This show cases the behavior
   of the resource-handler being able to locate webapp local resources and rendering them with the appropriate URL.

## Building, Running & Verifying the App

    $> gradle run
    
    Access the site @ http://localhost:8080/
    
    Ensure that the resources are being rendered properly, i.e.,
    
    1. There should be no errors in the browser console.
    2. View-source of the page should show you a direct URL to the popper.js file instead of the JSF resource library URL.
    
    
    Finally, just Ctrl+c to stop the running process!
 
