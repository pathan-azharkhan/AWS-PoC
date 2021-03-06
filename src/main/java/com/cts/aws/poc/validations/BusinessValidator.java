/**
 * 
 */
package com.cts.aws.poc.validations;

import com.cts.aws.poc.exceptions.ValidationException;

/**
 * @author Azharkhan
 *
 */
public interface BusinessValidator<T> {

	boolean validate(T input) throws ValidationException;
}