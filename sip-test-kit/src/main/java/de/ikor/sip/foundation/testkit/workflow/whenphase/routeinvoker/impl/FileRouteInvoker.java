package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import de.ikor.sip.foundation.core.proxies.ProcessorProxy;
import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.file.FileConsumer;
import org.apache.camel.component.file.FileEndpoint;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileConsumer;
import org.apache.camel.util.CastUtils;
import org.springframework.stereotype.Component;

/** Invoker class for triggering routes with File consumer */
@Component
@RequiredArgsConstructor
@Slf4j
public class FileRouteInvoker implements RouteInvoker {

  private final CamelContext camelContext;
  private Endpoint endpoint;

  @Override
  public Exchange invoke(Exchange inputExchange) {
    FileEndpoint fileEndpoint = (FileEndpoint) endpoint;
    Route route =
        camelContext.getRoute(
            (String) inputExchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY));
    GenericFileConsumer<File> fileConsumer = (GenericFileConsumer<File>) route.getConsumer();

    try {
      GenericFile<File> genericFile = FileConsumer.asGenericFile(
              fileEndpoint.getConfiguration().getDirectory(),
              createFile((String) inputExchange.getMessage().getBody()),
              fileEndpoint.getCharset(),
              fileEndpoint.isProbeContentType());

      Exchange fileExchange = fileConsumer.createExchange(true);
      genericFile.bindToExchange(fileExchange, fileEndpoint.isProbeContentType());
      prepareHeaders(fileExchange, inputExchange.getMessage().getHeaders());
      fileEndpoint.configureExchange(fileExchange);
      fileEndpoint.configureMessage(genericFile, fileExchange.getIn());

      LinkedList<Exchange> exchanges = new LinkedList<>();
      exchanges.add(fileExchange);
      fileConsumer.processBatch(CastUtils.cast((Queue<?>) exchanges));
    } catch (IOException e) {
      log.error("sip.testkit.workflow.whenphase.routeinvoker.file.notempfile");
    }

    return createEmptyExchange();
  }

  @Override
  public boolean isApplicable(Endpoint endpoint) {
    return endpoint instanceof FileEndpoint;
  }

  @Override
  public RouteInvoker setEndpoint(Endpoint endpoint) {
    this.endpoint = endpoint;
    return this;
  }

  @Override
  public boolean isSuspendable() {
    return true;
  }

  private void prepareHeaders(Exchange exchange, Map<String, Object> testKitHeaders) {
    Map<String, Object> headers = exchange.getMessage().getHeaders();
    headers.put(RouteInvoker.TEST_NAME_HEADER, testKitHeaders.get(RouteInvoker.TEST_NAME_HEADER));
    headers.put(
        ProcessorProxy.TEST_MODE_HEADER, testKitHeaders.get(ProcessorProxy.TEST_MODE_HEADER));
  }

  private File createFile(String body) throws IOException {
    File file = File.createTempFile("testing", ".txt");
    writeToFile(file, body);
    file.deleteOnExit();
    return file;
  }

  private void writeToFile(File file, String body) {
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(body);
      writer.flush();
    } catch (IOException e) {
      log.error("sip.testkit.workflow.whenphase.routeinvoker.file.nowritting");
    }
  }

  private Exchange createEmptyExchange() {
    ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
    return exchangeBuilder.build();
  }
}
