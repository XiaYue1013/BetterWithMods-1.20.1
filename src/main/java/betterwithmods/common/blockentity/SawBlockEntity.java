package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.SawBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.common.registry.BWMBlocks;
import betterwithmods.common.registry.BWMItems;
import betterwithmods.common.registry.BWMSounds;
import betterwithmods.util.MechanicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Containers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SawBlockEntity extends BlockEntity implements IMechanicalPower {
    private int power;
    private int cutCounter;
    public static final int CUT_TIME = 30;

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);

    public SawBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.SAW.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SawBlockEntity saw) {
        saw.power = saw.calculateInput();
        boolean isActive = saw.power > 0;

        if (state.getValue(SawBlock.ACTIVE) != isActive) {
            level.setBlock(pos, state.setValue(SawBlock.ACTIVE, isActive), 3);
        }

        if (isActive) {
            Direction facing = state.getValue(SawBlock.FACING);
            BlockPos targetPos = pos.relative(facing);

            // Damage entities in front of blade
            AABB bladeBox = new AABB(targetPos);
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, bladeBox);
            for (LivingEntity victim : victims) {
                victim.hurt(level.damageSources().generic(), 4.0f);
            }

            // Cut block in front
            BlockState targetState = level.getBlockState(targetPos);
            if (saw.canCut(targetState)) {
                saw.cutCounter++;
                if (saw.cutCounter % 10 == 0) {
                    level.playSound(null, pos, BWMSounds.SAW_CUT.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                }
                if (saw.cutCounter >= CUT_TIME) {
                    saw.cutBlock(targetPos, targetState);
                    saw.cutCounter = 0;
                }
            } else {
                saw.cutCounter = 0;
            }
        } else {
            saw.cutCounter = 0;
        }
    }

    private java.util.Optional<betterwithmods.common.recipe.SawRecipe> getSawRecipe(BlockState target) {
        if (level == null) return java.util.Optional.empty();
        net.minecraft.world.SimpleContainer tempContainer = new net.minecraft.world.SimpleContainer(new ItemStack(target.getBlock()));
        return level.getRecipeManager().getRecipeFor(betterwithmods.common.registry.BWMRecipes.SAW_TYPE.get(), tempContainer, level);
    }

    private boolean canCut(BlockState target) {
        if (target.isAir()) return false;
        if (getSawRecipe(target).isPresent()) return true;
        boolean isSteel = getBlockState().is(BWMBlocks.STEEL_SAW.get());
        if (target.is(BlockTags.LOGS) || target.is(BlockTags.PLANKS)) return true;
        if (isSteel && (target.is(BlockTags.MINEABLE_WITH_PICKAXE) || target.is(Blocks.OBSIDIAN))) return true;
        return false;
    }

    private void cutBlock(BlockPos targetPos, BlockState target) {
        level.destroyBlock(targetPos, false);
        java.util.Optional<betterwithmods.common.recipe.SawRecipe> recipe = getSawRecipe(target);
        if (recipe.isPresent()) {
            for (ItemStack out : recipe.get().getResults()) {
                Containers.dropItemStack(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, out.copy());
            }
            return;
        }

        if (target.is(BlockTags.LOGS)) {
            Containers.dropItemStack(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(Items.OAK_PLANKS, 4));
            Containers.dropItemStack(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(BWMItems.SAWDUST.get(), 2));
            Containers.dropItemStack(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(BWMItems.BARK_OAK.get(), 1));
        } else if (target.is(BlockTags.PLANKS)) {
            Containers.dropItemStack(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(Items.OAK_SLAB, 2));
            Containers.dropItemStack(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(BWMItems.SAWDUST.get(), 1));
        } else {
            Containers.dropItemStack(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(target.getBlock()));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.putInt("power", power);
        compound.putInt("cutCounter", cutCounter);
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.power = compound.getInt("power");
        this.cutCounter = compound.getInt("cutCounter");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityMechanical.MECHANICAL_POWER) {
            return powerCap.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        powerCap.invalidate();
    }

    @Override
    public int getMechanicalOutput(Direction facing) {
        return 0;
    }

    @Override
    public int getMechanicalInput(Direction facing) {
        BlockState state = getBlockState();
        if (state.hasProperty(SawBlock.FACING) && facing == state.getValue(SawBlock.FACING)) {
            return 0; // Blade face cannot receive power
        }
        BlockPos offset = worldPosition.relative(facing);
        return MechanicalUtil.getPowerOutput(level, offset, facing.getOpposite());
    }

    @Override
    public int getMaximumInput(Direction facing) {
        return 50;
    }

    @Override
    public int getMinimumInput(Direction facing) {
        return 1;
    }

    @Override
    public Block getBlock() {
        return getBlockState().getBlock();
    }

    @Override
    public Level getBlockWorld() {
        return level;
    }

    @Override
    public BlockPos getBlockPos() {
        return worldPosition;
    }
}
