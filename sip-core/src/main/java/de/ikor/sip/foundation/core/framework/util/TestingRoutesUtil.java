package de.ikor.sip.foundation.core.framework.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.model.ChoiceDefinition;
import org.apache.camel.model.OtherwiseDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.WhenDefinition;
import org.apache.commons.collections4.CollectionUtils;

public class TestingRoutesUtil {
  public static final String TESTING_SUFFIX = "-testing";

  public static void handleTestIDAppending(ProcessorDefinition<?> processorDefinition) {
    if (processorDefinition.getId() != null) {
      appendTestIdToProcessor(processorDefinition);
    }
    if (CollectionUtils.isNotEmpty(processorDefinition.getOutputs())) {
      processorDefinition.getOutputs().forEach(TestingRoutesUtil::handleTestIDAppending);
    }
  }

  private static void appendTestIdToProcessor(ProcessorDefinition<?> processorDefinition) {
    String id = processorDefinition.getId();
    if (processorDefinition instanceof ChoiceDefinition) {
      // handler for setId of ChoiceDefinition due to its custom implementation
      handleChoiceDefinitionID((ChoiceDefinition) processorDefinition, id);
    }
    processorDefinition.setId(id + TESTING_SUFFIX);
  }

  private static void handleChoiceDefinitionID(ChoiceDefinition choiceDefinition, String id) {
    // Save reference to when and otherwise definition
    List<WhenDefinition> whenDefinitions = choiceDefinition.getWhenClauses();
    OtherwiseDefinition otherwiseDefinition = choiceDefinition.getOtherwise();
    // remove when and otherwise definition so id would be set on choice definition
    choiceDefinition.setWhenClauses(new ArrayList<>());
    choiceDefinition.setOtherwise(null);
    // set choice definition id with testing suffix
    choiceDefinition.setId(id + TESTING_SUFFIX);
    // place back original when and otherwise definition
    choiceDefinition.setWhenClauses(whenDefinitions);
    choiceDefinition.setOtherwise(otherwiseDefinition);
  }
}
