package rbasamoyai.createbigcannons.cannons.big_cannons.breeches.screw_breech;

import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.createbigcannons.cannons.big_cannons.BigCannonBlock;
import rbasamoyai.createbigcannons.cannons.big_cannons.material.BigCannonMaterial;
import rbasamoyai.createbigcannons.cannons.big_cannons.cannon_end.BigCannonEnd;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;
import rbasamoyai.createbigcannons.index.CBCBlockEntities;
import rbasamoyai.createbigcannons.index.CBCShapes;

public class ScrewBreechBlock extends DirectionalKineticBlock implements IBE<ScrewBreechBlockEntity>, BigCannonBlock {

	public static final EnumProperty<BigCannonEnd> OPEN = EnumProperty.create("open", BigCannonEnd.class);

	private final BigCannonMaterial material;

	public ScrewBreechBlock(Properties properties, BigCannonMaterial material) {
		super(properties.pushReaction(PushReaction.BLOCK));
		this.material = material;
		this.registerDefaultState(this.getStateDefinition().any().setValue(OPEN, BigCannonEnd.CLOSED));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(OPEN);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
		return CBCShapes.SCREW_BREECH.get(state.getValue(FACING));
	}

	@Override
	public BigCannonMaterial getCannonMaterial() {
		return this.material;
	}

	@Override
	public CannonCastShape getCannonShape() {
		return CannonCastShape.SCREW_BREECH;
	}

	@Override
	public Direction getFacing(BlockState state) {
		return state.getValue(FACING).getOpposite();
	}

	@Override
	public BigCannonEnd getOpeningType(@Nullable Level level, BlockState state, BlockPos pos) {
		return state.getValue(OPEN);
	}

	@Override public BigCannonEnd getDefaultOpeningType() { return BigCannonEnd.CLOSED; }

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(FACING).getAxis();
	}

	@Override public boolean canConnectToSide(BlockState state, Direction dir) { return this.getFacing(state) == dir; }

	@Override
	public boolean isComplete(BlockState state) {
		return true;
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(FACING);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!level.isClientSide) this.onRemoveCannon(state, level, pos, newState, isMoving);
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide) this.playerWillDestroyBigCannon(level, pos, state, player);
		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public Class<ScrewBreechBlockEntity> getBlockEntityClass() {
		return ScrewBreechBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends ScrewBreechBlockEntity> getBlockEntityType() {
		return CBCBlockEntities.SCREW_BREECH.get();
	}

	@Override
	public InteractionResult onWrenched(BlockState state, UseOnContext context) {
		return state.getValue(OPEN) == BigCannonEnd.OPEN ? super.onWrenched(state, context) : InteractionResult.PASS;
	}

	@Override
	public Direction getPreferredFacing(BlockPlaceContext context) {
		Direction preferredSide = null;
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction clickedFace = context.getClickedFace();
		for (Direction side : Iterate.directions) {
			BlockState blockState = level.getBlockState(pos.relative(side));
			if (blockState.getBlock() instanceof BigCannonBlock cBlock
				&& cBlock.getCannonMaterial() == this.getCannonMaterial()
				&& cBlock.canConnectToSide(blockState, side.getOpposite())) {

				if (side.getOpposite() == clickedFace) {
					preferredSide = side;
					break;
				} else if (preferredSide != null && preferredSide.getAxis() != side.getAxis()) {
					preferredSide = null;
					break;
				} else {
					preferredSide = side;
				}
			}
		}
		return preferredSide != null ? preferredSide : super.getPreferredFacing(context);
	}

}
