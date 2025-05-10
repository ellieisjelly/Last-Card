package io.github.haykam821.lastcard.card;

import java.util.Objects;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.utils.ViewUtils;
import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.color.ColorRepresentation;
import io.github.haykam821.lastcard.card.color.ColorSelector;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class Card {
	private final ColorSelector selector;

	public Card(ColorSelector selector) {
		this.selector = Objects.requireNonNull(selector);
	}

	public abstract Text getName();

	public final Text getFullName() {
		return Text.empty()
			.append(this.selector.getName())
			.append(ScreenTexts.SPACE)
			.append(this.getName())
			.formatted(this.selector.getFormatting());
	}

	public final boolean canPlay(AbstractPlayerEntry player) {
		if (!player.hasTurn()) return false;
		if (player.getPhase().getTurnManager().getTurnAction() != null) return false;

		CardDeck deck = player.getPhase().getDeck();

		Card previousCard = deck.getPreviousCard();
		return previousCard == null || this.isMatching(previousCard, deck.getPreviousColor());
	}

	public boolean isMatching(Card card, CardColor color) {
		return this.selector.isMatching(color);
	}

	public void play(AbstractPlayerEntry player) {
		player.getPhase().sendMessageWithException(this.getCardPlayedMessage(player), player, this.getCardPlayedYouMessage());
		player.getPhase().updateBar();
	}

	public ColorSelector getSelector() {
		return this.selector;
	}

	public final DrawableCanvas render(ColorRepresentation overrideColor) {
		DrawableCanvas canvas = overrideColor.getTemplate().copy();
		CanvasColor textColor = overrideColor.getCanvasTextColor();

		this.renderOverlay(canvas, textColor);
		this.renderOverlay(ViewUtils.flipY(ViewUtils.flipX(canvas)), textColor);
		
		return canvas;
	}

	public abstract void renderOverlay(DrawableCanvas canvas, CanvasColor textColor);

	private Text getCardPlayedMessage(AbstractPlayerEntry player) {
		return Text.translatable("text.lastcard.card_played", player.getName(), this.getFullName()).formatted(Formatting.GOLD);
	}

	private Text getCardPlayedYouMessage() {
		return Text.translatable("text.lastcard.card_played.you", this.getFullName()).formatted(Formatting.GOLD);
	}
}
