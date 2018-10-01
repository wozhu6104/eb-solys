/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.franca.common.franca.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.franca.core.franca.FArrayType;
import org.franca.core.franca.FBasicTypeId;
import org.franca.core.franca.FCompoundType;
import org.franca.core.franca.FEnumerationType;
import org.franca.core.franca.FEnumerator;
import org.franca.core.franca.FField;
import org.franca.core.franca.FIntegerConstant;
import org.franca.core.franca.FMapType;
import org.franca.core.franca.FStringConstant;
import org.franca.core.franca.FStructType;
import org.franca.core.franca.FType;
import org.franca.core.franca.FTypeDef;
import org.franca.core.franca.FTypeRef;
import org.franca.core.franca.FTypedElement;
import org.franca.core.franca.FUnionType;

import com.elektrobit.ebrace.protobuf.messagedefinitions.TargetAgentProtocolDBusTAProto.DBusParamType;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;

public class FrancaFTypeHandler
{

    private static final Logger LOG = Logger.getLogger( FrancaFTypeHandler.class );

    public void handleArrayType(FType fType, DecodedNode node, FTypedElement arg)
    {
        FArrayType type = ((FArrayType)fType);
        handleArgsForFType( node, type.getElementType(), type.getName(), arg );
    }

    public void handleMapType(FType fType, DecodedNode node, FTypedElement arg)
    {
        FMapType type = ((FMapType)fType);
        node.setName( type.getName() );
        // handle key, is always primitive
        handlePrimitiveType( type.getKeyType(), node, type.getName(), arg );

        if (!node.getChildren().isEmpty())
        {
            // handle value
            handleArgsForFType( node.getChildren().get( 1 ), type.getValueType(), type.getName(), arg );
        }
    }

    public void handleMapType(String name, FTypeRef keyType, FTypeRef valueType, DecodedNode node, FTypedElement arg)
    {
        node.setName( name );
        // handle key, is always primitive
        handlePrimitiveType( keyType, node, name, arg );

        // handle value
        handleArgsForFType( node.getChildren().get( 1 ), valueType, name, arg );
    }

    public void handleArrayPropertyType(String typeName, List<FField> fields, DecodedNode node, FTypedElement arg,
            int index)
    {
        if (node.getParentNode() != null)
        {
            node.setName( typeName + "[" + index + "]" );
        }
        List<DecodedNode> childrenOfChildren = node.getChildren();
        int count = 0;
        for (FField fField : fields)
        {
            String fFieldName = fField.getName();
            if (!childrenOfChildren.isEmpty())
            {
                DecodedNode n = childrenOfChildren.get( count );
                n.setName( fFieldName );
                handleArgsForFType( n, fField.getType(), fFieldName, fField );
                count++;
            }
            else
            {
                handlePrimitiveType( fField.getType(), node, fFieldName, fField );
            }
        }

    }

    public void handleArgsForFType(DecodedNode node, FTypeRef typeRef, String name, FTypedElement arg)
    {
        if (typeRef != null && isDerived( typeRef ))
        {
            if (node.getChildren().isEmpty())
            {
                handlePrimitiveType( typeRef, node, name, arg );
            }
            else
            {
                handleArgssForDerivedFType( node, typeRef.getDerived(), name, arg );
            }
        }
        else
        {
            handlePrimitiveType( typeRef, node, name, arg );
        }
    }

    public void handleFCompoundType(FType fType, DecodedNode node, FTypedElement fTypedElement, int index)
    {
        FCompoundType type = ((FCompoundType)fType);
        int c = 0;
        for (FField fField : type.getElements())
        {
            if (index == c)
            {
                node.setName( fField.getName() );
            }
            if (isDerived( fField.getType() ))
            {
                handleArgsForFType( node, fField.getType(), fField.getName(), fField );
            }
            c++;
        }
    }

    public void handlePrimitiveType(FTypeRef fTypeRef, DecodedNode node, String name, FTypedElement arg)
    {
        FType fType = fTypeRef.getDerived();
        if (fType instanceof FEnumerationType)
        {
            handleEnumType( fType, node, name, arg );
        }
        else if (fType == null)
        {
            node.setName( name );
        }
    }

    public void handleEnumType(FType fType, DecodedNode node, String name, FTypedElement arg)
    {
        FEnumerationType type = ((FEnumerationType)fType);

        List<FEnumerator> enums = new ArrayList<FEnumerator>();

        while (type != null)
        {
            enums.addAll( type.getEnumerators() );
            type = type.getBase();
        }

        if (node.getChildren().isEmpty())
        {
            for (FEnumerator fEnum : enums)
            {
                if (fEnum.getValue() == null)
                {
                    node.setValue( null );
                }
                else
                {
                    String value = getEnumeratorValue( fEnum );
                    if (value.equals( node.getValue() ))
                    {
                        node.setName( name );
                        node.setValue( fEnum.getName() );
                    }
                    else if (value.equals( node.getName() ))
                    {
                        node.setName( fEnum.getName() );
                    }
                }
            }

        }
        else
        {
            handleArgssForDerivedFType( node, fType, name, arg );
        }
    }

    public static String getEnumeratorValue(FEnumerator e)
    {

        if (e.getValue() instanceof FIntegerConstant)
        {
            return ((FIntegerConstant)e.getValue()).getVal().toString();
        }
        else if (e.getValue() instanceof FStringConstant)
        {
            return ((FStringConstant)e.getValue()).getVal();
        }
        else
        {
            return "FIXME";
        }

    }

