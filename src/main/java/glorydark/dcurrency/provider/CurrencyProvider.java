package glorydark.dcurrency.provider;

import cn.nukkit.Player;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyProvider {

    default double getCurrencyBalance(Player player, String currencyName) {
        return getCurrencyBalance(player.getName(), currencyName);
    }

    double getCurrencyBalance(String playerName, String currencyName);

    default double getCurrencyBalance(Player player, String currencyName, double defaultValue) {
        return getCurrencyBalance(player.getName(), currencyName, defaultValue);
    }

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

    Map<String, Double> getPlayerCurrencyData(String playerName);

    Map<String, Double> getAllPlayerData(String currencyName);

    default double add(double origin, double value) {
        return new BigDecimal(origin).add(new BigDecimal(value)).doubleValue();
    }
}
