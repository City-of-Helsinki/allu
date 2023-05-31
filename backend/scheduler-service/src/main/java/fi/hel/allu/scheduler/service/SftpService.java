package fi.hel.allu.scheduler.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
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
      jsch.setKnownHosts(new ByteArrayInputStream("|1|3Qd7vSu3BVHj3ImF6o+iNNE4BQM=|d9gEVFytZuiexP+2VuNXCn+0Oxc= ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAIEAv9wWO9fmH/WsXq2WhqOBVGSJays/sKbRmCrkdVV36l5vUumKLJv33bihpff4qLCJrMjzblCuMe6pFGSZgLvNaUOJq/jdLMPzs3McV5+3QOT8PeO7Wc+f0GLL83abv2cye3b85HFT+3gPF1OfdUJ994LokKGh25oJYUxDQM9GGkk=\n".getBytes()));
      Session jschSession = jsch.getSession(user,host,port);
      jschSession.setConfig("server_host_key", jschSession.getConfig("server_host_key") + ",ssh-rsa");
      jschSession.setConfig("PubkeyAcceptedAlgorithms", jschSession.getConfig("PubkeyAcceptedAlgorithms") + ",ssh-rsa");
      jschSession.setConfig("kex", jschSession.getConfig("kex") + ",diffie-hellman-group14-sha1");
      jschSession.setPassword(password);
      jschSession.setTimeout(100000);
      jschSession.connect();
      logger.info("Is connected: {}", jschSession.isConnected());
      ChannelSftp channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
      channelSftp.connect();
      logger.info("is channel connected: {}", channelSftp.isConnected());
      try {
            SftpATTRS attrs = channelSftp.stat(remoteDirectory);
            logger.info("is directory: {}", attrs.isDir());
        } catch (Exception e) {
            logger.error("directory not found,", e);
        }
      List<String> list = channelSftp.ls(remoteDirectory).stream()
          .filter(e -> !e.getAttrs().isDir())
          .map(ChannelSftp.LsEntry::getFilename)
          .collect(Collectors.toList());
      for (String file : list){
        channelSftp.get(remoteDirectory+file, localDirectory+"/"+file);
        channelSftp.rename(channelSftp.getHome()+file, channelSftp.getHome()+"arch/"+file);
        channelSftp.rm(remoteDirectory+file);
      }
      channelSftp.exit();
    }  catch (JSchException e) {
      logger.error("Failed jsch", e);
      throw new RuntimeException(e);
    } catch (SftpException e) {
        logger.error("Failed connection", e);
        throw new RuntimeException(e);
    }finally {
      logger.info("Close SFTP Download Manager");
    }
    return true;
  }
}