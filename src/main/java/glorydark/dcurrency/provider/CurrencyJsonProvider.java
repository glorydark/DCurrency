package glorydark.dcurrency.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;

import java.io.File;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyJsonProvider implements CurrencyProvider {

    public Map<String, ConfigSection> playerCurrencyCache = new LinkedHashMap<>();

    public CurrencyJsonProvider() {

    }

    public double getCurrencyBalance(String playerName, String currencyName) {
        return getCurrencyBalance(playerName, currencyName, 0);
    }

    public double getCurrencyBalance(String playerName, String currencyName, double defaultValue) {
        ConfigSection configSection = getPlayerConfigWithoutCreate(playerName);
        return BigDecimal.valueOf(configSection.getDouble(currencyName, defaultValue)).doubleValue();
    }

    public void addCurrencyBalance(String playerName, String currencyName, double count) {
        double balance = add(getCurrencyBalance(playerName, currencyName, 0), count);
        setCurrencyBalance(playerName, currencyName, balance, false);
        Player player = Server.getInstance().getPlayer(playerName);
        if (player != null) {
            player.sendMessage(CurrencyMain.getLang("message_player_currencyReceive", currencyName, count));
        }
    }

    public void setCurrencyBalance(String playerName, String currencyName, double count, boolean tip) {
        Config config = new Config(CurrencyMain.getPlugin().getPath() + "/players/" + playerName + ".json", Config.JSON);
        config.set(currencyName, count);
        playerCurrencyCache.put(playerName, config.getRootSection());
        config.save();
        if (tip) {
            Player player = Server.getInstance().getPlayer(playerName);
            if (player != null) {
                player.sendMessage(CurrencyMain.getLang("message_player_currencySet", currencyName, count));
            }
        }
    }

    public boolean reduceCurrencyBalance(String playerName, String currencyName, double count) {
        double balance = add(getCurrencyBalance(playerName, currencyName, 0), -count);
        if (balance < 0) {
            return false;
        }
        setCurrencyBalance(playerName, currencyName, balance, false);
        Player player = Server.getInstance().getPlayer(playerName);
        if (player != null) {
            player.sendMessage(CurrencyMain.getLang("message_player_currencyReduced", currencyName, count));
        }
        CurrencyMain.getPluginLogger().info(CurrencyMain.getLang("log.command.reduce", playerName, currencyName, count, CurrencyAPI.getCurrencyBalance(playerName, currencyName)));
        return true;
    }

    public ConfigSection getPlayerConfigWithoutCreate(String playerName) {
        if (playerCurrencyCache.containsKey(playerName)) {
            return playerCurrencyCache.get(playerName);
        } else {
            ConfigSection section = getPlayerConfigs(playerName);
            playerCurrencyCache.put(playerName, section);
            return section;
        }
    }

    public ConfigSection getPlayerConfigs(String playerName) {
        File file = new File(CurrencyMain.getPlugin().getPath() + "/players/" + playerName + ".json");
        if (!file.exists()) {
            return new ConfigSection();
        }
        Config config = new Config(file, Config.JSON);
        return config.getRootSection();
    }
}
