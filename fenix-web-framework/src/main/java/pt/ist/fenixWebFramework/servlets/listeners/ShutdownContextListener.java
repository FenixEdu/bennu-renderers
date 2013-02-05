package pt.ist.fenixWebFramework.servlets.listeners;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

@WebListener
public class ShutdownContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ShutdownContextListener.class);

    private ClassLoader thisClassLoader;

    @Override
    public void contextInitialized(ServletContextEvent context) {
        // Nothing, this listener is only meant to perform some cleanup on
        // shutdown
    }

    @Override
    public void contextDestroyed(ServletContextEvent context) {
        this.thisClassLoader = this.getClass().getClassLoader();
        interruptThreads();
        deregisterJDBCDrivers();
    }

    private void deregisterJDBCDrivers() {

        Enumeration<Driver> drivers = DriverManager.getDrivers();

        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();

            ClassLoader loader = driver.getClass().getClassLoader();

            if (loader != null && loader.equals(this.thisClassLoader)) {
                try {
                    DriverManager.deregisterDriver(driver);
                    System.out.println("Successfully deregistered JDBC driver " + driver);
                } catch (SQLException e) {
                    logger.warn("Failed to deregister JDBC driver " + driver + ". This may cause a potential leak.", e);
                }
            }

        }
    }

    private void interruptThreads() {

        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread == null || thread.getContextClassLoader() != thisClassLoader || thread == Thread.currentThread()) {
                continue;
            }

            if (thread.isAlive()) {
                System.out.println("Killing initiated thread: " + thread + " of class " + thread.getClass());
                thread.interrupt();
            }
        }
    }

}
