package test;

import static org.junit.Assert.*;

import java.util.Date;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import data.ListOfPings;
import data.Ping;
import ping.AnalysisEngine;
import data.SystemParameter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;

public class AnalysisTest {
	
	private ListOfPings goodPings;
	private ListOfPings badPings;
	private ListOfPings mixPings;
	private ObservableList<SystemParameter> windowsParams;
	private ObservableList<SystemParameter> linuxParams;
	private ObservableList<SystemParameter> badParams;
	private ObservableList<SystemParameter> badIP;
	private ObservableList<SystemParameter> apipaIP;
	private ObservableList<SystemParameter> badDNS;
	private ObservableList<SystemParameter> badGW;

	
	@BeforeClass
	public static void initToolkit() throws Exception {
	    JFXPanel fxPanel = new JFXPanel();
	    fxPanel.setVisible(false);
	}

	@Before
	public void setUp() throws Exception {
		
		windowsParams = FXCollections.observableArrayList();
		linuxParams = FXCollections.observableArrayList();
		badParams = FXCollections.observableArrayList();
		badIP = FXCollections.observableArrayList();
		apipaIP = FXCollections.observableArrayList();
		badDNS = FXCollections.observableArrayList();
		badGW = FXCollections.observableArrayList();
		
		goodPings = new ListOfPings();
		badPings = new ListOfPings();
		mixPings = new ListOfPings();
		
		for(int i = 0; i < 60; i++) {
			Ping ping = new Ping(i, new Date().getTime(), true);
			goodPings.addPing(ping);
		}
		
		for(int i = 0; i < 60; i++) {
			Ping ping = new Ping((long)(i + 300), new Date().getTime(), false);
			badPings.addPing(ping);
		}
		
		for(int i = 0; i < 60; i++) {
			boolean everyOther = true;
			if((i % 2) == 0) {
				everyOther = !everyOther;
			}
			Ping ping = new Ping(i, new Date().getTime(), everyOther);
			mixPings.addPing(ping);
		}
		

		windowsParams.add(new SystemParameter("Operating System", "Windows"));
		windowsParams.add(new SystemParameter("Host Name", "JUnit"));
		windowsParams.add(new SystemParameter("IP Address", "192.168.1.1"));
		windowsParams.add(new SystemParameter("DNS Resolver", "8.8.8.8"));
		windowsParams.add(new SystemParameter("Netmask", "24"));
		windowsParams.add(new SystemParameter("Default Gateway", "8.8.8.8"));
		
		badParams.add(new SystemParameter("Operating System", "INVALID"));
		badParams.add(new SystemParameter("Host Name", "JUnit"));
		badParams.add(new SystemParameter("IP Address", "192.168.1.1"));
		badParams.add(new SystemParameter("DNS Resolver", "8.8.8.8"));
		badParams.add(new SystemParameter("Netmask", "24"));
		badParams.add(new SystemParameter("Default Gateway", "8.8.8.8"));
		
		linuxParams.add(new SystemParameter("Operating System", "Windows"));
		linuxParams.add(new SystemParameter("Host Name", "JUnit"));
		linuxParams.add(new SystemParameter("IP Address", "192.168.1.1"));
		linuxParams.add(new SystemParameter("DNS Resolver", "8.8.8.8"));
		linuxParams.add(new SystemParameter("Netmask", "24"));
		linuxParams.add(new SystemParameter("Default Gateway", "8.8.8.8"));
		
		badIP.add(new SystemParameter("Operating System", "Windows"));
		badIP.add(new SystemParameter("Host Name", "JUnit"));
		badIP.add(new SystemParameter("IP Address", ""));
		badIP.add(new SystemParameter("DNS Resolver", "8.8.8.8"));
		badIP.add(new SystemParameter("Netmask", "24"));
		badIP.add(new SystemParameter("Default Gateway", "8.8.8.8"));
		
		apipaIP.add(new SystemParameter("Operating System", "Windows"));
		apipaIP.add(new SystemParameter("Host Name", "JUnit"));
		apipaIP.add(new SystemParameter("IP Address", "169.254.1.1"));
		apipaIP.add(new SystemParameter("DNS Resolver", "8.8.8.8"));
		apipaIP.add(new SystemParameter("Netmask", "24"));
		apipaIP.add(new SystemParameter("Default Gateway", "8.8.8.8"));
		
		badDNS.add(new SystemParameter("Operating System", "Windows"));
		badDNS.add(new SystemParameter("Host Name", "JUnit"));
		badDNS.add(new SystemParameter("IP Address", "192.168.1.1"));
		badDNS.add(new SystemParameter("DNS Resolver", ""));
		badDNS.add(new SystemParameter("Netmask", "24"));
		badDNS.add(new SystemParameter("Default Gateway", "8.8.8.8"));
		
		badGW.add(new SystemParameter("Operating System", "Windows"));
		badGW.add(new SystemParameter("Host Name", "JUnit"));
		badGW.add(new SystemParameter("IP Address", "192.168.1.1"));
		badGW.add(new SystemParameter("DNS Resolver", "8.8.8.8"));
		badGW.add(new SystemParameter("Netmask", "24"));
		badGW.add(new SystemParameter("Default Gateway", ""));
		
	}

	@Test
	public void testAnalysisEngine() {
		AnalysisEngine analysisEngine = new AnalysisEngine(goodPings);
		assertNotNull("Constructor Test", analysisEngine);
	}

	@Test
	public void testAnalyze() {
		
		AnalysisEngine analysisEngine = new AnalysisEngine(goodPings);
		analysisEngine.loadParameters(windowsParams);
		assertTrue(analysisEngine.analyze());
		
		analysisEngine.loadParameters(badIP);
		assertFalse(analysisEngine.analyze());
		
		analysisEngine.loadParameters(apipaIP);
		assertFalse(analysisEngine.analyze());
		
		analysisEngine.loadParameters(badDNS);
		assertFalse(analysisEngine.analyze());
		
		analysisEngine.loadParameters(badGW);
		assertFalse(analysisEngine.analyze());
		
		AnalysisEngine badEngine = new AnalysisEngine(badPings);
		badEngine.loadParameters(windowsParams);
		assertFalse(badEngine.analyze());
		
		AnalysisEngine mixEngine = new AnalysisEngine(mixPings);
		mixEngine.loadParameters(windowsParams);
		assertFalse(badEngine.analyze());
	}

	@Test
	public void testLoadParameters() {
		AnalysisEngine analysisEngine = new AnalysisEngine(goodPings);
		assertTrue(analysisEngine.loadParameters(windowsParams));
		assertTrue(analysisEngine.loadParameters(linuxParams));
		assertTrue(analysisEngine.loadParameters(badParams));
	}

}
