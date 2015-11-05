package com.example;

import javax.ws.rs.Produces;

import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableOAuth2Sso
class Application {

  @RequestMapping("/")
  @Produces("text/json")
  public String home() {
	  System.out.println("==> Before returning from home()");
    return "GitHub Autorization succeeded, Response from Spring cloud application";
  }

}
