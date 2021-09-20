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

import com.callumwong.nullifier.common.tiles.NullifierTileEntity;
import com.callumwong.nullifier.core.event.EventHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class NullifierContainer extends AbstractContainerMenu {
    private final NullifierContents nullifierContents;

    public static NullifierContainer createContainerServerSide(int windowID, Inventory playerInventory, NullifierContents nullifierContents) {
        return new NullifierContainer(windowID, playerInventory, nullifierContents);
    }

    public static NullifierContainer createContainerClientSide(int windowID, Inventory playerInventory, FriendlyByteBuf extraData) {
        NullifierContents nullifierContents = NullifierContents.createForClientSideContainer(NULLIFIER_SLOT_COUNT);

        return new NullifierContainer(windowID, playerInventory, nullifierContents);
    }

    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 45 = nullifier slots (0 - 8)

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int HOTBAR_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX;
    private static final int PLAYER_INVENTORY_FIRST_SLOT_INDEX = HOTBAR_FIRST_SLOT_INDEX + HOTBAR_SLOT_COUNT;
    private static final int NULLIFIER_SLOT_INDEX = PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT;

    private static final int NULLIFIER_SLOT_COUNT = NullifierTileEntity.NUMBER_OF_SLOTS;

    public NullifierContainer(int windowID, Inventory invPlayer, NullifierContents nullifierContents) {
        super(EventHandler.nullifierContainerType, windowID);
        if (EventHandler.nullifierContainerType == null)
            throw new IllegalStateException("Must initialise containerTypeContainerFurnace before constructing a ContainerFurnace!");

        this.nullifierContents = nullifierContents;
        nullifierContents.startOpen(invPlayer.player);

        // Add the nullifier slots
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                this.addSlot(new Slot(nullifierContents, x + y * 3, 62 + y * 18, 17 + x * 18));
            }
        }

        // Add the player's inventory
        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(invPlayer, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        // Add the player's hotbar
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(invPlayer, l, 8 + l * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return nullifierContents.stillValid(player);
    }

    public ItemStack quickMoveStack(Player player, int sourceSlotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(sourceSlotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack sourceItemStack = slot.getItem();
            itemstack = sourceItemStack.copy();
            if (sourceSlotIndex < 9) {
                return ItemStack.EMPTY; // Don't allow taking items out of nullifier
            } else if (!this.moveItemStackTo(sourceItemStack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (sourceItemStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (sourceItemStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, sourceItemStack);
        }

        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.nullifierContents.stopOpen(player);
    }
}