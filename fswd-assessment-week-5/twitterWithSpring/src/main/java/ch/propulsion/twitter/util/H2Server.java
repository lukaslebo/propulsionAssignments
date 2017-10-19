package ch.propulsion.twitter.util;

import java.sql.SQLException;

import org.h2.tools.Server;

public class H2Server {
	
	private static final String[] H2_ARGUMENTS = new String[] { "-tcp", "-tcpAllowOthers" };
	private static Server server = null;
	
	public static Server getAndStartServer() {
		if (server == null) {
			try {
				server = Server.createTcpServer(H2_ARGUMENTS).start();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return server;
	}
	
	public static void stopServer() {
		if (server == null) {
			return;
		}
		System.out.println("Shutting down server with URL: " + server.getURL());
		server.stop();
	}

//	public static void printInfo() {
//		if (server == null) {
//			return;
//		}
//		System.out.println("Driver: " + org.h2.Driver.class.getName());
//		System.out.println("URL:    " + server.getURL());
//		System.out.println("Port:    " + server.getPort());
//		System.out.println("Status:    " + server.getStatus());
//		System.out.println("Hash value correct:    " + (server.hashCode() == 589446616));
//
//		// JDBC driver class: org.h2.Driver
//		// Database URL: jdbc:h2:tcp://localhost/~/h2_pa
//
//		System.out.println();
//		System.out.println("Press enter to stop the H2 database server...");
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
}
