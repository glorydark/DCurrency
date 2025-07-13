package glorydark.dcurrency.provider;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.smallaswater.easysqlx.sqlite.SQLiteHelper;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class CurrencySqliteProvider implements CurrencyProvider {

    protected final String COLUMN_PLAYER = "player";
    protected SQLiteHelper sqLiteHelper;

    public CurrencySqliteProvider() throws SQLException {
        try {
            this.sqLiteHelper = new SQLiteHelper(CurrencyMain.getInstance().getPath() + File.separator + "currency.db");
            for (String registeredCurrency : CurrencyMain.getRegisteredCurrencies()) {
                this.createTableIfAbsent(registeredCurrency);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public double getCurrencyBalance(String playerName, String currencyName) {
        return getCurrencyBalance(playerName, currencyName, 0);
    }

    public double getCurrencyBalance(String playerName, String currencyName, double defaultValue) {
        CurrencyData data = this.sqLiteHelper.get(currencyName, COLUMN_PLAYER, playerName, CurrencyData.class);
        return data == null? 0d : data.getBalance();
    }

    @Nullable
    public CurrencyData getCurrencyBalanceData(String playerName, String currencyName) {
        LinkedList<CurrencyData> dataList = this.sqLiteHelper.getDataByString(currencyName, COLUMN_PLAYER + " = ?", new String[]{playerName}, CurrencyData.class);
        if (!dataList.isEmpty()) {
            return dataList.getFirst();
        }
        return null;
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
        CurrencyData data = getCurrencyBalanceData(playerName, currencyName);
        if (data == null) {
            sqLiteHelper.add(currencyName, new CurrencyData(playerName, count));
            return;
        } else {
            sqLiteHelper.set(currencyName, COLUMN_PLAYER, currencyName, data);
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
        CurrencyMain.getRegisteredCurrencies().forEach(currency -> {
            if (currency.equals(currencyName)) {
                this.sqLiteHelper.getDataByString(currencyName, "true", new String[0], CurrencyData.class)
                        .forEach(data -> map.put(data.getPlayer(), data.getBalance()));
            }
        });
        return map;
    }

    @Override
    public void close() {
        this.sqLiteHelper.close();
    }

    public void createTableIfAbsent(String currencyName) {
        if (!this.sqLiteHelper.exists(currencyName)) {
            this.sqLiteHelper.addTable(currencyName,
                    SQLiteHelper.DBTable.asDbTable(CurrencyData.class));
        }
    }

    public static class CurrencyData {

        public long id;

        public String player;

        public double balance;

        public CurrencyData() {
            // no-op
        }

        public CurrencyData(String player, double balance) {
            this.player = player;
            this.balance = balance;
        }

        public double getBalance() {
            return balance;
        }

        public String getPlayer() {
            return player;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public void setPlayer(String player) {
            this.player = player;
        }

        public long getId() {
            return id;
        }
    }
}
