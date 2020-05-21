package com.ibm.microservices;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeValueRepository extends JpaRepository<ExchangeValue, Long>{
	
	ExchangeValue findByFrom(String from);
	ExchangeValue findByFromAndTo(String from,String to);

}
