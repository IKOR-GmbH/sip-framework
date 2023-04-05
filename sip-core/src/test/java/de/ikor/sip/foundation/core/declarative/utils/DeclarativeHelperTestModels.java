package de.ikor.sip.foundation.core.declarative.utils;

import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import java.util.ArrayList;

public class DeclarativeHelperTestModels {

  public static class NoArgsConstructorMapper implements ModelMapper<Object, Object> {

    public NoArgsConstructorMapper(String test) {}

    @Override
    public Object mapToTargetModel(Object sourceModel) {
      return null;
    }
  }

  public static class ExceptionThrowingConstructorMapper implements ModelMapper<Object, Object> {

    public ExceptionThrowingConstructorMapper() throws IllegalAccessException {
      throw new IllegalAccessException("Exception message");
    }

    @Override
    public Object mapToTargetModel(Object sourceModel) {
      return null;
    }
  }

  public static class MultipleMethodsMapper implements ModelMapper<Integer, Integer> {

    @Override
    public Integer mapToTargetModel(Integer sourceModel) {
      return null;
    }

    public String mapToTargetModel(String sourceModel) {
      return null;
    }
  }

  public static class MyIntegerList extends ArrayList<Integer> {}

  public static class MyExtendedIntegerList extends MyIntegerList {}
}
