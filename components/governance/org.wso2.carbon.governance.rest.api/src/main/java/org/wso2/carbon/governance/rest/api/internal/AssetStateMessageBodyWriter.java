/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.governance.rest.api.internal;

import com.google.gson.stream.JsonWriter;
import org.wso2.carbon.governance.rest.api.model.AssetState;
import org.wso2.carbon.governance.rest.api.model.LCState;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


@Provider
@Consumes({"application/json"})
public class AssetStateMessageBodyWriter implements MessageBodyWriter<AssetState> {

    public static final String INDENT = "  ";
    public static final String STATE = "states";
    @Context
    UriInfo uriInfo;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (AssetState.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    @Override
    public long getSize(AssetState assetState, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(AssetState assetState, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        PrintWriter printWriter = new PrintWriter(entityStream);
        JsonWriter writer = new JsonWriter(printWriter);
        writer.setIndent(INDENT);
        writer.beginObject();
        writer.name(STATE);
        if (assetState.getLcStates().size() > 0) {
            writer.beginArray();
            for (LCState lcState : assetState.getLcStates()) {
                writer.beginObject();
                writer.name("state").value(lcState.getState());
                writer.name("lcName").value(lcState.getLc());

                if (lcState.getActions().size() > 0) {
                    writer.name("actions");
                    writer.beginArray();
                    for (String action : lcState.getActions()) {
                        writer.beginObject();
                        writer.name("action").value(action);
                        writer.endObject();
                    }
                    writer.endArray();
                }
                writer.endObject();
            }
            writer.endArray();
        }
        writer.endObject();

        writer.flush();
        writer.close();
        printWriter.flush();
        printWriter.close();
    }
}
