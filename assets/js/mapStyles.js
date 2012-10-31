var defaultStyle = new OpenLayers.Style({
	fillColor: "#E50000",
	strokeColor: "#000000",
	strokeWidth: 1,
	externalGraphic: "img/cone_icon_green.png",
	graphicOpacity: 0.95,
	fillOpacity: 0.75,
	pointRadius: 105,
	fontColor: "#000000",
	fontFamily: "sans-serif",
	fontWeight: "bold"
},{
  context: {
	label: function(f) {
	  return f.attributes.label != undefined ? f.attributes.label : "";
	},
	externalGraphic: function(f) {
		if (!editingVertices) {
			return "arrow_down_animated.gif"
		} else {
			//return "44.gif"
			return "arrow_down_animated.gif"
		}
	}
  }
}
);


var selectStyle = new OpenLayers.Style({
	fillColor: "#E50000",
	strokeColor: "#000000",
	strokeWidth: 1,
	externalGraphic: "img/cone_icon_green.png",
	graphicOpacity: 0.95,
	fillOpacity: 0.75,
	pointRadius: 105,
	fontColor: "#000000",
	fontFamily: "sans-serif",
	fontWeight: "bold"
},{
  context: {
	label: function(f) {
	  return f.attributes.label != undefined ? f.attributes.label : "";
	}
  }
}
);

var temporaryStyle = new OpenLayers.Style({
	fillColor: "#ffff66",
	strokeColor: "#ff9933",
	strokeWidth: 7,
	pointRadius: 105,
	fontColor: "#af3333",
	fontFamily: "sans-serif",
	fontWeight: "bold"
},{
  context: {
	label: function(f) {
	  return f.attributes.label != undefined ? f.attributes.label : "";
	  
	}
  }
}
);


var testStyleMap = new OpenLayers.StyleMap({'default': defaultStyle,
                         'select': selectStyle,'temporary':temporaryStyle});


						 
						 
var locationPointLayerDefaultStyle = new OpenLayers.Style({
	fillColor: "#2222ff",
	strokeColor: "#0000ff",
	strokeWidth: 2,
	externalGraphic: "blue-dot.png",
	graphicOpacity: 0.85,
	fillOpacity: 0.25,
	
	pointRadius: 10,
	fontColor: "#000000",
	fontFamily: "sans-serif",
	fontWeight: "bold"
},{
  context: {
	label: function(f) {
	  return f.attributes.label != undefined ? f.attributes.label : "";
	},
	externalGraphic: function(f) {
		if (!editingVertices) {
			return "arrow_down_animated.gif"
		} else {
			//return "44.gif"
			return "arrow_down_animated.gif"
			
		}
	}
  }
}
);

var locationPointLayerStyleMap = new OpenLayers.StyleMap({'default': locationPointLayerDefaultStyle,
                         'select': selectStyle,'temporary':temporaryStyle});

						 
						 
						 
var surveyPointDefaultStyle = new OpenLayers.Style({
	fillColor: "#FFFF00",
	strokeColor: "#000000",
	strokeWidth: 1,
	externalGraphic: "img/cone_icon_red.png",
	graphicOpacity: 0.85,
	fillOpacity: 0.85,
	pointRadius: 75,
	fontColor: "#000000",
	fontFamily: "sans-serif",
	fontWeight: "bold"
},{
  context: {
	label: function(f) {
	  return f.attributes.label != undefined ? f.attributes.label : "";
	},
	externalGraphic: function(f) {
		if (!editingVertices) {
			return "arrow_down_animated.gif"
		} else {
			//return "44.gif"
			return "arrow_down_animated.gif"
		}
	}
  }
}
);

var surveyPointStyleMap = new OpenLayers.StyleMap({'default': surveyPointDefaultStyle,
                         'select': selectStyle,'temporary':temporaryStyle});

						 
						 
						 