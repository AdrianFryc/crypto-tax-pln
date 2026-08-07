package pl.cryptotax.domain.service;

import pl.cryptotax.domain.model.CryptoTransaction;
import pl.cryptotax.domain.model.TaxSummary;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;

public class TaxCalculationService {

    private final NbpClient nbpClient;

    public TaxCalculationService(NbpClient nbpClient) {
        this.nbpClient = nbpClient;
    }

    public TaxSummary calculateTax(List<CryptoTransaction> transactions){
        BigDecimal totalIncome = new BigDecimal(0);
        BigDecimal totalCost = new BigDecimal(0);
        BigDecimal netProfitPln = new BigDecimal(0);

//        transactions.forEach(transaction -> {
//            var trasnactionDate = transaction.transactionDate().atZone(ZoneId.of("Europe/Warsaw")).toLocalDate();
//            if(transaction.fiatCurrency().contains("PLN")){
//
//            }else{
//
//            }
//            var exchangeRate = nbpClient.getExchangeRate(transaction.fiatCurrency(), trasnactionDate);
//        });



        return null;
    }
}
