package org.jboss.as.quickstart.hibernate.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.jboss.as.quickstart.hibernate.model.Stock;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

//@Stateless
public class SynchronisationMock {
  private static final String MANAGED_EXECUTOR_SERVICE = "java:jboss/ee/concurrency/executor/default/";

  @Inject
  private Logger log;

  @Resource(lookup = "java:/SYNCSOURCE-B-E-DS")
  private DataSource dataSource;

  public void doHibernate() throws NamingException {
    final ExecutorService managedExecutorService = (ExecutorService) new InitialContext().lookup(MANAGED_EXECUTOR_SERVICE);
    managedExecutorService.submit(() -> {
      try {
        log.info("Create Session1");
        final Session session = getSession("/hibernate.cfg.xml");
        log.info("saved stock:  " + saveStock(session));

        log.info("Create Session2");
        final Session session2 = getSession("/hibernate2.cfg.xml");
        log.info("saved stock:  " + saveStock(session2));
        session2.close();
      } catch (final Throwable e) {
        log.log(Level.SEVERE, "Error during sync", e);
      }
    });
  }

  public void doConnecton() {
    try {
      final Connection connection = dataSource.getConnection();
      connection.setAutoCommit(false);
    } catch (final SQLException e) {
      log.log(Level.SEVERE, "Error during connection ", e);
    }
  }

  private Integer saveStock(final Session session) {
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

  private Session getSession(final String name) {
    final Configuration configuration = new Configuration();
    configuration.configure(getClass().getResource(name));
    final SessionFactory sessionFactory = configuration.buildSessionFactory();
    return sessionFactory.openSession();
  }
}
