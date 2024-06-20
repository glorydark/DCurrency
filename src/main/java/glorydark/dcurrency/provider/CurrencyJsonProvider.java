package glorydark.dcurrency.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.CurrencyMain;

import java.io.File;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyJsonProvider implements CurrencyProvider {

    public CurrencyJsonProvider() {

    }

    public double getCurrencyBalance(String playerName, String currencyName) {
        return getCurrencyBalance(playerName, currencyName, 0);
    }

    public double getCurrencyBalance(String playerName, String currencyName, double defaultValue) {
        File file = new File(CurrencyMain.getPlugin().getPath() + "/players/" + playerName + ".json");
        if (!file.exists()) {
            return 0;
        }
        Config config = new Config(file, Config.JSON);
        return BigDecimal.valueOf(config.getDouble(currencyName, defaultValue)).doubleValue();
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
        CurrencyMain.getPluginLogger().info(CurrencyMain.getLang("log.command.reduce", playerName, currencyName, count));
        return true;
    }

    public Map<String, Object> getPlayerConfigs(String playerName) {
        File file = new File(CurrencyMain.getPlugin().getPath() + "/players/" + playerName + ".json");
        if (!file.exists()) {
            return new LinkedHashMap<>();
        }
        Config config = new Config(file, Config.JSON);
        return config.getAll();
    }
}
