package com.scc.zuulsvr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zuulservice")
public class AuthenticateConfig {

   @Value("${example.property}")
   private String example;

   public String getExample() {
      return example;
   }

}
