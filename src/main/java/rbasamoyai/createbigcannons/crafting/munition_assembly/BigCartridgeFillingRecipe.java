package rbasamoyai.createbigcannons.crafting.munition_assembly;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import rbasamoyai.createbigcannons.CBCTags;
import rbasamoyai.createbigcannons.index.CBCBlocks;
import rbasamoyai.createbigcannons.index.CBCRecipeTypes;
import rbasamoyai.createbigcannons.munitions.big_cannon.propellant.BigCartridgeBlockItem;

public class BigCartridgeFillingRecipe extends CustomRecipe {

	public BigCartridgeFillingRecipe(ResourceLocation id) { super(id, CraftingBookCategory.MISC); }

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		ItemStack cartridge = ItemStack.EMPTY;
		int powderCount = 0;

		for (int i = 0; i < container.getContainerSize(); ++i) {
			ItemStack stack = container.getItem(i);
			if (stack.isEmpty()) continue;
			if (CBCBlocks.BIG_CARTRIDGE.is(stack.getItem())) {
				if (!cartridge.isEmpty()) return false;
				cartridge = stack;
			} else if (stack.is(CBCTags.CBCItemTags.NITROPOWDER)) {
				++powderCount;
			} else {
				return false;
			}
		}
		return !cartridge.isEmpty() && powderCount > 0;
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
		ItemStack cartridge = ItemStack.EMPTY;
		BigCartridgeBlockItem cartridgeItem = null;
		int powderCount = 0;

		for (int i = 0; i < container.getContainerSize(); ++i) {
			ItemStack stack = container.getItem(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() instanceof BigCartridgeBlockItem foundCartridge) {
				if (!cartridge.isEmpty()) return ItemStack.EMPTY;
				cartridge = stack;
				cartridgeItem = foundCartridge;
			} else if (stack.is(CBCTags.CBCItemTags.NITROPOWDER)) {
				++powderCount;
			} else {
				return ItemStack.EMPTY;
			}
		}

		if (cartridge.isEmpty() || cartridgeItem == null || powderCount == 0) return ItemStack.EMPTY;

		ItemStack result = cartridge.copy();
		result.setCount(1);
		CompoundTag tag = result.getOrCreateTag();
		int oldPower = tag.getInt("Power");
		int newPower = Math.min(cartridgeItem.getMaximumPowerLevels(), oldPower + powderCount);
		tag.putInt("Power", newPower);
		return result;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
		NonNullList<ItemStack> result = super.getRemainingItems(container);
		BigCartridgeBlockItem cartridgeItem = null;
		int sz = container.getContainerSize();
		int powderCount = 0;

		ItemStack oldCartridge = ItemStack.EMPTY;
		for (int i = 0; i < sz; ++i) {
			ItemStack stack = container.getItem(i);
			if (stack.getItem() instanceof BigCartridgeBlockItem foundCartridge) {
				oldCartridge = stack;
				cartridgeItem = foundCartridge;
			} else if (stack.is(CBCTags.CBCItemTags.NITROPOWDER)) {
				++powderCount;
			}
		}

		if (oldCartridge.isEmpty() || cartridgeItem == null) return result;

		int oldPower = oldCartridge.getOrCreateTag().getInt("Power");
		int newPower = Math.min(cartridgeItem.getMaximumPowerLevels(), oldPower + powderCount);
		int consumed = newPower - oldPower;

		for (int i = 0; i < sz; ++i) {
			ItemStack stack = container.getItem(i);
			if (stack.is(CBCTags.CBCItemTags.NITROPOWDER)) {
				if (consumed > 0) --consumed;
				else stack.grow(1);
			}
		}

		return result;
	}

	@Override public boolean canCraftInDimensions(int width, int height) { return width * height > 1; }

	@Override public RecipeSerializer<?> getSerializer() { return CBCRecipeTypes.BIG_CARTRIDGE_FILLING.getSerializer(); }

}
