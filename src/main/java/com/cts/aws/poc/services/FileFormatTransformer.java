/**
 * 
 */
package com.cts.aws.poc.services;

/**
 * @author Azharkhan
 *
 */
public interface FileFormatTransformer<I, O> {

	O transform(I input);
}