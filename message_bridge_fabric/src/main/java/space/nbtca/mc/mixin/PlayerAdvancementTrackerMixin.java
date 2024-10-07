package space.nbtca.mc.mixin;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import space.nbtca.mc.MessageBridgeFabric;
@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayerEntity owner;
    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;onStatusUpdate(Lnet/minecraft/advancement/AdvancementEntry;)V"))
    void grantCriterion(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> ci) {
        advancement.value().display().ifPresent(display -> {
            if (display.shouldAnnounceToChat() && this.owner.getWorld().getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
                var title = display.getTitle();
                var description = display.getDescription();
                var criteria = advancement.value().criteria().keySet().toArray(new String[0]);
                MessageBridgeFabric.instance.onAdvancementAchieved(this.owner, title.getString(), description.getString(), criteria);
            }
        });
    }
}