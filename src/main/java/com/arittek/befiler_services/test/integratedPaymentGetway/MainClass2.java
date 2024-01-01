package com.arittek.befiler_services.test.integratedPaymentGetway;

public class MainClass2 {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		/*System.setProperty("javax.net.ssl.trustStore","C:\\Program Files\\Java\\jdk1.8.0_65\\jre\\lib\\security\\cacerts");
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		System.setProperty("javax.net.ssl.keyStore","C:/OpenSSL-Win64/bin/client_merchant_cert_p12.p12");
		System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
		System.setProperty("javax.net.ssl.keyStorePassword","Comtrust");
		 *//*  KeyStore clientStore = KeyStore.getInstance("PKCS12");
	        clientStore.load(new FileInputStream("C:/OpenSSL-Win64/bin/client_merchant_cert_p12.p12"), "Comtrust".toCharArray());

	        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	        kmf.init(clientStore, "Comtrust".toCharArray());
	        KeyManager[] kms = kmf.getKeyManagers();

	        KeyStore trustStore = KeyStore.getInstance("JKS");
	        trustStore.load(new FileInputStream("C:\\Program Files\\Java\\jdk1.8.0_65\\jre\\lib\\security\\cacerts"), "changeit".toCharArray());

	        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	        tmf.init(trustStore);
	        TrustManager[] tms = tmf.getTrustManagers();

	        SSLContext sslContext = null;
	        sslContext = SSLContext.getInstance("TLS");
	        sslContext.init(kms, tms, new SecureRandom());

	        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	        System.setProperty("java.protocol.handler.pkgs", "com.arittek.marchant");
	        		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
*//*
	        URL ur = new URL("https://demo-ipg.comtrust.ae:2443/MerchantAPI.svc?singleWsdl");

//	        HttpsURLConnection urlConn = (HttpsURLConnection) ur.openConnection();
//		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//		URL ur = new URL("https://demo-ipg.comtrust.ae:2443/MerchantAPI.svc?singleWsdl");
	        System.out.println(ur);
	        MerchantAPI_Service m=new MerchantAPI_ServiceLocator();
	        System.out.println(ur.getAuthority());
		MerchantAPI_PortType port = m.getBasicHttpsBinding_MerchantAPI(ur);
		FinalizationRequest fr = new FinalizationRequest();
		fr.setCustomer("Demo Merchant");
		fr.setLanguage("ENG");
		fr.setVersion(BigDecimal.valueOf(2.0));
		fr.setPassword("Comtrust");
		fr.setTransactionID("144169408146");
		System.out.println(port.finalize(fr).getBalance()+", desc:"+port.finalize(fr).getResponseDescription());
		System.out.println(port.finalize(fr).getOrderID());
		FinalizationResponse finalizationResponse = new FinalizationResponse();
		MerchantAPI_Service service = new MerchantAPI_ServiceLocator();
		MerchantAPI_PortType port2 = service.getBasicHttpsBinding_MerchantAPI(ur);
		finalizationResponse = port.finalize(fr);
		System.out.println(finalizationResponse.getResponseDescription());
*/

	}

}
