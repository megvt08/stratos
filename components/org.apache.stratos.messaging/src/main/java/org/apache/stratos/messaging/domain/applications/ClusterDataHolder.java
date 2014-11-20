/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.stratos.messaging.domain.applications;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Holder for a Cluster.
 * Will hold the Service Type and Cluster Id.
 */

public class ClusterDataHolder implements Serializable {

    // Service/Cartridge type
    private String serviceType;
    // Cluster id
    private String clusterId;
    // Group Instance map, key = group instance Id
    private Map<String, GroupInstanceContext> groupInstanceIdToGroupInstanceContextMap;

    public ClusterDataHolder (String serviceType, String clusterId) {

        this.serviceType = serviceType;
        this.clusterId = clusterId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void addGroupInstanceContext (GroupInstanceContext groupInstanceContext) {
        // the map will be created upon the first attempt to insert a GroupInstanceContext object.
        if (groupInstanceIdToGroupInstanceContextMap == null) {
            synchronized (this) {
                if (groupInstanceIdToGroupInstanceContextMap == null) {
                    groupInstanceIdToGroupInstanceContextMap =
                            new HashMap<String, GroupInstanceContext>();
                }
            }
        }

        groupInstanceIdToGroupInstanceContextMap.put(groupInstanceContext.getGroupInstanceId(),
                groupInstanceContext);
    }

    public GroupInstanceContext getGroupInstanceContext (String groupInstanceId) {
        // if the map is not created yet, return null
        if (groupInstanceIdToGroupInstanceContextMap == null) {
            return null;
        }

        return groupInstanceIdToGroupInstanceContextMap.get(groupInstanceId);
    }
}
