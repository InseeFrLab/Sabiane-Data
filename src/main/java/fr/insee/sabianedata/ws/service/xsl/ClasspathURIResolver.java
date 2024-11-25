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
        if (href.equals("./utils.xsl")) {
            String resolvedHref = href.replaceFirst(".", "/xslt");
            return new StreamSource(ClasspathURIResolver.class.getResourceAsStream(resolvedHref));
        }
        return new StreamSource(new File(href));
    }

}