    public void handleArgssForDerivedFType(final DecodedNode node, final FType fType, final String name,
            final FTypedElement arg)
    {
        int index = 0;
        for (DecodedNode child : node.getChildren())
        {
            if (fType instanceof FTypeDef)
            {
                if (isArrayPropertySet( arg ))
                {
                    child.getParentNode().setName( arg.getName() );
                    child.setName( fType.getName() + "[" + index + "]" );
                }

            }
            else if (fType instanceof FMapType)
            {
                if (isArrayPropertySet( arg ))
                {
                    child.setName( fType.getName() + "[" + index + "]" );

                    for (DecodedNode nextChild : child.getChildren())
                    {
                        handleMapType( fType.getName(),
                                       ((FMapType)fType).getKeyType(),
                                       ((FMapType)fType).getValueType(),
                                       nextChild,
                                       arg );
                    }
                }
                else
                {
                    handleMapType( fType, child, arg );
                }
            }
            else if (fType instanceof FArrayType)
            {
                handleArrayType( fType, child, arg );
            }
            else if (fType instanceof FUnionType)
            {
                node.setName( fType.getName() );
                if (child.getChildren().isEmpty())
                {
                    FBasicTypeId basicTypeId = getBasicTypeId( child.getName() );
                    if (basicTypeId != FBasicTypeId.UNDEFINED)
                    {
                        String fieldName = getFieldName( ((FUnionType)fType), basicTypeId );
                        if (fieldName != null)
                        {
                            child.setName( fieldName );
                        }
                    }
                    else
                    {
                        FField ffield = null;
                        for (FField nextField : ((FUnionType)fType).getElements())
                        {
                            FType derivedType = nextField.getType().getDerived();
                            if (derivedType != null)
                            {
                                if (derivedType.getName().equals( child.getName() ))
                                {
                                    ffield = nextField;
                                    break;
                                }
                            }
                        }
                        if (ffield != null)
                        {
                            handleArgsForFType( child, ffield.getType(), name, arg );
                        }
                    }
                    // FIXME handle complex types
                }
                else
                {
                    handleFCompoundType( fType, child, arg, index );
                }
            }
            else if (fType instanceof FStructType)
            {
                if (isArrayPropertySet( arg ))
                {
                    handleArrayPropertyType( fType.getName(), ((FStructType)fType).getElements(), child, arg, index );
                }
                else
                {
                    handleFCompoundType( fType, child, arg, index );
                }
            }
            else if (fType instanceof FEnumerationType)
            {
                handleEnumType( fType, child, name, arg );
            }
            index++;
        }
    }

    private String getFieldName(FUnionType fType, FBasicTypeId basicTypeId)
    {
        for (FField nextField : fType.getElements())
        {
            if (nextField.getType().getPredefined().equals( basicTypeId ))
            {
                return nextField.getName();
            }
        }

        LOG.warn( "Could not get field name for union. basicTypeId=" + basicTypeId );
        return null;
    }

    private boolean isArrayPropertySet(FTypedElement arg)
    {
        return arg.isArray();
    }

    private boolean isDerived(FTypeRef fTypeRef)
    {
        return fTypeRef.getDerived() != null;
    }

    private FBasicTypeId getBasicTypeId(String dbusTypeAsString)
    {
        DBusParamType dbusType = convertStringToDBusType( dbusTypeAsString );

        if (dbusType != null)
        {
            switch (dbusType)
            {
                case DBUS_MSG_PARAM_TYPE_BYTE :
                    return FBasicTypeId.UINT8;
                case DBUS_MSG_PARAM_TYPE_INT16 :
                    return FBasicTypeId.INT16;
                case DBUS_MSG_PARAM_TYPE_UINT16 :
                    return FBasicTypeId.UINT16;
                case DBUS_MSG_PARAM_TYPE_INT32 :
                    return FBasicTypeId.INT32;
                case DBUS_MSG_PARAM_TYPE_UINT32 :
                    return FBasicTypeId.UINT32;
                case DBUS_MSG_PARAM_TYPE_INT64 :
                    return FBasicTypeId.INT64;
                case DBUS_MSG_PARAM_TYPE_UINT64 :
                    return FBasicTypeId.UINT64;
                case DBUS_MSG_PARAM_TYPE_BOOLEAN :
                    return FBasicTypeId.BOOLEAN;
                case DBUS_MSG_PARAM_TYPE_DOUBLE :
                    return FBasicTypeId.DOUBLE;
                case DBUS_MSG_PARAM_TYPE_STRING :
                    return FBasicTypeId.STRING;
                case DBUS_MSG_PARAM_TYPE_OBJ_PATH :
                    return FBasicTypeId.STRING;
                case DBUS_MSG_PARAM_TYPE_SIGNATURE :
                    return FBasicTypeId.STRING;
                case DBUS_MSG_PARAM_TYPE_UNIX_FD :
                    return FBasicTypeId.UINT32;

                default :
                    return FBasicTypeId.UNDEFINED;
            }
        }
        return FBasicTypeId.UNDEFINED;
    }

    private DBusParamType convertStringToDBusType(String dbusTypeAsString)
    {
        DBusParamType dbusType = null;
        try
        {
            dbusType = DBusParamType.valueOf( dbusTypeAsString );
        }
        catch (Exception e)
        {
            // Ignore, is checked in caller!
        }
        return dbusType;
    }
}
