import qupath.lib.io.GsonTools
import java.nio.file.Path
import java.nio.file.Paths

// Define template to read json
def gson = GsonTools.getInstance(true)
def type = new com.google.gson.reflect.TypeToken<List<qupath.lib.objects.PathObject>>(){}.getType();
// Loop through iamges in project
def project = getProject()
for (entry in project.getImageList()) {
    // Find image name
    String imageName = GeneralTools.getNameWithoutExtension(entry.getImageName())
    print imageName[0..imageName.lastIndexOf(".ome")-1]
    // Get image data and hierarchy
    def imageData = entry.readImageData()
    def hierarchy = imageData.getHierarchy()
    //print hierarchy.getAnnotationObjects()
    // Find parent directory of the image
    def serverURI = imageData.getServer().getURIs().get(0)
    //def imagePath = server.getURIs().getAt(0).getPath()
    def parentDir = GeneralTools.toPath(serverURI).getParent()
    //print parentDir
    // Assuming annotation(.json) files are in the same directory
    // find the images, build the path to .json. Hardcoded imageName. Only works for oral pathology ome.tiff files
    String jsonPath = Paths.get(parentDir.toString(), imageName[0..imageName.lastIndexOf(".ome")-1] + ".json")
    print jsonPath

    //def json_fp = promptForFile(null)
    //def json_fp = new File(jsonPath).text
    //print json_fp


// json files need to be in the same folder as the tif files for this to work
    try {
        def json_fp = new File(jsonPath).text
        deserializedAnnotations = gson.fromJson(json_fp, type)
        hierarchy.addPathObjects(deserializedAnnotations)
        //print hierarchy.getAnnotationObjects()
    } 
    catch (FileNotFoundException e) {
        println("CAN NOT FIND ANNOTATIONS FOR IMAGE: " + imageName)
        continue;
    }
    //Read annotations
    //bufferedReader = new BufferedReader(new FileReader(json_fp))
    //deserializedAnnotations = gson.fromJson(bufferedReader.text, type)
                        //deserializedAnnotations = gson.fromJson(json_fp, type)
    // Apply annotations to hierarchy
                        //hierarchy.addPathObjects(deserializedAnnotations)
                        //print hierarchy.getAnnotationObjects()
    // Save image data
    entry.saveImageData(imageData)

    print "Done!"
}