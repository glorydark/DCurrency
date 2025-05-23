package glorydark.dcurrency.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.smallaswater.easysql.exceptions.MySqlLoginException;
import com.smallaswater.easysql.mysql.data.SqlData;
import com.smallaswater.easysql.mysql.data.SqlDataList;
import com.smallaswater.easysql.mysql.utils.DataType;
import com.smallaswater.easysql.mysql.utils.TableType;
import com.smallaswater.easysql.mysql.utils.UserData;
import com.smallaswater.easysql.v3.mysql.manager.SqlManager;
import com.smallaswater.easysql.v3.mysql.utils.SelectType;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;

import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyMysqlProvider implements CurrencyProvider {

    protected final String DATA_CURRENCY_VALUE = "value";
    protected final String DATA_PLAYER_NAME = "name";
    protected SqlManager sqlManager;

    public CurrencyMysqlProvider(String host, int port, String user, String password, String database) throws MySqlLoginException {
        this.sqlManager = new SqlManager(CurrencyMain.getInstance(), new UserData(
                user,
                password,
                host,
                port,
                database
        ));
        for (String registeredCurrency : CurrencyMain.getRegisteredCurrencies()) {
            createTableIfAbsent(registeredCurrency);
        }
    }

    public double getCurrencyBalance(String playerName, String currencyName) {
        return getCurrencyBalance(playerName, currencyName, 0);
    }

    public double getCurrencyBalance(String playerName, String currencyName, double defaultValue) {
        SqlDataList<SqlData> sqlDataList = this.sqlManager.getData(currencyName, new SelectType(DATA_PLAYER_NAME, playerName));
        if (sqlDataList.isEmpty()) {
            return 0d;
        }
        return sqlDataList.get().getDouble(DATA_CURRENCY_VALUE);
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
        SqlDataList<SqlData> sqlDataList = this.sqlManager.getData(currencyName, new SelectType(DATA_PLAYER_NAME, playerName));
        SqlData data = new SqlData().put(DATA_PLAYER_NAME, playerName).put(DATA_CURRENCY_VALUE, count);
        if (sqlDataList.isEmpty()) {
            this.sqlManager.insertData(currencyName, data);
        } else {
            this.sqlManager.setData(currencyName, data, new SqlData("name", playerName).put("value", count));
        }
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
        if (!this.sqlManager.isExistTable(currencyName)) {
            this.sqlManager.createTable(currencyName,
                    new TableType(DATA_PLAYER_NAME, DataType.getTEXT()),
                    new TableType(DATA_CURRENCY_VALUE, DataType.getDOUBLE())
            );
        }
    }
}
