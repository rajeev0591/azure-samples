package com.auth.test.azuresecuritytest.security.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
	@Autowired
	private OAuth2AuthorizedClientService authorizedClientService;

	@GetMapping("group1")
	@ResponseBody
	@PreAuthorize("hasRole('group1')")
	public String group1() {
		return "group1 message";
	}

	@GetMapping("group2")
	@ResponseBody
	@PreAuthorize("hasRole('group2')")
	public String group2() {
		return "group2 message";
	}
	
	@RequestMapping("/oauthinfo")  
    @ResponseBody  
    public String oauthUserInfo(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,  
                              @AuthenticationPrincipal OAuth2User oauth2User) {  
        return  
            "User Name: " + oauth2User.getName() + "<br/>" +  
            "User Authorities: " + oauth2User.getAuthorities() + "<br/>" +  
            "Client Name: " + authorizedClient.getClientRegistration().getClientName() + "<br/>" +  
            this.prettyPrintAttributes(oauth2User.getAttributes());  
    }  
    private String prettyPrintAttributes(Map<String, Object> attributes) {  
        String acc = "User Attributes: <br/><div >";  
        for (String key : attributes.keySet()){  
            Object value = attributes.get(key);  
            acc += "<div>"+key + ": " + value.toString() + "</div>";  
        }  
        return acc + "</div>";  
    }  

	@GetMapping("/account")
	@ResponseBody
	public String getAccount(OAuth2AuthenticationToken authentication) {
		// return
		// SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

		final OAuth2AuthorizedClient authorizedClient = this.authorizedClientService
				.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
		return "Username: " + authentication.getName() + "Client Name: "
				+ authorizedClient.getClientRegistration().getClientName() + " Principal Authoritites: "
				+ authentication.getAuthorities().toString();
	}

	@GetMapping("/")
	public String index(Model model, OAuth2AuthenticationToken authentication) {

		final OAuth2AuthorizedClient authorizedClient = this.authorizedClientService
				.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), authentication.getName());
		model.addAttribute("userName", authentication.getName());
		model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientName());
		return "index.html";

	}
}