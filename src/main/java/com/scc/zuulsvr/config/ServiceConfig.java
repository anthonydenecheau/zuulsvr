package com.scc.zuulsvr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceConfig{

  @Value("${authentification.max.attempt}")
  private int authMaxAttempt;

  @Value("${authentification.recovery.attempt}")
  private int authRecoveryAttempt;

  public int getAuthMaxAttempt(){
	return authMaxAttempt;
  }

  public int getAuthRecoveryAttempt(){
	return authRecoveryAttempt;
  }
   
}
