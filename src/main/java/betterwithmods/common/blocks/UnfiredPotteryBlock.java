package betterwithmods.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class UnfiredPotteryBlock extends Block {
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 4);

    public enum EnumType implements StringRepresentable {
        CRUCIBLE("crucible"),
        PLANTER("planter"),
        VASE("vase"),
        URN("urn"),
        BRICK("brick");

        private final String name;
        EnumType(String name) { this.name = name; }
        @Override public String getSerializedName() { return this.name; }
    }

    private static final VoxelShape CRUCIBLE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);
    private static final VoxelShape PLANTER_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    private static final VoxelShape VASE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 14.0, 13.0);
    private static final VoxelShape URN_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 12.0, 12.0);
    private static final VoxelShape BRICK_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 4.0, 12.0);

    public UnfiredPotteryBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(TYPE)) {
            case 0 -> CRUCIBLE_SHAPE;
            case 1 -> PLANTER_SHAPE;
            case 2 -> VASE_SHAPE;
            case 3 -> URN_SHAPE;
            default -> BRICK_SHAPE;
        };
    }
}
