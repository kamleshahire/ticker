/*
 * Copyright (c) 2012-2017 The original author or authorsgetRockQuestions()
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.moquette.spi.persistence;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.reactivetechnologies.ticker.datagrid.HazelcastOperations;
import org.springframework.beans.factory.annotation.Autowired;

import io.moquette.server.config.IConfig;
import io.moquette.spi.IMessagesStore;
import io.moquette.spi.ISessionsStore;

/**
 * MapDB main persistence implementation
 */
public class HazelcastPersistentStore {

    /**
     * This is a DTO used to persist minimal status (clean session and activation status) of
     * a session.
     * */
    public static class PersistentSession implements Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = 600256855231400453L;
		public final boolean cleanSession;

        public PersistentSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
        }
    }

    //private static final Logger LOG = LoggerFactory.getLogger(MapDBPersistentStore.class);

    @Autowired
    private HazelcastOperations m_db;
    
    //private final String m_storePath;
    //private final int m_autosaveInterval; // in seconds

    protected final ScheduledExecutorService m_scheduler = Executors.newScheduledThreadPool(1);
    
    private HazelcastMessagesStore m_messageStore;
    private HazelcastSessionsStore m_sessionsStore;

    public HazelcastPersistentStore(IConfig props) {
        //this.m_storePath = props.getProperty(PERSISTENT_STORE_PROPERTY_NAME, "");
        //this.m_autosaveInterval = Integer.parseInt(props.getProperty(AUTOSAVE_INTERVAL_PROPERTY_NAME, "30"));
    }

    /**
     * Factory method to create message store backed by MapDB
     * @return the message store instance.
     * */
    public IMessagesStore messagesStore() {
        return m_messageStore;
    }

    public ISessionsStore sessionsStore() {
        return m_sessionsStore;
    }
    
    public void initStore() {
        /*if (m_storePath == null || m_storePath.isEmpty()) {
            m_db = DBMaker.newMemoryDB().make();
        } else {
            File tmpFile;
            try {
                tmpFile = new File(m_storePath);
                boolean fileNewlyCreated = tmpFile.createNewFile();
                LOG.info("Starting with {} [{}] db file", fileNewlyCreated ? "fresh" : "existing", m_storePath);
            } catch (IOException ex) {
                LOG.error(null, ex);
                throw new MQTTException("Can't create temp file for subscriptions storage [" + m_storePath + "]", ex);
            }
            m_db = DBMaker.newFileDB(tmpFile).make();
        }
        m_scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                m_db.commit();
            }
        }, this.m_autosaveInterval, this.m_autosaveInterval, TimeUnit.SECONDS);*/

        //TODO check m_db is valid and
        m_messageStore = new HazelcastMessagesStore(m_db);
        m_messageStore.initStore();

        m_sessionsStore = new HazelcastSessionsStore(m_db, m_messageStore);
        m_sessionsStore.initStore();
    }

    public void close() {
        /*if (this.m_db.isClosed()) {
            LOG.debug("already closed");
            return;
        }
        this.m_db.commit();
        //LOG.debug("persisted subscriptions {}", m_persistentSubscriptions);
        this.m_db.close();
        LOG.debug("closed disk storage");
        this.m_scheduler.shutdown();
        LOG.debug("Persistence commit scheduler is shutdown");*/
    }
}
