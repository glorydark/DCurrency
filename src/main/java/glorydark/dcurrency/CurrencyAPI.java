package glorydark.dcurrency;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.provider.CurrencyJsonProvider;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class CurrencyAPI {

    public static Map<String, Object> getPlayerConfigs(String playerName) {
        return CurrencyMain.getProvider().getPlayerCurrencyData(playerName);
    }

    public static List<String> getAllPlayers() {
        if (CurrencyMain.getProvider() instanceof CurrencyJsonProvider) {
            List<String> results = new ArrayList<>();
            File file = new File(CurrencyMain.getInstance().getPath() + "/players/");
            for (File json : Objects.requireNonNull(file.listFiles())) {
                results.add(json.getName().substring(0, json.getName().lastIndexOf(".")));
            }
            return results;
        } else {
            CurrencyMain.getInstance().getLogger().error("Mysql Provider is not allowed to use getAllPlayers()");
            return new ArrayList<>();
        }
    }

    public static Map<String, Double> getPlayerAllCurrencyData(String currencyName) {
        if (CurrencyMain.getProvider() instanceof CurrencyJsonProvider) {
            Map<String, Double> map = new HashMap<>();
            File file = new File(CurrencyMain.getInstance().getPath() + "/players/");
            for (File json : Objects.requireNonNull(file.listFiles())) {
                Config config = new Config(json, Config.JSON);
                map.put(json.getName().substring(0, json.getName().lastIndexOf(".")), config.getDouble(currencyName));
            }
            return map;
        } else {
            CurrencyMain.getInstance().getLogger().error("Mysql Provider is not allowed to use getAllPlayers()");
            return new LinkedHashMap<>();
        }
    }

    public static double add(double n1, double n2) {
        return new BigDecimal(n1).add(new BigDecimal(n2)).doubleValue();
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

    public static Map<String, Double> getPlayerConfigs(Player player) {
        return CurrencyMain.getProvider().getPlayerCurrencyData(player.getName());
    }
}
