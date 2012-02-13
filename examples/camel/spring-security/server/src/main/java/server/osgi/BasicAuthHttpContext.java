/**
 * Copyright (C) 2010 Talend Inc. - www.talend.com
 */
package server.osgi;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * <p>
 * Delegates to the filter to do the authentication and requires that the authentication
 * was succesfull.
 * </p>
 */
public class BasicAuthHttpContext implements HttpContext {

    private Filter filter;
    private AuthenticationEntryPoint authenticationEntryPoint;

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public String getMimeType(String arg0) {
        return null;
    }

    @Override
    public URL getResource(String arg0) {
        return null;
    }

    @Override
    public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SecurityContextHolder.getContext().setAuthentication(null);
            DummyFilterChain dummyFilter = new DummyFilterChain();
            filter.doFilter(request, response, dummyFilter);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                AuthenticationException ex = new AuthenticationCredentialsNotFoundException("Anonymous access denied");
                authenticationEntryPoint.commence(request, response, ex);
                return false;
            }
            return dummyFilter.isCalled();
        } catch (ServletException e) {
            return false;
        }
    }
    
    class DummyFilterChain implements FilterChain {
        boolean called;

        public boolean isCalled() {
            return called;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException,
            ServletException {
            called=true;
        }
        
    }

}
