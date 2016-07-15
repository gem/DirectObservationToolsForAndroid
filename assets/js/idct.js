
var obj;
var testll;

var DEBUG_MODE = false;
var DEBUG_SHOW_DIFF_LAYERS = true;
var DEBUG_SHOW_MOUSE_POS = true;
var DEBUG_DISPLAY_OVERVIEW = false;
var DEBUG_DISPLAY_PANZOOM = false;
var DEBUG_FREE_ROTATE = false;
var DEBUG_SHOW_LAYER_SWITCHER = false;

var DEBUG_USE_BROWSER_CACHING = false; //Causing issues at the moment

var debugFeature;

var mapBounds = new OpenLayers.Bounds( 13.3589926433, 42.3394886236, 13.4150903442, 42.3687820513);
var mapMinZoom = 0;
var mapMaxZoom = 20;

// avoid pink tiles
OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;

var map, vectors, myPositions, locationPointLayer, prevSurveyPoints, vectorEditing, controls;

var currentSurveyPointLon = 0;
var currentSurveyPointLat = 0;

var layerNames;
var jsonStr;

var cacheWrite;
var cacheRead;

var newSurveyPointSelectControl;
var drawPointControl;
var regularPolygonControl;

var prevSurveyPointsSelectControl;
var prevSurveyPointsModifyControl;

var isEditingPoints = false;
var unsavedEditedPoint = false;
var editingPointGemId = 0;

var ll;

//var bingApiKey = "AqTGBsziZHIJYYxgivLBf0hVdrAk9mWO5cQcb8Yux8sW5M8c8opEC2lZqKR1ZZXf";
var bingApiKey = "Au1pvDVsDE5shJGLak2j1NZ8VSsQ9hcME9MO4BTOUiObFDsfAd-u8PNSqkywGHPB";

						 
OpenLayers.Util.onImageLoadError = function () {
    this.src = "img/close.gif";
}

						 
 	
OpenLayers.Control.Click = OpenLayers.Class(OpenLayers.Control, {
		  defaultHandlerOptions: {
			'single': true,
			'double': false,
			'pixelTolerance': 0,
			'stopSingle': false,
			'stopDouble': false
		  },
		  initialize: function(options) {
			this.handlerOptions = OpenLayers.Util.extend(
			  {}, this.defaultHandlerOptions
			);
			OpenLayers.Control.prototype.initialize.apply(
			  this, arguments
			);
			this.handler = new OpenLayers.Handler.Click(
			  this, {
				'click': this.trigger
			  }, this.handlerOptions
			);
		  }
	});
		

