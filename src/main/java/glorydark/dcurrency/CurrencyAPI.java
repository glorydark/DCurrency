package glorydark.dcurrency;

import cn.nukkit.Player;

import java.util.Map;

public class CurrencyAPI {

    public static double getCurrencyBalance(String playerName, String currencyName) {
        return getCurrencyBalance(playerName, currencyName, 0);
    }


    public static double getCurrencyBalance(String playerName, String currencyName, double defaultValue) {
        return CurrencyMain.getProvider().getCurrencyBalance(playerName, currencyName, defaultValue);
    }

    public static void addCurrencyBalance(String playerName, String currencyName, double count) {
        CurrencyMain.getProvider().addCurrencyBalance(playerName, currencyName, count);
    }

    public static void setCurrencyBalance(String playerName, String currencyName, double count, boolean tip) {
        CurrencyMain.getProvider().setCurrencyBalance(playerName, currencyName, count, tip);
    }

    public static boolean reduceCurrencyBalance(String playerName, String currencyName, double count) {
        return CurrencyMain.getProvider().reduceCurrencyBalance(playerName, currencyName, count);
    }

    public static Map<String, Object> getPlayerConfigs(String playerName) {
        return CurrencyMain.getProvider().getPlayerConfigs(playerName);
    }

    public static double getCurrencyBalance(Player player, String currencyName) {
        return getCurrencyBalance(player.getName(), currencyName, 0);
    }

    public static double getCurrencyBalance(Player player, String currencyName, double defaultValue) {
        return CurrencyMain.getProvider().getCurrencyBalance(player.getName(), currencyName, defaultValue);
    }

    public static void addCurrencyBalance(Player player, String currencyName, double count) {
        CurrencyMain.getProvider().addCurrencyBalance(player.getName(), currencyName, count);
    }

    public static void setCurrencyBalance(Player player, String currencyName, double count, boolean tip) {
        CurrencyMain.getProvider().setCurrencyBalance(player.getName(), currencyName, count, tip);
    }

    public static boolean reduceCurrencyBalance(Player player, String currencyName, double count) {
        return CurrencyMain.getProvider().reduceCurrencyBalance(player.getName(), currencyName, count);
    }

    public static Map<String, Object> getPlayerConfigs(Player player) {
        return CurrencyMain.getProvider().getPlayerConfigs(player.getName());
    }
}
