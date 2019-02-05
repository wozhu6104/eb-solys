package de.systemticks.solys.data.api;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

import de.systemticks.solys.db.sqlite.api.DataStorageAccess;

public class DataServiceHost {

	   private TSimpleServer server;

		public void start(DataStorageAccess access) {
	        TServerTransport serverTransport = null;
			try {
				serverTransport = new TServerSocket(9090);
			} catch (TTransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        server = new TSimpleServer(new TServer.Args(serverTransport)
	          .processor(new DataServiceInterface.Processor<>(new DataServiceImpl(access))));

	        System.out.print("Starting the server... ");
	         	 
	        server.serve();
	 
	        System.out.println("done.");
	    }
	 
	    public void stop() {
	        if (server != null && server.isServing()) {
	            System.out.print("Stopping the server... ");
	 
	            server.stop();
	 
	            System.out.println("done.");
	        }
	    }
	
	
}
