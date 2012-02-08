/**
 * 
 */
package io.spire.tests;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.spire.Spire;
import io.spire.api.Account;
import io.spire.api.Billing;
import io.spire.api.Billing.Plan;
import io.spire.api.Billing.Plans;
import io.spire.api.BillingSubscription;
import io.spire.api.Channel;
import io.spire.api.Channel.Channels;
import io.spire.api.Session;
import io.spire.api.Api.APIDescriptionModel;
import io.spire.api.Subscription;
import io.spire.api.Subscription.Subscriptions;
import io.spire.request.ResponseException;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author jorge
 * 
 */
public class SpireTest {

	public static final String SPIRE_URL = "http://localhost:1337";

	private Spire spire;
	private APIDescriptionModel description;
	private String key;
	private String email;
	private String password;

	public SpireTest() {

	}

	private Spire createSpire(APIDescriptionModel description) {
		Spire spire = new Spire(SPIRE_URL);
		if (description != null)
			spire.getApi().setApiDescription(description);
		return spire;
	}

	private String uniqueEmail() {
		Date now = new Date();
		return "test+jclient+" + now.getTime() + "@spire.io";
	}
	
	private void print(String string){
		System.out.println(string);
	}

	@Before
	public void setUp() throws Exception {
		email = uniqueEmail();
//		print(email);
//		email = "test+1326765873.501@spire.io";
		password = "carlospants";
		spire = createSpire(null);
		spire.discover();
		description = spire.getApi().getApiDescription();
		spire.register(email, password, null);
//		spire.login(email, password);
		key = spire.getSession().getAccount().getKey();
	}

	@Test
	public void discover() throws Exception {
		spire.discover();
		assertNotNull(spire.getApi().getApiDescription());
	}

	@Test
	public void start() throws Exception {
		Spire spire = createSpire(description);
		spire.start(key);
		assertNotNull(spire.getSession());
	}

	@Test
	public void login() throws Exception {
		Spire spire = createSpire(description);
		spire.login(email, password);
		assertNotNull(spire.getSession());
		assertNotNull(spire.getSession().getAccount());
	}

	@Test
	public void register() throws Exception {
		Spire spire = createSpire(description);
		String email = uniqueEmail();
		String password = "somepassword";
		spire.register(email, password, null);
		assertNotNull(spire.getApi());
		assertNotNull(spire.getSession());
		assertNotNull(spire.getSession().getAccount());
	}

	@Test
	public void getAccount() throws Exception {
		// account data
		Account account = spire.getSession().getAccount();
		assertNotNull(account);
		assertNotNull(account.getCapability());
		assertNotNull(account.getEmail());
		// account billing subscription data
		BillingSubscription billing = account.getBilling();
		assertNotNull(billing);
		assertNotNull(billing.getCapability());
		assertNotNull(billing.getId());
		assertNotNull(billing.getInvoices());
		assertNotNull(billing.getInvoices().getUpcomingInvoice());
	}
	
	@Test
	public void getChannels() throws Exception {
		Channels channels = spire.getChannels();
		assertNotNull(channels);
		assertNotNull(channels.getCapability());
		assertNotNull(channels.getUrl());
	}
	
	@Test
	public void update() throws Exception {
		Account account = spire.getSession().getAccount();
		String companyName = "The Company";
		String accountName = "Account Name";
		account.getOrigin().setHost("test.com");
		account.getOrigin().setScheme("http");
		account.getOrigin().setPort(8080);
		account.setCompany(companyName);
		account.setName(accountName);
		account.update();
		assertEquals("Update account company", account.getCompany(), companyName);
		assertEquals("Update account company", account.getName(), accountName);
		assertEquals(account.getOrigin().getHost(), "test.com");
//		print(account.getKey());
		
		Account account2 = new Account(description.schema);
		account2.setCapability(account.getCapability());
		account2.setUrl(account.getUrl());
		account2.get();
		assertEquals(account.getKey(), account2.getKey());
		assertEquals(account.getName(), account2.getName());
		// test account origin properties
		assertEquals(account.getOrigin().getHost(), account2.getOrigin().getHost());
		assertEquals(account.getOrigin().getHost(), account2.getOrigin().getHost());
		assertEquals(account.getOrigin().getHost(), account2.getOrigin().getHost());
	}
	
	@Test
	public void billing() throws Exception {
		Billing billing = spire.billing();
		assertNotNull(billing);
		assertNotNull(billing.getUrl());
		assertNotNull(billing.getPlans());
		List<Plan> plans = billing.getPlans();
		Plan p = plans.get(0);
		assertNotNull(p.getFeatures());
		assertNotNull(p.getFeatures().getQueue());
	}
	
	@Test
	public void channels() throws Exception {
		Channels channels = spire.channels();
		assertNotNull(channels);
		assertNotNull(channels.getCapability());
		assertNotNull(channels.getUrl());
	}
	
	@Test
	public void createChannel() throws Exception {
		String channelName = "SomeChannel0001";
		Channel channel = null;
		channel = spire.createChannel(channelName);
		assertNotNull(channel);
		assertNotNull(spire.getChannels().getChannel(channelName));
		
		Spire spire2 = createSpire(description);
		spire2.start(key);
		Channels channels = spire.channels();
		assertNotNull(channels.getChannel(channelName));
	}
	
	@Test
	public void getSubscriptions() throws Exception {
		Subscriptions subscriptions = spire.getSubscriptions();
		assertNotNull(subscriptions);
		assertNotNull(subscriptions.getUrl());
		assertNotNull(subscriptions.getCapability());
	}
	
	@Test
	public void subscriptions() throws Exception {
		Subscriptions subscriptions = spire.subscriptions();
		assertNotNull(subscriptions);
		assertNotNull(subscriptions.getUrl());
		assertNotNull(subscriptions.getCapability());
	}
	
	@Test
	public void subscribe() throws Exception {
		String[] channelList = {"bar1_channel", "bar2_channel", "bar3_channel"};
		String subscriptionName = "foo_subscription";
		Subscription subscription = spire.subscribe(subscriptionName, channelList);
		assertNotNull(subscription);
		assertNotNull(subscription.getUrl());
		assertNotNull(subscription.getCapability());
		assertEquals(subscription.getName(), subscriptionName);
		
		// check that the channels where created
		List<String> channels = subscription.getChannels();
		assertNotNull(channels);
		for (int i = 0; i < channelList.length; i++) {
			Channel channel = spire.getChannels().getChannel(channelList[i]);
			assertEquals(channels.get(i), channel.getUrl());
		}
		
		// Subscription should be in the current session subscriptions 
		Subscriptions subscriptions = spire.getSubscriptions();
		assertNotNull(subscriptions);
		assertEquals(subscriptions.size(), 1);
		assertNotNull(subscriptions.getSubscription(subscriptionName));
	}
	
	@Test
	public void channelSubscribe() throws Exception {
		Channel channel = new Channel(description.schema);
		channel.setName("foo_channel");
		Subscription subscription = channel.subscribe("bar_subscription", spire.getSession());
		assertNotNull(subscription);
		assertNotNull(channel.getKey());
		assertNotNull(channel.getCapability());
		assertNotNull(channel.getUrl());
	}
	
	@Test
	public void channelPublish() throws Exception {
		Channel channel = new Channel(description.schema);
		channel.setName("foo_channel");
		Subscription subscription = channel.subscribe("bar_subscription", spire.getSession());
		channel.publish("the great message");
	}
}
