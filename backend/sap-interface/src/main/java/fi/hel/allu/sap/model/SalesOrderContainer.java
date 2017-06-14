package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * XML Mapping for a SAP sales order container.
 * <p>
 * To send bills to the SAP system, their data must be encoded in a
 * SalesOrderContainer that is then written out as an XML document and
 * transferred to SAP.
 */
@XmlRootElement(name = "SBO_SalesOrderContainer")
public class SalesOrderContainer {
  private List<SalesOrder> salesOrders;

  /**
   * Get the sales orders in this container
   */
  @XmlElement(name = "SBO_SalesOrder")
  public List<SalesOrder> getSalesOrders() {
    return salesOrders;
  }

  public void setSalesOrders(List<SalesOrder> salesOrders) {
    this.salesOrders = salesOrders;
  }
}
