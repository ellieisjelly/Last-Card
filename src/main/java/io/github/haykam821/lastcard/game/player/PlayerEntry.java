package io.github.haykam821.lastcard.game.player;

import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.card.display.player.PrivateCardDisplay;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import xyz.nucleoid.map_templates.TemplateRegion;

public class PlayerEntry extends AbstractPlayerEntry {
	private final ServerPlayerEntity player;
	private final CardDisplay privateDisplay;

	public PlayerEntry(LastCardActivePhase phase, ServerPlayerEntity player, TemplateRegion chair, TemplateRegion privateDisplay, TemplateRegion publicDisplay) {
		super(phase, chair, publicDisplay);

		this.player = player;
		this.privateDisplay = new PrivateCardDisplay(this, privateDisplay);
	}

	@Override
	public void spawn() {
		this.getChair().teleport(this.player);
	}

	@Override
	public Text getName() {
		return this.player.getDisplayName();
	}

	@Override
	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	@Override
	protected CardDisplay getDisplayViewableBy(ServerPlayerEntity viewer) {
		return this.isPlayer(viewer) ? this.privateDisplay : super.getDisplayViewableBy(viewer);
	}

	@Override
	public void destroyDisplays() {
		super.destroyDisplays();
		this.privateDisplay.destroy();
	}

	@Override
	public ItemStack createHeadStack() {
		ItemStack stack = new ItemStack(Items.PLAYER_HEAD);

		ProfileComponent component = new ProfileComponent(this.player.getGameProfile());
		stack.set(DataComponentTypes.PROFILE, component);

		return stack;
	}

	@Override
	public void updateDisplays() {
		super.updateDisplays();
		this.privateDisplay.update();
	}

	@Override
	public String toString() {
		return "PlayerEntry{player=" + this.player + ", chair=" + this.getChair() + "}";
	}
}
