//Colors
var redFill = "rgba(255,0,0,1)"
var greenFill = "rgba(34,139,34,1)"
var orangeFill = "rgba(255,140,0,1)"
var blueFill = "rgba(0,0,255,1)"
var blackFill = "rgba(0,0,0,1)"
var whiteFill = "rgba(255,255,255,1)"
var lightblueFill = "rgba(100,100,255,1)"
var greyFill = "rgba(255,255,255,.5)"
var lightgreyFill = "rgba(200,200,200,1)"
var darkgreyFill = "rgba(169,169,169,1)"
var brownFill = "rgba(139,69,19,1)"
var purpleFill = "rgba(170,0,255,1)"
	
var default_font = '15px sans-serif';

function getTextWidth(text, font)
{
	context.font = typeof font == 'undefined' ? default_font : font;
	return context.measureText(text);
}

//draws text to screen, font is optional
function drawText(x, y, text, color, font)
{
	context.fillStyle = color;
	context.font = typeof font == 'undefined' ? default_font : font;
	context.textBaseline = 'bottom';
	context.fillText(text, x, y);
};

function strokeText(x, y, text, color, font)
{
	context.strokeStyle = color;
	context.font = typeof font == 'undefined' ? default_font : font;
	context.lineWidth = 9;
	context.textBaseline = 'bottom';

	context.fillStyle = whiteFill;
	context.lineWidth = 3;
	context.fillText(text, x, y);
	
	context.strokeText(text, x, y);
};

function saveToNewCanvas()
{
	var backCanvas = document.createElement('canvas');
	backCanvas.width = canvas.width;
	backCanvas.height = canvas.height;
	var backCtx = backCanvas.getContext('2d');
	
	
	backCtx.drawImage(canvas,0,0);
	
	return backCanvas;
}

function drawCanvasToCurrentCanvas(storedCanvas, alpha)
{
	var bkCanvas = storedCanvas;
	var bkContext = bkCanvas.getContext('2d');
	context.globalAlpha = alpha;	
	context.drawImage(bkCanvas,0,0);
}

