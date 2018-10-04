package com.scc.zuulsvr.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.scc.zuulsvr.config.ServiceConfig;

@Service
@RefreshScope
public class LoginAttemptService {

	@Autowired
	protected ServiceConfig config;

	private int MAX_ATTEMPT;
    private int DELAY_ATTEMPT;
    private boolean ACTIVE_LOCK;
    private LoadingCache<String, Integer> attemptsCache;

    @PostConstruct
    public void init() {
        MAX_ATTEMPT = config.getAuthMaxAttempt();
        DELAY_ATTEMPT = config.getAuthRecoveryAttempt();
        ACTIVE_LOCK = (MAX_ATTEMPT == -1 ? false : true);
        
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(DELAY_ATTEMPT, TimeUnit.MINUTES).build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(final String key) {
                return 0;
            }
        });
    }
    
    public void loginSucceeded(final String key) {
    	if (!ACTIVE_LOCK)
    		return;
    	
        attemptsCache.invalidate(key);
    }

    public void loginFailed(final String key) {

    	if (!ACTIVE_LOCK)
    		return;
        
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(final String key) {
    	
    	if (!ACTIVE_LOCK)
    		return false;

    	try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (final ExecutionException e) {
            return false;
        }
    }
}