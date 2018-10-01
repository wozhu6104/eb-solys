/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.datainput;

import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

import com.elektrobit.ebrace.core.datainput.api.DataStream;
import com.elektrobit.ebrace.core.datainput.api.DataStreamParser;
import com.elektrobit.ebrace.core.datainput.api.DataInputService;
import com.elektrobit.ebrace.core.datainput.api.DataStreamTokenizer;
import com.elektrobit.ebrace.core.datainput.service.DataInputServiceImpl;
import com.elektrobit.ebrace.core.datainput.tokenizer.ByteArrayDelimiterTokenizer;

public class DataStreamServiceTest {

	private static final String jsonDescription = "resources/datainputs.json";

	private static final DataStream stringStream = new DataStream() {

		@Override
		public String getType() {
			return "String";
		}

		@Override
		public BufferedInputStream getInputStream() throws IOException {
			return new BufferedInputStream(
					new ByteArrayInputStream("123,message1#234,message2#345,message3#".getBytes()));
		}

		@Override
		public String getImplementationDetails() {
			return "{}";
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public void open() throws Exception {
		}
	};

	private static final DataStream fileStream = new DataStream() {

		@Override
		public String getType() {
			return "File";
		}

		@Override
		public BufferedInputStream getInputStream() throws IOException {
			return new BufferedInputStream(new FileInputStream(new File("resources/testFile.txt")));
		}

		@Override
		public String getImplementationDetails() {
			return "{}";
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public void open() throws Exception {
		}
	};

	private static final DataStreamParser parser = new DataStreamParser() {

		@Override
		public String parse(byte[] message) {
			String[] parts = (new String(message)).split(",");
			if (parts.length == 2) {
				String result = "{\"uptime\":" + parts[0] + ",\"channel\":\"arduino\",\"summary\":" + parts[1] +",\"value\":" + parts[1] +"}";
				System.out.println(result);
				return result;
			} else {
				return "";
			}
		}

		@Override
		public String getId() {
			return "SimpleCommaParser";
		}
	};

	@Test
	public void createDataStream() throws IOException {
		DataInputServiceImpl service = new DataInputServiceImpl();
		service.loadDataInputDescriptionsFromFile(jsonDescription);
		assertTrue(service.getDataInputs().contains("FirstTarget"));
	}

	@Test
	public void startReading() {
		DataInputService service = new DataInputServiceImpl();
		try {
			DataStreamTokenizer tokenizer = new ByteArrayDelimiterTokenizer(new byte[] { '#' });
			tokenizer.setDataStream(stringStream);
			service.createDataInputOnTheFly("TestDataInput", stringStream,
					tokenizer, parser);
			service.startReading("TestDataInput");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void readFile() {
		DataInputService service = new DataInputServiceImpl();
		try {
			DataStreamTokenizer tokenizer = new ByteArrayDelimiterTokenizer(new byte[] { '\r', '\n' });
			tokenizer.setDataStream(fileStream);
			service.createDataInputOnTheFly("TestDataInput", fileStream,
					tokenizer, parser);
			service.startReading("TestDataInput");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
