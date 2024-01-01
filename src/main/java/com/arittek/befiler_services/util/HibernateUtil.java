package com.arittek.befiler_services.util;

/*import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;*/

public class HibernateUtil {
	
	/* private static SessionFactory sessionFactory=null;
	    private static Session session = null;
	 
	    private static SessionFactory buildSessionFactory() {
	        try {
	            // Create the SessionFactory from hibernate.cfg.xml
	        	if(sessionFactory==null){
	        		
	        		Configuration configuration = new Configuration();
	            	configuration.configure("hibernate.cfg.xml");
	            	MyPrint.println("Hibernate Configuration loaded");
	            	MyPrint.println("Hibernate Configuration loaded");
	            	ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
	            	MyPrint.println("Hibernate serviceRegistry created");
	            	MyPrint.println("Hibernate serviceRegistry created");
	            	SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	        		
	        		//SessionFactory sessionFactory =   new AnnotationConfiguration().configure().buildSessionFactory();
	                 MyPrint.println("Creating new Session Factory Created....");
	                 MyPrint.println("Creating new Session Factory Created....");
	                 return sessionFactory;
	        	}
	        	
	        }
	        catch (Exception ex) {
	            // Make sure you log the exception, as it might be swallowed
	            MyPrint.errln("Initial SessionFactory creation failed." + ex);
	            ex.printStackTrace();
	            Logger4j.getLogger().error("Exception:",ex);
	            //throw new ExceptionInInitializerError(ex);
	        }finally{
	        	if(sessionFactory!=null)
	        		MyPrint.println("connection to Database build...");
	        }
	        
	        return sessionFactory;
	    }
	 
	    private static SessionFactory getSessionFactory() {
	    	if(sessionFactory==null)
	    		sessionFactory = buildSessionFactory();
	        return sessionFactory;
	    }
	    
	    *//**
	     * Existing Session will be returned<b>(Recommended)</b>
	     * 
	     * @return  org.hibernate.Session
	     *//*
	    public static Session getCurrentSession(){
	    	if(session==null)
	    		session = getSessionFactory().openSession();
	    	else{
	    		try{
	    			SQLQuery sql=session.createSQLQuery("select 1");
	    			sql.uniqueResult();
	    		}catch(org.hibernate.exception.JDBCConnectionException e){
	    			reconnectAllConnections();
	    			e.printStackTrace();
	    		}catch(org.hibernate.SessionException ses){
	    			session = getSessionFactory().openSession();
	    		}
	    	}
	        return session;
	    }
	    
	    public static void refreshSession(){
	    	session =  getNewSession();
	    }
	  
	    *//**
	     * new Session will be returned
	     * 
	     * @return  org.hibernate.Session
	     *//*
	   public static Session getNewSession(){
		   try{
	    	if(session!=null){
	    		session.flush();
	    		session.clear();
	    	}else{
	    		session = getSessionFactory().openSession();
	    	}
	   }catch(HibernateException ex){
		   session = buildSessionFactory().openSession();
			 return session;
		}
	    	return session;
	    }
	   public static Session getNewSession(){
	    	return sessionFactory.openSession();
	    }
	 
	    public static void shutdown() {
	    	// Close caches and connection pools
	    	getSessionFactory().close();
	    }
	    public static Session getCurrentSessionBySchema(String s){
	    	return null;
	    }

		public static void reconnectAllConnections() {
			shutdown();
			Configuration configuration = new Configuration();
			configuration.configure("hibernate.cfg.xml");
			MyPrint.println("Re-Hibernate Configuration loaded");
			MyPrint.println("Re-Hibernate Configuration loaded");
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties()).build();
			MyPrint.println("Re-Hibernate serviceRegistry created");
			MyPrint.println("Re-Hibernate serviceRegistry created");
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			MyPrint.println("Re-Creating new Session Factory Created....");
			MyPrint.println("Re-Creating new Session Factory Created....");
			session = sessionFactory.openSession();
		}
	
		public static Connection getSqlConnectionBySchema(String schema) throws Exception {
			Properties props = ((SessionFactoryImpl) getCurrentSession().getSessionFactory()).getProperties();
			return DriverManager.getConnection(props.get("hibernate.connection.url").toString() + schema,
					props.get("hibernate.connection.username").toString(),
					props.get("hibernate.connection.password").toString());
		}

		
		public static String getDbUserNameAndPassword()  {
			String userNameAndPass=null;
			Properties props = ((SessionFactoryImpl) getCurrentSession().getSessionFactory()).getProperties();
			userNameAndPass=props.get("hibernate.connection.username").toString()+","+props.get("hibernate.connection.password").toString()+","+props.get("hibernate.connection.url").toString();
			
			return userNameAndPass;
		}

		public static JasperPrint getJasperPrintObject(JasperReport jasperReport, Map<String, Object> properties,
				String schema) throws Exception {
			Connection con = getSqlConnectionBySchema(schema);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, properties, con);
			con.close();
			return jasperPrint;
		}
*/
		}
