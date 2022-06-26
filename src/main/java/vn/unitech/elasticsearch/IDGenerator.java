package vn.unitech.elasticsearch;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;

@SuppressWarnings("deprecation")
public class IDGenerator implements IdentifierGenerator, Configurable {
    private static long starter = 16140996000l;
    private static long server = 1000;// tối đa 9 server, từ 1000 đến 9000
    private static long counter = 0;
    private static long realStarter = 0;

    public static synchronized Long get() {
        if (counter > 999) {
            try {
                Thread.sleep(1);
                while ((((System.currentTimeMillis() / 100) - starter) * 10000) == realStarter) {
                    Thread.sleep(1);
                }
            } catch (Exception e) {

            }
            counter = 0;
        }
        if (counter == 0) {
            realStarter = (((System.currentTimeMillis() / 100) - starter) * 10000);
        }
        return realStarter + server + counter++;
    }

    // for Entity
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        return IDGenerator.get();
    }

    @Override
    public void configure(org.hibernate.type.Type type, Properties properties, ServiceRegistry serviceRegistry)
            throws MappingException {

    }

    public static Long generate() {
        return IDGenerator.get();
    }
}
