package com.scc.zuulsvr.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.scc.zuulsvr.services.LoginAttemptService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackingFilter extends ZuulFilter {
	private static final int FILTER_ORDER = 1;
	private static final boolean SHOULD_FILTER = true;
	private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

	@Autowired
	FilterUtils filterUtils;

	@Autowired
    LoginAttemptService loginAttemptService;
	
	@Override
	public String filterType() {
		return FilterUtils.PRE_FILTER_TYPE;
	}

	@Override
	public int filterOrder() {
		return FILTER_ORDER;
	}

	public boolean shouldFilter() {
		return SHOULD_FILTER;
	}

	private boolean isAuthentificationKeyIsPresent() {
		if (filterUtils.getAuthentificationKey() != null) {
			return true;
		}

		return false;
	}

	public Object run() {
		
		RequestContext ctx = RequestContext.getCurrentContext();

		final String ip = getClientIP(ctx.getRequest());
		
		// Pour la consultation de la doc Swagger, pas de blocage d'IP
		if (ctx.getRequest().getRequestURI().indexOf("api-docs") > 0 ) {
			logger.debug("Consultation Swagger documentation: {} [{}].", ctx.getRequest().getRequestURI(), ip);
			return null;
		}
		
        // l'IP est bloquée jusqu'à expiration du cache
        if (loginAttemptService.isBlocked(ip)) {
			HttpServletResponse httpServletResponse = (HttpServletResponse) ctx.getResponse();
			httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			logger.error("Authentification, quota reached : [{}].", ip);
			ctx.setSendZuulResponse(false);
			return null;
        }
        
		if (isAuthentificationKeyIsPresent()) {
			logger.debug("Authentification key found in tracking filter: {} [{}].", filterUtils.getAuthentificationKey(), ip);
		} else {
			// loginFailed 
			logger.error("Authentification key not found: Non Authorized [{}].", ip);
			loginAttemptService.loginFailed(ip);
		}

		logger.debug("Processing incoming request for {} [{}].", ctx.getRequest().getRequestURI(), ip);
		return null;
	}
	
    private final String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}