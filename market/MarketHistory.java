package pkg.market;

import pkg.exception.StockMarketExpection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pkg.market.api.IObserver;
import pkg.market.api.ISubject;
import pkg.stock.Stock;

public class MarketHistory implements IObserver {
	private ISubject subject;
	Market market;
	Map<String, List<Double>> history;

	public MarketHistory(Market market) {
		super();
		this.market = market;
		history = new HashMap<String, List<Double>>();
	}

	@Override
	public void setSubject(ISubject priceSetter) {
		this.subject = priceSetter;
	}

	public void startHistoryWithPrice(String symbol, Double newPrice)
			throws StockMarketExpection {
		if (!history.containsKey(symbol)) {
			List<Double> priceList = new ArrayList<Double>();
			priceList.add(newPrice);
			history.put(symbol, priceList);
		}
	}

	@Override
	public void update() {
		Stock updatedStock = (Stock) subject.getUpdate();
		//If there are no stocks in the market.
                if (market.getStockForSymbol(updatedStock.getSymbol()) == null) {
			return;
		}
		if (history.containsKey(updatedStock.getSymbol())) {
			existStockUpdate(updatedStock);
                }
                else {
			// New entry to history
			newStockUpdate(updatedStock);
		}
	}
  
        private void existStockUpdate(Stock updatedStock) {
		List<Double> priceList = history.get(updatedStock.getSymbol());
		priceList.add(updatedStock.getPrice());
		history.put(updatedStock.getSymbol(), priceList);
	}
  
        private void newStockUpdate(Stock updatedStock) {
		List<Double> priceList = new ArrayList<Double>();
		priceList.add(updatedStock.getPrice());
		history.put(updatedStock.getSymbol(), priceList);
	}

	public ArrayList<Double> getPriceListFor(String symbol) {
		if (history.containsKey(symbol)) {
			return (ArrayList<Double>) history.get(symbol);
		} else {
			return new ArrayList<Double>();
		}
	}
}
