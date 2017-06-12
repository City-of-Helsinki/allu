package fi.hel.allu.model.testUtils;

import com.greghaskins.spectrum.Block;

import fi.hel.allu.model.ModelApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Arrays;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;

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

  /**
   * Assertion checker. Knows some assertion and makes sure it holds when a
   * given code bloce is executed.
   */
  protected static abstract class AssertChecker {
    public abstract void when(Block block) throws AssertionError;
  }

  /**
   * Asserts that the given code block throws one of the given throwables. For
   * example:
   * <p>
   * <code>
   * assertThrows(() -> { throw new RuntimeException(); }, IllegalArgumentException.class, RuntimeException.class);
   * </code>
   *
   * @param expected List of throwables. Code block must throw one of these.
   */

  @SafeVarargs
  protected static AssertChecker assertThrows(Class<? extends Throwable>... expected)
  {
    return new AssertChecker() {
      @Override
      public void when(Block block) throws AssertionError {
        try {
          block.run();
        } catch (Throwable t) {
          if (!Arrays.stream(expected).filter(ex -> ex.isAssignableFrom(t.getClass())).findAny().isPresent()) {
            throw new AssertionError("Unexpected throwable: " + t, t);
          }
          return;
        }
        throw new AssertionError("Did not throw!");
      }
    };
  }
}
