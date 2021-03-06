/*
* Based from class "CommandRemover" in the mod Watson
* https://github.com/vacla/Watson
*/

package com.raxdiam.teamperms.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Predicate;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.server.MinecraftServer;

public class CommandNodeHelper
{
    private static final Field CHILDREN, LITERALS, ARGUMENTS, REQUIREMENT;
    static
    {
        try
        {
            CHILDREN = CommandNode.class.getDeclaredField("children");
            LITERALS = CommandNode.class.getDeclaredField("literals");
            ARGUMENTS = CommandNode.class.getDeclaredField("arguments");
            REQUIREMENT = CommandNode.class.getDeclaredField("requirement");
            CHILDREN.setAccessible(true);
            LITERALS.setAccessible(true);
            ARGUMENTS.setAccessible(true);
            REQUIREMENT.setAccessible(true);
        }
        catch (ReflectiveOperationException e)
        {
            throw new RuntimeException("Unable to get CommandNode fields", e);
        }
    }

    public static void changeRequirement(CommandNode<?> node, String command, Predicate<?> predicate) {
        CommandNode<?> child = node.getChild(command);
        if (child != null) {
            changeRequirement(child, predicate);
        }
    }

    public static void changeRequirement(CommandNode<?> node, Predicate<?> predicate) {
        try {
            REQUIREMENT.set(node, predicate);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error changing requirement for command: " + node.getName(), e);
        }
    }

    /**
     * Remove the command with the given name from the given {@link MinecraftServer}
     *
     * @param server The server to remove the command from
     * @param command The name of the command to remove
     */
    public static void removeCommand(MinecraftServer server, String command)
    {
        removeCommand(server.getCommandManager().getDispatcher().getRoot(), command);
    }

    /**
     * Remove the command with the given name from the given {@link CommandNode}
     *
     * @param node The command node to remove the command from
     * @param command The name of the command to remove
     */
    @SuppressWarnings("unchecked")
    public static void removeCommand(CommandNode<?> node, String command)
    {
        Object child = node.getChild(command);
        if (child != null)
        {
            try
            {
                if (child instanceof LiteralCommandNode<?>)
                {
                    ((Map<String, ?>) LITERALS.get(node)).remove(command);
                }
                else if (child instanceof ArgumentCommandNode<?, ?>)
                {
                    ((Map<String, ?>) ARGUMENTS.get(node)).remove(command);
                }

                ((Map<String, ?>) CHILDREN.get(node)).remove(command);
            }
            catch (ReflectiveOperationException e)
            {
                throw new RuntimeException("Error removing command: " + command, e);
            }
        }
    }
}
