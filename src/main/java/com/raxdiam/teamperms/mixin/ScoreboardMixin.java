package com.raxdiam.teamperms.mixin;

import com.raxdiam.teamperms.events.ScoreboardCallbacks;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scoreboard.class)
public class ScoreboardMixin {
    @Inject(method = "updateScoreboardTeam", at = @At("TAIL"), cancellable = true)
    private void onTeamUpdate(Team team, CallbackInfo info) {
        if (ScoreboardCallbacks.TEAM_UPDATE.invoker().handle(team))
            info.cancel();
    }

    @Inject(method = "addPlayerToTeam", at = @At("TAIL"), cancellable = true)
    private void onTeamJoin(String playerName, Team team, CallbackInfoReturnable<Boolean> info) {
        if (ScoreboardCallbacks.TEAM_JOIN.invoker().handle(playerName, team))
            info.cancel();
    }

    @Inject(method = "removePlayerFromTeam", at = @At("TAIL"), cancellable = true)
    private void onTeamLeave(String playerName, Team team, CallbackInfo info) {
        if (ScoreboardCallbacks.TEAM_LEAVE.invoker().handle(playerName, team))
            info.cancel();
    }

    @Inject(method = "removeTeam", at = @At("HEAD"), cancellable = true)
    private void onTeamRemoveBefore(Team team, CallbackInfo info) {
        if (ScoreboardCallbacks.TEAM_REMOVE_BEFORE.invoker().handle(team))
            info.cancel();
    }

    @Inject(method = "removeTeam", at = @At("TAIL"), cancellable = true)
    private void onTeamRemoveAfter(Team team, CallbackInfo info) {
        if (ScoreboardCallbacks.TEAM_REMOVE_AFTER.invoker().handle(team))
            info.cancel();
    }
}
