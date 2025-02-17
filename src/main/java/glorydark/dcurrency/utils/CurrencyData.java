package glorydark.dcurrency.utils;

/**
 * @author glorydark
 */
public class CurrencyData {

    String player;

    double balance;

    public CurrencyData() {
        // no-op
    }

    public double getBalance() {
        return balance;
    }

    public String getPlayer() {
        return player;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
