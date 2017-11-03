/**
 * 
 */
package com.cts.aws.poc.validations;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.cts.aws.poc.exceptions.ValidationException;

/**
 * @author Azharkhan
 *
 */
@Component
public class ProprietoryFormatValidator implements FormatValidator<String> {

	@Override
	public boolean validate(String input) throws ValidationException {
		
		if (StringUtils.isBlank(input))
			throw new ValidationException("Generated outbound message is blank");
		
		String[] lines = input.split("\n");
		
		if (lines.length < 2) 
			throw new ValidationException("Generated outbound message should contain at least one header and one txn record");
		
		Set<String> errors = new HashSet<>();
		String header = lines[0];
		
		if (StringUtils.countMatches(header, "|") != 3)
			errors.add("line:1 -> Number of columns in a header record should be exactly 4");
		else {
			
			String[] columns = header.split("\\|");
			
			for (int i = 0; i < columns.length; i++) {
				
				if (StringUtils.isBlank(columns[i]) || "null".equals(columns[i]))
					errors.add("line:1, col:" + (i + 1) + " -> Invalid value specified");
			}
		}
		
		for (int i = 1; i < lines.length; i++) {
			
			String txnRecord = lines[i];
			
			if (StringUtils.countMatches(txnRecord, "|") != 7)
				errors.add("line:" + (i + 1) + " -> Number of columns in a transaction record should be exactly 8");
			else {
				
				String[] columns = header.split("\\|");
				
				for (int j = 0; j < columns.length; j++) {
					
					if (StringUtils.isBlank(columns[j]) || "null".equals(columns[j]))
						errors.add("line:" + (i + 1) + ", col:" + (j + 1) + " -> Invalid value specified");
				}
			}
		}
		
		if (CollectionUtils.isEmpty(errors))
			return true;
		else
			throw new ValidationException("Format validation of generated file failed", errors);
	}
}