/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.api;


public interface TargetConnection
{
    public void setTargetConnectionDownListener(TargetConnectionDownListener listener);

    public void unsetTargetConnectionDownListener(TargetConnectionDownListener listener);

    public boolean connect();

    public boolean disconnect();

    public void resumeReading();

    public void pauseReading();

    public boolean sendMessage(OutgoingMessage message);
}
