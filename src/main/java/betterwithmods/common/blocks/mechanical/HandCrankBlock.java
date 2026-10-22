package betterwithmods.common.blocks.mechanical;

import betterwithmods.common.blockentity.HandCrankBlockEntity;
import betterwithmods.common.config.BWMConfig;
import betterwithmods.common.registry.BWMItems;
import betterwithmods.common.registry.BWMSounds;
import betterwithmods.util.MechanicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class HandCrankBlock extends Block implements EntityBlock {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 7);
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    private static final int TICK_RATE = 3;

    public HandCrankBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int currentStage = state.getValue(STAGE);
        if (currentStage == 0) {
            double exhaustion = BWMConfig.COMMON.crankExhaustion.get();
            if (exhaustion > 0.0) {
                int minHunger = 6;
                if (player.getFoodData().getFoodLevel() > minHunger) {
                    player.causeFoodExhaustion((float) exhaustion);
                    if (!level.isClientSide) {
                        toggleSwitch(level, pos, state);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                } else {
                    if (level.isClientSide && hand == InteractionHand.MAIN_HAND) {
                        player.displayClientMessage(Component.translatable("bwm.message.exhaustion"), true);
                    }
                    return InteractionResult.CONSUME;
                }
            } else {
                if (!level.isClientSide) {
                    toggleSwitch(level, pos, state);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    private void toggleSwitch(Level level, BlockPos pos, BlockState state) {
        if (checkForOverpower(level, pos)) {
            overpower(level, pos);
            return;
        }
        level.setBlock(pos, state.setValue(STAGE, 1), 3);
        level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 1.0f, 2.0f);
        level.scheduleTick(pos, this, TICK_RATE);
    }

    public boolean checkForOverpower(Level level, BlockPos pos) {
        int count = 0;
        for (Direction direction : Direction.values()) {
            if (direction != Direction.UP) {
                BlockPos offset = pos.relative(direction);
                if (MechanicalUtil.canInput(level, offset, direction)) {
                    count++;
                }
            }
        }
        return count > 1;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int stage = state.getValue(STAGE);
        if (stage > 0) {
            if (stage < 7) {
                if (checkForOverpower(level, pos)) {
                    overpower(level, pos);
                    return;
                }
                level.setBlock(pos, state.setValue(STAGE, stage + 1), 3);
                level.scheduleTick(pos, this, TICK_RATE);
            } else {
                level.setBlock(pos, state.setValue(STAGE, 0), 3);
                level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 0.7f, 0.9f);
            }
            notifyNeighbors(level, pos);
        }
    }

    private void notifyNeighbors(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos offset = pos.relative(direction);
            BlockState neighborState = level.getBlockState(offset);
            neighborState.neighborChanged(level, offset, this, pos, false);
        }
    }

    public void overpower(Level level, BlockPos pos) {
        level.destroyBlock(pos, false);
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(BWMItems.GEAR.get()));
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.COBBLESTONE));
        level.playSound(null, pos, BWMSounds.MECHANICAL_OVERPOWER.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HandCrankBlockEntity(pos, state);
    }
}
