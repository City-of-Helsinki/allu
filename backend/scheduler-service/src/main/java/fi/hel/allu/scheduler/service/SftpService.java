package fi.hel.allu.scheduler.service;

import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SftpService {

  private static final Integer SFTP_TIMEOUT = Integer.valueOf(100000);
  private static final Logger logger = LoggerFactory.getLogger(SftpService.class);

  private  FileSystemOptions sftpOptions;
  private FileSystemOptions smallOptions;
  private StandardFileSystemManager manager;

  /**
   * Uploads all files from given local directory to SFTP server directory. Moves
   * uploaded files to local archive directory.
   *
   * @param host SFTP server host
   * @param port SFTP port
   * @param user SFTP username
   * @param password SFTP password
   * @param localDirectory Local source directory
   * @param localArchiveDirectory directory where to move files on local machine
   *        after successful upload
   * @param remoteDirectory Target directory on remote server
   * @return true if files uploaded successfully; otherwise, false
   */
  public boolean uploadFiles(String host, int port, String user, String password, String localDirectory, String localArchiveDirectory,
      String remoteDirectory) {
    try {
      initialize();
      FileObject localDirectoryObject  = createLocalDirectoryObject(localDirectory);
      FileObject localArchiveDirectoryObject = createLocalDirectoryObject(localArchiveDirectory);
      FileObject remoteDirectoryObject = createRemoteDirectoryObject(host, port, user, password, remoteDirectory);
      moveFiles(localDirectoryObject, remoteDirectoryObject, localArchiveDirectoryObject);
    } catch (IOException | URISyntaxException ex) {
      logger.warn("Failed to upload files.", ex);
      return false;
    }
    finally {
      logger.info("Close SFTP Upload Manager");
      manager.close();
    }
    return true;
  }

  /**
   * Downloads all files from given SFTP server directory. Moves downloaded files
   * to SFTP server's archive directory
   *
   * @param host SFTP server host
   * @param port SFTP port
   * @param user SFTP username
   * @param password SFTP password
   * @param remoteDirectory Directory in SFTP server where to download from
   * @param remoteArchiveDirectory Archive directory where to move files on
   *        server after successful download
   * @param localDirectory Local target directory
   * @return true if files downloaded successfully; otherwise, false
   */
  public boolean downloadFiles(String host, int port, String user, String password, String remoteDirectory, String remoteArchiveDirectory,
      String localDirectory) {
    try {
      initialize();
      FileObject remoteDirectoryObject = createRemoteDirectoryObject(host, port, user, password, remoteDirectory);
      FileObject remoteArchiveDirectoryObject = createRemoteDirectoryObject(host, port, user, password, remoteArchiveDirectory);
      FileObject localDirectoryObject  = createLocalDirectoryObject(localDirectory);
      moveFiles(remoteDirectoryObject, localDirectoryObject, remoteArchiveDirectoryObject);
    } catch (IOException | URISyntaxException ex) {
      logger.warn("Failed to download files.", ex);
    }
    finally {
      logger.info("Close SFTP Download Manager");
      manager.close();
    }
    return true;
  }

  private void initialize() throws FileSystemException {
    manager = new StandardFileSystemManager();
    manager.init();
    initializeSftpOptions();
  }

  /**
   * Copy files from given source directory to target directory. After file is copied
   * moves file from source directory to given archive directory.
   */
  private void moveFiles(FileObject sourceDirectory, FileObject targetDirectory, FileObject archiveDirectory) throws IOException {
    List<FileObject> files = Arrays.asList(sourceDirectory.getChildren()).stream().filter(f -> isFile(f)).collect(Collectors.toList());
    for (FileObject file : files) {
      FileObject targetFile = manager.resolveFile(targetDirectory.getName().getURI() + "/" + file.getName().getBaseName());
      targetFile.copyFrom(file, Selectors.SELECT_SELF);
      archiveFile(file, archiveDirectory);
    }
  }

  private void archiveFile(FileObject file, FileObject archiveDirectory) throws FileSystemException {
    try {
      FileObject targetFile = manager.resolveFile(archiveDirectory.getName().getURI() + "/" + file.getName().getBaseName(), smallOptions);
      targetFile.copyFrom(file, new AllFileSelector());
      file.delete();
    }
    catch ( FileSystemException e) {
      logger.warn("Archiving file failed");
      throw e;
    }
  }

  private boolean isFile(FileObject file) {
    try {
      return file.isFile();
    } catch (FileSystemException ex) {
      logger.warn("Error occurred when processing file {}.", file.getName().getBaseName(), ex);
      return false;
    }
  }

  private FileObject createLocalDirectoryObject(String localDirectory) throws IOException {
    FileObject localDirectoryObject = manager.resolveFile(localDirectory);
    if (!directoryExists(localDirectoryObject)) {
      throw new FileNotFoundException("Local directory not found");
    }
    return localDirectoryObject;
  }

  private FileObject createRemoteDirectoryObject(String host, int port, String user, String password,
      String directory) throws IOException, URISyntaxException {
    String connectionString = buildConnectionString(host, port, user, password, directory);
    FileObject remoteDirectoryObject = manager.resolveFile(connectionString, sftpOptions);
    if (!directoryExists(remoteDirectoryObject)) {
      throw new FileNotFoundException("Remote directory not found");
    }
    return remoteDirectoryObject;
  }

  private boolean directoryExists(FileObject remoteDirectoryObject) throws FileSystemException {
    return remoteDirectoryObject.exists() && remoteDirectoryObject.isFolder();
  }

  private String buildConnectionString(String host, int port, String user, String password, String remoteDirectory) throws URISyntaxException {
    return new URI("sftp", user + ":" + password, host, port, remoteDirectory, null, null).toString();
  }
  private void initializeSftpOptions() throws FileSystemException {
    sftpOptions = new FileSystemOptions();
    SftpFileSystemConfigBuilder configBuilder = SftpFileSystemConfigBuilder.getInstance();
    configBuilder.setStrictHostKeyChecking(sftpOptions, "no");
    configBuilder.setUserDirIsRoot(sftpOptions, true);
    configBuilder.setTimeout(sftpOptions, SFTP_TIMEOUT);
    smallOptions = new FileSystemOptions();
    configBuilder.setStrictHostKeyChecking(sftpOptions, "no");
    configBuilder.setTimeout(sftpOptions, SFTP_TIMEOUT);
  }
}
