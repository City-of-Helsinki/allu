package fi.hel.allu.common.util;

public class AttachmentUtil {

  public static String getFileExtension(String name) {
    int lastIndexOf = name.lastIndexOf(".");
    if (lastIndexOf == -1) {
      return "";
    }
    return name.substring(lastIndexOf);
  }

  public static double bytesToMegabytes(long bytes) {
      return bytes / (1024.0 * 1024.0);
  }

}
