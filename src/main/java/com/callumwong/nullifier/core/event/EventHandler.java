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

package com.callumwong.nullifier.core.event;

import com.callumwong.nullifier.common.blocks.NullifierBlock;
import com.callumwong.nullifier.common.containers.NullifierContainer;
import com.callumwong.nullifier.common.tiles.NullifierTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {
    public static Block nullifierBlock;
    public static BlockItem nullifierBlockItem;

    public static TileEntityType<NullifierTileEntity> nullifierTileEntityType;
    public static ContainerType<NullifierContainer> nullifierContainerType;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        nullifierBlock = new NullifierBlock(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(3.5F));
        nullifierBlock.setRegistryName("nullifier");
        event.getRegistry().register(nullifierBlock);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        nullifierBlockItem = new BlockItem(nullifierBlock, new Item.Properties().tab(ItemGroup.TAB_DECORATIONS));
        nullifierBlockItem.setRegistryName(nullifierBlock.getRegistryName());
        event.getRegistry().register(nullifierBlockItem);
    }

    @SubscribeEvent
    public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        nullifierTileEntityType = TileEntityType.Builder.of(NullifierTileEntity::new, nullifierBlock).build(null);
        nullifierTileEntityType.setRegistryName("nullifier");
        event.getRegistry().register(nullifierTileEntityType);
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        nullifierContainerType = IForgeContainerType.create(NullifierContainer::createContainerClientSide);
        nullifierContainerType.setRegistryName("nullifer");
        event.getRegistry().register(nullifierContainerType);
    }
}
