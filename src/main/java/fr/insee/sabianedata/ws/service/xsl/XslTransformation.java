package fr.insee.sabianedata.ws.service.xsl;

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
public class XslTransformation {


    public void xslTransform(Transformer transformer, InputStream xmlInput, OutputStream xmlOutput) throws Exception {
        transformer.transform(new StreamSource(xmlInput), new StreamResult(xmlOutput));
    }

    public void transformFods2XML(InputStream inputFile, OutputStream outputFile, InputStream xslSheet) throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
        xslTransform(transformer, inputFile, outputFile);
    }

}
