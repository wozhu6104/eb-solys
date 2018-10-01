/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.view;

import java.util.List;

import org.eclipse.jface.viewers.ColumnViewer;

/**
 * Interface for Views which contain a table viewer. All View Classes which need to contain a table viewer have to
 * implement this interface.
 */
public interface ITableViewerView
{
    /**
     * Returns the table viewer contained in the view.
     * 
     * @return the table viewer.
     */
    ColumnViewer getTreeViewer();

    List<?> getContent();
}
