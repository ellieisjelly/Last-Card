package io.github.haykam821.lastcard;

import io.github.haykam821.lastcard.game.LastCardConfig;
import io.github.haykam821.lastcard.game.phase.LastCardWaitingPhase;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.api.game.GameType;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;

public class Main implements ModInitializer {
	public static final String MOD_ID = "lastcard";
	public static final ModContainer MOD_CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).get();

	public static final GameRuleType WITHER_ROSE_WITHER_EFFECT = GameRuleType.create();

	private static final Identifier LAST_CARD_ID = Main.identifier("last_card");
	public static final GameType<LastCardConfig> LAST_CARD_TYPE = GameType.register(LAST_CARD_ID, LastCardConfig.CODEC, LastCardWaitingPhase::open);

	@Override
	public void onInitialize() {
		return;
	}

	public static Identifier identifier(String path) {
		return Identifier.of(MOD_ID, path);
	}
}