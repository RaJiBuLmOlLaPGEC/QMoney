
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private RestTemplate restTemplate;

  public PortfolioManagerImpl(){

  }

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        String url=buildUri(symbol, from, to);
        Candle[] arr=restTemplate.getForObject(url, TiingoCandle[].class);
        return Arrays.asList(arr);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
            + "startDate="+startDate+"&endDate="+endDate+"&token="+PortfolioManagerApplication.getToken();
      return uriTemplate;
  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws JsonProcessingException {
    // TODO Auto-generated method stub
      // String token=PortfolioManagerApplication.getToken();
      List<AnnualizedReturn> annualizedReturns=new ArrayList<>();
    // List<TotalReturnsDto> totalReturnsDtos = new ArrayList<>();
    for(PortfolioTrade pt:portfolioTrades){
      List<Candle>list=getStockQuote(pt.getSymbol(), pt.getPurchaseDate(), endDate);
      if(list!=null){
        pt.setPurchaseDate(list.get(0).getDate());
        AnnualizedReturn ar=PortfolioManagerApplication.calculateAnnualizedReturns(list.get(list.size()-1).getDate(), pt,list.get(0).getOpen(), list.get(list.size()-1).getClose());
        annualizedReturns.add(ar);
      }   
    }
    Collections.sort(annualizedReturns,getComparator());
    // Collections.sort(annualizedReturns,new Comparator<AnnualizedReturn>() {
    //   @Override
    //         public int compare(AnnualizedReturn t1, AnnualizedReturn t2) {
    //           if(t1.getAnnualizedReturn()>t2.getAnnualizedReturn()){
    //             return -1;
    //           }else if(t1.getAnnualizedReturn()==t2.getAnnualizedReturn()) return 0;
    //           return +1;
    //         }
    // });
    


     return annualizedReturns;
  }
}