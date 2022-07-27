/**
 * Flip the ROIs for QuPath objects (usually annotations) vertically or horizontally.
 *
 * This creates new objects that are then added to the current image hierarchy.
 *
 * This also shows the method by which any arbitrary AffineTransform may be
 * applied to an object by scripting.
 * 
 * Note: This version is updated for QuPath v0.2.
 *
 * @author Pete Bankhead
 */

import java.awt.geom.AffineTransform

// Parameters to adjust
boolean flipHorizontal = true
boolean flipVertical = false
boolean keepExisting = false

// Get selected object & its ROI
def selected = getSelectedObjects().findAll {it.hasROI()}
if (!selected) {
    print 'No objects selected!'
    return
}

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
def transformedObjects = selected.collect { PathObjectTools.transformObject(it, transform, false) }
if (!keepExisting)
    removeObjects(selected, true)
addObjects(transformedObjects)
selectObjects(transformedObjects)
