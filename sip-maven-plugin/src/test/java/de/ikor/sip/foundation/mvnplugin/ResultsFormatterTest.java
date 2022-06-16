package de.ikor.sip.foundation.mvnplugin;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.ikor.sip.foundation.mvnplugin.model.AnalyzeResult;
import de.ikor.sip.foundation.mvnplugin.model.BannedImportRecords;
import de.ikor.sip.foundation.mvnplugin.model.ImportStatement;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResultsFormatterTest {
  private ResultsFormatter subject = new ResultsFormatter();

  @Test
  void when_ResultHasBannedImportsInSourceCode_then_FormattedMessageContainsThem() {
    Path path = Paths.get("de", "ikor", "sips");
    List<ImportStatement> matchedImports = new LinkedList<>();
    ImportStatement importStatement = new ImportStatement("de.ikor.sip.AClass", 2, true);
    matchedImports.add(importStatement);
    BannedImportRecords sourceRecords = new BannedImportRecords(path, matchedImports);
    List<BannedImportRecords> bannedImportRecords = List.of(sourceRecords);

    AnalyzeResult analyzeResult = new AnalyzeResult(bannedImportRecords, new ArrayList<>());
    // act
    String formattedMessage = subject.formatMatches(analyzeResult);
    // assert
    assertThat(formattedMessage)
        .contains("in main folder")
        .contains("in file: de\\ikor\\sips")
        .contains("Line: 2")
        .contains("static de.ikor.sip.AClass");
  }
}
