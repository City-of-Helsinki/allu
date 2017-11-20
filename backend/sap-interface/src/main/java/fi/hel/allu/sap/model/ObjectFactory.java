package fi.hel.allu.sap.model;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface in the fi.hel.allu.sap.model package.
 */
@XmlRegistry
public class ObjectFactory {

  public LineItem createLineItem() {
    return new LineItem();
  }

  public OrderParty createOrderParty() {
    return new OrderParty();
  }

  public SalesOrder createSalesOrder() {
    return new SalesOrder();
  }

  public SalesOrderContainer createSalesOrderContainer() {
    return new SalesOrderContainer();
  }

  public DEBMAS06 createDEBMAS06() {
    return new DEBMAS06();
  }

  public IDOC createIDOC() {
    return new IDOC();
  }

  public E1KNA1M createE1KNA1M() {
    return new E1KNA1M();
  }
}
