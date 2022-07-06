/**
 * This script converts MRXS files into compressed pyramidal ome.tiff files
 * through the use of bioformats2raw and raw2ometiff conversion tools.
 * https://github.com/glencoesoftware
 * Conversion parameters include choosing resolution levels and compression type.
 * Script is executed through FIJI/ImageJ software.
 * @author: Nicolas BIOP, EPFL, 2021
 * https://github.com/NicoKiaru
 * @version: 1.0
 */ 

#@File(label = "File to convert to ome.tiff") originalFile
#@Integer(label = "Number of resolution levels", value = 5) nResolutions
#@Boolean(label = "Check if this is a RGB image") isRGB
#@String(label = "Compression", choices = {"Uncompressed", "LZW", "JPEG-2000", "JPEG-2000 Lossy","JPEG", "zlib"}, value = "LZW") compression
 
bf2rawPath = "/opt/bioformats2raw-0.4.1-SNAPSHOT/bin/bioformats2raw"
raw2ometiffPath = "/opt/raw2ometiff-0.3.0/bin/raw2ometiff"

// First step : create a temporary dir for raw data
String tmpdir = Files.createTempDirectory("raw2ometiff").toFile().getAbsolutePath();

// Starts the command
List<String> cmd = new ArrayList<>();

// bioformats2raw.bat --resolutions=4 source dest
cmd.add(bf2rawPath)
cmd.add("--resolutions="+nResolutions)
cmd.add(originalFile.getAbsolutePath())
cmd.add(tmpdir+File.separator+"raw")

println("Tmp folder = "+tmpdir+File.separator+"raw")

ProcessBuilder pb = new ProcessBuilder(cmd);
println("- Starting conversion to raw data (1/2) "+originalFile.getAbsolutePath())
Process p = pb.inheritIO().start();
p.waitFor();
println("- Done")

// raw2ometiff.bat --compression="LZW" source dest.tiff
// - first create output directory
def parentDir = originalFile.getParent()
def outputDir = new File(parentDir+File.separator+"OME-TIFF")
outputDir.mkdir()

// start the conversion
def ometiffFilePath = outputDir.getAbsolutePath() + File.separator + FilenameUtils.removeExtension(originalFile.getName()) + ".ome.tiff"

cmd.clear()
cmd.add(raw2ometiffPath)
cmd.add("--compression="+compression)
cmd.add("--progress")
cmd.add(tmpdir+File.separator+"raw")
cmd.add(ometiffFilePath)

pb = new ProcessBuilder(cmd);
//pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
//pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

println("- Starting writing as ome.tiff (2/2) : "+ometiffFilePath)
p = pb.inheritIO().start();
p.waitFor();
println("- Done")

println("Cleaning raw temp data");
FileUtils.deleteDirectory(new File(tmpdir+File.separator+"raw"));
println("- Done");
 

import java.io.*
import java.util.ArrayList
import java.util.List
import java.nio.file.Files
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.FileUtils

import ij.IJ
import ij.Prefs
