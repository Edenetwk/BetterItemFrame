package me.capseden.optimization;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterItemFrame extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this,this);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        ItemStack playerMainHandItem = inventory.getItemInMainHand();
        ItemStack playerOffHandItem = inventory.getItemInOffHand();

        Material playerMainHandItemType = playerMainHandItem.getType();
        Material playerOffHandItemType = playerOffHandItem.getType();
        if (playerMainHandItemType.equals(Material.SHEARS)) {
            playerMainHandItem.damage(1,player);
        } else if (playerOffHandItemType.equals(Material.SHEARS)) {
            playerOffHandItem.damage(1,player);
        } else {
            return;
        }

        Entity targetEntity = event.getRightClicked();

        if (!(targetEntity instanceof ItemFrame itemFrame)) return;
        if (!itemFrame.isVisible()) return;

        ItemStack itemFrameItem = itemFrame.getItem();
        if (itemFrameItem.getType().equals(Material.AIR)) return;

        itemFrame.setVisible(false);

        ItemStack dropItem;
        if (itemFrame instanceof GlowItemFrame){
            dropItem = new ItemStack(Material.GLOW_ITEM_FRAME);
        } else {
            dropItem = new ItemStack(Material.ITEM_FRAME);
        }

        Location location = itemFrame.getLocation();
        location.getWorld().dropItem(location,dropItem);

        location.getWorld().playSound(itemFrame, Sound.ENTITY_SHEEP_SHEAR, SoundCategory.PLAYERS, 1f, 1f);

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemFrameChange(PlayerItemFrameChangeEvent event){
        if (!event.getAction().equals(PlayerItemFrameChangeEvent.ItemFrameChangeAction.REMOVE)) return;
        ItemFrame itemFrame = event.getItemFrame();
        if (itemFrame.isVisible()) return;
        itemFrame.remove();
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event){
        Hanging entity = event.getEntity();
        if (!(entity instanceof ItemFrame itemFrame)) return;
        if (itemFrame.isVisible()) return;
        event.setCancelled(true);
        Location location = entity.getLocation();
        location.getWorld().playSound(location, Sound.ENTITY_ITEM_FRAME_BREAK, SoundCategory.NEUTRAL, 1f, 1f);
        location.getWorld().dropItem(location,itemFrame.getItem());
        itemFrame.remove();
    }

}
