
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.portfolio.PortfolioManagerImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
         + "startDate="+startDate+"&endDate="+endDate+"&token="+PortfolioManagerApplication.getToken();
   return uriTemplate;
  }
  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    // TODO Auto-generated method stub
    String url=buildUri(symbol, from, to);
    String str=restTemplate.getForObject(url, String.class);
    ObjectMapper om=new ObjectMapper();
    om.registerModule(new JavaTimeModule());
    Candle[] arr=om.readValue(str,TiingoCandle[].class);
    return Arrays.asList(arr);
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
