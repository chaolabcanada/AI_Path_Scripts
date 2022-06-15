/**
 * This script creates a new QuPath project and import
 * images (validation set only) from URLs stored in 
 * train-val-split.json
 * @author: Jesse Chao, PhD
 * @email: jesse.chao@sri.utoronto.ca 
 * @version: 0.3
 */ 

import java.util.Map
import java.awt.image.BufferedImage
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import java.nio.file.Path
import java.nio.file.Paths
import groovy.io.FileType
import qupath.lib.io.GsonTools
import qupath.lib.images.servers.ImageServerProvider
import qupath.lib.gui.commands.ProjectCommands
import com.google.gson.reflect.TypeToken

// Create QuPath project directory
JFileChooser dirChooser = new JFileChooser() 
dirChooser.setCurrentDirectory(new java.io.File(".")) // set where to start looking for your directory
dirChooser.setDialogTitle("Choose a location to save the project to")
dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY) // filter to show only directories
int dirReturnVal = dirChooser.showOpenDialog()
if(dirReturnVal == JFileChooser.APPROVE_OPTION) {
    File selectedDir = dirChooser.getSelectedFile()
    // Check if a QuPath Project directory exists, else create one
    def projectName = Dialogs.showInputDialog(
        "Project Name",
        "What would you like to name this project?",
        "ROI_validation"
    )
    directory = new File(selectedDir.toString() + File.separator + projectName)
    if (!directory.exists()) {
        print("No project directory found. Creating one...")
        directory.mkdirs()
        print(projectName + " created!" + System.lineSeparator())
    }
}
// Create project
def project = Projects.createProject(directory , BufferedImage.class)

// Read image list from train-val-split
JFileChooser fileChooser = new JFileChooser() 
FileNameExtensionFilter filter = new FileNameExtensionFilter (
    "json files", "json"
)
fileChooser.setFileFilter(filter)
fileChooser.setCurrentDirectory(new java.io.File(".")) 
fileChooser.setDialogTitle("Locate json file containing image paths")
fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
int fileReturnVal = fileChooser.showOpenDialog()
if(fileReturnVal == JFileChooser.APPROVE_OPTION) {
    File selectedFile = fileChooser.getSelectedFile()
    def gson = GsonTools.getInstance(true)
    def type = new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType();
    def json_fp = new File(selectedFile.toString()).text
    def map = gson.fromJson(json_fp, type)
    //map.forEach((x, y) -> print ("key : " + x + " , value : " + y))
    for (Map.Entry<String, Map<String, Object>> rootMap: map.entrySet()) {
        if (rootMap.getKey() == "val") {
            def firstNodeMap = rootMap.getValue()
            for (Map.Entry<String, List> secondNodeMap: firstNodeMap.entrySet()) {
                if (secondNodeMap.getKey() == "images") {
                    imageList = secondNodeMap.getValue()
                    print("Found " + imageList.size() + " images in this dataset. Begin importing images..." + System.lineSeparator())
                }
            }
        }
    }
}

// Add image files to the project
// Create file chooser
JFileChooser chooser = new JFileChooser() 
chooser.setCurrentDirectory(new java.io.File(".")) // set where to start looking for your directory
chooser.setDialogTitle("Locate folder containing predicted annotations")
chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY) // filter to show only directories
// Get user action
int returnVal = chooser.showOpenDialog()
if(returnVal == JFileChooser.APPROVE_OPTION) {
    // get the directory and start your logic
    File jsonDir = chooser.getSelectedFile()
    // Define template to read json
    def gson = GsonTools.getInstance(true)
    def type = new com.google.gson.reflect.TypeToken<List<qupath.lib.objects.PathObject>>(){}.getType() 
    int numImages = imageList.size()
    for (int i = 0; i < numImages; i++) {
        // If changed computer, need to correct the path 
        String updatedImagePath = imageList.get(i).replace("MT8", "MT8")
        // Get matching json
        File imageURL = new File(updatedImagePath)
        String imageNameWithExtension = imageURL.getName()
        String imageName = GeneralTools.getNameWithoutExtension(imageNameWithExtension.toString())
        String jsonPath = Paths.get(jsonDir.toString(), "pred_" + imageName + ".json")
        // Start QuPath image import logic
        def support = ImageServerProvider.getPreferredUriImageSupport(BufferedImage.class, imageURL.getCanonicalPath())
        def builder = support.builders.get(0)
        // Make sure we don't have null
        if (builder == null) {
        print("Image not supported: " + imageURL.getCanonicalPath())
        continue
        }
        // Add the image as entry to the project
        entry = project.addImage(builder)
        def imageData = entry.readImageData()
        imageData.setImageType(ImageData.ImageType.BRIGHTFIELD_H_E)
        def hierarchy = imageData.getHierarchy()
        // Add predicted annotations
        try {
            def json_fp = new File(jsonPath).text
            deserializedAnnotations = gson.fromJson(json_fp, type)
            hierarchy.addPathObjects(deserializedAnnotations)
            entry.saveImageData(imageData)
        }
        catch (FileNotFoundException) {
            print(String.format("CANNOT FIND ANNOTATIONS FOR %s", imageName))
            continue
        }
        // Write a thumbnail if we can
        var img = ProjectCommands.getThumbnailRGB(imageData.getServer());
        entry.setThumbnail(img)
        // Add an entry name (the filename)
        entry.setImageName(imageURL.getName())
        // Show progress
        int progressBar = 30
        if (numImages > progressBar) {
        step = Math.round(numImages/ progressBar)
        } else {
            step = Math.round(progressBar/ numImages)
        }
        x = i+1
        if (x%step == 0) {
            int count = Math.round(x/ step)
            def remainder = numImages - x
            String progress = ""
            if (remainder > 0) {
                progress = String.format("Progress: [%s%s]\r", "="*count,  " "*Math.max(1, (progressBar-count)))
            } else {
                progress = String.format("Progress: [%sDone!]\r", "="*(count-5))
            }
            print(progress)
        }
    }
}
print(System.lineSeparator()+ "Finished importing images!" + System.lineSeparator())

// Changes should now be reflected in the project directory
project.syncChanges()