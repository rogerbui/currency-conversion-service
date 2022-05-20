package com.roger.microservices.currencyconversionservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateConversion(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
            ) {

        HashMap<String, String> urlVariables = new HashMap<>();
        urlVariables.put("from", from);
        urlVariables.put("to", to);
        CurrencyConversion conversion = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversion.class, urlVariables).getBody();

        return new CurrencyConversion(
                conversion.getId(),
                from,
                to,
                conversion.getConversionMultiple(),
                quantity,
                quantity.multiply(conversion.getConversionMultiple()),
                conversion.getEnvironment()
        );
    }


    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateConversionFeign(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    ) {
        CurrencyConversion conversion = currencyExchangeProxy.retrieveExchangeValue(from, to);

        return new CurrencyConversion(
                conversion.getId(),
                from,
                to,
                conversion.getConversionMultiple(),
                quantity,
                quantity.multiply(conversion.getConversionMultiple()),
                conversion.getEnvironment() + " feign"
        );
    }
}
