package de.ikor.sip.foundation.core.apps.declarative.mappingadapter;

import de.ikor.sip.foundation.core.apps.declarative.mappingadapter.CommonDomainTypes.ResourceRequest;
import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

public class FrontEndTypes {

  @Value
  @Builder
  @Jacksonized
  public static class UserRequest {
    Integer userId;
  }

  @Data
  @Builder
  @Jacksonized
  public static class UserResponse {
    private Integer userId;
    private String username;
  }

  public static class FrontEndSystemRequestMapper
      implements ModelMapper<UserRequest, ResourceRequest> {

    @Override
    public ResourceRequest mapToTargetModel(UserRequest sourceModel) {
      return ResourceRequest.builder().id(sourceModel.getUserId()).resourceType("USER").build();
    }
  }
}
