/* begin with general event management*/
function keypressed(e)
{
	//alert(e.keyCode);
	
	setLevelDefinition();
}

function mouseDown(e) {
	var clickPoint = getCursorPosition(e);
	var clickpt = new Vector(clickPoint[0], clickPoint[1]);
	
	setLevelDefinition();
}

function mouseMove(e) {
	var clickPoint = getCursorPosition(e);
	var clickpt = new Vector(clickPoint[0], clickPoint[1]);
	
	setLevelDefinition();
}

function mouseUp(e) {
	var clickPoint = getCursorPosition(e);
	var clickpt = new Vector(clickPoint[0], clickPoint[1]);
	
	setLevelDefinition();
}

/*helpful methads for events above*/
function getCursorPosition(e) {
	var x, y;
	if (e.pageX || e.pageY)
	{
	  x = e.pageX;
	  y = e.pageY;
	}
	else {
	  x = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
	  y = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
	}
	
	x -= document.getElementById("theCanvas").offsetLeft;
	y -= document.getElementById("theCanvas").offsetTop;
	return [x,y];
}

function byte2Hex(n) {
    var nybHexString = "0123456789ABCDEF";
    return String(nybHexString.substr((n >> 4) & 0x0F,1)) + nybHexString.substr(n & 0x0F,1);
}
 
/*program entry and loading point*/
function didload()
{
	reallyLoad();
}
function reallyLoad()
{
	document.getElementById("canvas").innerHTML = '<canvas id="theCanvas" width="'+width+'" height="'+height+'" >You need to use Firefox, Google Chrome or IE 9 to Play"</canvas>';
	canvas = document.getElementById("theCanvas");
	context = canvas.getContext("2d");
	document.onkeydown = keypressed;
	canvas.addEventListener('mousedown', mouseDown, false);
	canvas.addEventListener('mousemove', mouseMove, false);
	canvas.addEventListener('mouseup',   mouseUp, false);
	
	setLevelDefinition();
	updateGame();
}

/*write the level out to the window*/
function setLevelDefinition()
{
	openLevelCode(true);
}

function openLevelCode(refresh)
{

	if(refresh)
	{
		if(loadLevelWindow)
		{
			if(!loadLevelWindow.closed)
			{
				loadLevelWindow.document.getElementById("leveldef").value = getLevelDefinition();
			}
		}
	}
	else
	{
		if(!loadLevelWindow || loadLevelWindow.closed)
		{
			loadLevelWindow=window.open('','','width=600,height=600,location=no');
			loadLevelWindow.document.write('<head><title>Load Level</title></head><textarea id="leveldef" onchange="levelChanged()" cols="70" rows="30">' + getLevelDefinition() + '</textarea><br>');
			loadLevelWindow.document.write('<center><button onClick="window.opener.levelChanged(document.getElementById(\'leveldef\').value);">Load Level</button></center>');
		}
		else
		{
			loadLevelWindow.document.getElementById("leveldef").value = getLevelDefinition();
			loadLevelWindow.focus();
		}
	}
}

/*get the level definition that will be placed in window*/
function getLevelDefinition()
{
	var def = '[new Player(new Vector(' + player.startPos.add(levelAdj).value() + '), new Vector(' + player.lastLaunchDir.value() + '), ' + player.mass + ', ' + player.radius + '),';
	for(p in planets)
	{
		def += '\n new Planet(new Vector(' + planets[p].position.add(levelAdj).value() + '), ' + planets[p].mass + ', ' + planets[p].radius + ', ' + planets[p].isGoal + ', ' + planets[p].isBonus + ')';
		if(p < planets.length-1)
			def += ',';
	}
	def += '];';
	return def;
}

//this is called for every frame to update the location of the objects in the game
function updateGame()
{

	
	//dont touch below
	redraw();
	
	setTimeout( function(){	 updateGame(); }, 30-(new Date().getTime()-time));
};

function reset()
{
	
}