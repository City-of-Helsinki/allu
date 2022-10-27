package fi.hel.allu.common.wfs;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.io.StringReader;
import java.util.Base64;

/**
 * Utility methods for using WFS.
 */
public class WfsUtil {

    private static final Logger logger = LoggerFactory.getLogger(WfsUtil.class);

    public static <T> T unmarshalWfs(String wfsXml, Class<T> unmarshalClass) {
        try {
            JAXBContext jc = JAXBContext.newInstance(unmarshalClass);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(wfsXml));
        } catch (JAXBException e) {
            logger.error("Unexpected exception while parsing WFS response\n{}", wfsXml);
            throw new RuntimeException(e);
        }
    }

    public static HttpHeaders createAuthHeaders(String wfsUsername, String wfsPassword) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        final String auth = wfsUsername + ":" + wfsPassword;
        final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        final String authHeader = "Basic " + new String(encodedAuth);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, authHeader);
        return httpHeaders;
    }
}
