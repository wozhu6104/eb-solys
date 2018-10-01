/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer.csvimporter;

import static org.junit.Assert.*;

import org.junit.Test;
import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.targetdata.importer.internal.csvimporter.CsvToJsonTransformer;

public class CsvToJsonTransformerTest {

	@Test
	public void transform() {
		String input = "1234,12.0,numeric channel";
		String output = "{\"summary\":\"12.0\",\"channel\":\"numeric channel\",\"value\":\"12.0\",\"uptime\":\"1234\"}";
		CsvToJsonTransformer transformer = new CsvToJsonTransformer();
		transformer.acquireMetaData(null, ".");
		
		assertEquals(output, transformer.transformEvent(input));
	}
	
	
	@Test
	public void transformWithHint() {
		String input = "1234;12.0";
		String output = "{\"summary\":\"12.0\",\"channel\":\"trace.csv\",\"value\":\"12.0\",\"uptime\":\"1234\"}";
		CsvToJsonTransformer transformer = new CsvToJsonTransformer();
		transformer.acquireMetaData("timestamp;value", FileHelper.getBundleRootFolderOfClass(getClass()));
		
		assertEquals(output, transformer.transformEvent(input));
	}

}
