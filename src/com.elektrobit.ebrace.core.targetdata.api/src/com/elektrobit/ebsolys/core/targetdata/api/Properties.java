/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api;

import java.util.Set;

/**
 * {@link Properties} is used to provide a generic implementation of an extensible list of information items.
 * {@link Properties} objects contain detail information about entities of the data manager such as
 * {@link com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode} or
 * {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}. A property is a name value pair. Both
 * name and value can be arbitrary Java objects. Every provider package is responsible for managing and providing the
 * properties objects for its own entities. The
 * {@link com.elektrobit.ebsolys.core.targetdata.api.structure.StructureProvider} manages the properties regarding the
 * structure of a {@link com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode}.
 * 
 * @author rage2903
 * @version 11.06
 */
public interface Properties
{

    /**
     * Returns the value that belongs to the given key.
     * 
     * @param key
     *            whose value should be returned.
     * @return The value that belongs to the given key.
     */
    Object getValue(Object key);

    /**
     * Returns the description of given key.
     * 
     * @param key
     *            The key whose description should be returned.
     * @return The description of the given key.
     */
    String getDescription(Object key);

    /**
     * Returns all keys of this instance of {@link Properties} as {@link Set}.
     * 
     * @return Returns all keys as {@link Set}.
     */
    Set<Object> getKeys();

    /**
     * Adds a {@link PropertyChangedListener} to this {@link Properties}, which get informed, if a any property of this
     * {@link Properties} was added, removed or changed.
     * 
     * @param listener
     *            The {@link PropertyChangedListener}, which want to get informed, if a any property of this
     *            {@link Properties} was added, removed or changed.
     * @return true, if the {@link PropertyChangedListener} could be added, false if not.
     */
    boolean addPropertyChangedListener(PropertyChangedListener listener);

    /**
     * Removes a {@link PropertyChangedListener} from this {@link Properties}. After that, the
     * {@link PropertyChangedListener} won't get informed about properties changes.
     * 
     * @param listener
     *            The {@link PropertyChangedListener}, which should be removed.
     */
    void removePropertyChangedListener(PropertyChangedListener listener);

}
