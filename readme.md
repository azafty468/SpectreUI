To Do list:

0) draw basic screen objects
1) setup opengl on base page (basic triangles and such)

Here's what I need to consider.  Where and how are commands processed.  How are they held until they can be used?  what is a better way than using a build cycle
for the UI Translate Graphics command?  Is there a better holding ground than 1-variable and 3 functions for this one temporary variable?  How can I get is so that there
is no modification on the view, controller or main view for this temporary piece of data?

How can I get it so that this graphics data is available at init of the opengl screen.  That way I would not need a permanent storage location for it and I can immediately
pass back the response.  Still though, I think I will need a processing area for UI commands so maybe this will provide the solution anyways.  How about a base class for
the controllers that stores UICommands to be processed?  I can queue up key presses there and remove the state variable at the GLCanvas layer.  Anyways, some thoughts there to be had.
Or just look inot how I can get current profile and capabilities of a gl window through some other means

1a) build the controller that backs the page
2) load graphics
3) send graphics list to base page using a print list
(DONE THROUGH HERE)

4) setup the variable screens
5) pass around UI interactions
6) change the printlist depending on the UI interactions and have the variable screens display a GameObject (or item or creature or whatever)

// TODO design this application to be an entirely separate repository
// TODO design a core set of definitions to be used by all
// TODO design the primary application to be an entirely separate repository


What I need is this:
A way to estimate how many pixels a text write will use in order to do word wrapping
An intelligent way to specify that a label field should be wrapped and not (...)
A hover over text that will display the whole thing
in general, UI fields like buttons for the variable screen that will be the basis of intelligent UI
a list of available screens and a concise way to communicate commands
