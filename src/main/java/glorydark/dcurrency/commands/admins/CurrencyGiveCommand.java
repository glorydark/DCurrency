package glorydark.dcurrency.commands.admins;

import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;

public class CurrencyGiveCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencyGiveCommand(String command, String help) {
        this.command = command;
        this.help = help;
    }

    @Override
    public boolean execute(CommandSender sender, String[] strings) {
        if (sender.isPlayer() && !sender.isOp()) {
            return false;
        }
        if (strings.length != 4) {
            return false;
        }
        if (!CurrencyMain.getRegisteredCurrencies().contains(strings[2])) {
            sender.sendMessage(CurrencyMain.getLang("message_op_giveCurrency_unregistered_currencies"));
            return false;
        }
        CurrencyMain.getProvider().addCurrencyBalance(strings[1], strings[2], Double.parseDouble(strings[3]));
        sender.sendMessage(CurrencyMain.getLang("message_op_giveCurrency", strings[1], strings[2], strings[3]));
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