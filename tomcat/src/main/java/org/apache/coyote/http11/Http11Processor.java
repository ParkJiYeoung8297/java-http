package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.apache.coyote.http11.handler.LoginPageHandler;
import org.apache.coyote.http11.handler.LoginRequestHandler;
import org.apache.coyote.http11.handler.RegisterRequestHandler;
import org.apache.coyote.http11.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESOURCE_PATH = "/";
    private static final String DEFAULT_VALUE = "Hello world!";

    private final Socket connection;
    private final Map<Route, RequestHandler> handlers;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.handlers = Map.of(
                new Route(HttpMethod.GET, "/login"), new LoginPageHandler(),
                new Route(HttpMethod.POST, "/login"), new LoginRequestHandler(),
                new Route(HttpMethod.POST, "/register"), new RegisterRequestHandler()
        );
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final HttpRequestParser httpRequestParser = new HttpRequestParser();
            HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);
            HttpResponse httpResponse = handleRequest(httpRequest);

            String responsePath = httpRequest.path();
            if (httpResponse.headers().containsKey("Location")){
                responsePath = httpResponse.headers().get("Location");
            }

            final var responseBody = createResponseBody(responsePath);
            final HttpResponseWriter httpResponseWriter = new HttpResponseWriter();
            String response = httpResponseWriter.write(httpRequest, httpResponse, responseBody);
            
            log.info("mehtod: {} , path: {}, http status: {}",
                    httpRequest.httpMethod(), responsePath, httpResponse.httpStatus().getMessage());

            System.out.println(response);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        final RequestHandler requestHandler = handlers.get(new Route(request.httpMethod(), request.path()));

        if (requestHandler == null) {
            return new HttpResponse(HttpStatus.OK, new HashMap<>(),"");
        }

        return requestHandler.handle(request);
    }

    private byte[] createResponseBody(String responsePath) throws IOException, URISyntaxException {
        String resourcePath = getResourcePath(responsePath);

        if (responsePath.equals(DEFAULT_RESOURCE_PATH)) {
            return DEFAULT_VALUE.getBytes();
        }

        final URL resource = Objects.requireNonNull(
                getClass().getClassLoader().getResource(resourcePath));
        final Path path = new File(resource.getFile()).toPath();
        return Files.readAllBytes(path);
    }

    private String getResourcePath(String requestTarget) {
        String resourcePath = "static" + requestTarget;
        if (!requestTarget.contains(".")) {
            resourcePath = resourcePath.concat(".html");
        }
        return resourcePath;
    }
}
