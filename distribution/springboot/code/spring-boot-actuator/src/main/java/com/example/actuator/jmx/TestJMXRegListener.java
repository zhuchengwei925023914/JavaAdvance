package com.example.actuator.jmx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestJMXRegListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private JmxTest jmxTest;

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = null;
            objectName = new ObjectName("jmxBean:name=testJMX");
            server.registerMBean(jmxTest, objectName);

            // 使得客户端可以使用rmi通过url方式来连接JMXConnectorServer
            Registry registry = LocateRegistry.createRegistry(1099);
            JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
            JMXConnectorServer jmxConnectorServer =
                    JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, null, server);
            jmxConnectorServer.start();
        } catch (Exception e) {

        }
    }
}
