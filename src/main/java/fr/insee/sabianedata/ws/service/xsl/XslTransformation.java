package fr.insee.sabianedata.ws.service.xsl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * Main Saxon Service used to perform XSLT transformations
 *
 * @author gerose
 */
@Service
@Slf4j
public class XslTransformation {

    private final TransformerFactory tFactory = TransformerFactory.newInstance();

    private void xslTransform(Transformer transformer, InputStream xmlInput, OutputStream xmlOutput) throws Exception {
        try {

        transformer.transform(new StreamSource(xmlInput), new StreamResult(xmlOutput));
        }catch (Exception e){
            log.warn("Error when transforming",e);
            throw new Exception("Transformation failed");
        }
    }

    public void transformFods2XML(InputStream inputFile, OutputStream outputFile, InputStream xslSheet) throws Exception {

        log.info("TransformerFactory: {} " , tFactory.getClass().getName());
        Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
        xslTransform(transformer, inputFile, outputFile);
    }

}
