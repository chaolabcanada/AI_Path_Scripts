setImageType('BRIGHTFIELD_H_E');
setColorDeconvolutionStains('{"Name" : "H&E default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049", "Stain 2" : "Eosin", "Values 2" : "0.2159 0.8012 0.5581", "Background" : " 255 255 255"}');
def imageData = getCurrentImageData()
def server = imageData.getServer()
def imageName = server.getMetadata().name
//def f_path = getCurrentServerPath()

String server_path = imageData.getServerPath()
String f_path = server_path[server_path.lastIndexOf(':')+1..-1].replace('%20', ' ')
print f_path

String im_name = f_path[f_path.lastIndexOf('/')+1..-1]
print im_name

String im_dir = f_path[0..f_path.lastIndexOf('/')]
print im_dir

String json_path = im_dir + im_name[0..-5] + '.json'
print json_path

if (fileExists(json_path)) {
    print "found the matching json"
    }
else{
    print "didn't find the matching json"
    }

importObjectsFromFile(json_path)

print "Done!"