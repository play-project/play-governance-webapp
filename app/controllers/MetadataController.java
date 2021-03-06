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

import java.util.ArrayList;
import java.util.List;

import models.JSONConfiguration;

import org.ow2.play.metadata.api.Data;
import org.ow2.play.metadata.api.MetaResource;
import org.ow2.play.metadata.api.Metadata;
import org.ow2.play.metadata.api.MetadataException;
import org.ow2.play.metadata.api.Resource;
import org.ow2.play.metadata.api.service.MetadataBootstrap;
import org.ow2.play.metadata.api.service.MetadataService;

import play.data.validation.Required;
import play.jobs.Job;
import play.mvc.With;
import utils.Locator;

/**
 * @author chamerling
 * 
 */
@With(Secure.class)
public class MetadataController extends PlayController {

	/**
	 * @GET
	 */
	public static void list() {
		MetadataService client = null;
		try {
			client = Locator.getMetaService(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		List<MetaResource> list = null;
		try {
			list = client.list();
		} catch (Exception e) {
			flash.error("Unable to get metadata list '%s'", e.getMessage());
		}
		
		render(list);
	}

	/**
	 * @POST
	 * 
	 * @param store
	 */
	public static void loadResources(final @Required String store) {
		MetadataBootstrap client = null;
		try {
			client = Locator.getMetaBootstrap(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		final MetadataBootstrap c = client;
		final List<String> l = new ArrayList<String>();
		l.add(store);
		
		try {
			new Job() {
				@Override
				public void doJob() throws Exception {
					c.init(l);
				}

			}.now();
		} catch (Exception e) {
			e.printStackTrace();
			flash.error("Unable to load metadata '%s'", e.getMessage());
		}
		
		flash.success("Data is loading in the background ('%s')", store);
		load();
	}

	/**
	 * @GET
	 */
	public static void load() {
		List<JSONConfiguration> jsonconfig = JSONConfiguration.all().fetch();
		render(jsonconfig);
	}

	/**
	 * @GET
	 * 
	 * @param name
	 * @param url
	 */
	public static void resource(String name, String url) {
		MetadataService client = null;
		try {
			client = Locator.getMetaService(getNode());
		} catch (Exception e) {
			handleException("Can not get the metadata service", e);
		}
		Resource resource = new Resource(name, url);

		List<Metadata> meta = null;
		try {
			meta = client.getMetaData(resource);
		} catch (MetadataException e) {
			flash.error(e.getMessage());
		}
		MetaResource metaresource = new MetaResource(resource, meta);
		render(metaresource, resource, meta);
	}

	/**
	 * @GET
	 */
	public static void download() {
		MetadataService client = null;
		try {
			client = Locator.getMetaService(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		try {
			renderJSON(client.list());
		} catch (MetadataException e) {
			renderJSON(e.getMessage());
		}
	}

	/**
	 * @POST
	 * 
	 * @param name
	 * @param url
	 */
	public static void createResource(String name, String url) {
		if (name == null || url == null || name.length() == 0
				|| url.length() == 0) {
			flash.error("Null or empty values are not allowed");
			create();
		}

		MetadataService client = null;
		try {
			client = Locator.getMetaService(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		try {
			client.addMetadata(new Resource(name, url), null);
		} catch (MetadataException e) {
			flash.error(e.getMessage());
		}

		resource(name, url);
	}

	public static void metadata() {
		list();
	}

	public static void create() {
		render();
	}

	public static void addMeta(String name, String url, String mname,
			String mtype, String mvalue) {
		if (mname == null || mtype == null || mvalue == null
				|| mname.length() == 0 || mtype.length() == 0
				|| mvalue.length() == 0) {
			flash.error("Null values are not allowed");
			resource(name, url);
		}

		MetadataService client = null;
		try {
			client = Locator.getMetaService(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		try {
			client.addMetadata(new Resource(name, url), new Metadata(mname,
					new Data(mtype, mvalue)));
		} catch (Exception e) {
			flash.error(e.getMessage());
		}
		resource(name, url);
	}

    /**
     * Remove metadata value from its name
     *
     * @param name
     * @param url
     * @param meta
     */
	public static void removeMeta(String name, String url, String meta) {
        if (name == null || url == null || meta == null
                || name.length() == 0 || url.length() == 0
                || meta.length() == 0) {
            flash.error("Null values are not allowed");
            resource(name, url);
        }

        MetadataService client = null;
        try {
            client = Locator.getMetaService(getNode());
        } catch (Exception e) {
            handleException("Locator error", e);
        }

        try {
            // while not provided directly by the API, just get metadata from its name and call remove with the
            // returned value

            Metadata remove = client.getMetadataValue(new Resource(name, url), meta);
            if (remove == null) {
                flash.error("Can not find such resource");
                resource(name, url);
            }

            // let's delete!
            client.removeMetadata(new Resource(name, url), remove);
            flash.success("Metadata '%s' removed from resource!", meta);
        } catch (Exception e) {
            flash.error(e.getMessage());
        }
        resource(name, url);
        list();

	}

	/**
	 * GET
	 * @param name
	 * @param url
	 */
    public static void deleteResource(String name, String url) {
        if (name == null || url == null) {
            flash.error("Can not delete null resource");
            list();
        }

        MetadataService client = null;
        try {
            client = Locator.getMetaService(getNode());
        } catch (Exception e) {
            handleException("Locator error", e);
        }

        try {
            client.deleteResource(new Resource(name, url));
            flash.success("Resource has been deleted!");
        } catch (Exception e) {
            flash.error(e.getMessage());
        }
        list();
    }

}
