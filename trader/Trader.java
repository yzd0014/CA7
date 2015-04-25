package pkg.trader;

import java.util.ArrayList;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.order.BuyOrder;
import pkg.order.Order;
import pkg.order.OrderType;
import pkg.order.SellOrder;

public class Trader {
	// Name of the trader
	String name;
	// Cash left in the trader's hand
	double cashInHand;
	// Stocks owned by the trader
	ArrayList<Order> position;
	// Orders placed by the trader
	ArrayList<Order> ordersPlaced;

	public Trader(String name, double cashInHand) {
		super();
		this.name = name;
		this.cashInHand = cashInHand;
		this.position = new ArrayList<Order>();
		this.ordersPlaced = new ArrayList<Order>();
	}

	public void buyFromBank(Market m, String symbol, int volume)
			throws StockMarketExpection {
		// Buy stock straight from the bank
		// Need not place the stock in the order list
		// Add it straight to the user's position
		// If the stock's price is larger than the cash possessed, then an
		// exception is thrown
		// Adjust cash possessed since the trader spent money to purchase a
		// stock.
		double price = m.getStockForSymbol(symbol).getPrice();
		if(price * volume > this.cashInHand){
			throw new StockMarketExpection("Cannot place order for stock: " + symbol
					+ " since there is not enough money. Trader: " + this.name);
		}
		Order order = new BuyOrder(symbol, volume, price, this);
		this.position.add(order);
		this.cashInHand = this.cashInHand - price * volume;
	}

	public void placeNewOrder(Market m, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		// Place a new order and add to the orderlist
		// Also enter the order into the orderbook of the market.
		// Note that no trade has been made yet. The order is in suspension
		// until a trade is triggered.
		//
		// If the stock's price is larger than the cash possessed, then an
		// exception is thrown
		// A trader cannot place two orders for the same stock, throw an
		// exception if there are multiple orders for the same stock.
		// Also a person cannot place a sell order for a stock that he does not
		// own. Or he cannot sell more stocks than he possesses. Throw an
		// exception in these cases.
		for(Order o : ordersPlaced){
	    	if(o.getStockSymbol().equals(symbol)){
	    		throw new StockMarketExpection(symbol + " is already placed.");
	    	}
	    }
		Order order;
		if(orderType == OrderType.BUY){
			if(price * volume > this.cashInHand){
				throw new StockMarketExpection("Cannot place order for stock: " + symbol
						+ " since there is not enough money. Trader: " + this.name);
			}
			order = new BuyOrder(symbol, volume, price, this);
			this.ordersPlaced.add(order);
			m.addOrder(order);
		}
		else{
			int bit = 0;
			for(Order o: position){
				if(o.getStockSymbol().equals(symbol)&&o.getSize()>=volume){
					bit = 1;
				}
			}
			if(bit == 0){
				throw new StockMarketExpection("You don't have the stock or enough stocks to sell. Trader: "+ this.name);
			}
			order = new SellOrder(symbol, volume, price, this);
			this.ordersPlaced.add(order);
			m.addOrder(order);
		}
	}

	public void placeNewMarketOrder(Market m, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		// Similar to the other method, except the order is a market order
		for(Order o : ordersPlaced){
	    	if(o.getStockSymbol().equals(symbol)){
	    		throw new StockMarketExpection(symbol + " is already placed.");
	    	}
	    }
		Order order;
		if(orderType == OrderType.BUY){
			order = new BuyOrder(symbol, volume, true, this);
			this.ordersPlaced.add(order);
			m.addOrder(order);
		}
		else{
			int bit = 0;
			for(Order o: position){
				if(o.getStockSymbol().equals(symbol)&&o.getSize()>=volume){
					bit = 1;
				}
			}
			if(bit == 0){
				throw new StockMarketExpection("You don't have the stock or enough stocks to sell. Trader: " + this.name);
			}
			order = new SellOrder(symbol, volume, true, this);
			this.ordersPlaced.add(order);
			m.addOrder(order);
		}
	}

	public void tradePerformed(Order o, double matchPrice)
			throws StockMarketExpection {
		// Notification received that a trade has been made, the parameters are
		// the order corresponding to the trade, and the match price calculated
		// in the order book. Note than an order can sell some of the stocks he
		// bought, etc. Or add more stocks of a kind to his position. Handle
		// these situations.

		// Update the trader's orderPlaced, position, and cashInHand members
		// based on the notification.
		ordersPlaced.remove(o);
		if(o.getClass() == BuyOrder.class){
			this.cashInHand = this.cashInHand - matchPrice*o.getSize();
			this.position.add(o);
		}
		else{
			this.cashInHand = this.cashInHand + matchPrice*o.getSize();
            for(Order O: position){
            	if(O.getStockSymbol().equals(o.getStockSymbol())){
            		if(o.getSize() < O.getSize()){
            			O.setSize(O.getSize() - o.getSize());
            		}
            		else{
            			this.position.remove(O);
            		}
            		break;
            	}
            }
		}		
	}

	public void printTrader() {
		System.out.println("Trader Name: " + name);
		System.out.println("=====================");
		System.out.println("Cash: " + cashInHand);
		System.out.println("Stocks Owned: ");
		for (Order o : position) {
			o.printStockNameInOrder();
		}
		System.out.println("Stocks Desired: ");
		for (Order o : ordersPlaced) {
			o.printOrder();
		}
		System.out.println("+++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++");
	}
}
