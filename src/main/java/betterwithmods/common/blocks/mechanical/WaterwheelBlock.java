package betterwithmods.common.blocks.mechanical;

import betterwithmods.common.blockentity.WaterwheelBlockEntity;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.common.registry.BWMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WaterwheelBlock extends AxleGeneratorBlock {

    public WaterwheelBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getGeneratorItem(BlockState state) {
        return new ItemStack(BWMItems.WATERWHEEL.get());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WaterwheelBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BWMBlockEntities.WATERWHEEL.get(), WaterwheelBlockEntity::tick);
    }
}
