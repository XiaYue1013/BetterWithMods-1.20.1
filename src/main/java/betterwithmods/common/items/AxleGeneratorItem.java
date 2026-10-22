package betterwithmods.common.items;

import betterwithmods.common.blocks.mechanical.AxleBlock;
import betterwithmods.common.blocks.mechanical.AxleGeneratorBlock;
import betterwithmods.common.registry.BWMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AxleGeneratorItem extends Item {
    public enum GeneratorType {
        WINDMILL,
        WATERWHEEL,
        VERTICAL_WINDMILL
    }

    private final GeneratorType type;

    public AxleGeneratorItem(Properties properties, GeneratorType type) {
        super(properties);
        this.type = type;
    }

    public GeneratorType getType() {
        return type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof AxleBlock axleBlock)) {
            return InteractionResult.PASS;
        }

        Direction.Axis axis = state.getValue(AxleBlock.AXIS);
        boolean isSteel = axleBlock.isSteel();

        if (type == GeneratorType.VERTICAL_WINDMILL) {
            if (axis != Direction.Axis.Y) {
                if (level.isClientSide && player != null) {
                    player.displayClientMessage(Component.translatable("bwm.message.vertical_windmill.1"), true);
                }
                return InteractionResult.FAIL;
            }

            // Find center pos if clicked near top or bottom
            float clickY = (float) (context.getClickLocation().y - pos.getY());
            int yOffset = clickY > 0.5f ? 3 : -3;
            BlockPos targetPos = pos.above(yOffset);

            if (!checkSupportingAxles(level, targetPos)) {
                // If offset target doesn't work, test clicked pos as center
                if (checkSupportingAxles(level, pos)) {
                    targetPos = pos;
                } else {
                    if (level.isClientSide && player != null) {
                        player.displayClientMessage(Component.translatable("bwm.message.vertical_windmill.1"), true);
                    }
                    return InteractionResult.FAIL;
                }
            }

            if (!validateVerticalClearance(level, targetPos)) {
                if (level.isClientSide && player != null) {
                    player.displayClientMessage(Component.translatable("bwm.message.vertical_windmill.0"), true);
                }
                return InteractionResult.FAIL;
            }

            if (!level.isClientSide) {
                BlockState newGenState = BWMBlocks.WINDMILL.get().defaultBlockState()
                        .setValue(AxleGeneratorBlock.AXIS, Direction.Axis.Y)
                        .setValue(AxleGeneratorBlock.STEEL, isSteel);
                level.setBlock(targetPos, newGenState, 3);

                level.playSound(null, targetPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (player != null && !player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            // Horizontal generators: WINDMILL and WATERWHEEL
            if (axis == Direction.Axis.Y) {
                return InteractionResult.FAIL;
            }

            int radius = (type == GeneratorType.WATERWHEEL) ? 2 : 6;
            if (!validateHorizontalClearance(level, pos, radius, axis, type == GeneratorType.WATERWHEEL)) {
                if (level.isClientSide && player != null) {
                    String msgKey = (type == GeneratorType.WATERWHEEL) ? "bwm.message.waterwheel.0" : "bwm.message.windmill.0";
                    player.displayClientMessage(Component.translatable(msgKey), true);
                }
                return InteractionResult.FAIL;
            }

            if (!level.isClientSide) {
                BlockState targetGen = (type == GeneratorType.WATERWHEEL ? BWMBlocks.WATERWHEEL.get() : BWMBlocks.WINDMILL.get())
                        .defaultBlockState()
                        .setValue(AxleGeneratorBlock.AXIS, axis)
                        .setValue(AxleGeneratorBlock.STEEL, isSteel);
                level.setBlock(pos, targetGen, 3);

                level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (player != null && !player.getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    private boolean checkSupportingAxles(Level level, BlockPos center) {
        for (int i = -3; i <= 3; i++) {
            BlockPos p = center.above(i);
            BlockState s = level.getBlockState(p);
            if (!(s.getBlock() instanceof AxleBlock)) return false;
            if (s.getValue(AxleBlock.AXIS) != Direction.Axis.Y) return false;
        }
        return true;
    }

    private boolean validateVerticalClearance(Level level, BlockPos center) {
        for (int yOffset = -3; yOffset <= 3; yOffset++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    if (x == 0 && z == 0) continue;
                    if (x * x + z * z > 20) continue;

                    BlockPos check = center.offset(x, yOffset, z);
                    BlockState s = level.getBlockState(check);
                    if (!s.canBeReplaced() && !s.isAir()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean validateHorizontalClearance(Level level, BlockPos pos, int radius, Direction.Axis axis, boolean isWheel) {
        for (int vert = -radius; vert <= radius; vert++) {
            for (int i = -radius; i <= radius; i++) {
                if (vert == 0 && i == 0) continue;

                int xP = (axis == Direction.Axis.Z ? i : 0);
                int zP = (axis == Direction.Axis.X ? i : 0);
                BlockPos checkPos = pos.offset(xP, vert, zP);

                BlockState s = level.getBlockState(checkPos);
                boolean clear = s.canBeReplaced() || s.isAir();
                if (isWheel && (vert == -radius || i == -radius || i == radius)) {
                    clear = clear || !s.getFluidState().isEmpty();
                }
                if (!clear) {
                    return false;
                }
            }
        }
        return true;
    }
}
