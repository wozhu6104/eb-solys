/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.structure;

/**
 * Listener interface to get notified if a {@link Tree} was added or removed from the structure.
 */
public interface StructureModificationListener
{
    /**
     * Listener interface that is called if a new {@link Tree} was added to the structure.
     * 
     * @param t
     *            The {@link Tree} which was added.
     */
    void onTreeCreated(Tree t);

    /**
     * Listener interface that is called if a new {@link Tree} was removed from the structure.
     * 
     * @param t
     *            The {@link Tree} which was removed.
     */
    void onTreeRemoved(Tree t);
}
