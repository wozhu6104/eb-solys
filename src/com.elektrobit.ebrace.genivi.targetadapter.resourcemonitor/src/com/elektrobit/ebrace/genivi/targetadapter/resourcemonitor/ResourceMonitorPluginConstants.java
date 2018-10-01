/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor;

public interface ResourceMonitorPluginConstants
{
    public static final String PLUGIN_ID = "com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor";

    public static final String RESOURCE_MONITOR_ENABLE_KEYWORD = "ENABLE_RESOURCE_MONITOR";
    public static final boolean RESOURCE_MONITOR_ENABLE_DEFAULT_VALUE = true;

    public static final String GENIVI_TARGET_TIMESTAMP_PROVIDER_KEY = "GENIVI_TARGET_TIMESTAMP_PROVIDER";
}
