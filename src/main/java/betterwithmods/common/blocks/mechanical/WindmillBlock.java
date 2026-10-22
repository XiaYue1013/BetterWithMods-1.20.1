package betterwithmods.common.blocks.mechanical;

import betterwithmods.common.blockentity.WindmillBlockEntity;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.common.registry.BWMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class WindmillBlock extends AxleGeneratorBlock {

    public WindmillBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getGeneratorItem(BlockState state) {
        if (state.getValue(AXIS) == Direction.Axis.Y) {
            return new ItemStack(BWMItems.VERTICAL_WINDMILL.get());
        }
        return new ItemStack(BWMItems.WINDMILL.get());
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof DyeItem dyeItem) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof WindmillBlockEntity windmill) {
                    DyeColor color = dyeItem.getDyeColor();
                    if (windmill.dye(color)) {
                        if (!player.getAbilities().instabuild) {
                            held.shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WindmillBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BWMBlockEntities.WINDMILL.get(), WindmillBlockEntity::tick);
    }
}
