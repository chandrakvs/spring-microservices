package com.ibm.microservices;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyExchangeController {

	private Logger logger =  LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private ExchangeValueRepository exchangeValueRepository;
	
	@PostMapping("/currency-exchange/from/{from}/conversionFactor/{conversionFactor}")
	public void addExchangeValue(@PathVariable String from,@PathVariable BigDecimal conversionFactor) {
		ExchangeValue exchangeValue=new ExchangeValue();
		exchangeValue.setFrom(from);
		exchangeValue.setConversionFactor(conversionFactor);
		exchangeValue.setTo("INR");
		exchangeValueRepository.save(exchangeValue);
		
	}
	
	@PutMapping("/currency-exchange/from/{from}/conversionFactor/{conversionFactor}")
	public void updateExchangeValue(@PathVariable String from,@PathVariable BigDecimal conversionFactor) {
		ExchangeValue exchangeValue=exchangeValueRepository.findByFrom(from);
		exchangeValue.setConversionFactor(conversionFactor);
		exchangeValueRepository.save(exchangeValue);
		
	}
	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public ExchangeValue getExchangeValue(@PathVariable String from,@PathVariable String to) {
		ExchangeValue exchangeValue=exchangeValueRepository.findByFromAndTo(from, to);
		exchangeValue.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
		logger.info("{} "+exchangeValue);
		return exchangeValue;
	}
	

}
