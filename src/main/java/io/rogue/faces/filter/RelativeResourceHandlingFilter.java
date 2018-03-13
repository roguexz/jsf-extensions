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

package io.rogue.faces.filter;

import javax.faces.application.ResourceHandler;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Certain web resources (like CSS / Fonts) tend to use relative positioning to locate each other. This works well in
 * the case of standard web applications. But if you need to include them in a Faces application (as embedded resources),
 * this can become a hassle. This filter attempts to rectify such scenarios by looking at the "referer" URL and then
 * redirecting the browser to the correct resource.
 */
@WebFilter(urlPatterns = ResourceHandler.RESOURCE_IDENTIFIER + "/*")
public class RelativeResourceHandlingFilter implements Filter {

    private static final String CLASS_NAME = RelativeResourceHandlingFilter.class.getName();
    private static Logger LOGGER = Logger.getLogger(CLASS_NAME);

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final String METHOD_NAME = "doFilter";
        boolean requestRedirected = false;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            if (!req.getRequestURI().contains(".xhtml")) {
                // This is the scenario where a relative path has been requested. Fix it.
                String referrer = req.getHeader("referer");
                if (referrer != null) {
                    // Extract the library name from the referrer.
                    String queryParams = new URL(referrer).getQuery();
                    Map<String, String> params = new HashMap<>();
                    for (String s : queryParams.split("&")) {
                        String[] tmp = s.split("=");
                        params.put(tmp[0], tmp.length == 2 ? tmp[1] : null);
                    }
                    String libraryName = params.get("ln");
                    if (libraryName != null) {
                        String redirectURL = req.getRequestURI() + ".xhtml?ln=" + libraryName;
                        LOGGER.logp(Level.FINE, CLASS_NAME, METHOD_NAME, "Redirecting the request to: {0}", redirectURL);
                        ((HttpServletResponse) response).sendRedirect(redirectURL);
                        requestRedirected = true;
                    }
                }
            }
        }
        if (!requestRedirected) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
