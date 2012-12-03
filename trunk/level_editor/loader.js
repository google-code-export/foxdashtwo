/*write the level out to the window*/
function setLevelDefinition()
{
	openLevelCode(true);
}

function openLevelCode(refresh)
{

	if (refresh)
	{
		if (loadLevelWindow)
		{
			if (!loadLevelWindow.closed)
			{
				loadLevelWindow.document.getElementById("leveldef").value = getLevelDefinition(loadLevelWindow.document.getElementById("leveldef"));
			}
		}
	} else
	{
		if (!loadLevelWindow || loadLevelWindow.closed)
		{
			loadLevelWindow = window.open('', '', 'width=600,height=600,location=no');
			loadLevelWindow.document.write('<head><title>Load Level</title></head><textarea style="width: 100%; height: 80%;" id="leveldef" onchange="levelChanged()" cols="70" rows="30">' + getLevelDefinition() + '</textarea><br>');
			loadLevelWindow.document.write('<center><button onClick="window.opener.levelChanged(document.getElementById(\'leveldef\').value);">Load Level</button></center>');
		} else
		{
			loadLevelWindow.document.getElementById("leveldef").value = getLevelDefinition(loadLevelWindow.document.getElementById("leveldef"));
			loadLevelWindow.focus();
		}
	}
}

/* get the level definition that will be placed in window */
function getLevelDefinition(thisthis)
{
	var def = '<level>\n';
	def += '  <player>\n    <this_object>player</this_object>\n    <id>000</id>\n    <z_plane>5.0</z_plane>\n';
	def += '    <x_pos>' + $('#player_x').val() + '</x_pos>\n';
	def += '    <y_pos>' + $('#player_y').val() + '</y_pos>\n';
	def += '      <degree>0</degree>\n';
	def += '      <scale>1</scale>\n';
	def += '    <active>true</active>\n  </player>\n';
	def += '  <object_list>\n';

	for (o in objects_array)
	{
		def += '    <levelObject>\n';
		def += '      <id>' + objects_array[o].object_name_id + '</id>\n';
		def += '      <this_object>' + objects_array[o].type + '</this_object>\n';
		def += '      <z_plane>' + objects_array[o].z_plane + '</z_plane>\n';
		def += '      <x_pos>' + objects_array[o].x + '</x_pos>\n';
		def += '      <y_pos>' + objects_array[o].y + '</y_pos>\n';
		def += '      <degree>' + objects_array[o].degree + '</degree>\n';
		def += '      <scale>' + objects_array[o].scale + '</scale>\n';
		def += '      <active>true</active>\n';
		def += '    </levelObject>\n';
	}

	def += '  </object_list>\n  <light_list>\n';

	if(thisthis != undefined)
	{
	if(lights_array.length == 0)
		thisthis.style.backgroundColor = '#FF9B82';
	else
		thisthis.style.backgroundColor = '#BAFFBA';
	}
	
	for (l in lights_array)
	{
		if (lights_array[l].type == 'spot')
		{
			def += '    <levelLight class="com.kobaj.level.LevelTypeLight.LevelSpotLight">\n';
			def += '      <close_width>' + lights_array[l].closewidth + '</close_width>\n';
			def += '      <degree>' + lights_array[l].degree + '</degree>\n';
			def += '      <far_width>' + lights_array[l].farwidth + '</far_width>\n';
		}

		if (lights_array[l].type == 'point')
			def += '    <levelLight class="com.kobaj.level.LevelTypeLight.LevelPointLight">\n';

		if (lights_array[l].type == 'point' || lights_array[l].type == 'spot')
		{
			def += '      <light_effect>' + lights_array[l].light_effect + '</light_effect>\n';
			def += '      <is_bloom>' + lights_array[l].bloom + '</is_bloom>\n';
			def += '      <y_pos>' + lights_array[l].y + '</y_pos>\n';
			def += '      <x_pos>' + lights_array[l].x + '</x_pos>\n';
			def += '      <radius>' + lights_array[l].throw_length + '</radius>\n';
			def += '      <blur_amount>0</blur_amount>\n';
		}

		if (lights_array[l].type == 'ambient')
			def += '    <levelLight class="com.kobaj.level.LevelTypeLight.LevelAmbientLight">\n';

		if (lights_array[l].type == 'custom')
		{
			def += '    <levelLight class="com.kobaj.level.LevelTypeLight.LevelCustomLight">\n';
			def += '      <is_bloom>' + lights_array[l].bloom + '</is_bloom>\n';
			def += '      <y_pos>' + lights_array[l].y + '</y_pos>\n';
			def += '      <x_pos>' + lights_array[l].x + '</x_pos>\n';
			def += '      <light_object>' + lights_array[l].custom + '</light_object>\n';
		}

		def += '      <active>' + lights_array[l].active + '</active>\n';
		def += '      <id>' + lights_array[l].light_name_id + '</id>\n';
		def += '      <color>' + rgbaToInt(lights_array[l].color) + '</color>\n'; /*custom light doesn't matter*/
		def += '    </levelLight>\n';
	}

	def += '  </light_list>\n  <event_list>\n';

	for (o in events_array)
	{
		def += '    <levelEvent>\n';
		def += '      <this_event>' + events_array[o].type + '</this_event>\n';
		def += '      <id>' + events_array[o].event_name_id + '</id>\n';
		def += '      <x_pos>' + events_array[o].x + '</x_pos>\n';
		def += '      <y_pos>' + events_array[o].y + '</y_pos>\n';
		def += '      <width>' + events_array[o].width + '</width>\n';
		def += '      <height>' + events_array[o].height + '</height>\n';

		def += '      <id_strings>\n';
		var strings_array = events_array[o].affected_strings.split(',');
		if (strings_array.length > 0)
			for (n in strings_array)
				def += '        <String>' + $.trim(n) + '</String>\n';
		def += '      </id_strings>\n';

		def += '    </levelEvent>\n';
	}

	def += '  </event_list>\n';
	def += '  <backdrop_color>' + $('#backdrop_color').val() + '</backdrop_color>\n';
	def += '  <right_limit>' + $('#right-limit').val() + '</right_limit>\n';
	def += '  <bottom_limit>' + $('#bottom-limit').val() + '</bottom_limit>\n';
	def += '  <top_limit>' + $('#top-limit').val() + '</top_limit>\n';
	def += '  <left_limit>' + $('#left-limit').val() + '</left_limit>\n';
	def += '</level>';

	return def;
}

