package de.ikor.sip.foundation.core.apps.declarative.mapping;

import de.ikor.sip.foundation.core.apps.declarative.mapping.CommonDomainTypes.ResourceRequest;
import de.ikor.sip.foundation.core.declarative.annonation.GlobalMapper;
import de.ikor.sip.foundation.core.declarative.model.ModelMapper;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

public class BackendTypes {

  @Value
  @Builder
  @Jacksonized
  public static class BackendResourceRequest {
    Integer id;
    String resourceTypeName;
  }

  @GlobalMapper
  public static class BackendRequestModelMapper
      implements ModelMapper<ResourceRequest, BackendResourceRequest> {

    @Override
    public BackendResourceRequest mapToTargetModel(ResourceRequest sourceModel) {
      return BackendResourceRequest.builder()
          .resourceTypeName(sourceModel.getResourceType())
          .id(sourceModel.getId())
          .build();
    }

    @Override
    public Class<ResourceRequest> getSourceModelClass() {
      return ResourceRequest.class;
    }

    @Override
    public Class<BackendResourceRequest> getTargetModelClass() {
      return BackendResourceRequest.class;
    }
  }
}
