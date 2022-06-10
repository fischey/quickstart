package org.jboss.as.quickstart.hibernate.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.jboss.as.quickstart.hibernate.model.Stock;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SynchronisationMock {
  private static final String MANAGED_EXECUTOR_SERVICE = "java:jboss/ee/concurrency/executor/default/";

  @Inject
  private Logger log;

  public void doHibernate() throws NamingException {
    final ExecutorService managedExecutorService = (ExecutorService) new InitialContext().lookup(MANAGED_EXECUTOR_SERVICE);
    managedExecutorService.submit(() -> {
      log.info("Create Session");
      try {
        final Session session = getSession();
        log.info("saved stock:  " + saveStock(session));
      } catch (Throwable e) {
        log.log(Level.SEVERE, "Error during sync", e);
      }
    });
  }

  private Integer saveStock(Session session) {
    final Stock o = new Stock();
    o.setStockCode("BLA" + UUID.randomUUID());
    o.setStockName("Bliese Bluse");
    session.beginTransaction();
    final Integer id = (Integer) session.save(o);
    session.flush();
    final Transaction transaction = session.getTransaction();
    transaction.commit();
    return id;
  }

  private Session getSession() {
    final Configuration configuration = new Configuration();
    configuration.configure(getClass().getResource("/hibernate.cfg.xml"));
    final SessionFactory sessionFactory = configuration.buildSessionFactory();
    final Session session = sessionFactory.openSession();
    return session;
  }
}
