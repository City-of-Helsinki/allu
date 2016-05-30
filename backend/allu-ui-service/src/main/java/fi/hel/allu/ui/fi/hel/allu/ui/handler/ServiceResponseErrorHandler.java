package fi.hel.allu.ui.fi.hel.allu.ui.handler;

import fi.hel.allu.ui.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ServiceResponseErrorHandler implements ResponseErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServiceResponseErrorHandler.class);


    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        if (clientHttpResponse.getStatusCode() != HttpStatus.OK) {
            logger.debug("Status code: " + clientHttpResponse.getStatusCode());
            logger.debug("Response" + clientHttpResponse.getStatusText());
            return true;
        }
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        if (clientHttpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            logger.debug(HttpStatus.NOT_FOUND + " response. Throwing not found exception");
            throw new NotFoundException(clientHttpResponse.getStatusText());
        }
    }
}
