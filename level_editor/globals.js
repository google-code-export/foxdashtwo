// manage our overall objects
var canvas;
var context;

//very important
var player = new Player(20,20);
var objects_array = new Array();

//manage screen size
var width  = 1280;
var height = 800;

//handle events
var click_point = new Vector(0,0);
var drag_point = new Vector(0,0);
var world_drag_point = new Vector(0, 0);
var drag_delta = new Vector(0,0);
var mouse_down = false;

//not sure what this is
var levelAdj = new Vector(-width/2, -height/2);

//store the different coordinates
var world_coords = new Vector(0,0);

//movement of camera
var multiplier = 2;
var left_vector = new Vector(-1 * multiplier, 0);
var right_vector = new Vector(1 * multiplier,0);
var up_vector = new Vector(0, 1 * multiplier);
var down_vector = new Vector(0, -1 * multiplier);

//again, not so sure what this is
var screenLeft = 0;
var screenRight = width;
var screenTop = 0;
var screenBottom = height;

//seriously dunno
var loadLevelWindow = undefined;

//fps
var fps;

//currently set tab in the interface
var current_tab;

//Colors
var redFill 		= "rgba(255,0,0,1)";
var lightredFill 	= "rgba(128, 0, 0, 1)";
var greenFill 		= "rgba(0,255,0, 1)";
var lightgreenFill	= "rgba(0,128,0,1)";
var orangeFill 		= "rgba(255,140,0,1)";
var blueFill 		= "rgba(0,0,255,1)";
var lightblueFill 	= "rgba(0, 0, 128, 1)";
var blackFill 		= "rgba(0,0,0,1)";
var whiteFill 		= "rgba(255,255,255,1)";
var lightblueFill 	= "rgba(100,100,255,1)";
var greyFill 		= "rgba(255,255,255,.5)";
var lightgreyFill 	= "rgba(200,200,200,1)";
var darkgreyFill 	= "rgba(169,169,169,1)";
var brownFill 		= "rgba(139,69,19,1)";
var purpleFill 		= "rgba(170,0,255,1)";
	
var default_font = '15px sans-serif';