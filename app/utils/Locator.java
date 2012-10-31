/**
 * 
 */
package utils;

import models.Node;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.ow2.play.governance.api.BootSubscriptionService;
import org.ow2.play.governance.api.EventGovernance;
import org.ow2.play.governance.api.PatternRegistry;
import org.ow2.play.governance.api.SubscriptionManagement;
import org.ow2.play.governance.api.SubscriptionRegistry;
import org.ow2.play.governance.api.SubscriptionService;
import org.ow2.play.governance.api.TopicAware;
import org.ow2.play.metadata.api.service.MetadataBootstrap;
import org.ow2.play.metadata.api.service.MetadataService;
import org.ow2.play.service.registry.api.Constants;
import org.ow2.play.service.registry.api.Registry;
import org.petalslink.dsb.jbi.se.wsn.api.StatsService;

/**
 * Deprecated...
 * 
 * @author chamerling
 * 
 */
public class Locator {
	
	public static PatternRegistry getPatternRegistry(Node node) throws Exception {
		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.GOVERNANCE_PATTERN_REGISTRY);

		if (url == null) {
			throw new Exception(
					"Can not find the pattern endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(PatternRegistry.class);
		Object o = factory.create();
		return PatternRegistry.class.cast(o);
	}

	public static MetadataService getMetaService(Node node) throws Exception {

		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.METADATA);

		if (url == null) {
			throw new Exception(
					"Can not find the metadata endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(MetadataService.class);
		Object o = factory.create();
		return MetadataService.class.cast(o);
	}

	public static MetadataBootstrap getMetaBootstrap(Node node)
			throws Exception {

		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.METADATA + ".boot");

		if (url == null) {
			throw new Exception(
					"Can not find the metadata endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(MetadataBootstrap.class);
		Object o = factory.create();
		return MetadataBootstrap.class.cast(o);
	}

	public static Registry getServiceRegistry(Node node) {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(node.baseURL);
		factory.setServiceClass(Registry.class);
		Object o = factory.create();
		return Registry.class.cast(o);
	}

	public static EventGovernance getEventGovernance(Node node)
			throws Exception {
		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.GOVERNANCE);

		if (url == null) {
			throw new Exception(
					"Can not find the topic endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(EventGovernance.class);
		Object o = factory.create();
		return EventGovernance.class.cast(o);
	}
	
	public static SubscriptionRegistry getSubscriptionRegistry(Node node)
			throws Exception {
		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.GOVERNANCE_SUBSCRIPTION_REGISTRY);

		if (url == null) {
			throw new Exception(
					"Can not find the topic endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(SubscriptionRegistry.class);
		Object o = factory.create();
		return SubscriptionRegistry.class.cast(o);
	}
	
	public static SubscriptionService getSubscriptionService(Node node) throws Exception {
		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.GOVERNANCE_SUBSCRIPTION_SERVICE);

		if (url == null) {
			throw new Exception(
					"Can not find the subscription service endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(SubscriptionService.class);
		Object o = factory.create();
		return SubscriptionService.class.cast(o);
	}
	
	public static BootSubscriptionService getBootSubscriptionService(Node node) throws Exception {
		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.GOVERNANCE_BOOTSUBSCRIPTION_REGISTRY);

		if (url == null) {
			throw new Exception(
					"Can not find the boot subscription service endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(BootSubscriptionService.class);
		Object o = factory.create();
		return BootSubscriptionService.class.cast(o);
	}
	
	public static SubscriptionManagement getSubscriptionManagement(Node node) throws Exception {
		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.GOVERNANCE_SUBSCRIPTIONMANAGEMENT_SERVICE);

		if (url == null) {
			throw new Exception(
					"Can not find the boot subscription service endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(SubscriptionManagement.class);
		Object o = factory.create();
		return SubscriptionManagement.class.cast(o);
	}
	
	public static TopicAware getDSBTopicManagement(Node node) throws Exception {
		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.DSB_BUSINESS_TOPIC_MANAGEMENT);

		if (url == null) {
			throw new Exception(
					"Can not find the DSB Topic Management service endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(TopicAware.class);
		Object o = factory.create();
		return TopicAware.class.cast(o);
	}
	
	public static StatsService getDSBStatsService(Node node) throws Exception {
		Registry registry = getServiceRegistry(node);
		String url = registry.get(Constants.DSB_BUSINESS_TOPIC_STATS);

		if (url == null) {
			throw new Exception(
					"Can not find the DSB Topic Stats service endpoint in the registry");
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setAddress(url);
		factory.setServiceClass(StatsService.class);
		Object o = factory.create();
		return StatsService.class.cast(o);
	}
	

}
