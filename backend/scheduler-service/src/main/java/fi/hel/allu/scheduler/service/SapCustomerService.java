package fi.hel.allu.scheduler.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.sap.marshaller.AlluUnmarshaller;
import fi.hel.allu.sap.model.DEBMAS06;
import fi.hel.allu.sap.model.E1KNA1M;
import fi.hel.allu.scheduler.config.ApplicationProperties;

/**
 * Service for updating customer with data from SAP.
 */
@Service
public class SapCustomerService {

  private static final Logger logger = LoggerFactory.getLogger(SapCustomerService.class);

  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private FtpService ftpService;


  @Autowired
  public SapCustomerService(RestTemplate restTemplate, ApplicationProperties applicationProperties, FtpService ftpService) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.ftpService = ftpService;
  }

  /**
   * Downloads SAP customer XML files from FTP and executes
   * customer update.
   */
  public void updateCustomers() {
    boolean successfullyDownloaded = downloadFilesFromFtp();
    if (successfullyDownloaded) {
      List<File> customerFiles = listCustomerFiles();
      Map<File, DEBMAS06> sapCustomersByFile = readCustomers(customerFiles);
      updateCustomers(sapCustomersByFile);
    }
  }

  private boolean downloadFilesFromFtp() {
    return ftpService.downloadFiles(applicationProperties.getSapFtpCustomerHost(), applicationProperties.getSapFtpCustomerPort(),
        applicationProperties.getSapFtpCustomerUser(), applicationProperties.getSapFtpCustomerPassword(),
        applicationProperties.getSapFtpCustomerDirectory(), applicationProperties.getSapFtpCustomerArchive(), getCustomerSourceDirectory().toString());
  }

  private List<File> listCustomerFiles() {
    File customerFileFolder = getCustomerSourceDirectory().toFile();
    return Arrays.asList(customerFileFolder.listFiles());
  }

  private Map<File, DEBMAS06> readCustomers(List<File> customerFiles) {
    AlluUnmarshaller unmarshaller = new AlluUnmarshaller();
    Map<File, DEBMAS06> customers = new HashMap<>();
    for (File customerFile : customerFiles) {
      DEBMAS06 customer = readCustomerDataFromFile(unmarshaller, customerFile);
      if (customer != null) {
        customers.put(customerFile, customer);
      } else {
        moveFileToFailed(customerFile);
      }
    }
    return customers;
  }

  private DEBMAS06 readCustomerDataFromFile(AlluUnmarshaller unmarshaller, File customerFile) {
    DEBMAS06 customer = null;
    try (InputStream inputStream = new FileInputStream(customerFile)) {
      customer = unmarshaller.unmarshal(inputStream);
    } catch (JAXBException | IOException e) {
      logger.warn("Failed to read customer from file {}", customerFile.getName(), e);
    }
    return customer;
  }

  private void updateCustomers(Map<File, DEBMAS06> sapCustomers) {
    for (Entry<File, DEBMAS06> sapCustomer : sapCustomers.entrySet()) {
      E1KNA1M customerData = sapCustomer.getValue().getiDoc().getE1kna1m();
      boolean succesfullyUpdated = updateCustomer(customerData);
      if (succesfullyUpdated) {
        archiveCustomerFile(sapCustomer.getKey());
      } else {
        moveFileToFailed(sapCustomer.getKey());
      }
    }
  }

  private boolean updateCustomer(E1KNA1M sapCustomerData) {
    try {
      restTemplate.exchange(
          applicationProperties.getCustomerUpdateUrl(),
          HttpMethod.PUT,
          new HttpEntity<>(sapCustomerData, createAuthenticationHeader()), Void.class);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private HttpHeaders createAuthenticationHeader() {
    return new HttpHeaders() {{
      setContentType(MediaType.APPLICATION_JSON);
      set(AUTHORIZATION, "Bearer " + applicationProperties.getExternalServiceAuthenticationToken());
    }};
  }

  private void archiveCustomerFile(File customerFile) {
    String archiveDirectory = getCustomerArchiveDirectory();
    moveFile(customerFile, archiveDirectory);
  }
  private void moveFileToFailed(File customerFile) {
    String failedDirectory = getFailedDirectory();
    moveFile(customerFile, failedDirectory);
  }

  private void moveFile(File customerFile, String targetDirectory) {
    String fileName = customerFile.getName();
    File archiveFile = new File(targetDirectory, addTimestampToFileName(fileName));
    boolean success = customerFile.renameTo(archiveFile);
    if (!success) {
      logger.warn("Failed to rename file from {} to {}", customerFile.getAbsolutePath(), archiveFile.getAbsolutePath());
    }
  }

  private String addTimestampToFileName(String fileName) {
    int i = fileName.contains(".") ? fileName.lastIndexOf('.') : fileName.length();
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("-yyyy-MM-dd-HHmmss"));
    fileName = fileName.substring(0, i) + timestamp + fileName.substring(i);
    return fileName;
  }

  private Path getCustomerSourceDirectory() {
    return  Paths.get(applicationProperties.getCustomerSourceDir());
  }

  private String getCustomerArchiveDirectory() {
    return  applicationProperties.getCustomerArchiveDir();
  }

  private String getFailedDirectory() {
    return  applicationProperties.getFailedCustomerUpdateDir();
  }

  /**
   * Returns value indicating whether SAP customer update is enabled
   * @return
   */
  public boolean isUpdateEnabled() {
    return applicationProperties.isCustomerUpdateEnabled();
  }

}
