/**
 * This script imports annotations stored in *.qpdata
 * and adds them to images in the current QuPath project.
 * The images and .qpdata files must have matching names.
 * @author: Chao Lab @Sunnybrook
 * @version: 1.0
 */
 
import qupath.lib.io.PathIO
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.NoSuchFileException
import javax.swing.JFileChooser

// Define current project
def project = getProject()
// create the file chooser
JFileChooser chooser = new JFileChooser() 
chooser.setCurrentDirectory(new java.io.File(".")) // set where to start looking for your directory
chooser.setDialogTitle("Select the folder containing .qpdata files")
chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY) // filter to show only directories
// get the user action
int returnVal = chooser.showOpenDialog()
if(returnVal == JFileChooser.APPROVE_OPTION) {
   // get the directory and start your logic
   File qpdataDir = chooser.getSelectedFile()
    // Loop through iamges in project
    for (entry in project.getImageList()) {
        // Find image name
        def fileURI = entry.readImageData().getServer().getURIs().get(0)   
        def filePath = GeneralTools.toPath(fileURI).getFileName()  
        String imageName = GeneralTools.getNameWithoutExtension(filePath.toString()) //GeneralTools.getNameWithoutExtension(entry.getImageName())
        print imageName
        // Get image data and hierarchy
        def imageData = entry.readImageData()
        def hierarchy = imageData.getHierarchy()
        def currentAnno = hierarchy.getAnnotationObjects()
        if (currentAnno.isEmpty()) {
            // Build the path to .qpdata   
            String qpdataPath = Paths.get(qpdataDir.toString(), imageName + ".qpdata")       
        
            try {
                // Read associated qpdata file and add annotations
                print ("Found matching qpdata file at" + qpdataPath)
                def objFromFile = PathIO.readObjects(new File(qpdataPath))
                hierarchy.addPathObjects(objFromFile)
                print ("Adding annotations to " + entry.getImageName())
                // Save image data
                entry.saveImageData(imageData)
            } 
            catch (NoSuchFileException e) {
                print ("CANNOT FIND ANNOTATIONS FOR " + entry.getImageName() + "\n")
                continue;
            }
            print "Done!\n"
            }
         else {
             print ("file: " + imageName + " has already been annotated\n")
             continue;
         }    
    }
}
project.syncChanges()
