package glorydark.dcurrency;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;
import com.smallaswater.npc.variable.VariableManage;
import glorydark.dcurrency.commands.CommandsExecutor;
import glorydark.dcurrency.logger.LoggerFormatter;
import glorydark.dcurrency.provider.CurrencyJsonProvider;
import glorydark.dcurrency.provider.CurrencyMysqlProvider;
import glorydark.dcurrency.provider.CurrencyProvider;
import glorydark.dcurrency.provider.CurrencySqliteProvider;
import glorydark.dcurrency.utils.Language;
import tip.utils.Api;
import tip.utils.variables.BaseVariable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class CurrencyMain extends PluginBase {

    public static Language lang = new Language();
    protected static String path;
    protected static List<String> registeredCurrencies = new ArrayList<>();
    protected static CurrencyMain plugin;
    protected static CurrencyProvider provider;

    protected static Logger pluginLogger;
    protected static FileHandler fileHandler = null;
    protected static String date;

    @Override
    public void onLoad() {
        this.getLogger().info("DCurrency OnLoad");
    }

    @Override
    public void onEnable() {
        // initialize
        path = this.getDataFolder().getPath();
        plugin = this;
        date = getDateWithoutDetails(System.currentTimeMillis());
        pluginLogger = Logger.getLogger("dcurrency");
        new File(path + "/logs/").mkdirs();
        try {
            fileHandler = new FileHandler(path + "/logs/" + getDateWithoutDetails(System.currentTimeMillis()) + ".log", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileHandler.setFormatter(new LoggerFormatter());
        pluginLogger.addHandler(fileHandler);
        pluginLogger.setUseParentHandlers(false);
        //load language config
        this.saveDefaultConfig();
        this.saveResource("languages/zh_CN.properties", false);
        this.saveResource("languages/en_US.properties", false);
        this.loadAll();

        // register Commands
        this.getServer().getCommandMap().register("", new CommandsExecutor(lang.getTranslation("command_main")));
        // loading functions
        if (this.getServer().getPluginManager().getPlugin("Tips") != null) {
            Api.registerVariables("DCurrency", TipsVariable.class);
            this.getLogger().info("Detect Tips Enabled!");
        }

        if (this.getServer().getPluginManager().getPlugin("RsNPC") != null) {
            VariableManage.addVariableV2("DCurrency", RsNPCVariable.class);
            this.getLogger().info("Detect RsNPC Enabled!");
        }

        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            for (String registeredCurrency : registeredCurrencies) {
                PlaceholderAPI.getInstance().builder("{DCurrency_" + registeredCurrency + "_balance}", String.class)
                        .visitorLoader(stringVisitorEntry -> String.valueOf(CurrencyMain.getProvider().getCurrencyBalance(stringVisitorEntry.getPlayer(), registeredCurrency)));
            }
            this.getLogger().info("Detect PlaceholderAPI Enabled!");
        }
        this.getLogger().info("DCurrency OnEnable");
    }

    public void loadAll() {
        Config config = new Config(path + File.separator + "config.yml", Config.YAML);
        lang.setDefaultLanguage(config.getString("default_language", "en_US"));
        lang.addLanguage(new File(path + File.separator + "languages" + File.separator + "zh_CN.properties"));
        lang.addLanguage(new File(path + File.separator + "languages" + File.separator + "en_US.properties"));
        registeredCurrencies = new ArrayList<>(config.getStringList("registered_currencies"));
        if (config.exists("mysql") && config.getBoolean("mysql.enable", false)) {
            ConfigSection section = config.getSection("mysql");
            String host = section.getString("host");
            int port = section.getInt("port");
            String database = section.getString("database");
            String user = section.getString("user");
            String password = section.getString("password");
            try {
                provider = new CurrencyMysqlProvider(host, port, user, password, database);
                this.getLogger().info(lang.getTranslation("tips_mysql_enabled"));
            } catch (Exception e) {
                this.getLogger().info(lang.getTranslation("tips_mysql_disabled"));
                throw new RuntimeException(e);
            }
        } else if (config.exists("sqlite") && config.getBoolean("sqlite.enable", false)) {
            try {
                provider = new CurrencySqliteProvider();
                this.getLogger().info(lang.getTranslation("tips_sqlite_enabled"));
            } catch (Exception e) {
                this.getLogger().info(lang.getTranslation("tips_sqlite_disabled"));
                throw new RuntimeException(e);
            }
        } else {
            this.getLogger().info(lang.getTranslation("tips_json_enabled"));
            provider = new CurrencyJsonProvider();
        }
        // create folder
        File dic = new File(this.getDataFolder() + "/players/");
        if (!dic.exists()) {
            if (!dic.mkdirs()) {
                this.getLogger().info(lang.getTranslation("tips_createFolder_fail"));
                this.setEnabled(false);
            }
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DCurrency OnDisable");
    }

    public static CurrencyProvider getProvider() {
        return provider;
    }

    public static CurrencyMain getInstance() {
        return plugin;
    }

    public static List<String> getRegisteredCurrencies() {
        return registeredCurrencies;
    }

    public static void setRegisteredCurrencies(List<String> registeredCurrencies) {
        CurrencyMain.registeredCurrencies = registeredCurrencies;
    }

    public static Language getLang() {
        return lang;
    }

    @Override
    public PluginLogger getLogger() {
        return super.getLogger();
    }

    public String getPath() {
        return path;
    }

    public static class TipsVariable extends BaseVariable {

        public TipsVariable(Player player) {
            super(player);
        }

        @Override
        public void strReplace() {
            for (String currency : registeredCurrencies) {
                this.addStrReplaceString("{DCurrency_balance_" + currency + "}", String.valueOf(provider.getCurrencyBalance(player.getName(), currency)));
            }
        }
    }

    public static class RsNPCVariable extends BaseVariableV2 {

        @Override
        public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
            for (String currency : registeredCurrencies) {
                this.addVariable("{DCurrency_balance_" + currency + "}", String.valueOf(provider.getCurrencyBalance(player.getName(), currency)));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (provider instanceof CurrencyJsonProvider) {
            ((CurrencyJsonProvider) provider).playerCurrencyCache.remove(event.getPlayer().getName());
        }
    }

    public static void writeLog(String info) {
        if (pluginLogger == null) {
            return;
        }
        String currentDate = getDateWithoutDetails(System.currentTimeMillis());
        if (!date.equals(currentDate)) {
            date = currentDate;
            pluginLogger.removeHandler(fileHandler);
            try {
                fileHandler = new FileHandler(path + "/logs/" + date + ".log", true);
                // Set a formatter to format log records
                LoggerFormatter formatter = new LoggerFormatter();
                fileHandler.setFormatter(formatter);
                // Add the FileHandler to the logger
                pluginLogger.addHandler(fileHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pluginLogger.info(info);
    }

    public static String getDateWithoutDetails(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(date);
    }
}