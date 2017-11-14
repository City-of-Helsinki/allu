package fi.hel.allu.ui.controller;

import java.io.PrintWriter;
import java.util.List;

import fi.hel.allu.servicecore.domain.CustomerJson;

/**
 * Writes customer fields in CSV format.
 *
 */
public class CustomerCsvWriter extends CustomerExport {

  private PrintWriter writer;
  private List<CustomerJson> customers;

  public CustomerCsvWriter(PrintWriter writer, List<CustomerJson> customers) {
    this.writer = writer;
    this.customers = customers;
  }

  public void write() {
    writer.println(String.join(",", getHeaders()));
    for (CustomerJson customer : customers) {
      writer.println(String.join(",", getValues(customer)));
    }
  }
}
