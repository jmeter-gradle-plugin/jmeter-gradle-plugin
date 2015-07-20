package net.foragerr.jmeter.gradle.plugins.utils

class FileUtils {

    /*
     *  Delete File or Directory (recursively)
     */
    public static boolean delete(File f){
        if ( f == null || !f.exists()) {
            return true;
        }else {
            if(f.isDirectory()){
                deleteDir(f);
            }else{
                deleteFile(f);
            }
        }
    }

    /*
     *  Delete File or Directory (recursively)
     */
    public static boolean delete(File f,FilenameFilter filter){
        if ( f == null || !f.exists()) {
            return true;
        }else {
            if(f.isDirectory()){
                deleteDir(f,filter);
            }else{
                deleteFile(f);
            }
        }
    }


    private static boolean deleteDir(f, filter=null) {
        boolean re = false;
        File[] fs = f.listFiles(filter);
        if (null != fs) {
            if (fs.length == 0)
                return f.delete()|true;
            for (File file : fs) {
                if (file.isFile())
                    re |= deleteFile(file);
                else
                    re |= deleteDir(file, filter);
            }
            re |= f.delete();
        }
        return re;
    }

    private static boolean deleteFile(File f) {
        return f.delete();
    }
}
