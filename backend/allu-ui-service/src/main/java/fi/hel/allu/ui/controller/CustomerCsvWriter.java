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

  public CustomerCsvWriter(PrintWriter writer) {
    this.writer = writer;
  }

  @Override
  public void write(List<CustomerJson> customers) {
    writer.println(String.join(",", getHeaders()));
    for (CustomerJson customer : customers) {
      writer.println(String.join(",", getValues(customer)));
    }
  }
}
