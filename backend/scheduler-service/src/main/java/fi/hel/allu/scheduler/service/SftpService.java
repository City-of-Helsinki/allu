package fi.hel.allu.scheduler.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SftpService {

  private static final Duration SFTP_TIMEOUT = Duration.ofMinutes(50L);
  private static final Logger logger = LoggerFactory.getLogger(SftpService.class);

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
    logger.info("start downloading sftp");
    try {
      JSch jsch = new JSch();
      String config = "Port 8022\n" + "\n" + "Host localhost\n" + "  User " + user + "\n" + "  Hostname "
          + "127.0.0.1" + "\n" + "Host *\n" + "  ConnectTime 30000\n"
          + "  PreferredAuthentications password,publickey\n"
          + "  #ForwardAgent yes\n" + "  StrictHostKeyChecking no\n"
          + "  KexAlgorithms +diffie-hellman-group1-sha1\n"
          + "  #IdentityFile ~/.ssh/id_rsa\n" + "  UserKnownHostsFile ~/.ssh/known_hosts";

     logger.info("Generated configurations:");
      logger.info(config);

      ConfigRepository configRepository = com.jcraft.jsch.OpenSSHConfig.parse(config);

      jsch.setConfigRepository(configRepository);
      Session jschSession = jsch.getSession("localhost");
      jschSession.setConfig("server_host_key", jschSession.getConfig("server_host_key") + ",ssh-rsa");
      jschSession.setConfig("PubkeyAcceptedAlgorithms", jschSession.getConfig("PubkeyAcceptedAlgorithms") + ",ssh-rsa");
      jschSession.setConfig("kex", jschSession.getConfig("kex") + ",diffie-hellman-group14-sha1");
      jschSession.setPassword(password);
      jschSession.setTimeout(100000);
      jschSession.connect();
      logger.info("Is connected: {}", jschSession.isConnected());
      ChannelSftp channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
      List<String> list = channelSftp.ls(".").stream()
          .filter(e -> !e.getAttrs().isDir())
          .map(ChannelSftp.LsEntry::getFilename)
          .collect(Collectors.toList());
      for (String file : list){
        channelSftp.get(remoteDirectory+file, localDirectory+file);
        channelSftp.rename(remoteDirectory+file, remoteArchiveDirectory+file);
        channelSftp.rm(remoteDirectory+file);
      }
      channelSftp.exit();
    }  catch (JSchException e) {
      logger.error("Failed jsch", e);
      throw new RuntimeException(e);
    } catch (SftpException e) {
      logger.error("Failed connection", e);
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      logger.info("Close SFTP Download Manager");
    }
    return true;
  }
}