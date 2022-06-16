package de.ikor.sip.foundation.mvnplugin;

import de.ikor.sip.foundation.mvnplugin.model.AnalyzeResult;
import org.apache.maven.plugin.MojoExecutionException;

public class ResultsValidator {
  /**
   * Checks the content of input param and stops further execution if issues (cross dependencies)
   * are found.
   *
   * @param analyzeResult {@link AnalyzeResult} contains the report of possible cross dependencies
   *     found.
   * @throws MojoExecutionException thrown with preformatted message when issues are detected.
   */
  public void validate(AnalyzeResult analyzeResult) throws MojoExecutionException {
    if (analyzeResult.bannedImportsFound()) {
      throw new MojoExecutionException(new ResultsFormatter().formatMatches(analyzeResult));
    }
  }
}
