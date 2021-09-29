/**
 * <h1> main </h1>
 *
 * Connection to openstreemap services, retrieval and display of reports with array creation
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

 var ip_address = "http://localhost:4000/allReports"; //Local version
const tileURL = "https://b.tile.openstreetmap.org/{z}/{x}/{y}.png"; //default
const tileURL2 = "https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png";
const tileURL3 = "http://a.tile.stamen.com/toner/{z}/{x}/{y}.png"; //other: https://wiki.openstreetmap.org/wiki/Tiles

//Arrays populated at getJSON with Report data objects based on their category
var criminalCategory = [];
var suspiciousCategory = [];
var infrastructureCategory = [];
var perceivedDangerCategory = [];

//adds clustering functionality
var markers = L.markerClusterGroup();


//Retrieve all information from the reports filter them based on their category, place markers, and circles
$.getJSON(ip_address, function(result) {
  $.each(result, function(index, data) {
    switch (data.categoryID) {
      case 0:
        criminalCategory.push(reportMarker(data));
        markers.addLayer(reportMarker(data));
        if (isConfirmed(data)) {
          criminalCategory.push(reportCircle(data));
        }
        break;
      case 1:
        suspiciousCategory.push(reportMarker(data));
        markers.addLayer(reportMarker(data));
        if (isConfirmed(data)) {
          suspiciousCategory.push(reportCircle(data));
        }
        break;
      case 3:
        infrastructureCategory.push(reportMarker(data));
        markers.addLayer(reportMarker(data));
        if (isConfirmed(data)) {
          infrastructureCategory.push(reportCircle(data));
        }
        break;
      case 2:
        perceivedDangerCategory.push(reportMarker(data));
        markers.addLayer(reportMarker(data));
        if (isConfirmed(data)) {
          perceivedDangerCategory.push(reportCircle(data));
        }
        break;
    }
  });
  var overlayMaps = {
    "Criminal Activity": L.layerGroup(criminalCategory),
    "Suspicious Activity": L.layerGroup(suspiciousCategory),
    "Infrastructure": L.layerGroup(infrastructureCategory),
    "Perceived Danger": L.layerGroup(perceivedDangerCategory)
  };
  L.control.layers(baseMaps, overlayMaps).addTo(mymap);
  console.log("Calling all reports api");
});

var detailed = L.tileLayer(tileURL),
    streets = L.tileLayer(tileURL2),
    grayscale = L.tileLayer(tileURL3);
var baseMaps = {
  "Detailed": detailed,
  "Streets": streets,
  "<span style='color: gray'>Grayscale</span>": grayscale
};

var mymap = L.map('map-template', {
  center: [20.71185, -103.367341],
  zoom: 12.5,
  layers: [detailed] //, criminalLayer, suspiciousLayer, infrastructureLayer, perceivedDangerLayer]
});

mymap.addLayer(markers);
mymap.locate({
  enableHighAccuracy: true
});
mymap.on('locationfound', e => {
  const marker = L.marker([e.latitude, e.longitude]); //marker of my location
  marker.bindPopup('Current location');
});

var popup = L.popup();

//POSITION OF THE USER
var currentPosition = L.latLng([20.672587, -103.3635]); //need to change to current location

// Icons
const criminal = "gavel";
const suspicious = "user-secret";
const perceivedDanger = "exclamation-triangle";
const infrastructure = "gears";
const mobility = "bus";
const unconfirmed = "pencil-square-o";

//Severity Colors
const severity1 = '#9EB661';
const severity2 = '#FAF041';
const severity3 = '#E08439';
const severity4 = '#C8342D';

// Function to display where user clicked on the map
function onMapClick(e) {
  popup
      .setLatLng(e.latlng)
      .setContent("You clicked the map at " + e.latlng.toString())
      .openOn(mymap);
}

//Returns a marker object depending on report data
function reportMarker(data) {
  marker = L.marker(getLocation(data)).bindPopup(popUpInfo(data));
  var iconName = getMarkerObject(data).icon;
  if (!isConfirmed(data)) {
    iconName = unconfirmed;
  }
  var icon = L.AwesomeMarkers.icon({
    extraClasses: "fa-rotate-0",
    icon: iconName,
    iconColor: "white",
    markerColor: "gray", //Icon color default to gray
    prefix: "fa"
  });
  marker.setIcon(icon);
  return marker;
}

//Information from the reports that pop up after tapping on the marker
function popUpInfo(data) {
  var theTime = new Date(getTimestamp(data)); //time registered in the report
  theTime = theTime.toLocaleString();

  //Used to avoid display of decimals and commas
  var kmDistance = currentPosition.distanceTo(getLocation(data)) / 1000.0; //distanceTo return meters, divide by 1000 for km
  kmDistance = kmDistance.toLocaleString(undefined, {
    maximumFractionDigits: 2
  });

  return `<div style="text-align: center;"> <strong>${getMarkerObject(data).name}</strong></div>
    <strong>Severity</strong>: ${getSeverityWeight(data)}<br>
    <strong>Description</strong>: ${data["description"]}
    <br>
    <div style="text-align:center; padding: 5px 5px 5px 5px;">
       <i class="fa fa-thumbs-up"></i> ${getConfirmedBy(data)} | ${getDeniedBy(data)} <i class="fa fa-thumbs-down"></i>
    </div>    
    <div style="color:gray;font-size:10px;text-align:right;">
        ~<strong>${kmDistance}</strong> km from you <br>
        ${theTime}
    </div>`;
}

function reportCircle(data) {
  var opactiy = 0.5;
  var radius = 60;
  console.log(data);
  console.log(getSeverityColor(data));
  return L.circle(getLocation(data), {
    color: null,
    fillColor: getSeverityColor(data),
    fillOpacity: opactiy,
    radius: radius
  }).bindPopup(`${getMarkerObject(data).name}`);
}

// Util function to get category of report
// e.g. criminal, infrastructure or mobility
function getCategory(data) {
  return data["categoryID"];
}
// Util function to get coordinates of report
function getLocation(data) {
  return [data["latitude"], data["longitude"]];
}
// Util function to get severity weight of the report
function getSeverityWeight(data) {
  return Number(data["severityWeight"]);
}
// Util function to get the timestamp of when the report was made
function getTimestamp(data) {
  return Number(data["timestamp"]);
}
// Util function to get confirmations and denies of a report
function getConfirmedBy(data) {
  return Number(data["confirmedBy"]);
}
// Util function to get confirmations and denies of a report
function getDeniedBy(data) {
  return Number(data["deniedBy"]);
}
// Util function that computes whether a report has more confirmations than denies
function isConfirmed(data) {
  return Number(data["confirmedBy"]) >= Number(data["deniedBy"]);
}

// Util function that returns an object containing color and icon of marker
function getMarkerObject(data) {
  var markerObject;
  if (data.categoryID === 0) { //fix type comparison type coercion with === instead of ==
    markerObject = {
      color: "red",
      icon: criminal,
      name: "Criminal Activity"
    };
  } else if (data.categoryID === 1) {
    markerObject = {
      color: "gray",
      icon: suspicious,
      name: "Suspicious Activity"
    };
  } else if (data.categoryID === 2) {
    markerObject = {
      color: "green",
      icon: perceivedDanger,
      name: "Perceived Danger"
    };
  } else if (data.categoryID === 3) {
    markerObject = {
      color: "orange",
      icon: infrastructure,
      name: "Infrastructure"
    };
  } else if (data.categoryID === 4) {
    markerObject = {
      color: "blue",
      icon: mobility,
      name: "Mobile"
    };
  }
  return markerObject;
}

// Util function that returns the corresponding color of the current severity
function getSeverityColor(data) {
  var severityColor;
  if (getSeverityWeight(data) === 1) {
    severityColor = severity1; //light yellow
  } else if (getSeverityWeight(data) === 2) {
    severityColor = severity2; //yellow
  } else if (getSeverityWeight(data) === 3) {
    severityColor = severity3; //orange
  } else if (getSeverityWeight(data) === 4) {
    severityColor = severity4; //burgundy
  }
  else {
    severityColor = severity4;
  }
  return severityColor;
}

// Colors taken from the main constant based on severity level
function getLegendColor(d) {
  return d <= 30 && d > 0 ? severity4 : //light yellow
      d <= 70 && d > 30 ? severity3 : //yellow
          d <= 100 && d > 70 ? severity2 : //orange
              severity1; //burgundy
}

// Create map legend object
var legend = L.control({
  position: 'topleft'
});
// Define what will be displayed in the legend
legend.onAdd = function() {
  var div = L.DomUtil.create('div', 'info legend'),
      grades = [100, 70, 30, 1],
      labels = ["high", "", "", "low"];
  // Loop through our density intervals and generate a label with a colored square for each interval
  div.innerHTML += '<div><strong>Safety</strong></div>'; //Legend Title
  for (var i = 0; i < grades.length; i++) {
    div.innerHTML +=
        '<i style="background:' + getLegendColor(grades[i] + 1) + '"></i>' +
        labels[i] + '<br>';
  }
  return div;
};

// Add legend object to the map
legend.addTo(mymap);

// Button to turn on/off further description of the legend
let questionButton = null;
var stateChangingButton = L.easyButton({
  position: 'bottomright',
  states: [{
    stateName: 'legend-description-on', // Name the state
    icon: 'fa-question',
    title: 'Legend Description Off', // Text whenever you hover over button
    onClick: function(btn) { // and its callback
      var questions = L.Control.extend({
        options: {
          position: 'bottomright'
        },
        onAdd: function() {
          this._div = L.DomUtil.create('div', 'buttonCSS');
          this._div.innerHTML = "@copyright 2018-2021, IRES: " +
              "U.S.-Mexico Interdisciplinary Research Collaboration for  " +
              "Smart Cities investigators and contributing participants. " +
              "Licensed under the GNU General Public License v3.0."+
              "This material is based upon work supported by the National Science"+
              "Foundation (NSF) Award #1658733 IRES: US-Mexico"+
              "Interdisciplinary Collaboration for Smart Cities.";
          return this._div;
        },
      });

      questionButton = new questions();
      mymap.addControl(questionButton);
      btn.state('legend-description-off');
    }
  }, {
    stateName: 'legend-description-off',
    icon: 'fa-question',
    title: 'Legend Description On',
    onClick: function(btn) {
      btn.state('legend-description-on');
    }
  }]
});
stateChangingButton.addTo(mymap);