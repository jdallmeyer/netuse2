package test;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import data.ListOfPings;
import javafx.embed.swing.JFXPanel;
import ping.Pinger;

public class PingerTest {

	@BeforeClass
	public static void initToolkit() throws Exception {
	    JFXPanel fxPanel = new JFXPanel();
	}

	@Test
	public void testPinger() {
		Pinger pingDNSName = new Pinger("www.google.com", false, new ListOfPings());
		assertNotNull(pingDNSName);
		
		Pinger pingByIP = new Pinger("8.8.8.8", false, new ListOfPings());
		assertNotNull(pingByIP);
	}

	@Test
	public void testDoPing() {
		ListOfPings temp = new ListOfPings();
		Pinger pingDNSNameRecord = new Pinger("www.google.com", false, temp);
		assertTrue("Ping Google with record flag cleared", pingDNSNameRecord.doPing());
		assertTrue("Is array empty", temp.getTotalPings() == 0);
		
		
		Pinger pingDNSNameNoRecord = new Pinger("www.google.com", true, temp);
		assertTrue("Ping Google with record flag set", pingDNSNameNoRecord.doPing());
		assertTrue("Is array empty", temp.getTotalPings() == 1);
		temp.reset();
		
		assertTrue("Has array been cleared", temp.getTotalPings() == 0);
				
		Pinger multiplePings = new Pinger("www.google.com", true, temp);
		multiplePings.doPing();
		multiplePings.doPing();
		multiplePings.doPing();
		multiplePings.doPing();
		multiplePings.doPing();
		assertTrue("5x Ping with record flag set", temp.getTotalPings() == 5);
		temp.reset();
		
		Pinger pingByIPNoRecord = new Pinger("8.8.8.8", false, temp);
		assertTrue("Ping by IP with record flag cleared", pingByIPNoRecord.doPing());
		assertTrue("Is array empty", temp.getTotalPings() == 0);
		temp.reset();

		Pinger pingByIPRecord = new Pinger("8.8.8.8", true, temp);
		assertTrue("Ping by IP with record flag set", pingByIPRecord.doPing());
		assertTrue("Is array empty", temp.getTotalPings() == 1);


	}

}
