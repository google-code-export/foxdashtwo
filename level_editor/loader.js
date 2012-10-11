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
	var def = '<level>\n';
	def +=		'<player><this_object>player</this_object><draw_from>bottom_left</draw_from><id>0</id><z_plane>5.0</z_plane>\n';
    def += 		'<x_pos>' + $('#player_x').val() + '</x_pos>\n';
    def +=		'<y_pos>' + $('#player_y').val() + '</y_pos>\n';
    def += 		'<active>true</active></player>\n';
    def +=			'<object_list>\n';
    
    for(o in objects_array)
    {
    	def += '<levelObject>\n';
    	def += '<this_object>' + objects_array[o].type + '</this_object>\n';
    	def += '<draw_from>bottom_left</draw_from>\n';
    	def += '<id>' + objects_array[o].id + '</id>\n';
    	def += '<z_plane>' + objects_array[o].z_plane + '</z_plane>\n';
    	def += '<x_pos>' + objects_array[o].x + '</x_pos>\n';
    	def += '<y_pos>' + objects_array[o].y + '</y_pos>\n';
    	def += '<active>true</active>\n';
    	def += '</levelObject>\n';
    }
    
    def += '</object_list><light_list>\n';
    
    for(l in lights_array)
    {
    	if(lights_array[l].type == 'spot')
    	{
    		def += '<levelLight class="com.kobaj.level.LevelSpotLight">\n';
    		def += '<close_width>' + lights_array[l].closewidth + '</close_width>\n';
    		def += '<degree>' + lights_array[l].degree + '</degree>\n';
    		def += '<far_width>' + lights_array[l].farwidth + '</far_width>\n';
    	}
       
    	if(lights_array[l].type == 'point')
    		def += '<levelLight class="com.kobaj.level.LevelPointLight">\n';
    	
    	if(lights_array[l].type == 'point' || lights_array[l].type == 'spot')
    	{
    		def += '<is_bloom>' + lights_array[l].bloom + '</is_bloom>\n';
       		def += '<y_pos>' + lights_array[l].y + '</y_pos>\n';
       		def += '<radius>' + lights_array[l].throw_length + '</radius>\n';
       		def += '<x_pos>' + lights_array[l].x + '</x_pos>\n';
       		def += '<blur_amount>0</blur_amount>\n';
    	}
    	
       if(lights_array[l].type == 'ambient')
    	   def += '<levelLight class="com.kobaj.level.LevelAmbientLight">\n';
       
       def += '<active>' + lights_array[l].active + '</active>\n';
       def += '<id>' + lights_array[l].id + '</id>\n';
       def += '<color>' + rgbaToInt(lights_array[l].color) + '</color>\n';
       def += '</levelLight>\n';
    }
    
    def += '</light_list>\n';
    def += '<right_limit>' + $('#right-limit').val() + '</right_limit>\n';
    def += '<bottom_limit>' + $('#bottom-limit').val() + '</bottom_limit>\n';
    def += '<top_limit>' + $('#top-limit').val() + '</top_limit>\n';
    def += '<left_limit>' + $('#left-limit').val() + '</left_limit>\n';
    def += '</level>';
    
    return def;
}

function rgbaToInt(rgbString)
{
	var parts = rgbString.match(/^rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*(1|0|0?\.d+))?\)$/);
	// parts now should be ["rgb(0, 70, 255", "0", "70", "255"]

	if(!parts)
		return 0;
		
	delete (parts[0]);
	for (var i = 1; i <= 3; ++i) 
	    parts[i] = parseInt(parts[i]);
	
	var rgb = parts[4] * 255; //alpha;
	rgb = (rgb << 8) + parts[1]; //red;
	rgb = (rgb << 8) + parts[2]; //green;
	rgb = (rgb << 8) + parts[3]; //blue;

	return rgb;
}

function intToRgba(intstring)
{
	var num = parseInt(intstring);
	
	num >>>= 0;
    var b = num & 0xFF,
        g = (num & 0xFF00) >>> 8,
        r = (num & 0xFF0000) >>> 16,
        a = ( (num & 0xFF000000) >>> 24 ) / 255 ;
    return "rgba(" + [r, g, b, a].join(",") + ")";

}

/*build a level that is typed in*/
function levelChanged(level)
{ 
	objects_array.length = 0;
	lights_array.length = 0;
	
	 xmlDoc = $.parseXML( level );
	 $xml = $( xmlDoc );
	 
	 //start with player
	 $xml.find( "player" ).each(function()
	 {
		 $('#player_x').val(parseInt($(this).find("x_pos").text()));
		 $('#player_y').val(parseInt($(this).find("y_pos").text())); 
	 });

	 //then objects
	 $xml.find( "levelObject" ).each(function()
     {
		 var temp = new my_objects(
				 parseInt($(this).find("x_pos").text()),
				 parseInt($(this).find("y_pos").text()),
				 parseInt($('option[value="' + $(this).find("this_object").text() + '"]').attr('my_width')),
				 parseInt($('option[value="' + $(this).find("this_object").text() + '"]').attr('my_height')),
				 $(this).find("this_object").text(),
				 objects_array.length); // remember, this is zero based indexing
		 
		 temp.z_plane =  parseInt($(this).find("z_plane").text());
		 
		 objects_array.push(temp);
	 });
	 if(objects_array.length > 0)
		 setup_objects_interface(objects_array[objects_array.length - 1]);
	 
	 //then lights
	 $xml.find("levelLight").each(function(){
		 
		 if($(this).attr('class') == 'com.kobaj.level.LevelAmbientLight')
			 var the_type = 'ambient';

		 if($(this).attr('class') == 'com.kobaj.level.LevelPointLight')
			 var the_type = 'point';

		 if($(this).attr('class') == 'com.kobaj.level.LevelSpotLight')
			 var the_type = 'spot';
		 
		 var temp = new my_lights(
				 parseInt(($(this).find("x_pos").text() ? $(this).find("x_pos").text() : 0)),
				 parseInt(($(this).find("y_pos").text() ? $(this).find("y_pos").text() : 0)),
				 the_type,
				lights_array.length);
		 
		 temp.closewidth = parseInt(($(this).find("close_width").text() ? $(this).find("close_width").text() : 10));
		 temp.farwidth = parseInt(($(this).find("far_width").text() ? $(this).find("far_width").text() : 100));
		 temp.degree = parseInt(($(this).find("degree").text() ? $(this).find("degree").text() : 100));
		 
		 temp.bloom = ($(this).find("is_bloom").text() ? $(this).find("is_bloom").text() : false);
		 temp.throw_length = parseInt(($(this).find("radius").text() ? $(this).find("radius").text() : 100));
		 temp.active = $(this).find("active").text();
		 
		 temp.color = intToRgba($(this).find("color").text());
		 
		 lights_array.push(temp);
	 });
	 if(lights_array.length > 0)
		 setup_lights_interface(lights_array[lights_array.length - 1]);
	 
	 //finally four position limits
	 $('#right-limit').val($xml.find("right_limit").text());
	 $('#top-limit').val($xml.find("top_limit").text());
	 $('#bottom-limit').val($xml.find("bottom_limit").text());
	 $('#left-limit').val($xml.find("left_limit").text());

}