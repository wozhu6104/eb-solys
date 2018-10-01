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

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

import com.elektrobit.ebrace.core.datainput.api.DataStream;
import com.elektrobit.ebrace.core.datainput.tokenizer.ByteArrayDelimiterTokenizer;

public class ByteArrayDelimiterTokenizerTest {

	private static final DataStream stream = new DataStream() {

		@Override
		public String getType() {
			return "String";
		}

		@Override
		public BufferedInputStream getInputStream() throws IOException {
			return new BufferedInputStream(
					new ByteArrayInputStream("123,message1#?234,message2#?345,message3#?".getBytes()));
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

	private static final DataStream streamLineBreaks = new DataStream() {

		@Override
		public String getType() {
			return "String";
		}

		@Override
		public BufferedInputStream getInputStream() throws IOException {
			return new BufferedInputStream(
					new ByteArrayInputStream("123,message1\r\n234,message2\r345,message3\r\n456,message1\n".getBytes()));
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

	@Test
	public void testSimple() throws Exception {
		ByteArrayDelimiterTokenizer tokenizer = new ByteArrayDelimiterTokenizer(new byte[] { '#', '?' });
		tokenizer.setDataStream(stream);
		assertEquals("123,message1", new String(tokenizer.readNextMessage()));
		assertEquals("234,message2", new String(tokenizer.readNextMessage()));
	}

	@Test
	public void testLineBreaks() throws Exception {
		ByteArrayDelimiterTokenizer tokenizer = new ByteArrayDelimiterTokenizer(new byte[] { '\r', '\n' });
		tokenizer.setDataStream(streamLineBreaks);
		assertEquals("123,message1", new String(tokenizer.readNextMessage()));
		assertEquals("234,message2\r345,message3", new String(tokenizer.readNextMessage()));
	}
	
	@Test
	public void testLineBreaks2() throws Exception {
		ByteArrayDelimiterTokenizer tokenizer = new ByteArrayDelimiterTokenizer(new byte[] { '\r', '\n' });
		tokenizer.setDataStream(streamLineBreaks);
		assertEquals("123,message1", new String(tokenizer.readNextMessage()));
		assertEquals("234,message2\r345,message3", new String(tokenizer.readNextMessage()));
		assertEquals("456,message1\n", new String(tokenizer.readNextMessage()));
		
	}

}
