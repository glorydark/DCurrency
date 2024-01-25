package glorydark.dcurrency.provider;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyProvider {

    double getCurrencyBalance(String playerName, String currencyName);

    double getCurrencyBalance(String playerName, String currencyName, double defaultValue);

    void addCurrencyBalance(String playerName, String currencyName, double count);

    void setCurrencyBalance(String playerName, String currencyName, double count, boolean tip);

    boolean reduceCurrencyBalance(String playerName, String currencyName, double count);

    Map<String, Object> getPlayerConfigs(String playerName);

    default double add(double origin, double value) {
        return new BigDecimal(origin).add(new BigDecimal(value)).doubleValue();
    }
}
