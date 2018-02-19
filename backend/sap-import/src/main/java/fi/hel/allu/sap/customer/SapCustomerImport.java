package fi.hel.allu.sap.customer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerChange;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.sap.marshaller.AlluUnmarshaller;
import fi.hel.allu.sap.model.DEBMAS06;
import fi.hel.allu.sap.model.E1KNA1M;

/**
 * Imports sap customers to Allu. Reads customer XML files from given directory,
 * maps to customer model objects and inserts to DB with model-service.
 */
@SpringBootApplication
public class SapCustomerImport implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(SapCustomerImport.class);

  private String sourceDirectory;
  private String archiveDirectory;
  private String modelServiceUrl;
  private Integer userId;
  private RestTemplate restTemplate;

  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("Incorrect number of arguments, usage java -jar sap-import.jar [source directory] [archive directory] [model service url] [userid] ");
      return;
    }
    SpringApplication app = new SpringApplication(SapCustomerImport.class);
    app.setWebEnvironment(false);
    app.run(args);
  }

  @Override
  public void run(String... args) throws Exception {
    sourceDirectory = args[0];
    archiveDirectory = args[1];
    modelServiceUrl = args[2];
    userId = Integer.valueOf(args[3]);
    restTemplate = new RestTemplate();
    List<File> customerFiles = listCustomerFiles();
    Map<File, DEBMAS06> sapCustomersByFile = readCustomers(customerFiles);
    importCustomers(sapCustomersByFile);
    logger.info("Imported customers from {} files.", sapCustomersByFile.keySet().size());
    System.exit(0);
  }

  private void importCustomers(Map<File, DEBMAS06> sapCustomersByFile) {
    for (Entry<File, DEBMAS06> sapCustomer : sapCustomersByFile.entrySet()) {
      logger.info("Importing customer from file {}", sapCustomer.getKey().getName());
      E1KNA1M customerData = sapCustomer.getValue().getiDoc().getE1kna1m();
      importCustomer(customerData);
      archiveCustomerFile(sapCustomer.getKey());
    }
  }

  private void importCustomer(E1KNA1M customerData) {
    Customer customerModel = new Customer();
    PostalAddress postalAddress = new PostalAddress(customerData.getStras(), customerData.getPstlz(),
        customerData.getOrt01());
    customerModel.setType(customerData.getStcd1() != null ? CustomerType.COMPANY : CustomerType.PERSON);
    customerModel.setName(customerData.getName1());
    customerModel.setRegistryKey(customerData.getStcd1() != null ? customerData.getStcd1() : customerData.getStcd2());
    customerModel.setPostalAddress(postalAddress);
    customerModel.setActive(true);
    customerModel.setSapCustomerNumber(customerData.getKunnr());
    customerModel.setInvoicingProhibited("X".equals(customerData.getSperr()));
    CustomerChange customerChange = new CustomerChange(userId, customerModel);
    restTemplate.postForObject(modelServiceUrl, customerChange, Customer.class);
  }

  private void archiveCustomerFile(File customerFile) {
    String fileName = customerFile.getName();
    File archiveFile = new File(archiveDirectory, fileName);
    boolean success = customerFile.renameTo(archiveFile);
    if (!success) {
      logger.warn("Failed to rename file {}", customerFile.getAbsolutePath());
    }
  }

  private List<File> listCustomerFiles() {
    File customerFileFolder = new File(sourceDirectory);
    return Arrays.asList(customerFileFolder.listFiles()).stream().filter(f -> f.isFile()).collect(Collectors.toList());
  }

  private static Map<File, DEBMAS06> readCustomers(List<File> customerFiles) {
    AlluUnmarshaller unmarshaller = new AlluUnmarshaller();
    Map<File, DEBMAS06> customers = new HashMap<>();
    for (File customerFile : customerFiles) {
      DEBMAS06 customer = readCustomerDataFromFile(unmarshaller, customerFile);
      if (customer != null) {
        customers.put(customerFile, customer);
      } else {
        logger.warn("Failed to read customer from file {}", customerFile.getName());
      }
    }
    return customers;
  }

  private static DEBMAS06 readCustomerDataFromFile(AlluUnmarshaller unmarshaller, File customerFile) {
    DEBMAS06 customer = null;
    try (InputStream inputStream = new FileInputStream(customerFile)) {
      customer = unmarshaller.unmarshal(inputStream);
    } catch (JAXBException | IOException e) {
      logger.warn("Failed to read customer from file {}", customerFile.getName(), e);
    }
    return customer;
  }
}