function init(){

	map = new OpenLayers.Map({
		div: "map",
		projection: new OpenLayers.Projection("EPSG:900913"),
		displayProjection: new OpenLayers.Projection("EPSG:4326"),
		units: "m",
		//maxResolution: 156543.0339,
		//maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508.34),
										
		controls: [
           new OpenLayers.Control.TouchNavigation({
                dragPanOptions: {
                    enableKinetic: true
                }
            })
		]
	});

	if (DEBUG_SHOW_LAYER_SWITCHER) {
		map.addControl(new OpenLayers.Control.LayerSwitcher({ roundedCornerColor : '#000000' }));
    }
	
            
	 var mapnik =   new OpenLayers.Layer.OSM("OpenStreetMap", 
            [
                "http://otile1.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png",
                 "http://otile2.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png",
                  "http://otile3.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png",
                   "http://otile4.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png"
            ], 
            {
            eventListeners: {
                //tileloaded: updateStatus
            }
    });
   
	
	
	/*
    var gphy = new OpenLayers.Layer.Google(
        "Google Physical",
        {type: google.maps.MapTypeId.TERRAIN}
    );
    var gmap = new OpenLayers.Layer.Google(
        "Google Streets", // the default
        {numZoomLevels: 20}
    );
    var ghyb = new OpenLayers.Layer.Google(
        "Google Hybrid",
        {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20}
    );
    var gsat = new OpenLayers.Layer.Google(
        "Google Satellite",
        {type: google.maps.MapTypeId.SATELLITE, numZoomLevels: 22}
    );	
	*/
	
	var bingRoads = new OpenLayers.Layer.Bing({
		key: bingApiKey,
		type: "Road",
		name: "Bing Roads",
		// custom metadata parameter to request the new map style - only useful
		// before May 1st, 2011
		metadataParams: {mapVersion: "v1"}
	});
	
	var bingAerial = new OpenLayers.Layer.Bing({
		key: bingApiKey,
		type: "Aerial",
		name: "Bing Aerial"
	});
	
	var bingHybrid = new OpenLayers.Layer.Bing({
		key: bingApiKey,
		type: "AerialWithLabels",
		name: "Bing Aerial With Labels"
	});
	

	var cached = new OpenLayers.Layer.XYZ(
		"tiles 2",
		[
			"tiles2/${z}/${x}/${y}.png.tile"
		], {
			attribution: "Tiles © <a href='http://mapbox.com/'>MapBox</a> | " + 
				"Data © <a href='http://www.openstreetmap.org/'>OpenStreetMap</a> " +
				"and contributors, CC-BY-SA",
			sphericalMercator: true,
			transitionEffect: "resize",
			buffer: 1,
			numZoomLevels: 20
		}
	);
	
	//Added as dummy spacer layer
	var sdtiles = new OpenLayers.Layer.XYZ(
		"OpenStreetMap (cached)",
		[
			"file:////mnt/sdcard/idctdo/maptiles2/sdtiles/${z}/${x}/${y}.png.tile"
		], {
			attribution: "Tiles © " + 
				"Data © <a href='http://www.openstreetmap.org/'>OpenStreetMap</a> " +
				"and contributors, CC-BY-SA",
			sphericalMercator: true,
			transitionEffect: "resize",
			buffer: 1,
			numZoomLevels: 20
		}
	);					  

	//Added as dummy spacer layer
	var localTMSTiles = new OpenLayers.Layer.XYZ("Local TMS Tiles",
	"tiles/",
	{ 
		type: 'png', 
		getURL: xyz_getTileURL, 
		alpha: true, 
		isBaseLayer: true,
		numZoomLevels: 20
	});		


	
	myPositions = new OpenLayers.Layer.Vector("My Location", {
		styleMap:  testStyleMap
	});

	locationPointLayer = new OpenLayers.Layer.Vector("Current Survey Point", {
		styleMap:  locationPointLayerStyleMap
	});
	
	prevSurveyPoints = new OpenLayers.Layer.Vector("prevSurveyPoints", {
		styleMap:  surveyPointStyleMap
	});
	
	
//	map.addLayers([mapnik,bingHybrid,bingRoads,bingAerial,myPositions,locationPointLayer,prevSurveyPoints,sdtiles,localTMSTiles]);
	map.addLayers([bingHybrid,bingRoads,bingAerial,mapnik,myPositions,locationPointLayer,prevSurveyPoints,sdtiles,localTMSTiles]);

	/* Causing issues at the moment*/
	//Browser caching
	if (DEBUG_USE_BROWSER_CACHING) {
	    cacheWrite = new OpenLayers.Control.CacheWrite({
	        autoActivate: true,
	        imageFormat: "image/png",
	        eventListeners: {
	            cachefull: function() {
	            	//status.innerHTML = "Cache full.";
	            }
	        }
	    });          
	    map.addControl(cacheWrite);
	    cacheRead = new OpenLayers.Control.CacheRead();
	    map.addControl(cacheRead);    
	}
   
	
	if (DEBUG_DISPLAY_PANZOOM) {
		map.addControl(new OpenLayers.Control.PanZoomBar());
	}

	if (DEBUG_SHOW_MOUSE_POS) {
		map.events.register("move", map, function(){
		        updateScreenDetails();		        
		    });
		    
		map.events.register("zoomend", map, function(){
		        updateScreenDetails();

		    });
		    
		 function updateScreenDetails() {
			var a = document.getElementById("mapCenterCoords");
			var ll = map.getCenter().transform(map.getProjectionObject(),new OpenLayers.Projection("EPSG:4326"));
			a.innerHTML = "Lat: " + Math.round(ll.lat*1000)/1000  + " , lon:" + Math.round(ll.lon*1000)/1000 + " Zoom: " + map.getZoom();
		         	
		 }
	}	 
		 
	 
	map.addControl( new OpenLayers.Control.LoadingPanel());
	 

 	// add animation transition when zooming 
	for (var i=map.layers.length-1; i>=0; --i) {
		map.layers[i].animationEnabled = true;
	}
	
    // Google.v3 uses EPSG:900913 as projection, so we have to
    // transform our coordinates
    map.setCenter(new OpenLayers.LonLat(0.2, 28.9).transform(
        new OpenLayers.Projection("EPSG:4326"),
        map.getProjectionObject()
    ), 5);


	newSurveyPointSelectControl = new OpenLayers.Control.SelectFeature(myPositions, {
		hover: true,
		highlightOnly: false,
		multiple:false,
		enderIntent: "temporary",
		clickout: false,
		toggle:false,
		onUnselect: function() {  
			console.log("UNSELECTED MY POSITIONS");
		},
		onSelect: function(feature) { 
			console.log("SELECTED MY POSITIONS");
		} 		
	});			
	map.addControl(newSurveyPointSelectControl);
	newSurveyPointSelectControl.activate();	
	
	dragControl = new OpenLayers.Control.DragFeature(myPositions, {
		vertexRenderIntent: 'temporary',
		displayClass: 'olControlZoomBox',
		clickout: false,
		onComplete: function(feature,pixel) {
			console.log("drag ended");			
		},
		toggle:false
	});	
	map.addControl(dragControl);
	dragControl.activate();
	
	newSurveyPointModifyControl = new OpenLayers.Control.ModifyFeature(myPositions, {
		vertexRenderIntent: 'temporary',
		displayClass: 'olControlZoomBox',
		clickout: false,
		toggle:false,

		mode: OpenLayers.Control.ModifyFeature.RESHAPE,
		createVertices: false
	});	
	map.addControl(newSurveyPointModifyControl);
	newSurveyPointModifyControl.activate();




	prevSurveyPointsSelectControl = new OpenLayers.Control.SelectFeature(prevSurveyPoints, {
		hover: false,
		highlightOnly: true,
		multiple:false,
		enderIntent: "temporary",
		clickout: false,
		toggle:false,
		onUnselect: function() {  
			console.log("PREV SURVEY POINTS UNSELECTED");
		},
		onSelect: function(feature) { 

			if (!unsavedEditedPoint) {			

				console.log("PREV SURVEY POINTS SELECTED " + feature.attributes.id);
				debugFeature = feature;
				//Get the coordinates of the selected feature
				var pt = feature.geometry;
				var myLocation = new OpenLayers.Geometry.Point(pt.x, pt.y);
				myLocation.transform(map.getProjectionObject(),new OpenLayers.Projection("EPSG:4326"));

				//Draw the old feature as a candidate point
				drawCandidateSurveyPoint(myLocation.x, myLocation.y,feature.attributes.id);			

				//remove the point from the previous points layer
				prevSurveyPoints.removeFeatures(feature);
				unsavedEditedPoint = true;

				//Now reactivate the the survey point controls on the candidate point layer
				newSurveyPointSelectControl.activate();
				newSurveyPointModifyControl.activate();
				dragControl.activate();

				//Deactivate the listeners on the existing points to stop further selections
				//prevSurveyPointsDragControl.deactivate();
				prevSurveyPointsSelectControl.deactivate();	

			} else {
				console.log("Previous unsaved edited point to deal with");
			}

		},
		onclick : function(feature) {  
			console.log("PREV SURVEY POINTS Clicked");
			console.log("PREV SURVEY POINTS Clicked feature " + feature.attributes.id);
		}
	});			
	map.addControl(prevSurveyPointsSelectControl);	
		
	prevSurveyPointsDragControl = new OpenLayers.Control.DragFeature(prevSurveyPoints, {
		vertexRenderIntent: 'temporary',
		displayClass: 'olControlZoomBox',
		clickout: false,
		onComplete: function(feature,pixel) {
			console.log("PREV SURVEY POINTS drag ended");
			debugFeature = feature;
		},
		toggle:false
	});	
	map.addControl(prevSurveyPointsDragControl);

	var click = new OpenLayers.Control.Click( { trigger: function(e) {		

		if ((isEditingPoints && unsavedEditedPoint) || !isEditingPoints) {
			console.log("Click event");		
			console.log("map click MOBILE");
			var lonlat = map.getLonLatFromViewPortPx(e.xy);
			// create some attributes for the feature			
			//myLocation.transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject() );
			console.log("opx" + lonlat);
			
			var myLocation = new OpenLayers.Geometry.Point(lonlat.lon, lonlat.lat );
			myLocation.transform(map.getProjectionObject(),new OpenLayers.Projection("EPSG:4326"));		
			drawCandidateSurveyPoint(myLocation.x, myLocation.y,"0");		
		}
	}});
	 
	map.addControl(click);
    click.activate();
    
    

	
	//This doesn't seem to work on the Galaxy Tab / Android 2.3.4
	map.events.register("click", map , function(e){
		/*
			console.log("clicked event");		
			console.log("map click MOBILE");
			var lonlat = map.getLonLatFromViewPortPx(e.xy);
			//alert('mousedown');
			// create some attributes for the feature
			
			//myLocation.transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject() );
			console.log("opx" + lonlat);
	
			
			var attributes = {name: "my position"};
			
			var myLocation = new OpenLayers.Geometry.Point(lonlat.lon, lonlat.lat);
			var feature = new OpenLayers.Feature.Vector(myLocation,attributes);
				
			myPositions.removeAllFeatures();			
			myPositions.addFeatures([feature]);			
			myPositions.redraw();	
			
			
			//lonlat.transform(map.getProjectionObject(),new OpenLayers.Projection("EPSG:4326"));

			updateSurveyPointPositionFromMap(lonlat.lon,lonlat.lat);
			*/
	});
	
	
	
	
	
	map.events.register('mousedown', function() { alert('mousedown')});
	

	
	//map.setCenter(new OpenLayers.LonLat(point.x, point.y), 5);

	
	var ll = new OpenLayers.LonLat(-1, 52);
    map.setCenter(ll.transform(
        new OpenLayers.Projection("EPSG:4326"),
        map.getProjectionObject()
    ), 5);
    
	
		
	var defaultLL = new OpenLayers.LonLat(-1.182725429534912,52.95185706277731);
	defaultLL.transform(	new OpenLayers.Projection("EPSG:4326"),	map.getProjectionObject());
	map.setCenter(defaultLL, 6);
    	
	// create some attributes for the feature
	//var attributes = {name: "my name", bar: "foo"};

	var myLocation = new OpenLayers.Geometry.Point(ll.lon, ll.lat);
	//var feature = new OpenLayers.Feature.Vector(myLocation, attributes);
	var feature = new OpenLayers.Feature.Vector(myLocation);
	
	
	//Load in dummy survey points around BGS office
	var points = new Array(
				  new OpenLayers.Geometry.Point(-1.07581,52.87976),
				  new OpenLayers.Geometry.Point(-1.080536,52.878658),
				  new OpenLayers.Geometry.Point( -1.080434, 52.878576),
				  new OpenLayers.Geometry.Point(  -1.080913,  52.879008)
				  );
				  
    for (var i = 0; i < points.length; i++) {	

		var myLocation = points[i];
		myLocation.transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject() );
		// create some attributes for the feature
		var attributes = {id: "gem ID string", name: "my name", bar: "foo"};
		var feature = new OpenLayers.Feature.Vector(myLocation, attributes);
		
		prevSurveyPoints.addFeatures([feature]);
	}
	prevSurveyPoints.redraw();

} //end of init()



