package de.ikor.sip.foundation.testkit.workflow;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
public class TestCaseCollector {

  private List<TestCase> testCases = new LinkedList<>();
}
