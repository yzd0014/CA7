package pkg.util;

import java.util.ArrayList;

import pkg.order.BuyOrder;
import pkg.order.Order;
import pkg.order.SellOrder;

public class OrderUtility {
	public static boolean isAlreadyPresent(ArrayList<Order> ordersPlaced,
			Order newOrder) {
		for (Order orderPlaced : ordersPlaced) {
			boolean isBuyOrderList = orderPlaced instanceof BuyOrder;
			boolean isSellOrderList = orderPlaced instanceof SellOrder;
			boolean isBuyOrder = newOrder instanceof BuyOrder;
			boolean isSellOrder = newOrder instanceof SellOrder;
                  
                        if ((isBuyOrderList && isBuyOrder) || (isSellOrderList && isSellOrder)){
				if (orderPlaced.getStockSymbol().equals(
						newOrder.getStockSymbol())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean owns(ArrayList<Order> position, String symbol) {
		for (Order stock : position) {
			if (stock.getStockSymbol().equals(symbol)) {
				return true;
			}
		}
		return false;
	}

	public static Order findAndExtractOrder(ArrayList<Order> position,
			String symbol) {
          if(position != null && symbol != null){
		for (Order stock : position) {
			if (stock.getStockSymbol().equals(symbol)) {
				position.remove(stock);
				return stock;
			}
		}
          }
		return null;
	}

	public static int ownedQuantity(ArrayList<Order> position, String symbol) {
		long ownedQuantity = 0;
		for (Order stock : position) {
			if (stock.getStockSymbol().equals(symbol)) {
				ownedQuantity += stock.getSize();
			}
		}
		return ownedQuantity;
	}

}
