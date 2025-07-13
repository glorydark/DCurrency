package glorydark.dcurrency.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.smallaswater.easysqlx.common.data.SqlData;
import com.smallaswater.easysqlx.common.data.SqlDataList;
import com.smallaswater.easysqlx.exceptions.MySqlLoginException;
import com.smallaswater.easysqlx.mysql.utils.DataType;
import com.smallaswater.easysqlx.mysql.utils.TableType;
import com.smallaswater.easysqlx.mysql.utils.UserData;
import com.smallaswater.easysqlx.mysql.manager.SqlManager;
import com.smallaswater.easysqlx.mysql.utils.SelectType;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;

import java.util.LinkedHashMap;
import java.util.Map;

public class CurrencyMysqlProvider implements CurrencyProvider {

    protected static final String DATA_PLAYER_NAME = "player";
    protected static final String DATA_CURRENCY_VALUE = "balance";
    protected SqlManager sqlManager;

    private final String TABLE_NAME_PREFIX = "DCurrency_";

    public CurrencyMysqlProvider(String host, int port, String user, String password, String database) {
        try {
            this.sqlManager = new SqlManager(CurrencyMain.getInstance(), new UserData(
                    user,
                    password,
                    host,
                    port,
                    database
            ));
            for (String registeredCurrency : CurrencyMain.getRegisteredCurrencies()) {
                this.createTableIfAbsent(registeredCurrency);
            }
        } catch (MySqlLoginException e) {
            throw new RuntimeException(e);
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

    public Map<String, Double> getPlayerCurrencyData(String playerName) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (String registeredCurrency : CurrencyMain.getRegisteredCurrencies()) {
            double balance = getCurrencyBalance(playerName, registeredCurrency);
            if (balance > 0) {
                map.put(registeredCurrency, balance);
            }
        }
        return map;
    }

    @Override
    public Map<String, Double> getAllPlayerData(String currencyName) {
        LinkedHashMap<String, Double> map = new LinkedHashMap<>();
        if (CurrencyMain.getRegisteredCurrencies().contains(currencyName)) {
            return map;
        }
        SqlData emptyData = new SqlData();
        SqlDataList<SqlData> sqlDataList = this.sqlManager.getData(TABLE_NAME_PREFIX + currencyName, "*", emptyData);
        if (sqlDataList == null) {
            return map;
        }
        for (SqlData sqlData : sqlDataList) {
            LinkedHashMap<String, Object> data = sqlData.getData();
            try {
                String playerId = (String) data.get(DATA_PLAYER_NAME);
                long moneyObj = (long) data.get(DATA_CURRENCY_VALUE);
                map.put(playerId, moneyObj / 100.0);
            } catch (Throwable t) {
                CurrencyMain.getInstance().getLogger().error("Error processing SqlData: " + sqlData, t);
            }
        }
        return map;
    }

    @Override
    public void close() {
        this.sqlManager.disable();
    }

    public void createTableIfAbsent(String currencyName) {
        if (!this.sqlManager.isExistTable(TABLE_NAME_PREFIX + currencyName)) {
            this.sqlManager.createTable(TABLE_NAME_PREFIX + currencyName,
                    new TableType(DATA_PLAYER_NAME, DataType.getTEXT()),
                    new TableType(DATA_CURRENCY_VALUE, DataType.getDOUBLE())
            );
        }
    }
}
