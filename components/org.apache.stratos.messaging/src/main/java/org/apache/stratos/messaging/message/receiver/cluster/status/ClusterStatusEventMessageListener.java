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
package org.apache.stratos.messaging.message.receiver.cluster.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ClusterStatusEventMessageListener implements MessageListener {
    private static final Log log = LogFactory.getLog(ClusterStatusEventMessageListener.class);

    private ClusterStatusEventMessageQueue messageQueue;

    public ClusterStatusEventMessageListener(ClusterStatusEventMessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage receivedMessage = (TextMessage) message;
            try {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Tenant message received: %s", ((TextMessage) message).getText()));
                }
                // Add received message to the queue
                messageQueue.add(receivedMessage);

            } catch (JMSException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
