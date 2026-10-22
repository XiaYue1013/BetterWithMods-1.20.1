package betterwithmods.common.blocks.mechanical;

import betterwithmods.common.blockentity.ScrewPumpBlockEntity;
import betterwithmods.common.registry.BWMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class ScrewPumpBlock extends Block implements EntityBlock {
    public ScrewPumpBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ScrewPumpBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return type == BWMBlockEntities.PUMP.get() ?
                (lvl, p, st, be) -> ScrewPumpBlockEntity.serverTick(lvl, p, st, (ScrewPumpBlockEntity) be) : null;
    }
}
