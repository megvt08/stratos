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

package org.apache.stratos.autoscaler.partition;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.stratos.autoscaler.client.cloud.controller.CloudControllerClient;
import org.apache.stratos.autoscaler.exception.AutoScalerException;
import org.apache.stratos.autoscaler.exception.PartitionValidationException;
import org.apache.stratos.autoscaler.registry.RegistryManager;
import org.apache.stratos.autoscaler.util.AutoScalerConstants;
import org.apache.stratos.cloud.controller.deployment.partition.Partition;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

/**
 * The model class for managing Partitions.
 */
public class PartitionManager {

private static final Log log = LogFactory.getLog(PartitionManager.class);
	
	// Partitions against partitionID
	private static Map<String,Partition> partitionListMap = new HashMap<String, Partition>();
	
	private static PartitionManager instance;
	
	String partitionResourcePath = AutoScalerConstants.AUTOSCALER_RESOURCE 
			+ AutoScalerConstants.PARTITION_RESOURCE + "/";
	
	private PartitionManager(){}
	
	public static PartitionManager getInstance(){
		if(null == instance)
			return new PartitionManager();
		else
			return instance;
	}
	
	public boolean partitionExist(String partitionId){
		return partitionListMap.containsKey(partitionId);
	}
	
	public boolean addPartition( Partition partition) throws AutoScalerException{
		String partitionId = partition.getId();
		if(this.partitionExist(partition.getId()))
			throw new AutoScalerException("A parition with the ID " +  partitionId + " already exist.");
				
		String resourcePath= this.partitionResourcePath + partition.getId();
		
        RegistryManager regManager = RegistryManager.getInstance();     
        
        try {
        	this.validatePartition(partition);
			regManager.persist(partition, resourcePath);
	        partitionListMap.put(partitionId, partition);	
		} catch (RegistryException e) {
			throw new AutoScalerException(e);
		} catch(PartitionValidationException e){
			throw new AutoScalerException(e);
		}
                
		log.info("Partition :" + partition.getId() + " is deployed successfully.");
		return true;
	}
	
	
	
	public Partition getPartitionById(String partitionId){
		if(partitionExist(partitionId))
			return partitionListMap.get(partitionId);
		else
			return null;
	}
	
	public Partition[] getAllPartitions(){
		//return Collections.unmodifiableList(new ArrayList<Partition>(partitionListMap.values()));
		return partitionListMap.values().toArray(new Partition[0]);
		
	}
	
	public boolean validatePartition(Partition partition) throws PartitionValidationException{		
		return CloudControllerClient.getInstance().validatePartition(partition);
	}

	public void updatePartition(Partition newPartition,Partition oldPartition){
		if(!oldPartition.getId().equals(newPartition.getId()))
			throw new AutoScalerException("Can not update.Id s of the two partitions did not match.");
		
		oldPartition=newPartition;
		//overwrite the registry resource.
		try {
			RegistryManager.getInstance().persist(
					newPartition, this.partitionResourcePath + newPartition.getId());
		} catch (RegistryException e) {
			throw new AutoScalerException(e);
		}
	}

}
