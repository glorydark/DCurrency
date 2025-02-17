package glorydark.dcurrency.commands.admins;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;
import glorydark.dcurrency.provider.CurrencyMysqlProvider;
import glorydark.dcurrency.provider.CurrencyProvider;

public class CurrencyCreateCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyCreateCommand(String command, String help) {
        this.command = command;
        this.help = help;
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return !sender.isPlayer() || sender.isOp();
    }

    @Override
    public String getName() {
        return command;
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        if (sender.isPlayer() && !sender.isOp()) {
            return false;
        }
        if (strings.length == 2) {
            String currencyName = strings[1];
            if (CurrencyMain.getRegisteredCurrencies().contains(currencyName)) {
                sender.sendMessage(CurrencyMain.getLang().getTranslation("message_default_moneyAlreadyRegistered", currencyName));
            } else {
                CurrencyMain.getRegisteredCurrencies().add(currencyName);
                Config config = new Config(CurrencyMain.getInstance().getPath() + "/config.yml", Config.YAML);
                config.set("registered_currencies", CurrencyMain.getRegisteredCurrencies());
                config.save();
                sender.sendMessage(CurrencyMain.getLang().getTranslation("message_default_moneyRegisteredSuccessfully", currencyName));
                CurrencyMain.writeLog(CurrencyMain.getLang().getTranslation("log.command.create", sender.getName(), currencyName));
                CurrencyProvider provider = CurrencyMain.getProvider();
                if (provider instanceof CurrencyMysqlProvider) {
                    ((CurrencyMysqlProvider) provider).createTableIfAbsent(currencyName);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String getHelp() {
        return help;
    }
}