package io.github.haykam821.lastcard.turn;

import io.github.haykam821.lastcard.card.color.CardColor;
import io.github.haykam821.lastcard.card.display.CardDisplay;
import io.github.haykam821.lastcard.game.phase.LastCardActivePhase;
import io.github.haykam821.lastcard.game.player.AbstractPlayerEntry;
import io.github.haykam821.lastcard.turn.action.TurnAction;
import net.minecraft.SharedConstants;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TurnManager {
	private static final double PARTICLE_RADIUS = 1.5d;
	public static final float PARTICLE_SIZE = 0.8f;

	public static final int BLACK_PARTICLE_COLOR = 0x000000;
	public static final ParticleEffect NO_COLOR_PARTICLE = CardColor.createParticleEffect(0xDDDDDD);

	private static final double PARTICLE_SPEED = 1 / 15d;
	private static final int PARTICLE_UPDATE_RATE = 1;

	private static final int TURN_ACTION_TICKS = SharedConstants.TICKS_PER_SECOND;

	private final LastCardActivePhase phase;

	private final CardDisplay privatePileDisplay;
	private final CardDisplay publicPileDisplay;

	private AbstractPlayerEntry turn;
	private TurnAction action;
	private TurnDirection direction = TurnDirection.CLOCKWISE;
	private int turnTicks = 0;

	private final Vec3d particleOrigin;
	private int ticks = 0;

	public TurnManager(LastCardActivePhase phase, Vec3d particleOrigin, CardDisplay privatePileDisplay, CardDisplay publicPileDisplay) {
		this.phase = phase;
		this.particleOrigin = particleOrigin;

		this.privatePileDisplay = privatePileDisplay;
		this.publicPileDisplay = publicPileDisplay;
	}

	public AbstractPlayerEntry getTurn() {
		return this.turn;
	}

	public boolean hasTurn(AbstractPlayerEntry entry) {
		return this.turn == entry;
	}

	public void setTurn(AbstractPlayerEntry turn) {
		this.turn = turn;
	}

	private int getNextTurnIndex() {
		return this.phase.getPlayers().indexOf(this.turn) + this.direction.multiply(1);
	}

	public void cycleTurn() {
		if (this.phase.getPlayers().isEmpty()) return;

		AbstractPlayerEntry oldTurn = this.turn;

		this.turn = this.phase.getPlayerEntry(this.getNextTurnIndex());
		this.turnTicks = 0;

		if (this.action == null) {
			this.action = this.turn.getTurnAction();
		}

		if (oldTurn != this.turn) {
			this.sendNextTurnEffects();

			this.privatePileDisplay.moveViewer(oldTurn.getPlayer(), this.publicPileDisplay);
			this.publicPileDisplay.moveViewer(this.turn.getPlayer(), this.privatePileDisplay);
		}

		oldTurn.markDirtyDisplays();
		this.turn.markDirtyDisplays();

		for (AbstractPlayerEntry player : this.phase.getPlayers()) {
			player.updateDirtyDisplays();
		}

		this.phase.updatePileDisplay();
	}

	public TurnAction getTurnAction() {
		return this.action;
	}

	/**
	 * Sets a turn action that should supersede the next player's {@linkplain AbstractPlayerEntry#getTurnAction() default turn action}.
	 * @param action the turn action that should be set for the next turn
	 */
	public void setNextTurnAction(TurnAction action) {
		this.action = action;
	}

	public TurnDirection reverseDirection() {
		return this.direction = this.direction.getOpposite();
	}

	public void sendNextTurnEffects() {
		if (this.turn != null && (this.action == null || this.action.hasNextTurnEffects())) {
			this.phase.sendMessage(this.turn.getNextTurnMessage());
			TurnSounds.playTurnSounds(this.turn.getPlayer());
		}
	}

	private void tickAction() {
		this.turnTicks += 1;

		if (this.turnTicks == TURN_ACTION_TICKS && this.turn != null && this.action != null) {
			TurnAction action = this.action;
			this.action = null;

			action.run(turn);
		}
	}

	private void tickParticles() {
		this.ticks += this.direction.multiply(-1);

		if (this.ticks % PARTICLE_UPDATE_RATE == 0) {
			double x = this.particleOrigin.getX();
			double y = this.particleOrigin.getY();
			double z = this.particleOrigin.getZ();

			double deltaX = Math.sin(this.ticks * PARTICLE_SPEED) * PARTICLE_RADIUS;
			double deltaZ = Math.cos(this.ticks * PARTICLE_SPEED) * PARTICLE_RADIUS;

			CardColor color = this.phase.getDeck().getPreviousColor();
			ParticleEffect particle = color == null ? NO_COLOR_PARTICLE : color.getParticle();

			ServerWorld world = this.phase.getWorld();

			world.spawnParticles(particle, x + deltaX, y, z + deltaZ, 1, 0, 0, 0, 0);
			world.spawnParticles(particle, x - deltaX, y, z - deltaZ, 1, 0, 0, 0, 0);
		}
	}

	public void tick() {
		if (!this.phase.isGameEnding()) {
			this.tickAction();
		}

		this.tickParticles();
	}
}
