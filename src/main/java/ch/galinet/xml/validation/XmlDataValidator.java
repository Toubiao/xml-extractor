package ch.galinet.xml.validation;

import ch.galinet.xml.extractor.XmlValidationProblem;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.Stax2ReaderAdapter;
import org.codehaus.stax2.validation.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class XmlDataValidator {
    private XMLStreamReader2 reader;
    private CustomValidationProblemHandler validationHandler;

    public XmlDataValidator(InputStream input, URL xsdLocation)
            throws XMLStreamException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        reader = Stax2ReaderAdapter.wrapIfNecessary(xmlInputFactory.createXMLStreamReader(input, "UTF-8"));
        if (xsdLocation != null) {
            XMLValidationSchemaFactory sf = XMLValidationSchemaFactory.newInstance(XMLValidationSchema.SCHEMA_ID_W3C_SCHEMA);
            XMLValidationSchema sv = sf.createSchema(xsdLocation);
            validationHandler = new CustomValidationProblemHandler();
            reader.setValidationProblemHandler(validationHandler);
            reader.validateAgainst(sv);
        }
    }

    public ArrayList<XmlValidationProblem> validate() throws XMLStreamException {
        while (reader.hasNext()) {
                reader.next();
        }
        return validationHandler.getErrors();
    }

    private class CustomValidationProblemHandler implements ValidationProblemHandler {
        ArrayList<XmlValidationProblem> errors = new ArrayList<>();

        @Override
        public void reportProblem(XMLValidationProblem problem) throws XMLValidationException {
            errors.add(handleFromXmlValidationProblem(problem));
        }

        private XmlValidationProblem handleFromXmlValidationProblem(XMLValidationProblem problem){
            return new XmlValidationProblem(problem.getLocation(),problem.getMessage(),problem.getSeverity(),problem.getType());

        }

        public ArrayList<XmlValidationProblem> getErrors() {
            return errors;
        }
    }

}