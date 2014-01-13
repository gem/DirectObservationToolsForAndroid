# IDCT-DO



Global Earthquake Model - Inventory Data Capture Tools 

Direct Observation Android Tool




[Release 0.2.14 apk](http://idct.github.io/DirectObservationToolsForAndroid/idct.apk) ~32MB

![ScreenShot](http://idct.github.io/DirectObservationToolsForAndroid/download.png)





The Direct Observation Android Tool has been compiled using Eclipse 3.6.2 using version 4.2.2 of the Android SDK. The application also makes use of the JavaScript map library OpenLayers, version 2.11.

Tiles (with extension png.tile) for offline usage may be downloaded using [MOBAC](http://mobac.sourceforge.net)


[Sample OpenStreetMap (MapQuest) tiles of Pavia, Italy](http://idct.github.io/DirectObservationToolsForAndroid/sample_pavia_png_tiles.zip) ~20MB

## Application Structure

###Database

The Global Earthquake Model IDCT SQLite database is located at *assets/gem.db3*. This database file may be updated with an alternative version of a GEM IDCT SQLite database prior to application complilation.


###Glossary / In-app help documentation

The Global Earthquake Model glossary .HTML files are located in *assets/glossary/glossary_cleaned*. Corresponding images (linked in the .HTML files) for the glossary are located *assets/images-1*.


###Forms

Forms within the application have corresponding Java classes to manage data IO to and from the SQLite database.

