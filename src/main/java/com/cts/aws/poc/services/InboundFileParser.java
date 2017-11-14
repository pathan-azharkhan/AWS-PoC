/**
 * 
 */
package com.cts.aws.poc.services;

import java.io.File;

/**
 * @author Azharkhan
 *
 */
public interface InboundFileParser<T> {

	T parseFile(File file);
}