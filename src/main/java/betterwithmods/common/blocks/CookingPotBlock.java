package betterwithmods.common.blocks;

import betterwithmods.common.blockentity.CookingPotBlockEntity;
import betterwithmods.common.registry.BWMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class CookingPotBlock extends Block implements EntityBlock {
    public enum PotType {
        CAULDRON,
        CRUCIBLE,
        DRAGON_VESSEL
    }

    private final PotType type;
    private static final VoxelShape INSIDE = box(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);
    private static final VoxelShape SHAPE = Shapes.join(Shapes.block(), INSIDE, BooleanOp.ONLY_FIRST);

    public CookingPotBlock(Properties properties, PotType type) {
        super(properties);
        this.type = type;
    }

    public PotType getType() {
        return type;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CookingPotBlockEntity pot && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, pot, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof CookingPotBlockEntity pot) {
                Containers.dropContents(level, pos, pot);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CookingPotBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;
        return blockEntityType == BWMBlockEntities.COOKING_POT.get() ?
                (lvl, p, st, be) -> CookingPotBlockEntity.serverTick(lvl, p, st, (CookingPotBlockEntity) be) : null;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CookingPotBlockEntity pot && pot.checkHeatLevel() > 0) {
            if (random.nextInt(3) == 0) {
                level.addParticle(ParticleTypes.BUBBLE_POP,
                        pos.getX() + 0.3 + random.nextDouble() * 0.4,
                        pos.getY() + 0.9,
                        pos.getZ() + 0.3 + random.nextDouble() * 0.4,
                        0.0, 0.05, 0.0);
            }
        }
    }
}
