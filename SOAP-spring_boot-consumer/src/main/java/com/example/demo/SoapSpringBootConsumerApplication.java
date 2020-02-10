package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.example.consumingwebservice.wsdl.GetCountryRequest;
import com.example.consumingwebservice.wsdl.GetCountryResponse;

@SpringBootApplication
public class SoapSpringBootConsumerApplication  {

	public static void main(String[] args) {
		SpringApplication.run(SoapSpringBootConsumerApplication.class, args);
	}
	@Bean
	  CommandLineRunner lookup(CountryClient quoteClient) {
	    return args -> {
	      String country = "Spain";

	      if (args.length > 0) {
	        country = args[0];
	      }
	      GetCountryResponse response = quoteClient.getCountry(country);
	      System.err.println("Currency: "+response.getCountry().getCurrency()+
	    		  			 "\nCapital: "+response.getCountry().getCapital()+
	    		  			 "\nName: "+response.getCountry().getName()+
	    		  			 "\nPopulation: "+response.getCountry().getPopulation());
	    };
	  }

}

class CountryClient extends WebServiceGatewaySupport {

	  private static final Logger log = LoggerFactory.getLogger(CountryClient.class);

	  public GetCountryResponse getCountry(String country) {

	    GetCountryRequest request = new GetCountryRequest();
	    request.setName(country);

	    log.info("Requesting location for " + country);

	    GetCountryResponse response = (GetCountryResponse) getWebServiceTemplate()
	        .marshalSendAndReceive("http://localhost:8080/ws/countries", request,
	            new SoapActionCallback(
	                "http://spring.io/guides/gs-producing-web-service/GetCountryRequest"));

	    return response;
	  }

	}

@Configuration
class CountryConfiguration {

	  @Bean
	  public Jaxb2Marshaller marshaller() {
	    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
	    // this package must match the package in the <generatePackage> specified in
	    // pom.xml
	    marshaller.setContextPath("com.example.consumingwebservice.wsdl");
	    return marshaller;
	  }

	  @Bean
	  public CountryClient countryClient(Jaxb2Marshaller marshaller) {
	    CountryClient client = new CountryClient();
	    client.setDefaultUri("http://localhost:8080/ws");
	    client.setMarshaller(marshaller);
	    client.setUnmarshaller(marshaller);
	    return client;
	  }

	}