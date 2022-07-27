/**
 * This script imports annotation objects from a json file
 * and add them to the current image. 
 * @author: Chao Lab @Sunnybrook
 * @email: jesse.chao@sri.utoronto.ca 
 * @version: 1.0
 */ 


import qupath.lib.io.GsonTools


// Instantiate tools
def gson = GsonTools.getInstance(true)

// Prepare template
def type = new com.google.gson.reflect.TypeToken<List<qupath.lib.objects.PathObject>>(){}.getType();
//def type = qupath.lib.objects.PathObject
def json_fp = promptForFile(null)

try {
    // Read annotations
    bufferedReader = new BufferedReader(new FileReader(json_fp))
    deserializedAnnotations = gson.fromJson(bufferedReader.text, type)
    // Add to dataset
    addObjects(deserializedAnnotations)
    // Resolve hierarchy
    resolveHierarchy()
}
catch (IllegalStateException ex) {
    importObjectsFromFile(jsonPath)
}

print "Done!"
