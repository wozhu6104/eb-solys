/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.resources.model.chart;

public enum ChartTypes {

    GANTT_CHART("GanttChart", 0, Boolean.class), LINE_CHART("LineChart", 1, Number.class);

    String name;
    int value;
    Class<?> assignableType;

    private ChartTypes(String name, int value, Class<?> assignableDataType)
    {
        this.name = name;
        this.value = value;
        this.assignableType = assignableDataType;
    }

    public String getName()
    {
        return this.name;
    }

    public Class<?> getAssignableType()
    {
        return assignableType;
    }
}
