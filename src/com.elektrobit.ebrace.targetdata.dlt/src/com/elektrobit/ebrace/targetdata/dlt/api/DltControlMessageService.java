/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.dlt.api;

public interface DltControlMessageService
{
    public void setLogLevel(String ecuId, String appIdSrc, String ctxIdSrc, long timestamp, String appId, String ctxId,
            int logLevel);

    public void setTraceStatus(String ecuId, String appIdSrc, String ctxIdSrc, long timestamp, String appId,
            String ctxId, int traceStatus);

    public void getLogInfo(String ecuId, String appIdSrc, String ctxIdSrc, long timestamp, int options, String appId,
            String ctxId);
}