function rgbaToInt(rgbString)
{
	var parts = rgbString.match(/^rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*(1|0|0?\.d+))?\)$/);
	// parts now should be ["rgb(0, 70, 255", "0", "70", "255"]

	if (!parts)
		return 0;

	delete (parts[0]);
	for ( var i = 1; i <= 3; ++i)
		parts[i] = parseInt(parts[i]);

	var rgb = parts[4] * 255; // alpha;
	rgb = (rgb << 8) + parts[1]; // red;
	rgb = (rgb << 8) + parts[2]; // green;
	rgb = (rgb << 8) + parts[3]; // blue;

	return rgb;
}

function intToRgba(intstring)
{
	var num = parseInt(intstring);

	num >>>= 0;
	var b = num & 0xFF, g = (num & 0xFF00) >>> 8, r = (num & 0xFF0000) >>> 16, a = ((num & 0xFF000000) >>> 24) / 255;
	return "rgba(" + [ r, g, b, a ].join(",") + ")";

}

function reset_item_list_dropdowns(val)
{
	$(val).html("<option value=''>None</option>");
}

/* build a level that is typed in */
function levelChanged(level)
{
	objects_array.length = 0;
	lights_array.length = 0;
	events_array.length = 0;

	reset_item_list_dropdowns('#object_drop_down');
	reset_item_list_dropdowns('#light_drop_down');
	reset_item_list_dropdowns('#event_drop_down');

	xmlDoc = $.parseXML(level);
	$xml = $(xmlDoc);

	// start with player
	$xml.find("player").each(function()
	{
		$('#player_x').val(parseInt($(this).find("x_pos").text()));
		$('#player_y').val(parseInt($(this).find("y_pos").text()));
	});

	// then objects
	$xml.find("levelObject").each(
			function()
			{
				var temp = new my_objects(parseInt($(this).find("x_pos").text()), parseInt($(this).find("y_pos").text()), parseInt($('option[value="' + $(this).find("this_object").text() + '"]')
						.attr('my_width')), parseInt($('option[value="' + $(this).find("this_object").text() + '"]').attr('my_height')), $(this).find("this_object").text(), objects_array.length); 
				// zero based indexing indexing

				temp.degree = ($(this).find("degree").text() ? parseInt($(this).find("degree").text()) : "0");
				temp.scale = ($(this).find("scale").text() ? parseFloat($(this).find("scale").text()) : "1");
				temp.z_plane = ($(this).find("z_plane").text() ? parseInt($(this).find("z_plane").text()) : "5");
				temp.object_name_id = ($(this).find("id").text() ? $(this).find("id").text() : "000x0")

				objects_array.push(temp);
				$('#object_drop_down').append($("<option></option>").attr("value", temp.id).text(temp.object_name_id + ': ' + temp.id));
			});
	if (objects_array.length > 0)
		setup_objects_interface(objects_array[objects_array.length - 1]);

	// then lights
	var ambient_light_count = 0;
	$xml.find("levelLight").each(
			function()
			{
				if ($(this).attr('class') == 'com.kobaj.level.LevelAmbientLight' || /* old */
				$(this).attr('class') == 'com.kobaj.level.LevelTypeLight.LevelAmbientLight' /* new */)
				{
					var the_type = 'ambient';
					ambient_light_count += 1;
				}

				if ($(this).attr('class') == 'com.kobaj.level.LevelPointLight' || $(this).attr('class') == 'com.kobaj.level.LevelTypeLight.LevelPointLight')
					var the_type = 'point';

				if ($(this).attr('class') == 'com.kobaj.level.LevelSpotLight' || $(this).attr('class') == 'com.kobaj.level.LevelTypeLight.LevelSpotLight')
					var the_type = 'spot';
				
				if ($(this).attr('class') == 'com.kobaj.level.LevelCustomLight' || $(this).attr('class') == 'com.kobaj.level.LevelTypeLight.LevelCustomLight')
					var the_type = 'custom';

				var temp = new my_lights(parseInt(($(this).find("x_pos").text() ? $(this).find("x_pos").text() : 100 * ambient_light_count)), parseInt(($(this).find("y_pos").text() ? $(this).find(
						"y_pos").text() : 0)), the_type, lights_array.length);

				temp.custom = ($(this).find("light_object").text() ? $(this).find("light_object").text() : 'test');
				temp.light_name_id = ($(this).find("id").text() ? $(this).find("id").text() : '000x0');
				temp.closewidth = parseInt(($(this).find("close_width").text() ? $(this).find("close_width").text() : 10));
				temp.farwidth = parseInt(($(this).find("far_width").text() ? $(this).find("far_width").text() : 100));
				temp.degree = parseInt(($(this).find("degree").text() ? $(this).find("degree").text() : 100));

				temp.bloom = ($(this).find("is_bloom").text() ? $(this).find("is_bloom").text() : false);
				temp.throw_length = parseInt(($(this).find("radius").text() ? $(this).find("radius").text() : 100));
				temp.active = $(this).find("active").text();

				temp.light_effect = ($(this).find("light_effect").text() ? $(this).find("light_effect").text() : 'none');
				temp.color = intToRgba($(this).find("color").text());

				lights_array.push(temp);
				$('#light_drop_down').append($("<option></option>").attr("value", temp.id).text(temp.light_name_id + ': ' + temp.id));
			});
	if (lights_array.length > 0)
		setup_lights_interface(lights_array[lights_array.length - 1]);

	// then events
	$xml.find("levelEvent").each(
			function()
			{
				var temp = new my_events(parseInt($(this).find("x_pos").text()), parseInt($(this).find("y_pos").text()), parseInt($(this).find("width").text()),
						parseInt($(this).find("height").text()), $(this).find("this_event").text(), events_array.length); // remember,

				temp.event_name_id = ($(this).find("id").text() ? $(this).find("id").text() : "000x0")

				var strings_array = new Array();
				
				// because I keep changing things...
				var id_strings = $(this).find("id_strings");
				if(id_strings.length == 0)
					id_strings = $(this).find("affected_object_strings");
				
				id_strings.each(function()
				{
					strings_array.push($(this).find("String").text());
				})
				if (strings_array.length > 0)
					temp.affected_strings = strings_array.join(', ');
				else
					temp.affected_strings = "";

				events_array.push(temp);
				$('#event_drop_down').append($("<option></option>").attr("value", temp.id).text(temp.event_name_id + ': ' + temp.id));
			});
	if (events_array.length > 0)
		setup_events_interface(events_array[events_array.length - 1]);

	// finally four position limits
	$('#backdrop_color').val($xml.find("backdrop_color").text());
	$('#right-limit').val($xml.find("right_limit").text());
	$('#top-limit').val($xml.find("top_limit").text());
	$('#bottom-limit').val($xml.find("bottom_limit").text());
	$('#left-limit').val($xml.find("left_limit").text());
}