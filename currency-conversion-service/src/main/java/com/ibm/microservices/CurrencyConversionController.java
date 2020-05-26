package com.ibm.microservices;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class CurrencyConversionController {
	
	private Logger logger =  LoggerFactory.getLogger(this.getClass());
	
    @Autowired
	private CurrencyExchangeServiceProxy proxy;
    
   
    @PostMapping("/currency-add-conversion-factor-feign/from/{from}/conversionFactor/{conversionFactor}")
    public void addConversionFactorFeign(@PathVariable String from,@PathVariable BigDecimal conversionFactor) {
	
    	proxy.addExchangeValue(from, conversionFactor);

    }
    
    @PutMapping("/currency-update-conversion-factor-feign/from/{from}/conversionFactor/{conversionFactor}")
    public void updateConversionFactorFeign(@PathVariable String from,@PathVariable BigDecimal conversionFactor) {
	
    	proxy.updateExchangeValue(from, conversionFactor);

    }
	
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	CurrencyConversionBean convertCurrency(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity) {
		
		Map<String, String> uriVariables = new HashMap<>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConversionBean> responseEntity=new RestTemplate().getForEntity("http://localhost:8001/currency-exchange/from/{from}/to/{to}",CurrencyConversionBean.class,uriVariables);
		
		CurrencyConversionBean response = responseEntity.getBody();
		return new CurrencyConversionBean(response.getId(),from,to,response.getConversionFactor(),quantity,quantity.multiply(response.getConversionFactor()),response.getPort());
	}
	
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	@HystrixCommand(fallbackMethod = "convertCurrencyFeign_Fallback")
	CurrencyConversionBean convertCurrencyFeign(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity) {
		
		CurrencyConversionBean response = proxy.getExchangeValue(from, to);
		
		logger.info("{} "+response);
		
		return new CurrencyConversionBean(response.getId(),from,to,response.getConversionFactor(),quantity,quantity.multiply(response.getConversionFactor()),response.getPort());
	}
	
	   	@SuppressWarnings("unused")
	   	CurrencyConversionBean convertCurrencyFeign_Fallback(@PathVariable String from,@PathVariable String to,@PathVariable BigDecimal quantity) {
	 
	        logger.info("currency-exchange-service is down!!! fallback route enabled...");
	 
	        logger.info("CIRCUIT BREAKER ENABLED!!! No Response From currency-exchange-service at this moment. " +
	                    " Service will be back shortly - " + new Date());
	        return new CurrencyConversionBean();
	    }
	
}
