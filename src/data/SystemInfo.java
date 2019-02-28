package data;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import main.ExceptionWindow;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class SystemInfo {

	private SystemParameter os = null;
	private SystemParameter host = null;
	private SystemParameter ip = null;
	private SystemParameter dns = null;
	private SystemParameter mask = null;
	private SystemParameter gateway = null;


	public SystemInfo() {
		if (!initializeSystemInfo()) {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Error initializing system parameters.");
			alert.setHeaderText("No Data");
			alert.setHeight(400);
			alert.show();		}

	}

	private boolean initializeSystemInfo() {
		boolean retval = true;
		if(!Desktop.isDesktopSupported()) {
			retval = false;
			new ExceptionWindow("Unsupported system!");
			return retval;
		}

		String operatingSystem;
		String hostName;
		String ipAddress;
		String dnsResolver = null;
		String netmask;
		String defaultGateway = null;


		operatingSystem = System.getProperty("os.name");
		InetAddress localhost = null;
		NetworkInterface netInterface = null;
		try {
			localhost = Inet4Address.getLocalHost();
			netInterface = NetworkInterface.getByInetAddress(localhost);
		} catch (UnknownHostException | SocketException unk) {
			retval = false;
			new ExceptionWindow(unk.getMessage());

		}

		hostName = localhost.getCanonicalHostName();
		ipAddress = localhost.getHostAddress();
		netmask = String.valueOf(netInterface.getInterfaceAddresses().get(0).getNetworkPrefixLength());

		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx;
		try {
			ictx = new InitialDirContext(env);
			String temp = (String) ictx.getEnvironment().get("java.naming.provider.url");
			dnsResolver = temp.replaceAll("[dns://]", "");
		} catch (NamingException ne) {
			retval = false;
			new ExceptionWindow(ne.getMessage());
		}

		String command = null;
		if (operatingSystem.contains("Windows")) {
			command = "ipconfig";
		} else if (operatingSystem.contains("Linux")) {
			command = "route";
		}
		Process process = null;
		BufferedReader bufferedReader = null;
		InputStreamReader input;
		try {
			process = Runtime.getRuntime().exec(command);
		} catch (IOException ioe) {
			retval = false;
			new ExceptionWindow(ioe.getMessage());
		}

		if(process != null) {
			input = new InputStreamReader(process.getInputStream());
			bufferedReader = new BufferedReader(input);
		}

		if (operatingSystem.contains("Windows")) {
			try {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					if (line.trim().startsWith("Default Gateway")) {
						String tmp = line.substring(line.indexOf(':') + 1).trim();
						if (!tmp.isEmpty()) {
							defaultGateway = tmp;
						}
					}
				}
			} catch (FileNotFoundException fnfe) {
				retval = false;
				new ExceptionWindow(fnfe.getMessage());
			} catch (IOException ioe) {
				retval = false;
				new ExceptionWindow(ioe.getMessage());
			} 
			catch (NullPointerException ne){
				retval = false;
				new ExceptionWindow(ne.getMessage());
			}
			finally {

				try {
					bufferedReader.close();
				} catch (IOException ioe) {
					retval = false;
					new ExceptionWindow(ioe.getMessage());
				}
			}
		} else if (operatingSystem.contains("Linux")) {
			try {
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					if (line.trim().contains("UG")) {
						String[] tmp = line.split("         ");
						if (tmp.length != 0) {
							defaultGateway = tmp[1];
						}
					}
				}
			} catch (IOException ioe) {
				retval = false;
				new ExceptionWindow(ioe.getMessage());
			}
			finally{
				try {
					bufferedReader.close();
				}
				catch (IOException ioe){
					retval = false;
					new ExceptionWindow(ioe.getMessage());
				}
			}
		}

		os = new SystemParameter("Operating System", operatingSystem);
		host = new SystemParameter("Host Name", hostName);
		ip = new SystemParameter("IP Address", ipAddress);
		dns = new SystemParameter("DNS Resolver", dnsResolver);
		mask = new SystemParameter("Netmask", netmask);
		gateway = new SystemParameter("Default Gateway", defaultGateway);

		return retval;
	}

	public ArrayList<SystemParameter> getParams(){
		ArrayList<SystemParameter> sysParams = new ArrayList<>();

		if((os != null) && (host != null) && (ip != null) && (dns != null) && (mask != null)) {
			sysParams.add(os);
			sysParams.add(host);
			sysParams.add(ip);
			sysParams.add(dns);
			sysParams.add(mask);
			sysParams.add(gateway);
		}

		return sysParams;
	}

}
