/**
 * 
 */
package io.spire.tests;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.spire.Spire;
import io.spire.api.Account;
import io.spire.api.Channel;
import io.spire.api.Channel.Channels;
import io.spire.api.Event;
import io.spire.api.Events;
import io.spire.api.Listener;
import io.spire.api.Message.MessageOptions;
import io.spire.api.Api.ApiDescriptionModel;
import io.spire.api.Subscription;
import io.spire.api.Subscription.Subscriptions;
import io.spire.request.ResponseException;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author Jorge Gonzalez
 * 
 */
public class SpireTest {

//	public static final String SPIRE_URL = "http://localhost:1337";
	public static final String SPIRE_URL = "http://build.spire.io";

	private Spire spire;
	private ApiDescriptionModel description;
	private String secret;
	private String email;
	private String password;

	public SpireTest() {

	}

	private Spire createSpire(ApiDescriptionModel description) {
		Spire spire = new Spire(SPIRE_URL);
		if (description != null)
			spire.getApi().setApiDescription(description);
		return spire;
	}

	private String uniqueEmail() {
		Date now = new Date();
		return "test+jclient+" + now.getTime() + "@spire.io";
	}
	
	@SuppressWarnings("unused")
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
		secret = spire.getSession().getAccount().getSecret();
	}

	@Test
	public void discover() throws Exception {
		spire.discover();
		assertNotNull(spire.getApi().getApiDescription());
	}

	@Test
	public void start() throws Exception {
		Spire spire = createSpire(description);
		spire.start(secret);
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
		/* Account CORS */
		// ** commented out until branch #137 is merged in **
		/*
		account.getOrigin().setHost("test.com");
		account.getOrigin().setScheme("http");
		account.getOrigin().setPort(8080);
		*/
		account.setCompany(companyName);
		account.setName(accountName);
		account.update();
		assertEquals("Update company name", account.getCompany(), companyName);
		assertEquals("Update account name", account.getName(), accountName);
		/* Account CORS */
		// ** commented out until branch #137 is merged in **
		//assertEquals(account.getOrigin().getHost(), "test.com");
		
		Account account2 = new Account(description.getSchema());
		account2.setCapability(account.getCapability());
		account2.setUrl(account.getUrl());
		account2.get();
		assertEquals(account.getSecret(), account2.getSecret());
		assertEquals(account.getName(), account2.getName());
		
		/* Account CORS */
		/* ** commented out until branch #137 is merged in **
		assertEquals(account.getOrigin().getHost(), account2.getOrigin().getHost());
		assertEquals(account.getOrigin().getHost(), account2.getOrigin().getHost());
		assertEquals(account.getOrigin().getHost(), account2.getOrigin().getHost());
		*/
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
		spire2.start(secret);
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
		Channel channel = new Channel(description.getSchema());
		channel.setName("foo_channel");
		Subscription subscription = channel.subscribe("bar_subscription", spire.getSession());
		assertNotNull(subscription);
		assertNotNull(channel.getCapability());
		assertNotNull(channel.getUrl());
	}
	
	@Test
	public void channelPublish() throws Exception {
		Channel channel = new Channel(description.getSchema());
		channel.setName("foo_channel");
		Subscription subscription1 = channel.subscribe("bar_subscription", spire.getSession());
		channel.publish("the great message");
		
		MessageOptions options = new MessageOptions();
		Events events = subscription1.retrieveMessages(options);
		assertEquals(events.getMessages().size(), 1);
		assertEquals(events.getMessages().get(0).getContent(), "the great message");
		
		Spire spire2 = createSpire(description);
		spire2.start(secret);
		Subscription subscription2 = spire2.subscribe("bar_subscription", channel.getName());
		Events events2 = subscription2.retrieveMessages(options);
		assertEquals(events2.getMessages().size(), 1);
		assertEquals(events2.getMessages().get(0).getContent(), "the great message");
	}
	
	@Test
	public void poll() throws Exception {
		Channel channel = new Channel(description.getSchema());
		channel.setName("foo_channel");
		Subscription subscription1 = channel.subscribe("bar_subscription", spire.getSession());
		
		int nSent = 3;
		for (int i = 1; i <= nSent; i++) {
			channel.publish("the great message" + i);
		}
		
		MessageOptions options = new MessageOptions();
		
		Events events = subscription1.poll(options);
		int nReceived = events.getMessages().size();
		assertEquals(nSent, nReceived);
		assertEquals(events.getMessages().get(nReceived-1).getContent(), "the great message" + nReceived);
		
		events = subscription1.poll(options);
		assertEquals(events.getMessages().size(), 0);
		int count = 2;
		for (int i = nReceived+1; i <= nReceived+count; i++) {
			channel.publish("the great message" + i);
		}
		
		events = subscription1.poll(options);
		assertEquals(events.getMessages().size(), 2);
		assertEquals(events.getMessages().get(count-1).getContent(), "the great message" + (nReceived+count));
	}

	@SuppressWarnings("unused")
	private class MessagePublisher implements Runnable {
		Channel channel;
		
		public MessagePublisher(Channel channel){
			this.channel = channel;
		}
		
		@Override
		public void run() {
			try {
				channel.publish("the great message1");
				channel.publish("the great message2");
				channel.publish("the great message3");
			} catch (ResponseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class MessageListenerWorker implements Runnable {
		Subscription subscription;
		
		public MessageListenerWorker(Subscription subscription){
			this.subscription = subscription;
		}
		
		@Override
		public void run() {
			try {
				MessageOptions options = new MessageOptions();
				// default timeout is 30 seconds
				options.timeout = 10;
				Events events = subscription.longPoll(options);
				assertEquals(events.getMessages().size(), 1);
				assertEquals(events.getMessages().get(0).getContent(), "the great message1");
			} catch (ResponseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void longPoll() throws Exception {
		Channel channel = Spire.SpireFactory.createChannel();
		channel.setName("foo_channel");
		Subscription subscription1 = channel.subscribe("bar_subscription", spire.getSession());
		
		// get subscription 'join' events
		Events events = subscription1.poll(new MessageOptions());
		assertEquals(events.getJoins().size(), 1);
		
		Thread threadListener = new Thread(new MessageListenerWorker(subscription1));
		threadListener.start();
		
		// quick test that long poll waits for arriving messages 
		Thread.sleep(3 * 1000);

		channel.publish("the great message1");
		channel.publish("the great message2");
		channel.publish("the great message3");
		
		threadListener.join();
		
		events = subscription1.poll(new MessageOptions());
		assertEquals(events.getMessages().size(), 2);
	}
	
	private class MyListener1 implements Listener{

		@Override
		public void process(Event message) {
			try {
				// simulate some work....
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class MyListener2 implements Listener{

		@Override
		public void process(Event message) {
			try {
				// simulate some work....
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Test
	public void testListeners() throws Exception {
		Subscription subscription1 = spire.subscribe("subscription1", "channel1");
		Channel channel = spire.getChannels().getChannel("channel1");
		MyListener1 myListener1 = new MyListener1();
		MyListener2 myListener2 = new MyListener2();
		
		subscription1.addListener(myListener1);
		
		MessageOptions options = new MessageOptions();
		options.timeout = 4;
		subscription1.startListening(options);
		for (int i = 0; i < 4; i++) {
			channel.publish("message " + i);
		}
		subscription1.stopListening();
		
		subscription1.addListener(myListener2);
		subscription1.startListening(options);
		for (int i = 4; i < 7; i++) {
			channel.publish("message " + i);
		}
		subscription1.stopListening();
	}
	
	public static void main(String[] args) {
		System.out.println("Running tests...");
		org.junit.runner.JUnitCore.runClasses(SpireTest.class);
		System.out.println("Spire test complete!");
	}
}
