package glorydark.dcurrency.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.smallaswater.easysqlx.sqlite.SQLiteHelper;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.utils.CurrencyData;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencySqliteProvider implements CurrencyProvider {

    protected final String COLUMN_PLAYER = "player";
    protected SQLiteHelper sqLiteHelper;

    public CurrencySqliteProvider() throws SQLException, ClassNotFoundException {
        this.sqLiteHelper = new SQLiteHelper(CurrencyMain.getInstance().getPath() + File.separator + "currency.db");
        for (String registeredCurrency : CurrencyMain.getRegisteredCurrencies()) {
            createTableIfAbsent(registeredCurrency);
        }
    }

    public double getCurrencyBalance(String playerName, String currencyName) {
        return getCurrencyBalance(playerName, currencyName, 0);
    }

    public double getCurrencyBalance(String playerName, String currencyName, double defaultValue) {
        return this.sqLiteHelper.get(currencyName, COLUMN_PLAYER, playerName, CurrencyData.class).getBalance();
    }

    public void addCurrencyBalance(String playerName, String currencyName, double count, String reason) {
        double balance = add(getCurrencyBalance(playerName, currencyName, 0), count);
        setCurrencyBalance(playerName, currencyName, balance, false);
        Player player = Server.getInstance().getPlayer(playerName);
        if (player != null) {
            player.sendMessage(CurrencyMain.getLang().getTranslation("message_player_currencyReceive", currencyName, count, reason));
        }
        CurrencyMain.writeLog(CurrencyMain.getLang().getTranslation("log.command.add", "console", playerName, currencyName, count, CurrencyAPI.getCurrencyBalance(playerName, currencyName), reason));
    }

    public void setCurrencyBalance(String playerName, String currencyName, double count, boolean tip, String reason) {
        CurrencyData data = new CurrencyData();
        data.setPlayer(playerName);
        data.setBalance(getCurrencyBalance(playerName, currencyName) + count);
        sqLiteHelper.set(currencyName, COLUMN_PLAYER, currencyName, data);
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

    public Map<String, Object> getPlayerConfigs(String playerName) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String registeredCurrency : CurrencyMain.getRegisteredCurrencies()) {
            double balance = getCurrencyBalance(playerName, registeredCurrency);
            if (balance > 0) {
                map.put(registeredCurrency, balance);
            }
        }
        return map;
    }

    public void createTableIfAbsent(String currencyName) {
        if (!this.sqLiteHelper.exists(currencyName)) {
            this.sqLiteHelper.addTable(currencyName,
                    SQLiteHelper.DBTable.asDbTable(CurrencyData.class));
        }
    }
}
