package glorydark.dcurrency;

import cn.nukkit.Player;

import java.util.Map;

public class CurrencyAPI {

    public static Map<String, Object> getPlayerConfigs(String playerName) {
        return CurrencyMain.getProvider().getPlayerConfigs(playerName);
    }

    // Offline Usages

    public static double getCurrencyBalance(String playerName, String currencyName) {
        return getCurrencyBalance(playerName, currencyName, 0);
    }

    public static double getCurrencyBalance(String playerName, String currencyName, double defaultValue) {
        return CurrencyMain.getProvider().getCurrencyBalance(playerName, currencyName, defaultValue);
    }

    public static void addCurrencyBalance(String playerName, String currencyName, double count) {
        addCurrencyBalance(playerName, currencyName, count, "plugin");
    }

    public static void addCurrencyBalance(String playerName, String currencyName, double count, String reason) {
        CurrencyMain.getProvider().addCurrencyBalance(playerName, currencyName, count, reason);
    }

    public static void setCurrencyBalance(String playerName, String currencyName, double count, boolean tip) {
        setCurrencyBalance(playerName, currencyName, count, tip, "plugin");
    }

    public static void setCurrencyBalance(String playerName, String currencyName, double count, boolean tip, String reason) {
        CurrencyMain.getProvider().setCurrencyBalance(playerName, currencyName, count, tip, reason);
    }

    public static boolean reduceCurrencyBalance(String playerName, String currencyName, double count) {
        return reduceCurrencyBalance(playerName, currencyName, count, "plugin");
    }

    public static boolean reduceCurrencyBalance(String playerName, String currencyName, double count, String reason) {
        return CurrencyMain.getProvider().reduceCurrencyBalance(playerName, currencyName, count, reason);
    }

    // Online Usages

    public static double getCurrencyBalance(Player player, String currencyName) {
        return getCurrencyBalance(player.getName(), currencyName, 0);
    }

    public static double getCurrencyBalance(Player player, String currencyName, double defaultValue) {
        return CurrencyMain.getProvider().getCurrencyBalance(player.getName(), currencyName, defaultValue);
    }

    public static void addCurrencyBalance(Player player, String currencyName, double count) {
        CurrencyMain.getProvider().addCurrencyBalance(player.getName(), currencyName, count);
    }

    public static void addCurrencyBalance(Player player, String currencyName, double count, String reason) {
        CurrencyMain.getProvider().addCurrencyBalance(player.getName(), currencyName, count, reason);
    }

    public static void setCurrencyBalance(Player player, String currencyName, double count, boolean tip) {
        CurrencyMain.getProvider().setCurrencyBalance(player.getName(), currencyName, count, tip);
    }

    public static void setCurrencyBalance(Player player, String currencyName, double count, boolean tip, String reason) {
        CurrencyMain.getProvider().setCurrencyBalance(player.getName(), currencyName, count, tip, reason);
    }

    public static boolean reduceCurrencyBalance(Player player, String currencyName, double count) {
        return CurrencyMain.getProvider().reduceCurrencyBalance(player.getName(), currencyName, count);
    }

    public static boolean reduceCurrencyBalance(Player player, String currencyName, double count, String reason) {
        return CurrencyMain.getProvider().reduceCurrencyBalance(player.getName(), currencyName, count, reason);
    }

    public static Map<String, Object> getPlayerConfigs(Player player) {
        return CurrencyMain.getProvider().getPlayerConfigs(player.getName());
    }
}
