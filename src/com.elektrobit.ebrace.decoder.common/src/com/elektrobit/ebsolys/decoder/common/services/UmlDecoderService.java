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

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

/**
 * Decoder service for the string representation of a list of runtime events, which is used for the generation of uml
 * diagrams for the call hierarchy of the events in the list.
 */
public interface UmlDecoderService
{
    String getSequenceChartSummary(List<RuntimeEvent<?>> runtimeEvents, String treeLevel);
}
