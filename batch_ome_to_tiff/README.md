Batch convert MRXS files to OME-TIFF format
===========================================
This script allows you to batch or single convert MRXS files into JPEG compressed pyramidal ome.tiff files through the use of Fiji-ImageJ.

Requirements:
=============
* [Fiji-ImageJ](https://imagej.net/software/fiji/downloads)
* [Bioformats2raw](https://github.com/glencoesoftware/bioformats2raw)
* [Raw2ometiff](https://github.com/glencoesoftware/raw2ometiff)
* [c-blosc library](https://sites.imagej.net/N5/lib/win64/)

Installing c-blosc:
===================
* **Ubuntu 18.04+**: In Terminal, type in `apt-get install libblosc1`

 

* **Mac-OS**: In Terminal, type in `brew install c-blosc`, then set `JAVA_OPTS='-Djna.library.path=/usr/local/Cellar/c-blosc/*/lib'`

 

* **Windows**: Pre-built blosc DLLs are available from the [Fiji project](https://sites.imagej.net/N5/lib/win64/). Rename the downloaded DLL to `blosc.dll` and place it in a fixed location, then set variable name by searching up _edit_ the _"system environment variables" --> "New system variable"_. Set the variable name as: `JAVA_OPTS=`. Set its variable value as:`"-Djna.library.path=C:\path\to\blosc\folder"`

Usage:
======
**With the script open on FIJI-ImageJ, Change the directory path on lines 17 and 18 of the script:**

* Line 17: change the directory to point towards the batch file. E.g. `path/to/bioformats2raw-0.4.1-SNAPSHOT/bin/bioformats2raw.bat`

* Line 18:  change the directory to point towards the batch file. E.g. `path/to/raw2ometiff-0.3.0/bin/raw2ometiff.bat`

Make sure to change all backslashes into forward slashes.


**Execute the script:**
In the GUI menu, click _"Batch"_

Click _"Add files"_ and choose all of the MRXS files you wish to convert. Click _"ok"_ once finished choosing

Set _"Number of resolution levels"_

Set _"compression"_ type

License:
========
