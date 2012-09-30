var canvas;
var context;

var width  = 800;
var height = 800;

var grav_const = .1;
		
var player;

var playing = false;

var planets = [];
var currentLevel = 2;

var breadCrumbsNDX = 0;
var breadCrumbs = [];
var breadCrumbsColorNDX = 0;
var numBreadCrumbsStored = 0;


var breadCrumbsCanvases = [];
var breadCrumbsImg = undefined;

var savedSinceLastStop = true;

var breadCrumbsMaxNDX = 4;

var flying = false;
var attempts = 0;
var dragging = false;

var touchPt = new Vector(0,0);
var objDragging = undefined;
var draggingOffset = new Vector(0,0);
var currMousePos = new Vector(0,0);
var levelAdj = new Vector(-width/2, -height/2);

var scaleX = 1;
var scaleY = 1;
var minX = 0;
var minY = 0;
var maxX = 0;
var maxY = 0;

var screenLeft = 0;
var screenRight = width;
var screenTop = 0;
var screenBottom = height;

var edit = 0;
var test = 1;
var mode = edit;
var loadLevelWindow = undefined;