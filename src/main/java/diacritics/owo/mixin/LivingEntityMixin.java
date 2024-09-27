package diacritics.owo.mixin;

import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fabric.net.mca.entity.VillagerEntityMCA;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
  protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "tryUseTotem", at = @At("HEAD"))
  private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
    if ((Entity) this instanceof VillagerEntityMCA) {
      VillagerEntityMCA entity = (VillagerEntityMCA) (Entity) this;
      Optional<ItemStack> totem = entity.getInventory().stacks.stream()
          .filter((stack) -> stack.isOf(Items.TOTEM_OF_UNDYING)).findFirst();

      if (totem.isPresent()) {
        entity.setStackInHand(Hand.MAIN_HAND, totem.get());
      }
    }
  }
}
