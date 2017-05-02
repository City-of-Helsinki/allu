package fi.hel.allu.model.testUtils;

import fi.hel.allu.model.ModelApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import static com.greghaskins.spectrum.Spectrum.*;

/**
 * Base class for spec tests, takes care of @Autowired and transactions
 */

@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
public class SpeccyTestBase {

  @Autowired
  private PlatformTransactionManager transactionManager;
  @Autowired
  protected TestCommon testCommon;

  private TransactionStatus transactionAll;
  private TransactionStatus transactionEach;

  {
    /* Init test context and start global transaction */
    beforeAll(() -> {
      new TestContextManager(getClass()).prepareTestInstance(this);
      transactionAll = createTransactionStatus("TransactionAll");
    });
    /* rollback global transaction */
    afterAll(() -> transactionManager.rollback(transactionAll));

    /* Take care of per-test transaction */
    beforeEach(() -> transactionEach = createTransactionStatus("TransactionEach"));
    afterEach(() -> transactionManager.rollback(transactionEach));
  }

  /*
   * Create transaction status for transaction manager
   *
   * @param transactionName the transaction's name
   * @return TransactionStatus
   */
  private TransactionStatus createTransactionStatus(String transactionName) {
    DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    transactionDefinition.setName(transactionName);
    transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    return transactionManager.getTransaction(transactionDefinition);
  }

}
