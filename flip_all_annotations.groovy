/**
 * Flips all annotations belonging to an image in QuPath project.
 * Certain images get flipped (most often horizontally) when imported
 * into QuPath, causing predicted annotations to not match up.
 * This script fixes the issue by flipping annotations.
 * @author: Jesse Chao, PhD
 * @email: jesse.chao@sri.utoronto.ca 
 * @version: 1.0
 */ 

import java.awt.geom.AffineTransform

// Parameters to adjust
boolean flipHorizontal = true
boolean flipVertical = false
boolean keepExisting = false

// Create the transform
def server = getCurrentServer()
def transform = new AffineTransform()
if (flipHorizontal) {
    transform.scale(-1, 1)
    transform.translate(-server.getWidth(), 0)
}
if (flipVertical) {
    transform.scale(1, -1)
    transform.translate(0, -server.getHeight())
}

// Apply transform & add objects
def originals = getAnnotationObjects()  // Get all annotations
for (i in originals) {
    def objectName = i.getName()
    transformedObject = PathObjectTools.transformObject(i, transform, false)
    transformedObject.setName(objectName)
    addObjects(transformedObject)
}

// Remove old annotations
if (!keepExisting)
    removeObjects(originals, true)