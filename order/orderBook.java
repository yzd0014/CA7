package pkg.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.MinimalHTMLWriter;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.market.api.PriceSetter;

public class OrderBook {
	Market market;
	HashMap<String, ArrayList<Order>> buyOrders;
	HashMap<String, ArrayList<Order>> sellOrders;

	public OrderBook(Market market) {
		this.market = market;
		buyOrders = new HashMap<String, ArrayList<Order>>();
		sellOrders = new HashMap<String, ArrayList<Order>>();
	}

	public void addToOrderBook(Order order) {
		// Populate the buyOrders and sellOrders data structures, whichever
		// appropriate
		if (order.getClass() == BuyOrder.class) {
			ArrayList<Order> newOrders = buyOrders.get(order.getStockSymbol());
			if (newOrders == null) {
				newOrders = new ArrayList<Order>();
				newOrders.add(order);
				buyOrders.put(order.getStockSymbol(), newOrders);
			} else {
				newOrders.add(order);
				buyOrders.put(order.getStockSymbol(), newOrders);
			}
		} else {
			ArrayList<Order> newOrders = sellOrders.get(order.getStockSymbol());
			if (newOrders == null) {
				newOrders = new ArrayList<Order>();
				newOrders.add(order);
				sellOrders.put(order.getStockSymbol(), newOrders);
			} else {
				newOrders.add(order);
				sellOrders.put(order.getStockSymbol(), newOrders);
			}
		}
	}

	public void trade() throws StockMarketExpection {
		// Complete the trading.
		// 1. Follow and create the orderbook data representation (see spec)
		// 2. Find the matching price
		// 3. Update the stocks price in the market using the PriceSetter.
		// Note that PriceSetter follows the Observer pattern. Use the pattern.
		// 4. Remove the traded orders from the orderbook
		// 5. Delegate to trader that the trade has been made, so that the
		// trader's orders can be placed to his possession (a trader's position
		// is the stocks he owns)
		// (Add other methods as necessary)
		
		PriceSetter pS = new PriceSetter();
		Double [] price = new Double[40];
		HashMap<Double, Integer> buyOrder = new HashMap<Double, Integer>();
		HashMap<Double, Integer> sellOrder = new HashMap<Double, Integer>();
		ArrayList<Order> allBuyOrders;
		ArrayList<Order> allSellOrders;
		
		Set set = buyOrders.entrySet();
        Iterator i = set.iterator();
        ArrayList<String> stockList = new ArrayList<String>();
        while(i.hasNext()){
        	Map.Entry element = (Map.Entry)i.next();
        	if(checkStockList(stockList, (String)element.getKey()) == 0){
        		stockList.add((String)element.getKey());
        	}
        }
        set = sellOrders.entrySet();
        i = set.iterator();
        while(i.hasNext()){
        	Map.Entry element = (Map.Entry)i.next();
        	if(checkStockList(stockList, (String)element.getKey()) == 0){
        		stockList.add((String)element.getKey());
        	}
        }
        
        for (String n: stockList){
        	allBuyOrders = buyOrders.get(n);
        	allSellOrders = sellOrders.get(n);
        	int j = 0;
        	
        	double dupPrice=0;
        	int flag = 0;
        	int marketBuyOrder = 0;
        	int marketSellOrder = 0;
        	for (Order o: allBuyOrders){
        		if(o.isMarketOrder == true){
        			marketBuyOrder = o.getSize();
        		}
        		else{
        		price[j] = o.getPrice();
        		j++;
        		}
        	}
        	for (Order o : allSellOrders){
        		if(o.isMarketOrder == true){
        			marketSellOrder = o.getSize();
        		}
        		else{
        		dupPrice = o.getPrice();
        		for(int x=0; x< j ; x++){
        			if(price[x] == dupPrice){
        				flag = 1;
        			}
        		}
        		if(flag == 0){
        			price[j]=dupPrice;
        			j++;
        			}
        		}
        	}
        	
        	double temp =0;
        	for (int y=0; y<j-1;y++){
        		for (int x = 0; x < j-1-y; x++){
        		if(price[x]>price[x+1]){
        			temp = price[x];
        			price[x]=price[x+1];
        			price[x+1]= temp;
        			
        		}
        		}
        	}
        	int value = marketBuyOrder;
        	for ( int x = j-1 ; x >=0 ; x--){
        		
        		flag = 0;
        		for(Order o: allBuyOrders){
        			if (o.getPrice() == price[x]){
        				value = value + o.getSize();
        				buyOrder.put(price[x], value);
        				flag = 1;
        			}
        		}
        			if(flag == 0){
        				buyOrder.put(price[x], 0);
        			}
        		
        	}
        	value = marketSellOrder;
        	for ( int x = 0 ; x <= j-1 ; x++){
        		
        		flag = 0;
        		for(Order o: allSellOrders){
        			if (o.getPrice() == price[x]){
        				value = value +o.getSize();
        				sellOrder.put(price[x], value);
        				flag = 1;
        			}
        		}
        			if(flag == 0){
        				sellOrder.put(price[x], 0);
        			}
        		
        		
        		
        	}
        	
        	 
        	 double matchingPrice = 0;
        	 int minVol = 0;
        	 int maxVol = 0;
        	 int bVol =0 ;
        	 int sVol =  0;
        	 for (int x= 0; x<= j-1; x++){
        		 
        		 
        		 bVol = buyOrder.get(price[x]);
        		 sVol = sellOrder.get(price[x]);
        	
        	minVol = Math.min(bVol, sVol);
        	if(maxVol <= minVol && minVol != 0){
        		maxVol = minVol;
        		matchingPrice = price[x];
        	}
        	
        	 }
        	 
        	 int shift = 0;
        	 int pool = 0;
        	 ArrayList<Order> copy = (ArrayList<Order>)allBuyOrders.clone();
	        	for(Order o: copy){
	        		if(o.getPrice() >= matchingPrice||o.isMarketOrder() == true){
	        			if(pool == maxVol){
	        				break;
	        			}
	        			if(o.getSize() + pool - maxVol > 0){
	        				shift = o.getSize() + pool - maxVol;
	        			}
	        			o.setSize(o.getSize() - shift);
	        			pool = pool + o.getSize();
	        			o.getTrader().tradePerformed(o, matchingPrice);
	        			allBuyOrders.remove(o);
	        		}
	        	}
	        	
	        	copy = (ArrayList<Order>)allSellOrders.clone();
	        	shift =0 ;
	        	pool =0 ;
	        	for(Order o: copy){
	        		if(o.getPrice() <= matchingPrice){
	        			if(pool == maxVol){
	        				break;
	        			}
	        			if(o.getSize() + pool - maxVol > 0){
	        				shift = o.getSize() + pool - maxVol;
	        			}
	        			o.setSize(o.getSize() - shift);
	        			pool = pool + o.getSize();
	        			o.getTrader().tradePerformed(o, matchingPrice);
	        			allSellOrders.remove(o);
	        		}
	        	}
	        	
	        	
	    		market.getMarketHistory().setSubject(pS);//let observer be associated with subject
	    		pS.registerObserver(market.getMarketHistory());//register observer
	    		pS.setNewPrice(market, n, matchingPrice);
        	
        }
        
        
		
		
		    }

	private int checkStockList(ArrayList<String> stockList, String n) {
		int bit = 0;
		for (String name : stockList) {
			if (n.equals(name)) {
				bit = 1;
				break;
			}
		}
		return bit;
	}

}
