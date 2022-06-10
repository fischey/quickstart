package org.jboss.as.quickstart.hibernate.model;

public class Stock implements java.io.Serializable {
  private Integer stockId;
  private String stockCode;
  private String stockName;

  public Integer getStockId() {
    return stockId;
  }

  public void setStockId(final Integer stockId) {
    this.stockId = stockId;
  }

  public String getStockCode() {
    return stockCode;
  }

  public void setStockCode(final String stockCode) {
    this.stockCode = stockCode;
  }

  public String getStockName() {
    return stockName;
  }

  public void setStockName(final String stockName) {
    this.stockName = stockName;
  }
}