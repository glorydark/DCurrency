package glorydark.dcurrency.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import glorydark.dcurrency.CurrencyMain;
import glorydark.dcurrency.commands.admins.*;
import glorydark.dcurrency.commands.players.CurrencyMeCommand;
import glorydark.dcurrency.commands.players.CurrencyPayCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 若水
 * @author Glorydark
 */

public class CommandsExecutor extends Command {

    public List<SubCommand> commands = new ArrayList<>();

    public HashMap<String, Integer> commandPairs = new HashMap<>();

    public CommandsExecutor(String name) {
        super(name);
        //Admins
        loadSubCommand(new CurrencyGiveCommand(CurrencyMain.getLang().getTranslation("command_minor_give"), CurrencyMain.getLang().getTranslation("help_give")));
        loadSubCommand(new CurrencySeeCommand(CurrencyMain.getLang().getTranslation("command_minor_see"), CurrencyMain.getLang().getTranslation("help_see")));
        loadSubCommand(new CurrencySetCommand(CurrencyMain.getLang().getTranslation("command_minor_set"), CurrencyMain.getLang().getTranslation("help_set")));
        // loadSubCommand(new CurrencyAdminRedeemCommand(CurrencyMain.getLang().getTranslation("command_minor_redeem_admin"), CurrencyMain.getLang().getTranslation("help_redeem_admin")));
        loadSubCommand(new CurrencyCreateCommand(CurrencyMain.getLang().getTranslation("command_minor_create"), CurrencyMain.getLang().getTranslation("help_create")));
        //Players
        loadSubCommand(new CurrencyMeCommand(CurrencyMain.getLang().getTranslation("command_minor_me"), CurrencyMain.getLang().getTranslation("help_me")));
        loadSubCommand(new CurrencyPayCommand(CurrencyMain.getLang().getTranslation("command_minor_pay"), CurrencyMain.getLang().getTranslation("help_pay")));
        // loadSubCommand(new CurrencyRedeemCommand(CurrencyMain.getLang().getTranslation("command_minor_redeem"), CurrencyMain.getLang().getTranslation("help_redeem")));
        loadSubCommand(new CurrencyAllCommand(CurrencyMain.getLang().getTranslation("command_minor_allMoney"), CurrencyMain.getLang().getTranslation("help_all_money")));
        loadSubCommand(new CurrencyTopCommand(CurrencyMain.getLang().getTranslation("command_minor_top"), CurrencyMain.getLang().getTranslation("help_top")));
        loadSubCommand(new CurrencyClearAllCommand(CurrencyMain.getLang().getTranslation("command_minor_clear_all"), CurrencyMain.getLang().getTranslation("help_clear_all")));
        loadSubCommand(new CurrencyTakeCommand(CurrencyMain.getLang().getTranslation("command_minor_take"), CurrencyMain.getLang().getTranslation("help_take")));
    }

    private void loadSubCommand(SubCommand cmd) {
        commands.add(cmd);
        int commandId = (commands.size()) - 1;
        commandPairs.put(cmd.getName().toLowerCase(), commandId);
        for (String alias : cmd.getAliases()) {
            commandPairs.put(alias.toLowerCase(), commandId);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(CurrencyMain.getLang().getTranslation("help_title"));
        sender.sendMessage(CurrencyMain.getLang().getTranslation("help_me"));
        sender.sendMessage(CurrencyMain.getLang().getTranslation("help_pay"));
        if (sender.isOp() || !sender.isPlayer()) {
            sender.sendMessage(CurrencyMain.getLang().getTranslation("help_give"));
            sender.sendMessage(CurrencyMain.getLang().getTranslation("help_set"));
            sender.sendMessage(CurrencyMain.getLang().getTranslation("help_see"));
            sender.sendMessage(CurrencyMain.getLang().getTranslation("help_all_money"));
            sender.sendMessage(CurrencyMain.getLang().getTranslation("help_create"));
            sender.sendMessage(CurrencyMain.getLang().getTranslation("help_top"));
        }
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
        } else {
            String subCommand = args[0].toLowerCase();
            if (commandPairs.containsKey(subCommand)) {
                SubCommand command = commands.get(commandPairs.get(subCommand));
                boolean canUse = command.canUse(sender);
                if (canUse) {
                    if (!command.execute(sender, args)) {
                        sender.sendMessage(command.getHelp());
                    }
                    return true;
                } else {
                    sender.sendMessage(CurrencyMain.getLang().getTranslation("message_default_noPermission"));
                    return false;
                }
            } else {
                this.sendHelp(sender);
                return true;
            }
        }
        return true;
    }
}
