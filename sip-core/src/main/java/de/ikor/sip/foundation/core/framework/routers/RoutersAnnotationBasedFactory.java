package de.ikor.sip.foundation.core.framework.routers;

import de.ikor.sip.foundation.core.framework.official.CentralRouterDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutersAnnotationBasedFactory {
  private final List<CentralRouterDefinition> centralRouters;

  public List<CentralRouterDefinition> getRouters() {
    return centralRouters.stream()
        .filter(router -> router.getClass().isAnnotationPresent(CentralRouterDomainModel.class))
        .collect(Collectors.toList());
  }
}
