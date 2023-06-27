
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  private RestTemplate restTemplate;
  
  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        
    // TODO Auto-generated method stub
    String url="https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+symbol+"&apikey="+getToken();
    String str=restTemplate.getForObject(url, String.class);
    System.out.println(str);
    ObjectMapper om=new ObjectMapper();
    om.registerModule(new JavaTimeModule());
    
    AlphavantageDailyResponse alphaResponses=om.readValue(str,AlphavantageDailyResponse.class);
    Map<LocalDate, AlphavantageCandle> allCandles=alphaResponses.getCandles();
    List<Candle> candles=new ArrayList<>();
    for(LocalDate i: allCandles.keySet()){
      if(i.isBefore(to.plusDays(1)) && i.isAfter(from.minusDays(1))){
        AlphavantageCandle acandle=allCandles.get(i);
        acandle.setDate(i);
        candles.add(acandle);
      }
    }

    candles.sort(getComparator());
    // allCandles.keySet().forEach((d)->{
    //   // System.out.println("in forEach");
    //   // if(d.compareTo(from)<0){
    //   //   allCandles.remove(d);
    //   // }else if(d.compareTo(to)>0){
    //   //   allCandles.remove(d);
    //   // }
    //   if(allCandles.get(d).before(fr))
    // });

    // System.out.println("after sort "+allCandles.size());
    // Map<LocalDate, AlphavantageCandle> tm=new TreeMap<>(allCandles);
    
    // for(Map.Entry<LocalDate, AlphavantageCandle> e:tm.entrySet()){
    //   candles.add(e.getValue());
    // }
    // // return Arrays.asList(arr);
    // System.out.println(candles.toArray());
    return candles;
  }
  private Comparator<Candle> getComparator() {
    return Comparator.comparing(Candle::getDate);
  }
  private String getToken() {
    return "FC33BP1W3XIV3TWS";
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  IMP: Do remember to write readable and maintainable code, There will be few functions like
  //    Checking if given date falls within provided date range, etc.
  //    Make sure that you write Unit tests for all such functions.
  //  Note:
  //  1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.

}

