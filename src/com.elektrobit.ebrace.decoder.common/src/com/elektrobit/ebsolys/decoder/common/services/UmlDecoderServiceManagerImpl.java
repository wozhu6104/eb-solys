/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.decoder.common.services;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.decoder.common.api.UmlDecoderServiceManager;
import com.elektrobit.ebsolys.decoder.common.api.UmlServiceConstants;

public final class UmlDecoderServiceManagerImpl implements UmlDecoderServiceManager
{
    private static UmlDecoderServiceManagerImpl INSTANCE = new UmlDecoderServiceManagerImpl();
    private final Map<String, UmlDecoderService> umlDecoderServiceCache = new HashMap<String, UmlDecoderService>();

    private UmlDecoderServiceManagerImpl()
    {
    }

    public static synchronized UmlDecoderServiceManager getInstance()
    {
        return INSTANCE;
    }

    private UmlDecoderService getUmlDecoderServiceForTreeLevelFromOSGIRegistry(String treeLevel)
    {
        if (treeLevel == null)
        {
            return null;
        }
        Map<Object, Properties> listOfServices = new GenericOSGIServiceTracker<UmlDecoderService>( UmlDecoderService.class )
                .getServices( UmlDecoderService.class.getName() );
        for (Object service : listOfServices.keySet())
        {
            if (service instanceof UmlDecoderService)
            {
                Dictionary<?, ?> serviceProps = listOfServices.get( service );
                String treeLayerType = (String)serviceProps.get( UmlServiceConstants.TREE_LAYER_TYPE );
                if (treeLayerType != null && !treeLayerType.isEmpty())
                {
                    if (treeLayerType.equals( UmlServiceConstants.EVM_TREE_LAYER_TYPE )
                            && UmlServiceConstants.EVM_TREE_LAYER_SET.contains( treeLevel ))
                    {
                        return (UmlDecoderService)service;
                    }
                    if (treeLayerType.equals( UmlServiceConstants.WM_TREE_LAYER_TYPE )
                            && UmlServiceConstants.WM_TREE_LAYER_SET.contains( treeLevel ))
                    {
                        return (UmlDecoderService)service;
                    }
                    if (treeLayerType.equals( UmlServiceConstants.DBUS_TREE_LAYER_TYPE )
                            && UmlServiceConstants.DBUS_TREE_LAYER_SET.contains( treeLevel ))
                    {
                        return (UmlDecoderService)service;
                    }
                    if (treeLayerType.equals( UmlServiceConstants.SINGLETON_GATEWAY_TREE_LAYER_TYPE )
                            && UmlServiceConstants.SINGLETON_GATEWAY_TREE_LAYER_SET.contains( treeLevel ))
                    {
                        return (UmlDecoderService)service;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public UmlDecoderService getUmlDecoderServiceForTreeLevel(String treeLevel)
    {
        UmlDecoderService umlDecoderService = umlDecoderServiceCache.get( treeLevel );

        if (umlDecoderService == null)
        {
            umlDecoderService = getUmlDecoderServiceForTreeLevelFromOSGIRegistry( treeLevel );
            if (umlDecoderService != null)
            {
                umlDecoderServiceCache.put( treeLevel, umlDecoderService );
            }
        }

        return umlDecoderService;
    }

}
