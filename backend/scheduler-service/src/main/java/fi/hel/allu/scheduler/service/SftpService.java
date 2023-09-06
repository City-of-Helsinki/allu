package fi.hel.allu.scheduler.service;

import com.google.common.io.Files;
import com.jcraft.jsch.*;
import fi.hel.allu.scheduler.domain.SFTPSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class SftpService {

  private static final Logger logger = LoggerFactory.getLogger(SftpService.class);

  /**
   * Uploads all files from given local directory to SFTP server directory. Moves
   * uploaded files to local archive directory.
   *
   * @param sftpSettings needed settings to create sftp connection
   * @param localDirectory Local source directory
   * @param localArchiveDirectory directory where to move files on local machine
   *        after successful upload
   * @param remoteDirectory Target directory on remote server
   * @return true if files uploaded successfully; otherwise, false
   */
  public boolean uploadFiles(SFTPSettings sftpSettings, String localDirectory, String localArchiveDirectory,
                             String remoteDirectory) {
    logger.info("Start uploading sftp");
    try {
      ChannelSftp channelSftp = createSession(sftpSettings);
      File sourceDir = new File(localDirectory);
      for (File file : Objects.requireNonNull(sourceDir.listFiles())){
        if(file.isFile()) {
          channelSftp.put(file.getAbsolutePath(), remoteDirectory + file.getName());
          Files.move(file, new File(localArchiveDirectory, file.getName()));
        }
      }
      channelSftp.exit();
    }  catch (JSchException e) {
      logger.error("Failed jsch", e);
      return false;
    } catch (SftpException e) {
      logger.error("Failed connection", e);
      return false;
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      logger.info("Uploading file through SFTP ended");
    }
    return true;
  }

  /**
   * Downloads all files from given SFTP server directory. Moves downloaded files
   * to SFTP server's archive directory
   *
   * @param sftpSettings needed settings to create sftp connection
   * @param remoteDirectory Directory in SFTP server where to download from
   * @param remoteArchiveDirectory Archive directory where to move files on
   *        server after successful download
   * @param localDirectory Local target directory
   * @return true if files downloaded successfully; otherwise, false
   */
  public boolean downloadFiles(SFTPSettings sftpSettings, String remoteDirectory, String remoteArchiveDirectory,
                               String localDirectory) {
    logger.info("Start downloading sftp");
    try {
      ChannelSftp channelSftp = createSession(sftpSettings);
      List<String> list = channelSftp.ls(remoteDirectory).stream()
          .filter(e -> !e.getAttrs().isDir())
          .map(ChannelSftp.LsEntry::getFilename)
          .toList();
      for (String file : list){
        channelSftp.get(remoteDirectory+file, localDirectory+"/"+file);
        channelSftp.rename(channelSftp.getHome()+file, channelSftp.getHome() + remoteArchiveDirectory
            + "/" + file);
      }
      channelSftp.exit();
    }  catch (JSchException e) {
      logger.error("Failed jsch", e);
      return false;
    } catch (SftpException e) {
      logger.error("Failed connection", e);
      return false;
    }finally {
      logger.info("Downloading file through SFTP ended");
    }
    return true;
  }

  private ChannelSftp createSession(SFTPSettings sftpSettings) throws JSchException {
    JSch jsch = new JSch();
    jsch.setKnownHosts(new ByteArrayInputStream(sftpSettings.getKnownHosts().getBytes()));
    Session jschSession = jsch.getSession(sftpSettings.getUser(), sftpSettings.getHost(), sftpSettings.getPort());
    jschSession.setConfig("server_host_key", jschSession.getConfig("server_host_key") + sftpSettings.getSignatureAlgorithm());
    jschSession.setConfig("PubkeyAcceptedAlgorithms", jschSession.getConfig("PubkeyAcceptedAlgorithms") + sftpSettings.getSignatureAlgorithm());
    jschSession.setConfig("kex", jschSession.getConfig("kex") + sftpSettings.getKeyAlgorithm());
    jschSession.setPassword(sftpSettings.getPassword());
    jschSession.setTimeout(sftpSettings.getTimeout());
    jschSession.connect();
    logger.info("Is connected: {}", jschSession.isConnected());
    ChannelSftp channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
    channelSftp.connect();
    return channelSftp;
  }
}