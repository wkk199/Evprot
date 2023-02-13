package com.evport.businessapp.data.bean;

import androidx.annotation.Keep;

@Keep
public class RemoteDataBean {

    String api;
    String message;
    String connectorId;
    String chargeboxId;
    String percent;
    String reason;
    String transactionPk;


    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getChargeboxId() {
        return chargeboxId;
    }

    public void setChargeboxId(String chargeboxId) {
        this.chargeboxId = chargeboxId;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTransactionPk() {
        return transactionPk;
    }

    public void setTransactionPk(String transactionPk) {
        this.transactionPk = transactionPk;
    }
}


