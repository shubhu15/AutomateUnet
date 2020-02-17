package org.automate.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionCreator {
    public SessionFactory sessionFactory = null;
    private static SessionCreator sessionCreator = null;
    private SessionCreator()  {
        sessionFactory = buildSessionFactory();
       
//        UNETMemberETA unetMemberETA= new UNETMemberETA();
//        unetProviderETA.getQuery(current_date, sessionFactory);
//        unetMemberETA.getQuery(current_date, sessionFactory);
    }
    public static SessionCreator getInstance() 
    { 
        if (sessionCreator == null) 
        	sessionCreator = new SessionCreator(); 
  
        return sessionCreator; 
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    private SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    private void shutdown() {
        getSessionFactory().close();
    }

}
