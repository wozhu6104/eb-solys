/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.decoder.common.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface UmlServiceConstants
{
    String TREE_LAYER_TYPE = "treeLayerType";

    String DBUS_TREE_LAYER_TYPE = "DBusTreeLayerType";
    String[] DBUS_TREE_LAYERS = new String[]{"DBusCallAnalysis", "DBusProcesses", "DBusServices", "DBusObjects",
            "DBusInterfaces", "DBusMembers"};
    Set<String> DBUS_TREE_LAYER_SET = new HashSet<String>( Arrays.asList( DBUS_TREE_LAYERS ) );

    String EVM_TREE_LAYER_TYPE = "EvmTreeLayerType";
    String[] EVM_TREE_LAYERS = new String[]{"EvmAnalysis", "EvmService"};
    Set<String> EVM_TREE_LAYER_SET = new HashSet<String>( Arrays.asList( EVM_TREE_LAYERS ) );

    String WM_TREE_LAYER_TYPE = "WmTreeLayerType";
    String[] WM_TREE_LAYERS = new String[]{"WMAnalysis", "WMService"};
    Set<String> WM_TREE_LAYER_SET = new HashSet<String>( Arrays.asList( WM_TREE_LAYERS ) );

    String SINGLETON_GATEWAY_TREE_LAYER_TYPE = "SingletonGatewayTreeLayerType";
    String[] SINGLETON_GATEWAY_TREE_LAYERS = new String[]{"SingletonGatewayAnalysis", "SingletonGatewayService"};
    Set<String> SINGLETON_GATEWAY_TREE_LAYER_SET = new HashSet<String>( Arrays
            .asList( SINGLETON_GATEWAY_TREE_LAYERS ) );

}
