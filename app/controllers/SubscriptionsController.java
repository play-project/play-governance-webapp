/**
 *
 * Copyright (c) 2013, Linagora
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 *
 */
package controllers;

import java.util.List;

import models.Message;

import org.ow2.play.governance.api.GovernanceExeption;
import org.ow2.play.governance.api.SubscriptionManagement;
import org.ow2.play.governance.api.SubscriptionRegistry;
import org.ow2.play.governance.api.SubscriptionService;
import org.ow2.play.governance.api.bean.Subscription;
import org.ow2.play.governance.api.bean.Topic;

import play.jobs.Job;
import play.mvc.With;
import utils.Locator;

/**
 * @author chamerling
 * 
 */
@With(Secure.class)
public class SubscriptionsController extends PlayController {

	/**
	 * Get all the subscriptions
	 * 
	 */
	public static void subscriptions() {
		SubscriptionRegistry registry = null;
		try {
			registry = Locator.getSubscriptionRegistry(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		List<Subscription> subscriptions = null;
		try {
			subscriptions = registry.getSubscriptions();
		} catch (GovernanceExeption e) {
			e.printStackTrace();
		}
		render(subscriptions);
	}

	public static void subscription(String id) {
		handleException("Null id", new Exception(
				"Can not get subscription from null ID"));

		SubscriptionRegistry registry = null;
		try {
			registry = Locator.getSubscriptionRegistry(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		Subscription filter = new Subscription();
		filter.setId(id);
		List<Subscription> subscriptions = null;
		try {
			subscriptions = registry.getSubscriptions(filter);
		} catch (GovernanceExeption e) {
			e.printStackTrace();
		}

		Subscription subscription = null;
		if (subscriptions != null && subscriptions.size() > 0) {
			subscription = subscriptions.get(0);
		}
		render(subscription);
	}

	/**
	 * Create page from null args
	 * 
	 */
	public static void create() {
		createFrom("", "", "");
	}

	/**
	 * GET
	 * 
	 * Create page from args
	 * 
	 * @param topicname
	 * @param topicns
	 * @param topicprefix
	 */
	public static void createFrom(String topicname, String topicns,
			String topicprefix) {
		// flash parameters to inject them in the template (this flash stuff is
		// also used when validating form data
		params.flash();

		renderTemplate("SubscriptionsController/create.html");
	}

	/**
	 * POST. Creates a subscription.
	 * 
	 * @param consumer
	 * @param provider
	 * @param topicName
	 * @param topicNS
	 * @param topicPrefix
	 */
	public static void createNew(String consumer, String provider,
			String topicname, String topicns, String topicprefix, boolean save) {

		validation.required(consumer);
		validation.required(provider);

		// validation url does not allow IP address...
		validation.isTrue(consumer != null
				&& (consumer.startsWith("http://") || consumer
						.startsWith("https://")));
		validation.isTrue(provider != null
				&& (provider.startsWith("http://") || provider
						.startsWith("https://")));

		validation.required(topicname);
		validation.url(topicns);
		validation.required(topicprefix);

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			createFrom(topicname, topicns, topicprefix);
		}

		try {
			SubscriptionService client = Locator
					.getSubscriptionService(getNode());

			Subscription subscription = new Subscription();
			subscription.setProvider(provider);
			subscription.setSubscriber(consumer);
			Topic topic = new Topic();
			topic.setName(topicname);
			topic.setNs(topicns);
			topic.setPrefix(topicprefix);
			subscription.setTopic(topic);

			Subscription result = client.subscribe(subscription);

			if (result != null) {
				flash.success("Subscription has been created %s",
						result.toString());
			}

			if (result != null && save) {
				// register
				SubscriptionRegistry registry = Locator
						.getSubscriptionRegistry(getNode());
				registry.addSubscription(result);
			}

		} catch (GovernanceExeption ge) {
			handleException("Can not subscribe", ge);
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		// Forward to subscription service
		create();
	}

	/**
	 * GET
	 * 
	 * Remove all the subscriptions from the registry. This does not means that
	 * we unregister, we just delete from storage, that's all...
	 */
	public static void removeAll() {
		try {
			SubscriptionRegistry client = Locator
					.getSubscriptionRegistry(getNode());

			client.removeAll();
			flash.success("Subscriptions have been removed");

		} catch (Exception e) {
			handleException("Problem while getting client", e);
		}
		subscriptions();
	}

	public static void manage() {
		render();
	}

	/**
	 * POST Remove all teh subscriptions for the given subscriber. Will get the
	 * list of all subscription with this ID and then call unsubscribe with the
	 * subscripton ID.
	 * 
	 * @param subscriber
	 */
	public static void removeAllFromSubscriber(final String url) {
		validation.required(url);

		// validation url does not allow IP address...
		validation.isTrue(url != null
				&& (url.startsWith("http://") || url.startsWith("https://")));

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			manage();
		}

		SubscriptionManagement client = null;
		try {
			client = Locator.getSubscriptionManagement(getNode());
		} catch (Exception e) {
			handleException("Can not get subscription management client", e);
		}

		try {
			final SubscriptionManagement c = client;
			new Job() {
				@Override
				public void doJob() throws Exception {
					List<Subscription> result = c
							.unsubscribeAllForSubscriber(url);
					System.out.println("Results from background task ...");
					for (Subscription subscription : result) {
						// TODO : Push to client with Web socket
						System.out.println("Background result : "
								+ subscription);
						Message message = new Message();
						message.title = "Unsubscribe All Result";
						message.content = subscription.getStatus();
						BackgroundTaskWebSocket.liveStream.publish(message);
					}
				}
			}.now();
		} catch (Exception e) {
			e.printStackTrace();
			flash.error("Unable to remove subscriptions '%s'", e.getMessage());
		}
		flash.success("Subscription(s) are removed in the background...", url);

		manage();
	}
	
	/**
	 * Unsubscribe from a subscription UUID
	 * 
	 * @param uuid
	 */
	public static void unsubscribeFromUUID(final String uuid) {
		validation.required(uuid);

		// validation url does not allow IP address...
		validation.isTrue(uuid != null && (uuid.length() > 0));

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			manage();
		}

		SubscriptionManagement client = null;
		SubscriptionRegistry registry = null;
		try {
			registry = Locator.getSubscriptionRegistry(getNode());
			client = Locator.getSubscriptionManagement(getNode());
		} catch (Exception e) {
			handleException("Can not get subscription management client", e);
		}

		try {
			final SubscriptionManagement c = client;
			final SubscriptionRegistry r = registry;
			new Job() {
				@Override
				public void doJob() throws Exception {
					
					// get the subscription from the registry
					Subscription filter = new Subscription();
					filter.setId(uuid);
					List<Subscription> list = r.getSubscriptions(filter);
					
					if (list == null || list.size() == 0) {
						System.out.println("Subscription can not be found on regstry");
						return;
					}
					
					List<Subscription> result = c.unsubscribe(list);
					System.out.println("Results from background task unsubscribe...");
					for (Subscription subscription : result) {
						// TODO : Push to client with Web socket
						System.out.println("Background result : "
								+ subscription);
						Message message = new Message();
						message.title = "Unsubscribe UUID Result";
						message.content = subscription.getStatus();
						BackgroundTaskWebSocket.liveStream.publish(message);
					}
				}
			}.now();
		} catch (Exception e) {
			e.printStackTrace();
			flash.error("Unable to remove subscription '%s'", e.getMessage());
		}
		flash.success("Subscription is removed in the background...");

		manage();
	}

	/**
	 * POST. Search subscriptions.
	 * 
	 * 
	 * @param subscriber
	 * @param producer
	 * @param topicName
	 * @param topicNS
	 * @param topicPrefix
	 */
	public static void search(String consumer, String provider,
			String topicname, String topicns, String topicprefix) {
		try {
			SubscriptionRegistry client = Locator
					.getSubscriptionRegistry(getNode());

			Subscription filter = new Subscription();
			if (consumer != null && consumer.length() > 0) {
				filter.setSubscriber(consumer);
			}

			if (provider != null && provider.length() > 0) {
				filter.setProvider(provider);
			}

			Topic topic = new Topic();
			if (topicname != null && topicname.length() > 0) {
				topic.setName(topicname);
				filter.setTopic(topic);
			}

			long start = System.currentTimeMillis();
			List<Subscription> subscriptions = client.getSubscriptions(filter);
			long stop = System.currentTimeMillis() - start;
			if (subscriptions == null) {
				flash.error("No result");
			} else {
				flash.success("Subscription search results : " + subscriptions.size() + " (took " + stop + " ms)");
			}
			render("SubscriptionsController/subscriptions.html", subscriptions);

		} catch (Exception e) {
			handleException("Problem while getting client", e);
		}
		flash.error("Can not find any subscription");
		subscriptions();
	}
	
	public static void getSubscribersForTopic(String topicname, String topicns, String topicprefix) {
		try {
			SubscriptionRegistry client = Locator
					.getSubscriptionRegistry(getNode());

			Subscription filter = new Subscription();

			Topic topic = new Topic();
			if (topicname != null && topicname.length() > 0) {
				topic.setName(topicname);
				filter.setTopic(topic);
			}

			long start = System.currentTimeMillis();
			List<Subscription> subscriptions = client.getSubscriptions(filter);
			long stop = System.currentTimeMillis() - start;
			if (subscriptions == null) {
				flash.error("No result");
			} else {
				flash.success("Subscription search results : " + subscriptions.size() + " (took " + stop + " ms)");
			}
			render("SubscriptionsController/subscriptions.html", subscriptions);

		} catch (Exception e) {
			handleException("Problem while getting client", e);
		}
		flash.error("Can not find any subscription");
		subscriptions();
	}
}
