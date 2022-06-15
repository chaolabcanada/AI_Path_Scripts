import qupath.lib.io.GsonTools
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.JFileChooser

// Define template to read json
def gson = GsonTools.getInstance(true)
def type = new com.google.gson.reflect.TypeToken<List<qupath.lib.objects.PathObject>>(){}.getType();
// Define current project
def project = getProject()
// create the file chooser
JFileChooser chooser = new JFileChooser() 
chooser.setCurrentDirectory(new java.io.File(".")) // set where to start looking for your directory
chooser.setDialogTitle("Select the folder containing .json files")
chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY) // filter to show only directories
// get the user action
int returnVal = chooser.showOpenDialog()
if(returnVal == JFileChooser.APPROVE_OPTION) {
   // get the directory and start your logic
   File jsonDir = chooser.getSelectedFile()
    // Loop through iamges in project
    for (entry in project.getImageList()) {
        // Find image name
        String imageName = GeneralTools.getNameWithoutExtension(entry.getImageName())
        print imageName
        // Get image data and hierarchy
        def imageData = entry.readImageData()
        def hierarchy = imageData.getHierarchy()
        def currentAnno = hierarchy.getAnnotationObjects()
        if (currentAnno.isEmpty()) {
            // Find parent directory of the image
            def serverURI = imageData.getServer().getURIs().get(0)
            //def imagePath = server.getURIs().getAt(0).getPath()
            //def parentDir = GeneralTools.toPath(serverURI).getParent()
            //print parentDir
            // Build the path to .json. Hardcoded imageName. Only works for Cam16 .tif files
            String jsonPath = Paths.get(jsonDir.toString(), imageName + ".json")
        
            //def json_fp = promptForFile(null)
            //def json_fp = new File(jsonPath).text
            //print json_fp
        
        
        // json files need to be in the same folder as the tif files for this to work
            try {
                // Read associated json file and add annotations
                def json_fp = new File(jsonPath).text
                print ("Found matching json at" + jsonPath)
                deserializedAnnotations = gson.fromJson(json_fp, type)
                hierarchy.addPathObjects(deserializedAnnotations)
                print ("Adding annotations to " + entry.getImageName())
                // Save image data
                entry.saveImageData(imageData)
            } 
            catch (FileNotFoundException e) {
                print ("CAN NOT FIND ANNOTATIONS FOR " + entry.getImageName() + "\n")
                continue;
            }
            //Read annotations
            //bufferedReader = new BufferedReader(new FileReader(json_fp))
            //deserializedAnnotations = gson.fromJson(bufferedReader.text, type)
                                //deserializedAnnotations = gson.fromJson(json_fp, type)
            // Apply annotations to hierarchy
                                //hierarchy.addPathObjects(deserializedAnnotations)
                                //print hierarchy.getAnnotationObjects()
            print "Done!\n"
            }
         else {
             print ("file: " + imageName + " has already been annotated\n")
             continue;
         }    
    }
}