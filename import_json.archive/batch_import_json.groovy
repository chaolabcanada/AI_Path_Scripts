def project = getProject()
for (entry in project.getImageList()) {
    String imageName = entry.getImageName()
    def imageData = entry.readImageData()
    def server = imageData.getServer()

    String serverPath = server.getURIs().path
    print serverPath
    String src_dir = serverPath[1..serverPath.lastIndexOf(File.separator)]
    print src_dir 
    String jsonPath = src_dir + imageName[0..imageName.lastIndexOf('.')] + 'json'
    print jsonPath
    print fileExists(jsonPath)

}