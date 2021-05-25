package com.gemini.demo;

public class GeminiNewBrokerorderRequest {
    private String request;
    private int nonce;
    private String source_counterparty_id;
    private String target_counterparty_id;
    private String symbol;
    private String amount;
    private double expires_in_hrs;
    private String price;
    private String side;
    
	public GeminiNewBrokerorderRequest() {
	}
	public GeminiNewBrokerorderRequest(String request, int nonce, String source_counterparty_id,
			String target_counterparty_id, String symbol, String amount, double expires_in_hrs, String price,
			String side) {
		super();
		this.request = request;
		this.nonce = nonce;
		this.source_counterparty_id = source_counterparty_id;
		this.target_counterparty_id = target_counterparty_id;
		this.symbol = symbol;
		this.amount = amount;
		this.expires_in_hrs = expires_in_hrs;
		this.price = price;
		this.side = side;
	}

	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public int getNonce() {
		return nonce;
	}
	public void setNonce(int nonce) {
		this.nonce = nonce;
	}
	public String getSource_counterparty_id() {
		return source_counterparty_id;
	}
	public void setSource_counterparty_id(String source_counterparty_id) {
		this.source_counterparty_id = source_counterparty_id;
	}
	public String getTarget_counterparty_id() {
		return target_counterparty_id;
	}
	public void setTarget_counterparty_id(String target_counterparty_id) {
		this.target_counterparty_id = target_counterparty_id;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public double getExpires_in_hrs() {
		return expires_in_hrs;
	}
	public void setExpires_in_hrs(double expires_in_hrs) {
		this.expires_in_hrs = expires_in_hrs;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}

}
