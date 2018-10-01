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

/**
 * This interface has to be implemented, if you want to get informed, if a element of
 * {@link com.elektrobit.ebsolys.core.targetdata.api.Properties} is added, removed or modified. Before you get informed,
 * you have to register as {@link com.elektrobit.ebsolys.core.targetdata.api.PropertyChangedListener} .
 * 
 * If you don't want to get informed anymore, you just unregister.
 */
public interface PropertyChangedListener
{
    /**
     * The method which is called, if a new property was added.
     * 
     * @param structureObject
     *            - The structure object, e.g. the TreeNode or the ComRelation, this property belongs to.
     * @param key
     *            - The key of the new property.
     */
    void added(Object structureObject, Object key);

    /**
     * The method which is called, if a property was changed.
     * 
     * @param structureObject
     *            - The structure object, e.g. the TreeNode or the ComRelation, this property belongs to.
     * @param key
     *            - The key of the changed property.
     */
    void changed(Object structureObject, Object key);

    /**
     * The method which is called, if a property was removed.
     * 
     * @param structureObject
     *            - The structure object, e.g. the TreeNode or the ComRelation, this property belongs to.
     * @param key
     *            - The key of the removed property.
     */
    void removed(Object structureObject, Object key);
}
