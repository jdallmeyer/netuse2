package ping;

import data.ListOfPings;
import data.SystemParameter;
import javafx.collections.ObservableList;
import main.Logger;

public class AnalysisEngine {

	private String ip = null;
	private String dns = null;
	private String gateway = null;
	private ListOfPings lop = null;
	private Logger logger = null;

	public AnalysisEngine(ListOfPings list) {
		lop = list;
		logger = Logger.getInstance();
	}

	public boolean analyze() {
		boolean retval = true;

		if(!analyzePings())
			retval = false;
		if(!testIP())
			retval = false;
		if(!testDNS())
			retval = false;
		if(!testGW())
			retval = false;
		return retval;
	}

	private boolean analyzePings() {
		logger.statusLogUpdate("Analyzing ping data");
		boolean retval = true;
		String message = null;
		if((lop.getBadPingCount() != 0) && (lop.getTotalPings() > 0)) {
			message = "Some bad replies detected.  Check wifi signal strength and cabling.";
			retval = false;
		}
		if(lop.getAverageRTT() > 300) {
			message = "High average latency detected.  Check wifi signal strength and network utilization.";
		}
		logger.statusLogUpdate(message);
		return retval;
	}

	public boolean loadParameters(ObservableList<SystemParameter> params) {
		boolean retval = true;

		for(SystemParameter p: params) {
			if(p.getParameter().matches("IP Address")) {
				ip = p.getValue();
				continue;
			}
			if(p.getParameter().matches("DNS Resolver")) {
				dns = p.getValue();
				continue;
			}
			if(p.getParameter().matches("Default Gateway")) {
				gateway = p.getValue();
			}
		}

		return retval;
	}

	private boolean testIP() {
		logger.statusLogUpdate("Testing valid system IP");
		boolean retval = true;
		String message = null;
		//does system have IP
		if(ip == null || ip.equals("")) {
			message = "No IP Address detected.  Check interface configuration.";
			retval = false;
		}
		//does system have valid IP
		else if(ip.contains("169.254.")) {
			message = "APIPA address detected.  Check DHCP settings and communication to DHCP server.";
			retval = false;
		}
		if(retval) {
			logger.statusLogUpdate("Valid system IP detected");
		}
		else {
			logger.logIssue(message);

		}
		return retval;
	}

	private boolean testDNS() {
		logger.statusLogUpdate("Testing DNS");
		String message = null;
		boolean retval = true;
		
		if(dns == null || dns.isEmpty()) {
			logger.logIssue("No DNS servers detected.  Check DNS configuration.");
			retval = false;
		}
		else {
			String[] dnsServer = dns.split(" ");
			for(int i = 0; i < dnsServer.length; i++) {
				Pinger dnsPing = new Pinger(dnsServer[i],false,null);
				logger.statusLogUpdate("Pinging DNS Server: " + dnsServer[i]);
				if(dnsPing.doPing()) {
					logger.statusLogUpdate("DNS Server " + dnsServer[i] + " responded.");
				}
				else {
					message = "DNS Server " + dnsServer[i] + " faied to respond to ping!  Check DNS configuration.";
					retval = false;
				}
				if(!retval) {
					logger.logIssue(message);
				}
			}
		}
		return retval;
	}

	private boolean testGW() {
		logger.statusLogUpdate("Testing gateway");
		boolean retval = true;
		String message = null;

		//does system have gateway
		if(gateway == null || gateway.isEmpty()) {
			message = "No default gateway detected.  Check interface configuration.";
			retval = false;
		}

		//is gateway reachable
		Pinger gwPing = new Pinger(gateway, false, null);
		logger.statusLogUpdate("Pinging gatway " + gateway);

		if(gwPing.doPing()){
			logger.statusLogUpdate("Gateway " + gateway + " responded.");
		}
		else {
			message = "Gateway " + gateway + " failed to respond to ping!  Check interface configuration.";
			retval = false;
		}
		if(!retval) {
			logger.logIssue(message);
		}

		return retval;
	}
}
