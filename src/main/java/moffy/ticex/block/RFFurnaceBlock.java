package moffy.ticex.block;

import javax.annotation.Nullable;

import moffy.ticex.block.entity.RFFurnaceBlockEntity;
import moffy.ticex.modules.TicEXRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fluids.FluidType;
import slimeknights.tconstruct.smeltery.block.component.OrientableSmelteryBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;

public class RFFurnaceBlock extends SearedTankBlock{

    private boolean isCreative;

    public RFFurnaceBlock(Properties pProperties, boolean isCreative) {
        super(pProperties, FluidType.BUCKET_VOLUME, PushReaction.DESTROY);
        this.isCreative = isCreative;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RFFurnaceBlockEntity(TicEXRegistry.RF_FURNACE_ENTITY.get(), pPos, pState, isCreative);
    }
    
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState,
            BlockEntityType<T> pBlockEntityType) {
        if(pBlockEntityType == TicEXRegistry.RF_FURNACE_ENTITY.get() && !pLevel.isClientSide()){
            return (Level level, BlockPos pos, BlockState state, T pBlockEntity)->RFFurnaceBlockEntity.serverTick(level, pos, state, (RFFurnaceBlockEntity)pBlockEntity);
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OrientableSmelteryBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(OrientableSmelteryBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Deprecated
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(OrientableSmelteryBlock.FACING, rotation.rotate(state.getValue(OrientableSmelteryBlock.FACING)));
    }

    @Deprecated
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(OrientableSmelteryBlock.FACING, mirror.mirror(state.getValue(OrientableSmelteryBlock.FACING)));
    }
}
