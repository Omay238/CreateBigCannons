package rbasamoyai.createbigcannons.forge.mixin;

import javax.annotation.Nonnull;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.simibubi.create.foundation.data.CreateRegistrate;

import dev.architectury.patchedmixin.staticmixin.spongepowered.asm.mixin.Shadow;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import rbasamoyai.createbigcannons.index.fluid_utils.CBCFlowingFluid;

@Mixin(CBCFlowingFluid.class)
public abstract class CBCFlowingFluidMixin extends FlowingFluid {

	@Shadow
	@Final
	protected ResourceLocation stillTex;
	@Shadow
	@Final
	protected ResourceLocation flowingTex;
	@Shadow
	@Final
	protected int color;
	@Shadow
	@Final
	protected SoundEvent fillSound;
	@Shadow
	@Final
	protected SoundEvent emptySound;

	@Unique private FluidType fluidType;

	@Nonnull
	@Override
	public FluidType getFluidType() {
		if (this.fluidType == null) {
			this.fluidType = CreateRegistrate.defaultFluidType(
				FluidType.Properties.create()
					.sound(SoundActions.BUCKET_FILL, this.fillSound)
					.sound(SoundActions.BUCKET_EMPTY, this.emptySound)
					.descriptionId(Util.makeDescriptionId("fluid", BuiltInRegistries.FLUID.getKey(this))), this.stillTex, this.flowingTex);
		}
		return this.fluidType;
	}
}
