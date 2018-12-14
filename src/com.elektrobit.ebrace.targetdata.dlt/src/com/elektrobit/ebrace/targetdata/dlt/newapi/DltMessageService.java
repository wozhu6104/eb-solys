/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.newapi;

import java.io.BufferedInputStream;
import java.io.IOException;

public interface DltMessageService
{
    String createEvent(byte[] message);

    byte[] tokenizeNextMessageStreamHeader(BufferedInputStream stream) throws IOException;

    byte[] tokenizeNextMessageFileHeader(BufferedInputStream stream) throws IOException;
}