//Add a single point to the map which can be edited
function drawCandidateSurveyPoint(lon, lat,idString) {
		console.log("Drawing Candidate Point");

		var attributes = {id: idString, name: "my position"};		
		var myLocation = new OpenLayers.Geometry.Point(lon, lat);
		myLocation.transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject() );
		var feature = new OpenLayers.Feature.Vector(myLocation,attributes);           
	
		myPositions.removeAllFeatures();			
		myPositions.addFeatures([feature]);			
		myPositions.redraw();
		        
		updateSurveyPointPositionFromMap(false);
}


//Helper method to get features from KML string
function GetFeaturesFromKMLString(strKML) {
	//renderers: ["SVG", "Canvas", "VML"]
	var format = new OpenLayers.Format.KML({
		'internalProjection': map.baseLayer.projection,
		'externalProjection': new OpenLayers.Projection("EPSG:4326"),
		renderers: ["SVG", "Canvas", "VML"]
	});
	return format.read(strKML);
};


//Helper method to OSM tile URLS
function osm_getTileURL(bounds) {

	//console.log("zoom:" + map.getZoom());
	var res = this.map.getResolution();
	//console.log("res: "+ res);
	var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));	
	var y = Math.round((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
	var z = map.getZoom();
	var limit = Math.pow(2, z);
	console.log("xyz: "+ this.url + z + "/" + x + "/" + y + "." + this.type);
	if (y < 0 || y >= limit) {
		return "http://www.maptiler.org/img/none.png";
	} else {
		x = ((x % limit) + limit) % limit;
		return this.url + z + "/" + x + "/" + y + "." + this.type;
	}
}


function overlay_getTileURL(bounds) {
	var res = this.map.getResolution();
	var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
	var y = Math.round((bounds.bottom - this.tileOrigin.lat) / (res * this.tileSize.h));
	var z = this.map.getZoom();
	if (this.map.baseLayer.name == 'Virtual Earth Roads' || this.map.baseLayer.name == 'Virtual Earth Aerial' || this.map.baseLayer.name == 'Virtual Earth Hybrid') {
	   z = z + 1;
	}
	if (mapBounds.intersectsBounds( bounds ) && z >= mapMinZoom && z <= mapMaxZoom ) {
	   //console.log( this.url + z + "/" + x + "/" + y + "." + this.type);
	   return this.url + z + "/" + x + "/" + y + "." + this.type;
	} else {
	   return "http://www.maptiler.org/img/none.png";
	}
}	


//This function allows a TMS tileset to be used with OpenLayers.Layer.XYZ
function xyz_getTileURL(bounds) {
	var res = this.map.getResolution();
	var x = Math.round((bounds.left - this.maxExtent.left) / (res * this.tileSize.w));
	var y = Math.round((this.maxExtent.top - bounds.top) / (res * this.tileSize.h));
	var z = this.map.getZoom();
	
	if (this.map.baseLayer.name == 'Bing Roads' || this.map.baseLayer.name == 'Bing Aerial' || this.map.baseLayer.name == 'Bing Aerial With Labels') {
	   z = z + 1;
	}
	
	var limit = Math.pow(2, z);
	console.log("xyz: "+ this.url + z + "/" + x + "/" + y + "." + this.type);
	  x = ((x % limit) + limit) % limit;
	  y = Math.pow(2,z) - y - 1;
	  return this.url + z + "/" + x + "/" + y + "." + this.type;
}
			