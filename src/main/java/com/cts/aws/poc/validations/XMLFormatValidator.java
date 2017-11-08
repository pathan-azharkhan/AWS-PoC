/**
 * 
 */
package com.cts.aws.poc.validations;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cts.aws.poc.exceptions.ValidationException;

/**
 * @author Azharkhan
 *
 */
@Component
public class XMLFormatValidator implements FormatValidator<File> {

	private static final String XML_SCHEMA = "pain.001.001.03.xsd";
	
	public boolean validate(File file) throws ValidationException {

		// parse an XML document into a DOM tree
		DocumentBuilder parser;

		Document document;
		Schema schema;
		try {
			parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = parser.parse(file);

			// create a SchemaFactory capable of understanding WXS schemas
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

			// load a WXS schema, represented by a Schema instance
			Source schemaFile = new StreamSource(new File(XML_SCHEMA));
			schema = factory.newSchema(schemaFile);

			// create a Validator instance, which can be used to validate an instance document
			Validator validator = schema.newValidator();
			
			Set<String> validationErrors = new HashSet<>();
			
			validator.setErrorHandler(new SchemaValidationErrorHandler(validationErrors));

			// validate the DOM tree
			validator.validate(new DOMSource(document));
			
			if (CollectionUtils.isEmpty(validationErrors))
				return true;
			else
				throw new ValidationException("Format Validation failed for input XML", validationErrors);
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ValidationException("Failed in Format Validation of input XML", e);
		}
	}
}

class SchemaValidationErrorHandler implements ErrorHandler {
	
	private final Set<String> validationErrors;

	SchemaValidationErrorHandler(Set<String> validationErrors) {
		
		super();
		Assert.notNull(validationErrors, "Argument to this Constructor cannot be null");
		this.validationErrors = validationErrors;
	}

	public void warning(SAXParseException exception) {
		// Do nothing
	}

	public void error(SAXParseException exception) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(exception.getLineNumber()).append(":").append(exception.getColumnNumber()).append(exception.getMessage());
		
		validationErrors.add(builder.toString());
	}

	public void fatalError(SAXParseException exception) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(exception.getLineNumber()).append(":").append(exception.getColumnNumber()).append(exception.getMessage());
		
		validationErrors.add(builder.toString());
	}
}