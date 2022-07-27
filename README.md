# IDCT-DO


[Global Earthquake Model](http://www.globalquakemodel.org) - [Inventory Data Capture Tools](https://www.globalquakemodel.org/what/physical-integrated-risk/inventory-capture-tools/)

Direct Observation Android Tool


[Install App via Google Play](https://play.google.com/store/apps/details?id=org.globalquakemodel.org.idctdo)


This tool allows users of Android tablets and smartphones to collect information about individual buildings for use in seismic or multi-hazard risk assessment. The building attributes are defined by the [GEM Building Taxonomy](https://www.globalquakemodel.org/what/physical-integrated-risk/building-taxonomy/).



The Direct Observation Android Tool has been compiled using [AndroidStudio](https://developer.android.com/studio/index.html).  The application also makes use of the JavaScript map library OpenLayers, version 2.11.

Tiles (with extension png.tile) for offline usage may be downloaded using [MOBAC](http://mobac.sourceforge.net)


[Sample OpenStreetMap (MapQuest) tiles of Pavia, Italy](http://gem.github.io/DirectObservationToolsForAndroid/sample_pavia_png_tiles.zip) ~20MB


Many thanks to [Corporación OSSO](http://osso.org.co) for their generous contribution of Spanish language support.


## Application Structure

### Database

The Global Earthquake Model IDCT SQLite database is located at *assets/gem.db3*. This database file may be updated with an alternative version of a GEM IDCT SQLite database prior to application complilation.


### Glossary / In-app help documentation

The Global Earthquake Model glossary .HTML files are located in *assets/glossary/glossary_cleaned*. Corresponding images (linked in the .HTML files) for the glossary are located *assets/images-1*.


### Forms

Forms within the application have corresponding Java classes to manage data IO to and from the SQLite database.