function redraw()
{		
    //drawRect(breadCrumbs[b].x, breadCrumbs[b].y, 2, 2, breadCrumbsColor(breadCrumbsColorNDX));
	//context.fillStyle = blackFill;
	//context.fillRect(0,0,width,height);
	//context.fill();
	//context.drawImage(breadCrumbsImg,0,0);
	
	for (img in breadCrumbsCanvases)
	{
		if(img < breadCrumbsCanvases.length-4)
		{
			if(breadCrumbsCanvases[img])
			{		
				drawCanvasToCurrentCanvas(breadCrumbsCanvases[img], .35);
				
				breadCrumbsImg = new Image();
				breadCrumbsImg.src = canvas.toDataURL("image/png");
				breadCrumbsCanvases.splice(img,1);
			}
		}
		else
		{
			var alpha = 1-(breadCrumbsCanvases.length-(1+parseInt(img)))*.12;
			drawCanvasToCurrentCanvas(breadCrumbsCanvases[img], alpha);
		}
	}
	
	context.globalAlpha = 1;	
    //context.save();
	//context.scale(.5, .5);
		
	for (b in breadCrumbs)
	{
		//drawCircle(breadCrumbs[b].x, breadCrumbs[b].y, 2, breadCrumbsColor(breadCrumbsColorNDX));
    drawRect(breadCrumbs[b].x, breadCrumbs[b].y, 2, 2, breadCrumbsColor(breadCrumbsColorNDX));
		
		if(!savedSinceLastStop)
			drawArrow(player.startPos.x, player.startPos.y, player.startPos.x + player.lastLaunchDir.x, player.startPos.y + player.lastLaunchDir.y, breadCrumbsColor(breadCrumbsColorNDX));	
	}
	
	for (p in planets)
		planets[p].draw();
	player.draw();
	
	if(dragging || (!flying && !player.crashed))
	{
		var mousept = player.position.add(player.direction.scale(1.3));
		drawText(mousept.x -10, mousept.y + 10, player.direction.x + "," + player.direction.y, whiteFill, 'italic bold 15px sans-serif');
	}
	
	if(player.position.x < screenLeft-player.radius && player.position.y > screenTop+player.radius && player.position.y < screenBottom-player.radius)
		drawArrow(screenLeft+20, player.position.y, screenLeft+2, player.position.y, whiteFill);
	if(player.position.x > screenRight-player.radius && player.position.y > screenTop+player.radius && player.position.y < screenBottom-player.radius)
		drawArrow(screenRight-20, player.position.y, screenRight-2, player.position.y, whiteFill);
	if(player.position.y < screenTop-player.radius && player.position.x > screenLeft+player.radius && player.position.x < screenRight-player.radius)
		drawArrow(player.position.x, screenTop+20, player.position.x, screenTop+2, whiteFill);
	if(player.position.y > screenBottom+player.radius && player.position.x > screenLeft+player.radius && player.position.x < screenRight-player.radius)
		drawArrow(player.position.x, screenBottom-20, player.position.x, screenBottom-2, whiteFill);
		
	if(player.position.x < screenLeft+player.radius && player.position.y < screenTop+player.radius)
	{		
		var arrowStart = new Vector(screenLeft+20, screenTop+20);
		var dist = player.position.dist(arrowStart);
		var scale = 20/dist;
		var vec = player.position.vecTo(arrowStart);
		var svec = vec.scale(scale);
		var pvec = svec.add(arrowStart);
		drawArrow(arrowStart.x,arrowStart.y,pvec.x, pvec.y, whiteFill);
	}
	if(player.position.x > screenRight-player.radius && player.position.y < screenTop+player.radius)
	{
		var arrowStart = new Vector(screenRight-20, screenTop+20);
		var dist = player.position.dist(arrowStart);
		var scale = 20/dist;
		var vec = player.position.vecTo(arrowStart);
		var svec = vec.scale(scale);
		var pvec = svec.add(arrowStart);
		drawArrow(arrowStart.x,arrowStart.y,pvec.x, pvec.y, whiteFill);
	}	
	if(player.position.x < screenLeft+player.radius && player.position.y > screenBottom-player.radius)
	{		
		var arrowStart = new Vector(screenLeft+20, screenBottom-20);
		var dist = player.position.dist(arrowStart);
		var scale = 20/dist;
		var vec = player.position.vecTo(arrowStart);
		var svec = vec.scale(scale);
		var pvec = svec.add(arrowStart);
		drawArrow(arrowStart.x,arrowStart.y,pvec.x, pvec.y, whiteFill);
	}
	if(player.position.x > screenRight-player.radius && player.position.y > screenBottom-player.radius)
	{
		var arrowStart = new Vector(screenRight-20, screenBottom-20);
		var dist = player.position.dist(arrowStart);
		var scale = 20/dist;
		var vec = player.position.vecTo(arrowStart);
		var svec = vec.scale(scale);
		var pvec = svec.add(arrowStart);
		drawArrow(arrowStart.x,arrowStart.y,pvec.x, pvec.y, whiteFill);
	}
	
	//context.restore();
	
	
	
	drawText(180, 30, ((mode == 0) ? "Edit" : "Test") + ' mode  -  Spacebar to swap modes', whiteFill, 'bold 25px sans-serif');
	//drawText(10, 330, "Position : " + touchPt.x.toFixed(0) + "," + touchPt.y.toFixed(0), whiteFill, 'italic bold 15px sans-serif');
	if(mode == edit)
	{
		drawText(5, height-105, "p to drop new planet", whiteFill, 'italic bold 25px sans-serif');
		drawText(5, height-80, "d while dragging planet/bonus to delete", whiteFill, 'italic bold 25px sans-serif');
		drawText(5, height-55, "up/down edit size, left/right edit mass while dragging planet", whiteFill, 'italic bold 25px sans-serif');
		drawText(5, height-30, "b to drop bonus", whiteFill, 'italic bold 25px sans-serif');
		drawText(5, height-5, "c to clear breadcrumbs", whiteFill, 'italic bold 25px sans-serif');
	}
	else
	{
		//drawText(5, height-55, "Color NDX " + breadCrumbsColorNDX, whiteFill, 'italic bold 25px sans-serif');
		drawText(5, height-30, "Click and drag to set direction, release to launch", whiteFill, 'italic bold 25px sans-serif');
		drawText(5, height-5, "Click anytime after launch to reset ", whiteFill, 'italic bold 25px sans-serif');
	}
	
	//drawText(10, 575, "Best Distance : " + player.maxDistTraveled.toFixed(1), whiteFill, 'italic bold 15px sans-serif');
	//drawText(10, 590, "Best Direction : " + player.bestLaunch.x.toFixed(0) + "," + player.bestLaunch.y.toFixed(0), whiteFill, 'italic bold 15px sans-serif');
};

function drawArrow(fromx, fromy, tox, toy, fill){
    var headlen = 10;   // length of head in pixels
    var angle = Math.atan2(toy-fromy,tox-fromx);
	context.beginPath()
	context.lineWidth = 3;
	context.strokeStyle = fill;
    context.moveTo(fromx, fromy);
    context.lineTo(tox, toy);
    context.lineTo(tox-headlen*Math.cos(angle-Math.PI/6),toy-headlen*Math.sin(angle-Math.PI/6));
    context.moveTo(tox, toy);
    context.lineTo(tox-headlen*Math.cos(angle+Math.PI/6),toy-headlen*Math.sin(angle+Math.PI/6));
	context.stroke();
}

//generic draw rectangle function
function drawRect(x1,y1,x2,y2,color)
{
	context.beginPath();
	context.fillStyle = color;
	context.fillRect(x1,y1,x2,y2);
	context.closePath();
	context.fill();
};

//only used for boss health outline
function drawBox(x1,y1,x2,y2,color)
{
	context.beginPath();
	context.fillStyle = color;
	context.strokeRect(x1,y1,x2,y2);
	context.closePath();
	context.fill();
};

//only used for drawing the weapon type
function drawCircle(x,y,radius,color)
{
	context.beginPath();
	context.fillStyle = color;
	context.arc(x, y, radius, 0, Math.PI*2,false);
	context.closePath();	
	context.fill();
};