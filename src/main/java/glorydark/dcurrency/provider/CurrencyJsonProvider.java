package glorydark.dcurrency.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;

import java.io.File;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CurrencyJsonProvider implements CurrencyProvider {

    public Map<String, Map<String, Double>> playerCurrencyCache = new LinkedHashMap<>();

    public CurrencyJsonProvider() {

    }

    public double getCurrencyBalance(String playerName, String currencyName) {
        return getCurrencyBalance(playerName, currencyName, 0);
    }

    public double getCurrencyBalance(String playerName, String currencyName, double defaultValue) {
        return BigDecimal.valueOf(this.getPlayerConfigWithoutCreate(playerName).getOrDefault(currencyName, defaultValue)).doubleValue();
    }

    public void addCurrencyBalance(String playerName, String currencyName, double count, String reason) {
        double balance = add(getCurrencyBalance(playerName, currencyName, 0), count);
        setCurrencyBalance(playerName, currencyName, balance, false);
        Player player = Server.getInstance().getPlayer(playerName);
        if (player != null) {
            player.sendMessage(CurrencyMain.getLang().getTranslation("message_player_currencyReceive", currencyName, count, reason));
        }
        CurrencyMain.writeLog(CurrencyMain.getLang().getTranslation("log.command.give", "console", playerName, currencyName, count, CurrencyAPI.getCurrencyBalance(playerName, currencyName), reason));
    }

    public void setCurrencyBalance(String playerName, String currencyName, double count, boolean tip, String reason) {
        Config config = new Config(CurrencyMain.getInstance().getPath() + "/players/" + playerName + ".json", Config.JSON);
        config.set(currencyName, count);
        Map<String, Double> result = new LinkedHashMap<>();
        for (String key : config.getKeys(false)) {
            result.put(key, config.getDouble(key));
        }
        playerCurrencyCache.put(playerName, result);
        config.save();
        if (tip) {
            Player player = Server.getInstance().getPlayer(playerName);
            if (player != null) {
                player.sendMessage(CurrencyMain.getLang().getTranslation("message_player_currencySet", currencyName, count, reason));
            }
            CurrencyMain.writeLog(CurrencyMain.getLang().getTranslation("log.command.set", "console", playerName, currencyName, count, CurrencyAPI.getCurrencyBalance(playerName, currencyName), reason));
        }
    }

    public boolean reduceCurrencyBalance(String playerName, String currencyName, double count, String reason) {
        double balance = add(getCurrencyBalance(playerName, currencyName, 0), -count);
        if (balance < 0) {
            return false;
        }
        setCurrencyBalance(playerName, currencyName, balance, false);
        Player player = Server.getInstance().getPlayer(playerName);
        if (player != null) {
            player.sendMessage(CurrencyMain.getLang().getTranslation("message_player_currencyReduced", currencyName, count));
        }
        CurrencyMain.writeLog(CurrencyMain.getLang().getTranslation("log.command.take", "console", playerName, currencyName, count, CurrencyAPI.getCurrencyBalance(playerName, currencyName), reason));
        return true;
    }

    public Map<String, Double> getPlayerConfigWithoutCreate(String playerName) {
        if (playerCurrencyCache.containsKey(playerName)) {
            return playerCurrencyCache.get(playerName);
        } else {
            Map<String, Double> result = getPlayerCurrencyData(playerName);
            playerCurrencyCache.put(playerName, result);
            return result;
        }
    }

    public Map<String, Double> getPlayerCurrencyData(String playerName) {
        Map<String, Double> result = new LinkedHashMap<>();
        File file = new File(CurrencyMain.getInstance().getPath() + "/players/" + playerName + ".json");
        if (!file.exists()) {
            return result;
        }
        try {
            Config config = new Config(file, Config.JSON);
            for (String key : config.getKeys(false)) {
                result.put(key, config.getDouble(key));
            }
            return result;
        } catch (Throwable t) {
            CurrencyMain.getInstance().getLogger().error("Found wrong player data: " + file, t);
            return result;
        }
    }

    @Override
    public Map<String, Double> getAllPlayerData(String currencyName) {
        Map<String, Double> result = new LinkedHashMap<>();
        File file = new File(CurrencyMain.getInstance().getPath() + "/players/");
        for (File json : Objects.requireNonNull(file.listFiles())) {
            if (json.isFile() && json.getName().endsWith(".json")) {
                try {
                    Config config = new Config(json, Config.JSON);
                    result.put(json.getName().substring(0, json.getName().lastIndexOf(".")), config.getDouble(currencyName, 0d));
                } catch (Throwable t) {
                    CurrencyMain.getInstance().getLogger().error("Found a broken file: " + json);
                    t.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    public void close() {

    }
}
