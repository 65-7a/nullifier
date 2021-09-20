/*
 * Copyright (c) 2021 Callum Wong
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.callumwong.nullifier.common.blocks;

import com.callumwong.nullifier.common.tiles.NullifierTileEntity;
import com.callumwong.nullifier.core.event.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;

public class NullifierBlock extends BaseEntityBlock {
    public NullifierBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new NullifierTileEntity(pos, blockState);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        if (world.isClientSide()) return InteractionResult.SUCCESS; // on client side, don't do anything

        MenuProvider namedContainerProvider = this.getMenuProvider(state, world, pos);
        if (namedContainerProvider != null) {
            if (!(player instanceof ServerPlayer))
                return InteractionResult.FAIL;  // should always be true, but just in case...
            ServerPlayer serverPlayer = (ServerPlayer) player;
            NetworkHooks.openGui(serverPlayer, namedContainerProvider, (packetBuffer) -> {
            });
        }

        return InteractionResult.SUCCESS;
    }

    // required because the default (super method) is INVISIBLE for BlockContainers.
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, EventHandler.nullifierTileEntityType, world.isClientSide ? null : NullifierTileEntity::serverTick);
    }
}
