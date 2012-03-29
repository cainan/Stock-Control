package br.csoliveira.stockcontrol.model.database;

public interface DatabaseInterface {
    void onSuccess();
    void onSuccess(Object obj);
    void onError();
}
