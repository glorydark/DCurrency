package glorydark.dcurrency.commands.admins;

import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Config;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;

import java.io.File;
import java.util.Objects;

public class CurrencyClearAllCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyClearAllCommand(String command, String help) {
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
                File file = new File(CurrencyMain.getInstance().getPath() + "/players/");
                for (File json : Objects.requireNonNull(file.listFiles())) {
                    Config config = new Config(json, Config.JSON);
                    config.remove(currencyName);
                    config.save();
                }
                sender.sendMessage(CurrencyMain.getLang().getTranslation("message.op.clear_currency.success", currencyName));
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