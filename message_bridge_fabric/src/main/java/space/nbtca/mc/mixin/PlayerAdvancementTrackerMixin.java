package space.nbtca.mc.mixin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
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
    @Shadow
    private AdvancementManager advancementManager;
    @Inject(method = "grantCriterion", at = @At("TAIL"))
    void preventGrantCriterion(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> ci) {
        var result = ci.getReturnValue();
        if (result) {
            advancement.value().display().ifPresent(display -> {
                if (display.shouldAnnounceToChat() && this.owner.getWorld().getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
                    var announce = display.getFrame().getChatAnnouncementText(advancement, this.owner);
                    MessageBridgeFabric.instance.onAdvancementAchieved(this.owner, advancement.value(), announce);
                }
            });
        }
    }
}