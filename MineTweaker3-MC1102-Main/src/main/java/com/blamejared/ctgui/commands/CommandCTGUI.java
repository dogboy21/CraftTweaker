package com.blamejared.ctgui.commands;

import com.blamejared.ctgui.MTRecipe;
import com.blamejared.ctgui.api.GuiRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by Jared.
 */
public class CommandCTGUI extends CommandBase {

    @Override
    public String getCommandName() {
        return "CTGUI";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "CTGUI <id>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0 || Integer.parseInt(args[0]) < 1 || GuiRegistry.getHandlerForID(Integer.parseInt(args[0])) == null) {
            sender.addChatMessage(new TextComponentString("Invalid ID!"));
            return;
        }

        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            player.openGui(MTRecipe.INSTANCE, Integer.parseInt(args[0]), sender.getEntityWorld(), 0, 0, 0);
        }
    }
}
