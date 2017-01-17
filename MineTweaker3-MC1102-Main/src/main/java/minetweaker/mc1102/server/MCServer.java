/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.mc1102.server;

import minetweaker.*;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.player.IPlayer;
import minetweaker.api.server.*;
import minetweaker.mc1102.MineTweakerMod;
import minetweaker.mc1102.player.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.math.BlockPos;
import stanhebben.zenscript.annotations.Optional;

import java.util.*;

/**
 * @author Stan
 */
public class MCServer extends AbstractServer {
	
	private final MinecraftServer server;
	
	public MCServer(MinecraftServer server) {
		this.server = server;
	}
	
	private static IPlayer getPlayer(ICommandSender commandSender) {
		if(commandSender instanceof EntityPlayer) {
			return MineTweakerMC.getIPlayer((EntityPlayer) commandSender);
		} else if(commandSender instanceof RConConsoleSource) {
			return new RconPlayer(commandSender);
		} else if(commandSender instanceof CommandBlockBaseLogic) {
			return new CommandBlockPlayer(commandSender);
		} else if(commandSender.getName().equals("Server")) {
			return ServerPlayer.INSTANCE;
		} else {
			System.out.println("Unsupported command sender: " + commandSender + " defaulting to server player!");
			System.out.println("player name: " + commandSender.getName());
			System.out.println("Please report to mod author if this is incorrect!");
			return ServerPlayer.INSTANCE;
		}
	}
	
	@Override
	public void addCommand(String name, String usage, String[] aliases, ICommandFunction function, @Optional ICommandValidator validator, @Optional ICommandTabCompletion completion) {
		ICommand command = new MCCommand(name, usage, aliases, function, validator, completion);
		MineTweakerAPI.apply(new AddCommandAction(command));
	}
	
	@Override
	public boolean isOp(IPlayer player) {
		if (player == null)
			return false;
		if(player == ServerPlayer.INSTANCE)
			return true;
		
		UserListOps ops = MineTweakerMod.server.getPlayerList().getOppedPlayers();
		if(server != null && server.isDedicatedServer() && ops != null) {
			//TODO figure if this is correct
			//            return ops.func_152690_d() || ops.func_152700_a(player.getName()) != null || player instanceof RconPlayer;
			return ops.isEmpty() || ops.getGameProfileFromName(player.getName()) != null || player instanceof RconPlayer;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean isCommandAdded(String name) {
		return MineTweakerMod.server.getCommandManager().getCommands().containsKey(name);
	}
	
	private class MCCommand implements ICommand {
		
		private final String name;
		private final String usage;
		private final List<String> aliases;
		private final ICommandFunction function;
		private final ICommandValidator validator;
		private final ICommandTabCompletion completion;
		
		public MCCommand(String name, String usage, String[] aliases, ICommandFunction function, ICommandValidator validator, ICommandTabCompletion completion) {
			this.name = name;
			this.usage = usage;
			this.aliases = Arrays.asList(aliases);
			this.function = function;
			this.validator = validator;
			this.completion = completion;
		}
		
		@Override
		public String getCommandName() {
			return name;
		}
		
		@Override
		public String getCommandUsage(ICommandSender var1) {
			return usage;
		}
		
		@Override
		public List getCommandAliases() {
			return aliases;
		}
		
		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			function.execute(args, getPlayer(sender));
		}
		
		@Override
		public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
			if(validator == null) {
				return true;
			} else {
				return validator.canExecute(getPlayer(sender));
			}
		}
		
		@Override
		public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			if(completion != null) {
				return Arrays.asList(completion.getTabCompletionOptions(args, getPlayer(sender)));
			} else {
				return null;
			}
		}
		
		@Override
		public boolean isUsernameIndex(String[] var1, int var2) {
			return false;
		}
		
		@Override
		public int compareTo(ICommand o) {
			return this.getCommandName().compareTo(o.getCommandName());
		}
	}
	
	private class AddCommandAction implements IUndoableAction {
		
		private final ICommand command;
		
		public AddCommandAction(ICommand command) {
			this.command = command;
		}
		
		@Override
		public void apply() {
			CommandHandler ch = (CommandHandler) MineTweakerMod.server.getCommandManager();
			if(!ch.getCommands().containsValue(command))
				ch.registerCommand(command);
		}
		
		@Override
		public boolean canUndo() {
			return true;
		}
		
		@Override
		public void undo() {
			
		}
		
		@Override
		public String describe() {
			CommandHandler ch = (CommandHandler) MineTweakerMod.server.getCommandManager();
			if(!ch.getCommands().containsValue(command))
				return "Adding command " + command.getCommandName();
			return "";
		}
		
		@Override
		public String describeUndo() {
			return "tried to remove command: " + command.getCommandName() + " failed. THIS IS NOT AN ERROR!";
		}
		
		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
	
}
