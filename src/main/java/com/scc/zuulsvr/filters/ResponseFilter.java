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
public class ResponseFilter extends ZuulFilter {
	private static final int FILTER_ORDER = 1;
	private static final boolean SHOULD_FILTER = true;
	private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

	@Autowired
    LoginAttemptService loginAttemptService;

	@Override
	public String filterType() {
		return FilterUtils.POST_FILTER_TYPE;
	}

	@Override
	public int filterOrder() {
		return FILTER_ORDER;
	}

	@Override
	public boolean shouldFilter() {
		return SHOULD_FILTER;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.getResponse().addHeader("scc-correlation-id", "[TODO]");
		
		// loginFailed ! 
		final String ip = getClientIP(ctx.getRequest());
		if (ctx.getResponseStatusCode() == HttpServletResponse.SC_UNAUTHORIZED )
			loginAttemptService.loginFailed(ip);
		
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
