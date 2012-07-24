/**
 *
 * Copyright (c) 2012, PetalsLink
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

import java.util.ArrayList;
import java.util.List;

import models.JSONConfiguration;
import models.Node;
import models.RegistryConfiguration;

import org.ow2.play.metadata.api.service.MetadataBootstrap;
import org.ow2.play.service.registry.api.Constants;
import org.ow2.play.service.registry.api.Registry;
import org.ow2.play.service.registry.api.RegistryException;

import play.data.validation.Required;
import play.jobs.Job;

import utils.Locator;

/**
 * @author chamerling
 * 
 */
public class RegistryController extends PlayController {

	/**
	 * Get the list of registered services
	 */
	public static void services() {
		List<Node> services = new ArrayList<Node>();
		Registry registry = Locator.getServiceRegistry(getNode());

		List<String> keys = null;
		try {
			registry.init();
			keys = registry.keys();
		} catch (Exception e) {
			handleException("Can not get service keys", e);
		}

		if (keys == null) {
			flash.error("No services found in the registry");
			load();
		}

		for (String string : keys) {
			Node n = new Node();
			n.name = string;
			try {
				n.baseURL = registry.get(string);
				services.add(n);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		render(services);
	}

	public static void service(String name) {
		Registry client = null;
		try {
			client = Locator.getServiceRegistry(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		String url = null;
		try {
			url = client.get(name);
		} catch (RegistryException e) {
			e.printStackTrace();
			flash.error("Unable to load service %s : '%s'", name,
					e.getMessage());
			services();
		}
		render(name, url);
	}

	public static void update(String name, String url) {
		Registry client = null;
		try {
			client = Locator.getServiceRegistry(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		try {
			client.put(name, url);
			flash.success("Service %s updated", name);
		} catch (RegistryException e) {
			e.printStackTrace();
			flash.error("Unable to update service %s : '%s'", name,
					e.getMessage());
			services();
		}
		service(name);
	}

	/**
	 * @POST
	 * 
	 * @param store
	 */
	public static void loadServices(final @Required String store) {
		Registry client = null;
		try {
			client = Locator.getServiceRegistry(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		try {
			final Registry c = client;
			new Job() {
				@Override
				public void doJob() throws Exception {
					c.load(store);
				}

			}.now();
		} catch (Exception e) {
			e.printStackTrace();
			flash.error("Unable to load services '%s'", e.getMessage());
		}
		
		// client.load(store);
		flash.success("Data is loading in the background from '%s'", store);
		load();
	}

	/**
	 * @GET
	 */
	public static void load() {
		List<RegistryConfiguration> configs = RegistryConfiguration.all()
				.fetch();
		render(configs);
	}

}
