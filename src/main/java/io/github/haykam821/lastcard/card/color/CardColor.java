package io.github.haykam821.lastcard.card.color;

import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import io.github.haykam821.lastcard.card.display.CardTemplates;
import io.github.haykam821.lastcard.turn.TurnManager;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum CardColor implements ColorRepresentation {
	RED("red", Items.RED_WOOL, Formatting.RED, BossBar.Color.RED, CardTemplates.RED_FRONT, CanvasColor.RED_NORMAL),
	GREEN("green", Items.GREEN_WOOL, Formatting.GREEN, BossBar.Color.GREEN, CardTemplates.GREEN_FRONT, CanvasColor.GREEN_NORMAL),
	YELLOW("yellow", Items.YELLOW_WOOL, Formatting.YELLOW, BossBar.Color.YELLOW, CardTemplates.YELLOW_FRONT, CanvasColor.YELLOW_NORMAL),
	BLUE("blue", Items.BLUE_WOOL, Formatting.DARK_AQUA, BossBar.Color.BLUE, CardTemplates.BLUE_FRONT, CanvasColor.BLUE_NORMAL);

	public static final CardColor[] VALUES = CardColor.values();

	private final Text name;
	private final Item item;

	private final Formatting formatting;
	private final BossBar.Color bossBarColor;

	private final DrawableCanvas template;
	private final CanvasColor canvasTextColor;

	private final ParticleEffect particle;

	private CardColor(String key, Item item, Formatting formatting, BossBar.Color bossBarColor, DrawableCanvas template, CanvasColor canvasTextColor, ParticleEffect particle) {
		this.name = Text.translatable("text.lastcard.card.color." + key);
		this.item = item;

		this.formatting = formatting;
		this.bossBarColor = bossBarColor;

		this.template = template;
		this.canvasTextColor = canvasTextColor;

		this.particle = particle;
	}

	private CardColor(String key, Item item, Formatting formatting, BossBar.Color bossBarColor, DrawableCanvas template, CanvasColor canvasTextColor) {
		this(key, item, formatting, bossBarColor, template, canvasTextColor, createParticleEffect(canvasTextColor));
	}

	@Override
	public Text getName() {
		return this.name;
	}

	@Override
	public Item getItem() {
		return this.item;
	}

	@Override
	public Formatting getFormatting() {
		return this.formatting;
	}

	@Override
	public BossBar.Color getBossBarColor() {
		return this.bossBarColor;
	}

	@Override
	public DrawableCanvas getTemplate() {
		return this.template;
	}

	@Override
	public CanvasColor getCanvasTextColor() {
		return this.canvasTextColor;
	}

	public ParticleEffect getParticle() {
		return this.particle;
	}

	private static ParticleEffect createParticleEffect(CanvasColor canvasColor) {
		return createParticleEffect(canvasColor.getRgbColor());
	}

	public static ParticleEffect createParticleEffect(int rgb) {
		return new DustColorTransitionParticleEffect(rgb, TurnManager.BLACK_PARTICLE_COLOR, TurnManager.PARTICLE_SIZE);
	}
}
