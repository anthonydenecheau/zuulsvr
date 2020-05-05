package com.scc.zuulsvr.config;

import org.springframework.beans.factory.annotation.Value;

public class AuthenticateConfig {

   @Value("${example.property}")
   private String example;

   public String getExample() {
      return example;
   }

}
