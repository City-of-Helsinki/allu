package fi.hel.allu.pdfcreator.util;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.springframework.stereotype.Component;

/**
 * Wrapper around Apache commons Executor class
 *
 * @author kimmo
 *
 */
@Component
public class Executioner {
  public int execute(CommandLine cmdLine) throws ExecuteException, IOException {
    final Executor executioner = new DefaultExecutor();
    return executioner.execute(cmdLine);
  }
}
