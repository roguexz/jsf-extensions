/*
 * Copyright 2018, Rogue.IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.rogue.faces.application;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.application.ResourceHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code AppResourceHandler} generates resources with absolute URLs to the actual location in the /resources
 * directory. This handler facilitates the integration of various open source technologies in to a JSF based
 * application.
 */
public class AppResourceHandler extends ResourceHandlerWrapper {

    private static final String CLASS_NAME = AppResourceHandler.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    private ResourceHandler wrapped;
    private String resourcesRoot;

    public AppResourceHandler(ResourceHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ResourceHandler getWrapped() {
        return wrapped;
    }

    @Override
    public Resource createResource(String resourceName, String libraryName) {
        Resource resource = super.createResource(resourceName, libraryName);
        return getWrappedResource(resource);
    }

    /**
     * If the given resource object can be rendered locally, then do so by
     * returning a wrapped object, otherwise return the input as is.
     */
    private Resource getWrappedResource(Resource resource) {
        final String METHOD_NAME = "getWrappedResource";
        WebAppResource webAppResource = null;
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        initResourcesRoot(context);

        if (resource != null) {
            URL baseURL = resource.getURL();
            if (baseURL != null) {
                String extForm = baseURL.toExternalForm();
                int idx = extForm.indexOf(resourcesRoot);
                if (idx != -1) {
                    try {
                        extForm = extForm.substring(idx);
                        URL resourceURL = context.getResource(extForm);
                        if (resourceURL != null) {
                            webAppResource = new WebAppResource(extForm, resource);
                        }
                    } catch (MalformedURLException e) {
                        LOGGER.logp(Level.FINEST, CLASS_NAME, METHOD_NAME, "Failed to identify resource.", e);
                    }
                }
            }
        }
        return webAppResource != null ? webAppResource : resource;
    }

    /**
     * Initialize the location of the webapp resources folder.
     *
     * @param context the current instance of ExternalContext.
     */
    private void initResourcesRoot(ExternalContext context) {
        if (resourcesRoot == null) {
            resourcesRoot = context.getInitParameter(ResourceHandler.WEBAPP_RESOURCES_DIRECTORY_PARAM_NAME);
            if (resourcesRoot == null) {
                resourcesRoot = "/resources";
            }
        }
    }
}
