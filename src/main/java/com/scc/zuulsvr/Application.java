package com.scc.zuulsvr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import com.scc.zuulsvr.utils.UserContextInterceptor;

import java.util.Collections;
import java.util.List;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableZuulProxy
@EnableSwagger2
@RefreshScope
public class Application {

   @LoadBalanced
   @Bean
   public RestTemplate getRestTemplate() {
      RestTemplate template = new RestTemplate();
      List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();
      if (interceptors == null) {
         template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
      } else {
         interceptors.add(new UserContextInterceptor());
         template.setInterceptors(interceptors);
      }

      return template;
   }

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   /*
   @Bean
   UiConfiguration uiConfig() {
      return new UiConfiguration("validatorUrl", "list", "alpha", "schema",
            UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L);
   }
   */
}
