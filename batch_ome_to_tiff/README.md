Batch convert MRXS files to OME-TIFF format
===========================================
This script allows you to batch or single convert MRXS files into JPEG compressed pyramidal ome.tiff files through the use of Fiji-ImageJ

Requirements:
==================
* [Fiji-ImageJ](https://imagej.net/software/fiji/downloads)
* [Bioformats2raw](https://github.com/glencoesoftware/bioformats2raw)
* [Raw2ometiff](https://github.com/glencoesoftware/raw2ometiff)
* [c-blosc library](https://github.com/Blosc/c-blosc)


Installing c-blosc:
======
* Ubuntu 18.04+: In Terminal, type in `apt-get install libblosc1`

 

* Mac-OS: In Terminal, type in `brew install c-blosc`, then set `JAVA_OPTS='-Djna.library.path=/usr/local/Cellar/c-blosc/*/lib'`

 

* Windows: Pre-built blosc DLLs are available from the Fiji project. Rename the downloaded DLL to blosc.dll and place in a fixed location then set variable name by searching up edit the "system environment variables" --> "New system variable" on windows. Set the variable name as: JAVA_OPTS="

set it's variable value as:-Djna.library.path=C:\path\to\blosc\folder"
