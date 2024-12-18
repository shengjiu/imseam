/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.imseam.chatpage.el.implicitobject;

import java.beans.FeatureDescriptor;

import javax.el.ELContext;

import com.imseam.chatpage.tag.Tag;

/**
 * Encapsulates information needed by the ImplicitObjectResolver
 * 
 * @author Leonardo Uribe (latest modification by $Author: struberg $)
 * @version $Revision: 1188953 $ $Date: 2011-10-25 17:41:51 -0500 (Tue, 25 Oct 2011) $
 */
public class ComponentImplicitObject extends ImplicitObject
{

    private static final String NAME = "tag";

    /** Creates a new instance of ComponentImplicitObject */
    public ComponentImplicitObject()
    {
    }

    @Override
    public Object getValue(ELContext context)
    {
        return Tag.getCurrentTag(chatpageContext(context));
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Class<?> getType()
    {
        return null;
    }

    @Override
    public FeatureDescriptor getDescriptor()
    {
        return makeDescriptor(NAME, "Represents the tag most recently pushed using Tag.pushTagToEL",
                              Tag.class);
    }
}
