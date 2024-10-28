package fr.insee.sabianedata.ws.service.xsl;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import lombok.extern.slf4j.Slf4j;

/**
 * Use for controlling the resolution of includes
 * FIXME we need to urgently change the includes to match a simpler scheme
 * i.e. import statements href are equal to <code>/path/to/resources/directory</code>
 */
@Slf4j
public class ClasspathURIResolver implements URIResolver {

    @Override
    public Source resolve(String href, String base) {
        log.debug("Resolving URI with href: {} and base: {}", href, base);
        String resolvedHref;
        if (href.equals("./utils.xsl")) {
            resolvedHref = href.replaceFirst(".", "/xslt");
            log.debug("Resolved XSLT URI is: {}", resolvedHref);

        } else {
            resolvedHref = href;
            log.debug("Resolved URI href is: {}", resolvedHref);
            return new StreamSource(new File(resolvedHref));
        }
        return new StreamSource(ClasspathURIResolver.class.getResourceAsStream(resolvedHref));
    }

}
