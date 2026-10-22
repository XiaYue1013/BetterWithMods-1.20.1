package betterwithmods.common.blocks.mechanical;

import betterwithmods.common.registry.BWMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AxleGeneratorBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty STEEL = BooleanProperty.create("steel");

    private static final VoxelShape X_SHAPE = Block.box(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    private static final VoxelShape Y_SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    private static final VoxelShape Z_SHAPE = Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);

    public AxleGeneratorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Z)
                .setValue(ACTIVE, false)
                .setValue(STEEL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, ACTIVE, STEEL);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(AXIS)) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }

    public abstract ItemStack getGeneratorItem(BlockState state);

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            boolean steel = state.getValue(STEEL);
            ItemStack axle = new ItemStack(steel ? BWMBlocks.STEEL_AXLE.get() : BWMBlocks.WOODEN_AXLE.get());
            ItemStack genItem = getGeneratorItem(state);
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), axle);
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), genItem);
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }
}
