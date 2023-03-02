package de.ikor.sip.foundation.core.apps.declarative.mapping;

import de.ikor.sip.foundation.core.apps.declarative.mapping.CommonDomainTypes.ResourceRequest;
import de.ikor.sip.foundation.core.apps.declarative.mapping.CommonDomainTypes.ResourceResponse;
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

  public static class FrontEndSystemResponseMapper
      implements ModelMapper<ResourceResponse, UserResponse> {

    @Override
    public UserResponse mapToTargetModel(ResourceResponse sourceModel) {
      return UserResponse.builder()
          .username(sourceModel.getResourceName())
          .userId(sourceModel.getId())
          .build();
    }

    @Override
    public Class<ResourceResponse> getSourceModelClass() {
      return ResourceResponse.class;
    }

    @Override
    public Class<UserResponse> getTargetModelClass() {
      return UserResponse.class;
    }
  }
}
