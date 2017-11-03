/**
 * 
 */
package com.cts.aws.poc.services.impl;

import iso.std.iso._20022.tech.xsd.pain_001_001.Document;
import iso.std.iso._20022.tech.xsd.pain_001_001.ObjectFactory;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.stereotype.Component;

import com.cts.aws.poc.exceptions.SystemException;
import com.cts.aws.poc.services.InboundFileParser;

/**
 * @author Azharkhan
 *
 */
@Component
public class InboundXMLFileParser implements InboundFileParser<Document> {

	@SuppressWarnings("unchecked")
	public Document parseFile(File file) {
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<Document> jaxbElement = (JAXBElement<Document>) unmarshaller.unmarshal(file);
			Document document = jaxbElement.getValue();
			
			return document;
		} catch (JAXBException e) {
			throw new SystemException("Failed to parse input message", e);
		}
	}
}