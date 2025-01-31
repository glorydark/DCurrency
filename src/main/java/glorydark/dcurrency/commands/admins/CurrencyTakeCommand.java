package glorydark.dcurrency.commands.admins;

import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.CurrencyAPI;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;

public class CurrencyTakeCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyTakeCommand(String command, String help) {
        this.command = command;
        this.help = help;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        if (sender.isPlayer() && !sender.isOp()) {
            return false;
        }
        if (strings.length < 4) {
            return false;
        }
        String reason = "default";
        if (strings.length == 5) {
            reason = strings[4];
        }
        if (!CurrencyMain.getRegisteredCurrencies().contains(strings[2])) {
            sender.sendMessage(CurrencyMain.getLang("message_op_unregistered_currencies", strings[2]));
            return false;
        }
        CurrencyMain.getProvider().reduceCurrencyBalance(strings[1], strings[2], Double.parseDouble(strings[3]));
        sender.sendMessage(CurrencyMain.getLang("message_op_takeCurrency", strings[1], strings[2], strings[3]));
        CurrencyMain.getPluginLogger().info(CurrencyMain.getLang("log.command.take", sender.getName(), strings[1], strings[2], String.valueOf(Double.parseDouble(strings[3])), CurrencyAPI.getCurrencyBalance(strings[1], strings[2]), reason));
        return true;
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
    public String getHelp() {
        return help;
    }
}