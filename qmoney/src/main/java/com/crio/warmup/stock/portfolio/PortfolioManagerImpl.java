
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;

  public PortfolioManagerImpl(StockQuotesService stockQuotesService){
    this.stockQuotesService=stockQuotesService;
  }

  public PortfolioManagerImpl() {}

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }








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
        // String tingoResponses=restTemplate.getForObject(url,String.class);
        // System.out.println(tingoResponses);
        // ObjectMapper om=new ObjectMapper();
        // om.registerModule(new JavaTimeModule());
        // System.out.println(url);
        // Candle[] arr=om.readValue(tingoResponses, TiingoCandle[].class);
        Candle[] arr=restTemplate.getForObject(url, TiingoCandle[].class);
        // System.out.println(Arrays.asList(arr));
        if(arr==null){
          return new ArrayList<>();
        }
        return Arrays.asList(arr);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
            + "startDate="+startDate+"&endDate="+endDate+"&token="+PortfolioManagerApplication.getToken();
      return uriTemplate;
  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws StockQuoteServiceException {
    // TODO Auto-generated method stub
      // String token=PortfolioManagerApplication.getToken();
      List<AnnualizedReturn> annualizedReturns=new ArrayList<>();
    // List<TotalReturnsDto> totalReturnsDtos = new ArrayList<>();
    try {
      for(PortfolioTrade pt:portfolioTrades){
        
        List<Candle> list;
        
          list = stockQuotesService.getStockQuote(pt.getSymbol(), pt.getPurchaseDate(), endDate);
          if(list!=null){
            pt.setPurchaseDate(list.get(0).getDate());
            AnnualizedReturn ar=PortfolioManagerApplication.calculateAnnualizedReturns(list.get(list.size()-1).getDate(), pt,list.get(0).getOpen(), list.get(list.size()-1).getClose());
            annualizedReturns.add(ar);
          } 
          
      }
      Collections.sort(annualizedReturns,getComparator());
      return annualizedReturns;
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        throw new StockQuoteServiceException("message");
      }
        
    
   

     
  }

  // @Override
  // public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
  //     List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
  //     throws InterruptedException, StockQuoteServiceException {
  //   // TODO Auto-generated method stub
    
  //   //Requared Result list
  //   List<AnnualizedReturn> list=new ArrayList<>();

  //   //Creating the jobs to be Execute
  //   List<TaskCallable> jobs=new ArrayList<>();
  //   for (int i = 0; i< portfolioTrades.size(); i++) {
  //       TaskCallable task = new TaskCallable(endDate,portfolioTrades.get(i));
  //       jobs.add(task);
  //   }
  //   //Creating n Number Of Thread pool
  //   ExecutorService service=Executors.newFixedThreadPool(numThreads);

  //   //DOing the task by thread
  //   for(TaskCallable job:jobs){
  //     Future<AnnualizedReturn> f= service.submit(job);
  //     try {
  //       list.add(f.get());
  //     } catch (ExecutionException e) {
  //       // TODO Auto-generated catch block
  //       e.printStackTrace();
  //       throw new StockQuoteServiceException("message");
  //     }
  //   }
  //   service.shutdown();
  //   Collections.sort(list,getComparator());
  //   return list;
  // }
  // class TaskCallable implements Callable{
  //   PortfolioTrade trades;
  //   LocalDate endDate;
  
  
  //   public TaskCallable(LocalDate endDate, PortfolioTrade trades) {
  //       this.endDate = endDate;
  //       this.trades = trades;
  //   }
  
  
  //   @Override
  //   public AnnualizedReturn call() throws Exception {
  //     // TODO Auto-generated method stub
  //     List<Candle> candles = stockQuotesService.getStockQuote(trades.getSymbol(), trades.getPurchaseDate(), endDate);
  
  //     Candle tiingoCandle = candles.get(0);
  //     Candle tiingoCandlelast = candles.get(candles.size()-1);
  
  //     AnnualizedReturn annualizedReturn = PortfolioManagerApplication.calculateAnnualizedReturns(tiingoCandlelast.getDate(), trades, tiingoCandle.getOpen(), tiingoCandlelast.getClose());
  //       return annualizedReturn;
  //   }
  // }
    @Override
    public List<AnnualizedReturn> calculateAnnualizedReturnParallel(
        List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads)
        throws InterruptedException, StockQuoteServiceException {
      
          ExecutorService executor = Executors.newFixedThreadPool(numThreads);

            List<Future<AnnualizedReturn>> futures = new ArrayList<>();
            //
            for (int i = 0; i < numThreads && i< portfolioTrades.size(); i++) {
                Callable<AnnualizedReturn> task = new TaskCallable(endDate,portfolioTrades.get(i));
                Future<AnnualizedReturn> future = executor.submit(task);
                futures.add(future);
            }

            // Collect the results from the futures
            List<AnnualizedReturn> results = new ArrayList<>();
            for (Future<AnnualizedReturn> future : futures) {
                try {
                    AnnualizedReturn annualizedReturn = future.get();
                    results.add(annualizedReturn);
                } catch (InterruptedException | ExecutionException e) {
                    throw new StockQuoteServiceException("Rate limit excided");
                }
            }
            executor.shutdown();
            Collections.sort(results,getComparator());
            return results;
        }

    class TaskCallable implements Callable<AnnualizedReturn> {
        PortfolioTrade trades;
        LocalDate endDate;


        public TaskCallable(LocalDate endDate, PortfolioTrade trades) {
            this.endDate = endDate;
            this.trades = trades;
        }

        @Override
        public AnnualizedReturn call() throws Exception {
          List<Candle> candles = stockQuotesService.getStockQuote(trades.getSymbol(), trades.getPurchaseDate(), endDate);
          // if(candles != null){
            Candle tiingoCandle = candles.get(0);
            Candle tiingoCandlelast = candles.get(candles.size()-1);
            AnnualizedReturn annualizedReturn = PortfolioManagerApplication.calculateAnnualizedReturns(tiingoCandlelast.getDate(), trades, tiingoCandle.getOpen(), tiingoCandlelast.getClose());
            return annualizedReturn;
        }
    }
}

