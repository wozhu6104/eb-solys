/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.impl.importer.json;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.importer.JsonEventHandler;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;

@Component
public class JsonEventHandlerImpl implements JsonEventHandler
{
    private RuntimeEventAcceptor runtimeEventAcceptor;
    private StructureAcceptor structureAcceptor;
    private ComRelationAcceptor comRelationAcceptor;
    private TimeSegmentAcceptorService timeSegmentAcceptor;

    private JsonToEvent handler;

    public JsonEventHandlerImpl()
    {

    }

    @Override
    public void handle(String jsonEvent)
    {
        handler.handle( jsonEvent );
    }

    @Activate
    public void activate()
    {
        handler = new JsonToEvent( runtimeEventAcceptor,
                                   structureAcceptor,
                                   comRelationAcceptor,
                                   timeSegmentAcceptor,
                                   "" );
    }

    @Reference
    public void bindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }

    @Reference
    public void bindStructureAcceptor(StructureAcceptor structureAcceptor)
    {
        this.structureAcceptor = structureAcceptor;
    }

    public void unbindStructureAcceptor(StructureAcceptor runtimeEventAcceptor)
    {
        this.structureAcceptor = null;
    }

    @Reference
    public void bindComRelationAcceptor(ComRelationAcceptor comRelationAcceptor)
    {
        this.comRelationAcceptor = comRelationAcceptor;
    }

    public void unbindComRelationAcceptor(ComRelationAcceptor comRelationAcceptor)
    {
        this.comRelationAcceptor = null;
    }

    @Reference
    public void bindTimeSegmentAcceptor(TimeSegmentAcceptorService timeSegmentAcceptor)
    {
        this.timeSegmentAcceptor = timeSegmentAcceptor;
    }

    public void unbindTimeSegmentAcceptor(TimeSegmentAcceptorService timeSegmentAcceptor)
    {
        this.timeSegmentAcceptor = null;
    }
}
