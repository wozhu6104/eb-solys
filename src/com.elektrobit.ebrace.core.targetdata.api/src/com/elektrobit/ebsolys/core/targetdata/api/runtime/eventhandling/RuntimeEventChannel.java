/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling;

import java.util.List;

/**
 * A RuntimeEventChannel is supposed to contain RuntimeEvents of a certain type. Hence a RuntimeChannel is typed either.
 * 
 * @param <T>
 *            The type of this runtime channel.
 * 
 * @see RuntimeEvent
 * @see RuntimeEventProvider
 * 
 * @author pedu2501@elektrobit.com
 */
public interface RuntimeEventChannel<T>
{
    public enum CommonParameterNames {
        NAME("Name"), LOG_LEVEL("Log Level"), TYPE("Type"), COLOR("Color"), TRACE_STATUS("Trace Status");

        private final String name;

        CommonParameterNames(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    /**
     * Gets the name of this Runtime Channel.
     * 
     * @return The name.
     */
    public String getName();

    /**
     * Gets the description of this Runtime Channel.
     * 
     * @return The description of this runtime channel.
     */
    public String getDescription();

    /**
     * Gets the typed Unit of channel. Only values of getUnit().getType() type can be added to channel.
     * 
     * @return
     */
    public Unit<T> getUnit();

    /**
     * Return the value columns of this Runtime Event Channel.
     */
    public List<String> getValueColumnNames();

    public Object getParameter(String key);

    public List<String> getParameterNames();

}
