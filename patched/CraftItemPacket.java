package pkgbadges.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import pkgbadges.init.PkgbadgesModItems;

public record CraftItemPacket(ResourceLocation itemId) implements CustomPacketPayload {
   public static final Type<CraftItemPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("pkgbadges", "craft_item"));
   private static final long COOLDOWN_PERIOD_MS = 4000L;
   public static final StreamCodec<FriendlyByteBuf, CraftItemPacket> STREAM_CODEC = StreamCodec.of(
      (buf, packet) -> buf.writeResourceLocation(packet.itemId), buf -> new CraftItemPacket(buf.readResourceLocation())
   );

   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static void handle(CraftItemPacket packet, IPayloadContext context) {
      context.enqueueWork(() -> {
         if (context.player() != null) {
            Player player = context.player();
            Item item = BuiltInRegistries.ITEM.get(packet.itemId());
            if (item == null || item == Items.AIR) {
               return;
            }
            long lastCraftTime = player.getPersistentData().getLong("LastCraftTime");
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCraftTime < COOLDOWN_PERIOD_MS) {
               player.displayClientMessage(Component.literal("Lütfen bekleyin!"), false);
               return;
            }

            List<ItemStack> materials = getRequiredMaterials(item);
            if (materials.isEmpty()) {
               player.displayClientMessage(Component.literal("Malzemeler eksik!"), false);
               return;
            }

            if (hasRequiredMaterials(player, materials)) {
               removeMaterials(player, materials);
               player.getPersistentData().putLong("LastCraftTime", currentTime);
               player.getInventory().add(new ItemStack(item));
            } else {
               player.displayClientMessage(Component.literal("Malzemeler eksik!"), false);
            }
         }
      });
   }

   private static boolean hasRequiredMaterials(Player player, List<ItemStack> materials) {
      for (ItemStack material : materials) {
         int requiredAmount = material.getCount();
         int availableAmount = 0;

         for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == material.getItem()) {
               availableAmount += stack.getCount();
            }
         }

         if (availableAmount < requiredAmount) {
            return false;
         }
      }

      return true;
   }

   private static void removeMaterials(Player player, List<ItemStack> materials) {
      for (ItemStack stack : materials) {
         int count = stack.getCount();

         for (int i = 0; i < player.getInventory().getContainerSize() && count > 0; i++) {
            ItemStack slotStack = player.getInventory().getItem(i);
            if (slotStack.is(stack.getItem())) {
               int removeAmount = Math.min(slotStack.getCount(), count);
               slotStack.shrink(removeAmount);
               count -= removeAmount;
            }
         }
      }
   }

   private static List<ItemStack> getRequiredMaterials(Item item) {
      List<ItemStack> materials = new ArrayList<>();
      if (item == PkgbadgesModItems.TRAINER_BACKPACK.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.TRAINER_BACKPACK_ORANGE.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.ORANGE_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.TRAINER_BACKPACK_PINK.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PINK_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.TRAINER_BACKPACK_GREEN.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GREEN_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.GENGAR_BACKPACK.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.ORANGE_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PURPLE_FABRIC.get(), 10));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.CHARMANDER_BACKPACK.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.ORANGE_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_BACKPACK.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.ORANGE_FABRIC.get(), 3));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GREEN_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.SERENA_BACKPACK.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PINK_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.BONNIE_WAIST_BAG.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.BROCKS_BACKPACK.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 12));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.MISTYS_BACKPACK.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 4));
         materials.add(new ItemStack(Items.CHEST, 1));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_INDIGO_LEAGUE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 8));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GREEN_FABRIC.get(), 1));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_JOHTO_LEAGUE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 8));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.LIME_GREEN_FABRIC.get(), 1));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_SINNOH_LEAGUE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 5));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 12));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_HOENN_LEAGUE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GREEN_FABRIC.get(), 5));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 12));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_UNOVA_LEAGUE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 8));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_KALOS_LEAGUE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 16));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_ALOLA_LEAGUE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 3));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PINK_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 8));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_GALAR_LEAGUE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 8));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 3));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.LIME_GREEN_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.PIKACHU_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.BULBASAUR_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.LIME_GREEN_FABRIC.get(), 8));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.EEVEE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BROWN_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 4));
      } else if (item == PkgbadgesModItems.SQUIRTLE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.CYAN_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.SNORLAX_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.CYAN_FABRIC.get(), 3));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.DARK_BLUE_FABRIC.get(), 13));
      } else if (item == PkgbadgesModItems.LEON_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 16));
      } else if (item == PkgbadgesModItems.FRIEDE_GLASSES.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.GLASS_PANE, 2));
         materials.add(new ItemStack(Items.GOLD_NUGGET, 4));
      } else if (item == PkgbadgesModItems.CAPTAIN_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 8));
      } else if (item == PkgbadgesModItems.SERENA_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.TRILBY_HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PINK_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.STRING, 2));
      } else if (item == PkgbadgesModItems.SERENA_GALAR_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.TRILBY_HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.STRING, 2));
      } else if (item == PkgbadgesModItems.ROCKET_TEAM_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 3));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 8));
      } else if (item == PkgbadgesModItems.HARD_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack(Items.IRON_INGOT, 8));
         materials.add(new ItemStack(Items.YELLOW_DYE, 4));
      } else if (item == PkgbadgesModItems.ALIANS_SCRAF_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 3));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.CYAN_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.KORRINA_HELMET.get()) {
         materials.add(new ItemStack(Items.IRON_INGOT, 3));
         materials.add(new ItemStack(Items.STRING, 2));
      } else if (item == PkgbadgesModItems.POLICE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.DARK_BLUE_FABRIC.get(), 8));
      } else if (item == PkgbadgesModItems.NURSE_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 10));
      } else if (item == PkgbadgesModItems.CHARMANDER_RACER_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.IRON_BLOCK, 1));
         materials.add(new ItemStack(Items.GLASS_PANE, 1));
      } else if (item == PkgbadgesModItems.SQUIRTLE_RACER_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.IRON_BLOCK, 1));
         materials.add(new ItemStack(Items.GLASS_PANE, 1));
      } else if (item == PkgbadgesModItems.JIGGLYPUFF_RACER_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PINK_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.IRON_BLOCK, 1));
         materials.add(new ItemStack(Items.GLASS_PANE, 1));
      } else if (item == PkgbadgesModItems.PIKACHU_RACER_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.IRON_BLOCK, 1));
         materials.add(new ItemStack(Items.GLASS_PANE, 1));
      } else if (item == PkgbadgesModItems.MEOWTH_RACER_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.IRON_BLOCK, 1));
         materials.add(new ItemStack(Items.GLASS_PANE, 1));
      } else if (item == PkgbadgesModItems.EEVEE_HAT.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GREEN_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.IRON_BLOCK, 1));
         materials.add(new ItemStack(Items.GLASS_PANE, 1));
      } else if (item == PkgbadgesModItems.GASTLY_RACER_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PURPLE_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.IRON_BLOCK, 1));
         materials.add(new ItemStack(Items.GLASS_PANE, 1));
      } else if (item == PkgbadgesModItems.BULBASAUR_RACER_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.LIME_GREEN_FABRIC.get(), 8));
         materials.add(new ItemStack(Items.IRON_BLOCK, 1));
         materials.add(new ItemStack(Items.GLASS_PANE, 1));
      } else if (item == PkgbadgesModItems.EEVEE_NEW_YEAR_SHINY_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GREEN_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 4));
      } else if (item == PkgbadgesModItems.EEVEE_NEW_YEAR_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BROWN_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 4));
      } else if (item == PkgbadgesModItems.PIKACHU_NEW_YEAR_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 3));
      } else if (item == PkgbadgesModItems.PIKACHU_NEW_YEAR_SHINY_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GREEN_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 3));
      } else if (item == PkgbadgesModItems.NINETALES_SHINY_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 3));
      } else if (item == PkgbadgesModItems.NINETALES_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.ORANGE_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 3));
      } else if (item == PkgbadgesModItems.ALOLA_NINETALES_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.DARK_BLUE_FABRIC.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.ALOLA_NINETALES_SHINY_HELMET.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.HAT_TEMPLATE.get(), 1));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PURPLE_FABRIC.get(), 16));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.DARK_BLUE_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 5));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 24));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_CLOTH_CHESTPLATE.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 8));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GREEN_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 4));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_CLOTH_LEGGINGS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLUE_FABRIC.get(), 10));
      } else if (item == PkgbadgesModItems.ASH_KETCHUM_CLOTH_BOOTS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 8));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.GARY_OAK_CLOTH_CHESTPLATE.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PURPLE_FABRIC.get(), 12));
         materials.add(new ItemStack(Items.GOLD_BLOCK, 1));
         materials.add(new ItemStack(Items.EMERALD, 1));
      } else if (item == PkgbadgesModItems.GARY_OAK_CLOTH_LEGGINGS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 10));
      } else if (item == PkgbadgesModItems.GARY_OAK_CLOTH_BOOTS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BROWN_FABRIC.get(), 8));
      } else if (item == PkgbadgesModItems.FRIEDE_CLOTH_CHESTPLATE.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BROWN_FABRIC.get(), 12));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.ORANGE_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.FRIEDE_CLOTH_LEGGINGS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.WHITE_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.ORANGE_FABRIC.get(), 4));
      } else if (item == PkgbadgesModItems.FRIEDE_CLOTH_BOOTS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BROWN_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.STEVEN_STONE_CLOTH_CHESTPLATE.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 10));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.RED_FABRIC.get(), 6));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PURPLE_FABRIC.get(), 4));
      } else if (item == PkgbadgesModItems.STEVEN_STONE_CLOTH_LEGGINGS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 10));
      } else if (item == PkgbadgesModItems.STEVEN_STONE_CLOTH_BOOTS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.PURPLE_FABRIC.get(), 8));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 2));
      } else if (item == PkgbadgesModItems.ORLA_CLOTH_CHESTPLATE.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 10));
      } else if (item == PkgbadgesModItems.ORLA_CLOTH_LEGGINGS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.YELLOW_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.ORANGE_FABRIC.get(), 6));
      } else if (item == PkgbadgesModItems.ORLA_CLOTH_BOOTS.get()) {
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.DARK_BLUE_FABRIC.get(), 4));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.GRAY_FABRIC.get(), 2));
         materials.add(new ItemStack((ItemLike)PkgbadgesModItems.BLACK_FABRIC.get(), 2));
      }

      return materials;
   }
}
