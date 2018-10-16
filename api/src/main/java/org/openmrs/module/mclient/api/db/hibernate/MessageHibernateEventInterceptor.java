package org.openmrs.module.mclient.api.db.hibernate;

import org.openmrs.module.atomfeed.api.db.hibernate.HibernateEventInterceptor;
import org.openmrs.module.mclient.api.db.MessageEventManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.hibernate.Transaction;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.atomfeed.api.db.EventAction;

import java.util.HashSet;
import java.util.Stack;

public class MessageHibernateEventInterceptor extends HibernateEventInterceptor {

    private static final long serialVersionUID = 1L;

    private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> inserts = new ThreadLocal<>();

    private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> updates = new ThreadLocal<>();

    private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> deletes = new ThreadLocal<>();

    private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> retiredObjects = new ThreadLocal<>();

    private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> unretiredObjects = new ThreadLocal<>();

    private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> voidedObjects = new ThreadLocal<>();

    private static ThreadLocal<Stack<HashSet<OpenmrsObject>>> unvoidedObjects = new ThreadLocal<>();


    @Autowired
    private transient MessageEventManager messageEventManager;


    @Override
    public void afterTransactionCompletion(Transaction tx) {
        try {
            if (tx.wasCommitted()) {
                for (OpenmrsObject delete : deletes.get().peek()) {
                    messageEventManager.serveEvent(delete, EventAction.DELETED);
                }
                for (OpenmrsObject insert : inserts.get().peek()) {
                    messageEventManager.serveEvent(insert, EventAction.CREATED);
                }
                for (OpenmrsObject update : updates.get().peek()) {
                    messageEventManager.serveEvent(update, EventAction.UPDATED);
                }
                for (OpenmrsObject retired : retiredObjects.get().peek()) {
                    messageEventManager.serveEvent(retired, EventAction.RETIRED);
                }
                for (OpenmrsObject unretired : unretiredObjects.get().peek()) {
                    messageEventManager.serveEvent(unretired, EventAction.UNRETIRED);
                }
                for (OpenmrsObject voided : voidedObjects.get().peek()) {
                    messageEventManager.serveEvent(voided, EventAction.VOIDED);
                }
                for (OpenmrsObject unvoided : unvoidedObjects.get().peek()) {
                    messageEventManager.serveEvent(unvoided, EventAction.UNVOIDED);
                }
            }
        } finally {
            //cleanup
            inserts.get().pop();
            updates.get().pop();
            deletes.get().pop();
            retiredObjects.get().pop();
            unretiredObjects.get().pop();
            voidedObjects.get().pop();
            unvoidedObjects.get().pop();

            removeStackIfEmpty();
        }
    }

    private void removeStackIfEmpty() {
        if (inserts.get().empty()) {
            inserts.remove();
        }
        if (updates.get().empty()) {
            updates.remove();
        }
        if (deletes.get().empty()) {
            deletes.remove();
        }
        if (retiredObjects.get().empty()) {
            retiredObjects.remove();
        }
        if (unretiredObjects.get().empty()) {
            unretiredObjects.remove();
        }
        if (voidedObjects.get().empty()) {
            voidedObjects.remove();
        }
        if (unvoidedObjects.get().empty()) {
            unvoidedObjects.remove();
        }
    }
}
