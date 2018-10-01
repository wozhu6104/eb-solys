/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.franca.mapper.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.franca.core.franca.FModel;
import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.ServiceConstants;
import com.elektrobit.ebrace.dbus.decoder.api.DBusDecodedRuntimeEvent;
import com.elektrobit.ebrace.dbus.decoder.api.DBusDecoderConstants;
import com.elektrobit.ebrace.franca.common.franca.mapper.api.FrancaDBusDecodedRuntimeEvent;
import com.elektrobit.ebrace.franca.common.franca.modelloader.api.DefaultFrancaModelFactory;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;

@Component(property = {ServiceConstants.CHANNEL_NAME + "=" + DBusDecoderConstants.DBUS_SESSIONBUS,
        ServiceConstants.CHANNEL_NAME + "=" + DBusDecoderConstants.DBUS_SYSTEMBUS})
public class DefaultDBusDecoderService implements DecoderService
{
    private static Logger LOG = Logger.getLogger( DefaultDBusDecoderService.class );
    private static List<FModel> francaModels = new DefaultFrancaModelFactory().getFrancaModels();

    @Override
    public DecodedRuntimeEvent decode(RuntimeEvent<?> event)
    {
        DBusDecodedRuntimeEvent dBusDecodedRuntimeEvent = new DBusDecodedRuntimeEvent( event );
        FrancaDBusDecodedRuntimeEvent francaDBusDecodedRuntimeEvent = tryToMapFrancaToDBusMessage( dBusDecodedRuntimeEvent );

        if (francaDBusDecodedRuntimeEvent != null)
        {
            return francaDBusDecodedRuntimeEvent;
        }
        else
        {
            return dBusDecodedRuntimeEvent;
        }
    }

    private FrancaDBusDecodedRuntimeEvent tryToMapFrancaToDBusMessage(DBusDecodedRuntimeEvent dBusDecodedRuntimeEvent)
    {
        FrancaDBusDecodedRuntimeEvent francaDBusDecodedRuntimeEvent = null;
        try
        {
            francaDBusDecodedRuntimeEvent = new FrancaDBusDecodedRuntimeEvent( francaModels, dBusDecodedRuntimeEvent );
        }
        catch (Exception e)
        {
            LOG.warn( "Couldn't map DBus message " + dBusDecodedRuntimeEvent.getSummary()
                    + " to Franca. Returning decoded DBus message." );
            LOG.info( "DBus message was: " + dBusDecodedRuntimeEvent.toString() );
        }
        return francaDBusDecodedRuntimeEvent;
    }
}
