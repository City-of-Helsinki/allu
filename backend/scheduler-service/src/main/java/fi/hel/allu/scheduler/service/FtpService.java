package fi.hel.allu.scheduler.service;

import java.io.*;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FtpService {

  private static final Logger logger = LoggerFactory.getLogger(FtpService.class);

  /**
   * Uploads all files from given local directory to FTP server directory. Moves
   * uploaded files to local archive directory.
   *
   * @param host FTP server host
   * @param port FTP server port
   * @param user FTP username
   * @param password FTP password
   * @param localDirectory Local source directory
   * @param localArchiveDirectory directory where to move files on local machine
   *        after successful upload
   * @param remoteDirectory Target directory on remote server
   * @return true if files uploaded successfully; otherwise, false
   */
  public boolean uploadFiles(String host, int port, String user, String password, String localDirectory, String localArchiveDirectory,
      String remoteDirectory) {
    FTPSClient ftpClient = null;
    try {
      ftpClient = connect(host, port, user, password, remoteDirectory);
      if (ftpClient == null) {
        return false;
      }
      uploadFiles(localDirectory, localArchiveDirectory, ftpClient);
    } catch (IOException ex) {
      logger.warn("FTP upload failed", ex);
      return false;
    } finally {
      disconnect(ftpClient);
    }
    return true;
  }

  /**
   * Downloads all files from given FTP server directory. Moves downloaded files
   * to FTP server's archive directory
   *
   * @param host FTP server host
   * @param port FTP server port
   * @param user FTP username
   * @param password FTP password
   * @param remoteDirectory Directory in FTP server where to download from
   * @param remoteArchiveDirectory Archive directory where to move files on
   *        server after successful download
   * @param localDirectory Local target directory
   * @return true if files downloaded successfully; otherwise, false
   */
  public boolean downloadFiles(String host, int port, String user, String password, String remoteDirectory, String remoteArchiveDirectory,
      String localDirectory) {
    FTPSClient ftpClient = null;
    try {
      ftpClient = connect(host, port, user, password, remoteDirectory);
      if (ftpClient == null) {
        return false;
      }
      downloadFiles(localDirectory, remoteArchiveDirectory,  ftpClient);
    } catch (IOException ex) {
      logger.warn("FTP download failed", ex);
      return false;
    } finally {
      disconnect(ftpClient);
    }
    return true;
  }

  private void uploadFiles(String localDirectory, String archiveDirectory, FTPSClient ftpClient)
      throws IOException {
    File sourceDirectory = new File(localDirectory);
    for (File file : sourceDirectory.listFiles()) {
      logger.debug("Uploading file {}", file.getName());
      try (FileInputStream sourceFile = new FileInputStream(file)) {
        ftpClient.storeFile(file.getName(), sourceFile);
      }
      archiveFile(file, archiveDirectory);
    }
  }

  private void archiveFile(File file, String localArchiveDirectory) {
    File archiveFile = new File(localArchiveDirectory, file.getName());
    boolean success = file.renameTo(archiveFile);
    if (!success) {
      logger.warn("Failed to rename file from {} to {}", file.getAbsolutePath(), archiveFile.getAbsolutePath());
    }
  }

  private void downloadFiles(String localDirectory, String archiveDirectory, FTPSClient ftpClient)
      throws IOException {
    FTPFile[] ftpFiles = ftpClient.listFiles();
    for (FTPFile file : ftpFiles) {
      if (!file.isFile()) {
        continue;
      }
      downloadFile(ftpClient, file, localDirectory);
      ftpClient.rename(file.getName(), archiveDirectory + "/" + file.getName());
    }
  }

  private void downloadFile(FTPSClient ftpClient, FTPFile file, String localDirectory) throws IOException {
    logger.debug("Downloading FTP file {}.", file.getName());
    try (OutputStream output = new FileOutputStream(localDirectory + "/" + file.getName())) {
      ftpClient.retrieveFile(file.getName(), output);
    }
  }

  private FTPSClient connect(String host, int port, String user, String password, String remoteDirectory) throws IOException {
    FTPSClient ftpClient = new FTPSClient();
    logger.debug("Connecting to FTP server {}", host);
    ftpClient.connect(host, port);
    int reply = ftpClient.getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {
      logger.warn("FTP server connection failed, reply {}", reply);
      disconnect(ftpClient);
      return null;
    }
    ftpClient.login(user, password);
    ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
    ftpClient.enterLocalPassiveMode();
    ftpClient.execPROT("P");
    ftpClient.changeWorkingDirectory(remoteDirectory);
    return ftpClient;
  }

  private void disconnect(FTPSClient ftpClient) {
    try {
      if (ftpClient != null && ftpClient.isConnected()) {
        ftpClient.logout();
        ftpClient.disconnect();
      }
    } catch (IOException ex) {
      logger.warn("Error occurred when closing FTP connection", ex);
    }
  }

}
