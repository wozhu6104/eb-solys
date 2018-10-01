/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dbus.decoder.api;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;

public class SignatureSummaryBuilder
{

    public static String createSummary(DecodedTree tree)
    {
        String summary = "";
        DecodedNode signatureNode = tree.getRootNode().getChildren().get( 0 );
        summary += signatureNode.getName();
        summary += "(";
        for (DecodedNode nextParamNode : signatureNode.getChildren())
        {
            if (nextParamNode.getName().equals( DBusDecoderConstants.MESSAGE_TYPE ))
            {
                continue;
            }
            else
            {
                summary += nextParamNode.getName() + ",";
            }
        }

        summary = replaceLastComma( summary );

        summary += ")";

        return summary;
    }

    private static String replaceLastComma(String summary)
    {
        if (summary.endsWith( "," ))
        {
            return summary.substring( 0, summary.length() - 1 );
        }

        return summary;
    }

}
