/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.comrelation;

import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * This class is the counter part to {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider}
 * and extends this class by the possibility to change the relations of the internal structure of the DataManager.
 * 
 * @author rage2903
 * @version 11.05
 */
public interface ComRelationAcceptor extends ComRelationProvider
{
    /**
     * Adds a new {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} to the internal structure
     * of the DataManager.
     * 
     * @param sender
     *            The sender of the new added {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}
     * @param receiver
     *            The receiver of the new added
     *            {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}
     * @param name
     *            The name of the new added {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}
     * @return the new added {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} or null if it
     *         already exists.
     */
    ComRelation addComRelation(TreeNode sender, TreeNode receiver, String name);

    /**
     * Adds a new {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} to the internal structure
     * of the DataManager and returns it.
     * 
     * @param sender
     *            The sender of the new added {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}
     * @param receiver
     *            The receiver of the new added
     *            {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}
     * @param name
     *            The name of the new added {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}
     * @return the new added {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}.
     */
    ComRelation createOrGetComRelation(TreeNode sender, TreeNode receiver, String name);

    /**
     * Removes a {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} from the DataManager.
     * 
     * @param comRelation
     *            the {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} which should be deleted
     */
    void removeComRelation(ComRelation comRelation);

    /**
     * Calls the associate action with this {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}.
     * 
     * For example, if two software components have a
     * {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}, that means one software components
     * calls a method of the connected component, then this method has to executed, if such a call happens.
     * 
     * @param comRelation
     *            the {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}, that has happened.
     */
    void doComRelation(ComRelation comRelation);

    /**
     * Reverts the latest action of this {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}.
     * 
     * @param comRelation
     *            the {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}, whose latest action
     *            should be reverted.
     */
    void undoComRelation(ComRelation comRelation);

    /**
     * Adds a property to the {@link Properties} of the given {@link ComRelation}. Adding is only possible, if the given
     * key isn't already available.
     * 
     * @param comRelation
     *            The {@link ComRelation} which the property belongs to. Null value isn't allowed.
     * @param key
     *            The key of this property. Null value isn't allowed.
     * @param value
     *            The value of this property
     * @param description
     *            The description of this property
     * @return true, if adding was successful, else false.
     */
    boolean addProperty(ComRelation comRelation, Object key, Object value, String description);

    /**
     * Changes the property with the given key.
     * 
     * @param comRelation
     *            The {@link ComRelation} which the property belongs to. Null value isn't allowed.
     * @param key
     *            The key of this property. Null value isn't allowed.
     * @param value
     *            The value of this property
     * @param description
     *            The description of this property
     * @return
     */
    boolean changeProperty(ComRelation comRelation, Object key, Object value, String description);

    /**
     * Removes the property with the given key
     * 
     * @param comRelation
     *            The {@link ComRelation} which the property belongs to. Null value isn't allowed.
     * @param key
     *            The key of this property which should be removed. Null value isn't allowed.
     */
    void removeProperty(ComRelation comRelation, Object key);
}
