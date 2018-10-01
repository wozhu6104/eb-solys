/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.resources.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class ResourcesModelManagerTest
{
    private final GenericOSGIServiceTracker<ResourcesModelManager> resourceManagerTracker = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class );

    @Test
    public void createTimeLineViewSuccessful()
    {
        ResourcesModelManager resourcesModelManager = resourceManagerTracker.getService();
        String name = "Timeline View Test Name";
        resourcesModelManager.createTimelineView( name );

        List<ResourceModel> charts = resourcesModelManager.getTimelineViews();
        ResourceModel createdChart = charts.get( 0 );

        assertEquals( name, createdChart.getName() );
        assertTrue( createdChart instanceof TimelineViewModel );
    }

}
