package glorydark.dcurrency.provider;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyProvider {

    double getCurrencyBalance(String playerName, String currencyName);

    double getCurrencyBalance(String playerName, String currencyName, double defaultValue);

    default void addCurrencyBalance(String playerName, String currencyName, double count) {
        addCurrencyBalance(playerName, currencyName, count, "default");
    }

    void addCurrencyBalance(String playerName, String currencyName, double count, String reason);

    default void setCurrencyBalance(String playerName, String currencyName, double count, boolean tip) {
        setCurrencyBalance(playerName, currencyName, count, tip, "default");
    }

    void setCurrencyBalance(String playerName, String currencyName, double count, boolean tip, String reason);

    default boolean reduceCurrencyBalance(String playerName, String currencyName, double count) {
        return reduceCurrencyBalance(playerName, currencyName, count, "default");
    }

    boolean reduceCurrencyBalance(String playerName, String currencyName, double count, String reason);

    Map<String, Object> getPlayerConfigs(String playerName);

    default double add(double origin, double value) {
        return new BigDecimal(origin).add(new BigDecimal(value)).doubleValue();
    }
}
