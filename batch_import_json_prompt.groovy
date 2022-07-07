/**
 * This script imports annotations stored as json files
 * and adds them to images in the current QuPath project.
 * The images and json files must have matching names.
 * @author: Chao Lab @Sunnybrook
 * @version: 1.0
 */

import qupath.lib.io.GsonTools
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JFileChooser

boolean addmore = false  // Set to true to add more annotations even if current image is already annotated

// Define template to read json
def gson = GsonTools.getInstance(true)
def type = new com.google.gson.reflect.TypeToken<List<qupath.lib.objects.PathObject>>(){}.getType();

// Get current project
def project = getProject()

// Create the file chooser
JFileChooser chooser = new JFileChooser() 
chooser.setCurrentDirectory(new java.io.File(".")) // set where to start looking for your directory
chooser.setDialogTitle("Select the folder containing .json files")
chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY) // filter to show only directories

// Define function to read annotations from json and add to image
def getAnnosFromJson(gson, type, jsonPath) {
    def json_fp = new File(jsonPath).text  // Read json
    print ("Found matching json at" + jsonPath)
    deserializedAnnotations = gson.fromJson(json_fp, type)
    return deserializedAnnotations
}

// Get user action
int returnVal = chooser.showOpenDialog()
if(returnVal == JFileChooser.APPROVE_OPTION) {
   // Get the directory and start your logic
   File jsonDir = chooser.getSelectedFile()
    // Loop through iamges in project
    for (entry in project.getImageList()) {
        // Find image name
        def fileURI = entry.readImageData().getServer().getURIs().get(0)   
        def filePath = GeneralTools.toPath(fileURI).getFileName()  
        String imageName = GeneralTools.getNameWithoutExtension(filePath.toString())
        print imageName
        // Get image data and hierarchy
        def imageData = entry.readImageData()
        def hierarchy = imageData.getHierarchy()
        def currentAnno = hierarchy.getAnnotationObjects()
        String jsonPath = Paths.get(jsonDir.toString(), imageName + ".json")  // Build the path to json file, which needs to have the same file prefix as the images
        if (currentAnno.isEmpty()) {        
            try {
                // Read associated json file and add annotations
                annotations = getAnnosFromJson(gson, type, jsonPath)
                hierarchy.addPathObjects(annotations)
                print ("Adding annotations to " + imageName)
                entry.saveImageData(imageData)  // Save image data
            } 
            catch (FileNotFoundException ex) {
                print ("CANNOT FIND ANNOTATIONS FOR " + imageName + "\n")
                continue;
            }
            catch (IllegalStateException ex) {
                importObjectsFromFile(jsonPath)
            }
            print ("Done!\n")
        }
         else {
             print ("!! " + imageName + " has already been annotated !!")
            if (addmore) {
                annotations = getAnnosFromJson(gson, type, jsonPath)
                hierarchy.addPathObjects(annotations)
                print ("Adding extra annotations... \n")
                entry.saveImageData(imageData)
            }
            else {
                print("No new annotation will be added.")
            }
             continue;
         }    
    }
}
