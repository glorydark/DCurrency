package glorydark.dcurrency.commands.admins;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.SubCommand;

public class CurrencySetCommand extends SubCommand {

    private final String command;
    private final String help;

    public CurrencySetCommand(String command, String help) {
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
        if (!Server.getInstance().lookupName(strings[1]).isPresent()) {
            sender.sendMessage(CurrencyMain.getLang().getTranslation("message.default.player_not_found", strings[1]));
            return false;
        }
        String reason = "default";
        if (strings.length == 5) {
            reason = strings[4];
        }
        CurrencyMain.getProvider().setCurrencyBalance(strings[1], strings[2], Double.parseDouble(strings[3]), true, reason);
        sender.sendMessage(CurrencyMain.getLang().getTranslation("message_op_setCurrency", strings[1], strings[2], strings[3]));
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
