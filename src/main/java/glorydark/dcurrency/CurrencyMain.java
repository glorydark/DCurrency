package glorydark.dcurrency;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;
import com.smallaswater.npc.variable.VariableManage;
import glorydark.dcurrency.commands.CommandsExecutor;
import glorydark.dcurrency.logger.LoggerFormatter;
import glorydark.dcurrency.provider.CurrencyJsonProvider;
import glorydark.dcurrency.provider.CurrencyMysqlProvider;
import glorydark.dcurrency.provider.CurrencyProvider;
import tip.utils.Api;
import tip.utils.variables.BaseVariable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class CurrencyMain extends PluginBase {

    public static Map<String, Object> lang = new HashMap<>();
    protected static String path;
    protected static List<String> registeredCurrencies = new ArrayList<>();
    protected static CurrencyMain plugin;
    protected static CurrencyProvider provider;
    protected static Logger pluginLogger;

    @Override
    public void onLoad() {
        this.getLogger().info("DCurrency OnLoad");
    }

    @Override
    public void onEnable() {
        // initialize
        path = this.getDataFolder().getPath();
        plugin = this;
        pluginLogger = Logger.getLogger("LotteryBox_" + UUID.randomUUID());
        new File(path + "/logs/").mkdirs();

        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler(path + "/logs/" + getDate(System.currentTimeMillis()) + ".log");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileHandler.setFormatter(new LoggerFormatter());
        pluginLogger.addHandler(fileHandler);
        pluginLogger.setUseParentHandlers(false);
        //load language config
        this.saveDefaultConfig();
        this.saveResource("lang.properties", false);

        lang = new Config(path + "/lang.properties", Config.PROPERTIES).getAll();

        Config config = new Config(path + "/config.yml", Config.YAML);
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
                this.getLogger().info(getLang("tips_mysql_enabled"));
            } catch (Exception e) {
                this.getLogger().info(getLang("tips_mysql_disabled"));
                throw new RuntimeException(e);
            }
        } else {
            this.getLogger().info(getLang("tips_json_enabled"));
            provider = new CurrencyJsonProvider();
        }
        // create folder
        File dic = new File(this.getDataFolder() + "/players/");
        if (!dic.exists()) {
            if (!dic.mkdirs()) {
                this.getLogger().info(getLang("tips_createFolder_fail"));
                this.setEnabled(false);
                return;
            }
        }
        // register Commands
        this.getServer().getCommandMap().register("", new CommandsExecutor(CurrencyMain.getLang("command_main")));
        // loading functions
        if (this.getServer().getPluginManager().getPlugin("Tips") != null) {
            this.getLogger().info("Detect Tips Enabled!");
            Api.registerVariables("DCurrency", TipsVariable.class);
        }

        if (this.getServer().getPluginManager().getPlugin("RsNPC") != null) {
            this.getLogger().info("Detect RsNPC Enabled!");
            VariableManage.addVariableV2("DCurrency", RsNPCVariable.class);
        }
        this.getLogger().info("DCurrency OnEnable");
    }

    public static Logger getPluginLogger() {
        return pluginLogger;
    }

    @Override
    public PluginLogger getLogger() {
        return super.getLogger();
    }

    public static CurrencyProvider getProvider() {
        return provider;
    }

    public static CurrencyMain getPlugin() {
        return plugin;
    }

    public static String getLang(String string, Object... params) {
        if (lang.containsKey(string)) {
            String out = (String) lang.get(string);
            for (int i = 1; i <= params.length; i++) {
                out = out.replace("%" + i + "%", String.valueOf(params[i - 1]));
            }
            return out;
        } else {
            return "Key Not Found!";
        }
    }

    public static List<String> getRegisteredCurrencies() {
        return registeredCurrencies;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DCurrency OnDisable");
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

    public static String getDate(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        return format.format(date);
    }
}