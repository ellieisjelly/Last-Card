package io.github.haykam821.lastcard.game.phase;

import io.github.haykam821.lastcard.game.LastCardConfig;
import io.github.haykam821.lastcard.game.map.LastCardMap;
import io.github.haykam821.lastcard.game.map.LastCardMapBuilder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.api.game.GameOpenContext;
import xyz.nucleoid.plasmid.api.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.api.game.GameResult;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.api.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;
import xyz.nucleoid.plasmid.api.game.player.JoinOffer;
import xyz.nucleoid.plasmid.api.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.EventResult;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class LastCardWaitingPhase implements GamePlayerEvents.Accept, GameActivityEvents.Tick, PlayerDamageEvent, PlayerDeathEvent, GameActivityEvents.RequestStart {
	private final GameSpace gameSpace;
	private final ServerWorld world;

	private final LastCardConfig config;
	private final LastCardMap map;

	public LastCardWaitingPhase(GameSpace gameSpace, ServerWorld world, LastCardConfig config, LastCardMap map) {
		this.gameSpace = gameSpace;
		this.world = world;

		this.config = config;
		this.map = map;
	}

	public static GameOpenProcedure open(GameOpenContext<LastCardConfig> context) {
		LastCardConfig config = context.config();

		MinecraftServer server = context.server();
		Random random = server.getOverworld().getRandom();

		LastCardMapBuilder mapBuilder = new LastCardMapBuilder(config);
		LastCardMap map = mapBuilder.create(server);

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
			.setTimeOfDay(config.getTimeOfDay().get(random))
			.setGenerator(map.createGenerator(server));

		return context.openWithWorld(worldConfig, (activity, world) -> {
			LastCardWaitingPhase phase = new LastCardWaitingPhase(activity.getGameSpace(), world, config, map);
			GameWaitingLobby.addTo(activity, config.getPlayerConfig());

			LastCardActivePhase.setRules(activity);
			activity.deny(GameRuleType.MODIFY_INVENTORY);

			// Listeners
			activity.listen(GamePlayerEvents.ACCEPT, phase);
			activity.listen(GamePlayerEvents.OFFER, JoinOffer::accept);
			activity.listen(GameActivityEvents.TICK, phase);
			activity.listen(PlayerDamageEvent.EVENT, phase);
			activity.listen(PlayerDeathEvent.EVENT, phase);
			activity.listen(GameActivityEvents.REQUEST_START, phase);
		});
	}

	@Override
	public JoinAcceptorResult onAcceptPlayers(JoinAcceptor acceptor) {
		return this.map.getWaitingSpawn().acceptPlayers(acceptor, this.world, GameMode.ADVENTURE);
	}

	@Override
	public void onTick() {
		for (ServerPlayerEntity player : this.gameSpace.getPlayers()) {
			if (!this.map.contains(player)) {
				this.spawn(player);
			}
		}
	}

	@Override
	public EventResult onDamage(ServerPlayerEntity player, DamageSource source, float amount) {
		return EventResult.DENY;
	}

	@Override
	public EventResult onDeath(ServerPlayerEntity player, DamageSource source) {
		this.spawn(player);
		return EventResult.DENY;
	}

	@Override
	public GameResult onRequestStart() {
		LastCardActivePhase.open(this.gameSpace, this.world, this.config, this.map);
		return GameResult.ok();
	}

	private void spawn(ServerPlayerEntity player) {
		this.map.getWaitingSpawn().teleport(player);
	}
}