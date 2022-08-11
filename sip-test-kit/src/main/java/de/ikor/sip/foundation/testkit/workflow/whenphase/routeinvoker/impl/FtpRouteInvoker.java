package de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.impl;

import static de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.headers.FtpExchangeHeaders.*;

import de.ikor.sip.foundation.testkit.workflow.givenphase.Mock;
import de.ikor.sip.foundation.testkit.workflow.whenphase.routeinvoker.RouteInvoker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.file.remote.*;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.ObjectHelper;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FtpRouteInvoker implements RouteInvoker {

    private final CamelContext camelContext;
    private Endpoint endpoint;

    @Override
    public Exchange invoke(Exchange inputExchange) {
        RemoteFileEndpoint<FTPFile> ftpEndpoint = (RemoteFileEndpoint<FTPFile>) endpoint;
        Route route =
                camelContext.getRoute(
                        (String) inputExchange.getProperty(Mock.ENDPOINT_ID_EXCHANGE_PROPERTY));
        RemoteFileConsumer<FTPFile> fileConsumer = (RemoteFileConsumer<FTPFile>) route.getConsumer();

        Exchange fileExchange = createFtpExchange(fileConsumer, ftpEndpoint, inputExchange);

        try {
            fileConsumer.getProcessor().process(fileExchange);
        } catch (Exception e) {
            log.error("sip.testkit.workflow.whenphase.routeinvoker.ftp.badrequest");
        }

        return createEmptyExchange();
    }

    @Override
    public boolean isApplicable(Endpoint endpoint) {
        return endpoint instanceof RemoteFileEndpoint;
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

    private Exchange createFtpExchange(RemoteFileConsumer<FTPFile> fileConsumer, RemoteFileEndpoint<FTPFile> ftpEndpoint, Exchange inputExchange) {
        Exchange ftpExchange = fileConsumer.createExchange(true);
        ftpExchange.getMessage().setBody(inputExchange.getMessage().getBody());
        ftpExchange.getMessage().setHeaders(prepareFtpHeaders(ftpEndpoint, inputExchange));
        return ftpExchange;
    }

    private Map<String, Object> prepareFtpHeaders(RemoteFileEndpoint<FTPFile> ftpEndpoint, Exchange inputExchange) {
        Map<String, Object> headers = inputExchange.getMessage().getHeaders();
        String endpointAbsolutePath = FileUtil.stripTrailingSeparator(ftpEndpoint.getConfiguration().getDirectory());

        prepareDefaultHeaders(headers, ftpEndpoint, inputExchange.getMessage().getBody(String.class), endpointAbsolutePath);

        if (headers.containsKey(CAMEL_FILE_NAME.getValue())) {
            prepareHeadersWithFilename(headers, endpointAbsolutePath);
        }
        prepareOtherHeaders(headers);
        return headers;
    }

    private void prepareDefaultHeaders(Map<String, Object> headers, RemoteFileEndpoint<FTPFile> ftpEndpoint, String bodyPayload, String endpointAbsolutePath) {
        if (!headers.containsKey(CAMEL_FILE_ABSOLUTE.getValue())) {
            headers.put(CAMEL_FILE_ABSOLUTE.getValue(), FileUtil.hasLeadingSeparator(endpointAbsolutePath));
        }
        if (!headers.containsKey(CAMEL_FILE_HOST.getValue())) {
            headers.put(CAMEL_FILE_HOST.getValue(), ((RemoteFileConfiguration) ftpEndpoint.getConfiguration()).getHost());
        }
        if (!headers.containsKey(CAMEL_FILE_LENGTH.getValue())) {
            headers.put(CAMEL_FILE_LENGTH.getValue(), (long) bodyPayload.length());
        }
        if (!headers.containsKey(CAMEL_FILE_PARENT.getValue())) {
            headers.put(CAMEL_FILE_PARENT.getValue(), FileUtil.stripLeadingSeparator(endpointAbsolutePath));
        }
        if (ftpEndpoint.getConfiguration().isStreamDownload() && !headers.containsKey(CAMEL_REMOTE_FILE_INPUT_STREAM.getValue())) {
            InputStream is = new ByteArrayInputStream(bodyPayload.getBytes());
            headers.put(CAMEL_REMOTE_FILE_INPUT_STREAM.getValue(), is);
        }
    }

    private void prepareHeadersWithFilename(Map<String, Object> headers, String endpointAbsolutePath) {
        String filename = normalizePathToProtocol(FileUtil.stripLeadingSeparator((String) headers.get(CAMEL_FILE_NAME.getValue())));
        headers.put(CAMEL_FILE_NAME.getValue(), filename);
        if (!headers.containsKey(CAMEL_FILE_NAME_CONSUMED.getValue())) {
            headers.put(CAMEL_FILE_NAME_CONSUMED.getValue(), filename);
        }
        if (!headers.containsKey(CAMEL_FILE_NAME_ONLY.getValue())) {
            headers.put(CAMEL_FILE_NAME_ONLY.getValue(), filename);
        }
        if (!headers.containsKey(CAMEL_FILE_RELATIVE_PATH.getValue())) {
            headers.put(CAMEL_FILE_RELATIVE_PATH.getValue(), filename);
        }

        if (!headers.containsKey(CAMEL_FILE_ABSOLUTE_PATH.getValue())) {
            headers.put(CAMEL_FILE_ABSOLUTE_PATH.getValue(), FileUtil.stripLeadingSeparator(endpointAbsolutePath
                    + "/" + filename));
        }
        if (!headers.containsKey(CAMEL_FILE_PATH.getValue())) {
            headers.put(CAMEL_FILE_PATH.getValue(), FileUtil.stripLeadingSeparator(endpointAbsolutePath)
                    + "/" + filename);
        }
    }

    private void prepareOtherHeaders(Map<String, Object> headers) {
        if (headers.containsKey(CAMEL_FILE_LAST_MODIFIED.getValue())) {
            Long lastModifiedTimestamp = (Long) headers.get(CAMEL_FILE_LAST_MODIFIED.getValue());
            if (!headers.containsKey(CAMEL_MESSAGE_TIMESTAMP.getValue())) {
                headers.put(CAMEL_MESSAGE_TIMESTAMP.getValue(), lastModifiedTimestamp);
            }
        }
    }

    private String normalizePathToProtocol(String path) {
        if (!ObjectHelper.isEmpty(path)) {
            path = path.replace('/', File.separatorChar);
            path = path.replace('\\', File.separatorChar);
        }
        return path;
    }

    private Exchange createEmptyExchange() {
        ExchangeBuilder exchangeBuilder = ExchangeBuilder.anExchange(camelContext);
        return exchangeBuilder.build();
    }

}
