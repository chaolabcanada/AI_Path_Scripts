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
        def fileURI = entry.readImageData().getServer().getURIs().get(0)   
        def filePath = GeneralTools.toPath(fileURI).getFileName()  
        String imageName = GeneralTools.getNameWithoutExtension(filePath.toString()) //GeneralTools.getNameWithoutExtension(entry.getImageName())
        print imageName
        // Get image data and hierarchy
        def imageData = entry.readImageData()
        def hierarchy = imageData.getHierarchy()
        def currentAnno = hierarchy.getAnnotationObjects()
        // Build the path to .json   
        String jsonPath = Paths.get(jsonDir.toString(), "pred_" + imageName + ".json")       
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
            print ("CANNOT FIND ANNOTATIONS FOR " + entry.getImageName() + "\n")
            continue;
        }
        print "Done!\n"
    }
}