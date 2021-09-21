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

package com.callumwong.nullifier.common.containers;

import com.callumwong.nullifier.core.interfaces.Notify;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class NullifierContents implements Container {
    /**
     * Use this constructor to create a NullifierContents which is linked to its parent TileEntity.
     * On the server, this link will be used by the Container to request information and provide notifications to the parent
     * On the client, the link will be unused.
     * There are additional notificationLambdas available; these two are explicitly specified because your TileEntity will
     * nearly always need to implement at least these two
     *
     * @param size                           the max number of ItemStacks in the inventory
     * @param canPlayerAccessInventoryLambda the function that the container should call in order to decide if the given player
     *                                       can access the container's contents not.  Usually, this is a check to see
     *                                       if the player is closer than 8 blocks away.
     * @param markDirtyNotificationLambda    the function that the container should call in order to tell the parent TileEntity
     *                                       that the contents of its inventory have been changed and need to be saved.  Usually,
     *                                       this is TileEntity::markDirty
     * @return the new ChestContents.
     */
    public static NullifierContents createForTileEntity(int size, Predicate<Player> canPlayerAccessInventoryLambda, Notify markDirtyNotificationLambda) {
        return new NullifierContents(size, canPlayerAccessInventoryLambda, markDirtyNotificationLambda);
    }

    /**
     * Use this constructor to create a FurnaceZoneContents which is not linked to any parent TileEntity; i.e. is used by the client side container:
     * * does not permanently store items
     * * cannot ask questions/provide notifications to a parent TileEntity
     *
     * @param size the max number of ItemStacks in the inventory
     * @return the new ChestContents
     */
    public static NullifierContents createForClientSideContainer(int size) {
        return new NullifierContents(size);
    }

    public void setCanPlayerAccessInventoryLambda(Predicate<Player> canPlayerAccessInventoryLambda) {
        this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;
    }

    public void setMarkDirtyNotificationLambda(Notify markDirtyNotificationLambda) {
        this.markDirtyNotificationLambda = markDirtyNotificationLambda;
    }

    public void setOpenInventoryNotificationLambda(Notify openInventoryNotificationLambda) {
        this.openInventoryNotificationLambda = openInventoryNotificationLambda;
    }

    public void setCloseInventoryNotificationLambda(Notify closeInventoryNotificationLambda) {
        this.closeInventoryNotificationLambda = closeInventoryNotificationLambda;
    }

    @Override
    public boolean stillValid(Player player) {
        return canPlayerAccessInventoryLambda.test(player);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return nullifierContents.isItemValid(index, stack);
    }

    @Override
    public void setChanged() {
        markDirtyNotificationLambda.invoke();
    }

    @Override
    public void startOpen(Player player) {
        openInventoryNotificationLambda.invoke();
    }

    @Override
    public void stopOpen(Player player) {
        closeInventoryNotificationLambda.invoke();
    }

    @Override
    public int getContainerSize() {
        return nullifierContents.getSlots();
    }

    @Override
    public boolean isEmpty() {
//        for (int i = 0; i < nullifierContents.getSlots(); ++i) {
//            if (!nullifierContents.getStackInSlot(i).isEmpty()) return false;
//        }

        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return nullifierContents.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return nullifierContents.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        int maxPossibleItemStackSize = nullifierContents.getSlotLimit(index);
        return nullifierContents.extractItem(index, maxPossibleItemStackSize, false);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        nullifierContents.setStackInSlot(index, stack);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < nullifierContents.getSlots(); ++i) {
            nullifierContents.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    private NullifierContents(int size) {
        this.nullifierContents = new ReadOnlyItemStackHandler(size);
    }

    private NullifierContents(int size, Predicate<Player> canPlayerAccessInventoryLambda, Notify markDirtyNotificationLambda) {
        this.nullifierContents = new ReadOnlyItemStackHandler(size);
        this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;
        this.markDirtyNotificationLambda = markDirtyNotificationLambda;
    }

    private Predicate<Player> canPlayerAccessInventoryLambda = x -> true;
    private Notify markDirtyNotificationLambda = () -> {
    };
    private Notify openInventoryNotificationLambda = () -> {
    };
    private Notify closeInventoryNotificationLambda = () -> {
    };
    private final ReadOnlyItemStackHandler nullifierContents;
}
