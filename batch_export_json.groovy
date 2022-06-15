/**
 * This script exports all annotations for images in 
 * a QuPath project as json.
 * @author: Chao Lab at Sunnybrook
 * @version: 1.0
 */
 
import javax.swing.JFileChooser
import java.nio.file.Path
import java.nio.file.Path

def project = getProject()

// allows user to choose the output folder
JFileChooser chooser = new JFileChooser() 
    chooser.setCurrentDirectory(new java.io.File(".")) // set where to start looking for your directory
    chooser.setDialogTitle("Select the folder to output the .json annotations")
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY) // filter to show only directories
    // get the user action
    int returnVal = chooser.showOpenDialog()
    returnVal == JFileChooser.APPROVE_OPTION
       // get the directory and start your logic
       File outDirectory = chooser.getSelectedFile()
       
// for each entry in the project - list images
for (entry in project.getImageList()) {
    // gets file path and name
    def fileURI = entry.readImageData().getServer().getURIs().get(0)
    def filePath = GeneralTools.toPath(fileURI).getFileName()  
    String imageName = GeneralTools.getNameWithoutExtension(filePath.toString())
    print imageName
    // gets annotations
    def imageData = entry.readImageData()
    def hierarchy = imageData.getHierarchy()
    def annotations = hierarchy.getAnnotationObjects()
    print annotations
    // converts path name to string
    String outDir = outDirectory.toString() + File.separator + imageName + ".json"
    print outDir
    
    exportObjectsToGeoJson(annotations, outDir, "PRETTY_JSON")
    print "Annotation saved to " + outDir + "\n"
}