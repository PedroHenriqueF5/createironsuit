package dev.createironsuit;

/** Todos os numeros de balanceamento num lugar so. */
public final class SuitConstants {
    private SuitConstants() {}

    /** Capacidade do reator (FE). */
    public static final int MAX_ENERGY = 1_000_000;
    /** Gasto passivo com o conjunto completo ligado (FE por segundo). */
    public static final int IDLE_DRAIN_PER_SECOND = 10;
    /** Gasto em voo (FE por tick). */
    public static final int FLIGHT_DRAIN_PER_TICK = 12;
    /** Custo de cada disparo do repulsor (FE). */
    public static final int REPULSOR_COST = 5_000;
    public static final float REPULSOR_DAMAGE = 12.0F;
    public static final double REPULSOR_RANGE = 48.0D;
    public static final int REPULSOR_COOLDOWN_TICKS = 12;
    /** FE gerados por tick para cada 1 RPM no carregador. */
    public static final float CHARGE_FE_PER_RPM_PER_TICK = 2.0F;
    /** Impacto de stress do carregador (por RPM). */
    public static final float CHARGER_STRESS_IMPACT = 8.0F;
    public static final float FLY_SPEED = 0.10F;
    public static final float DEFAULT_FLY_SPEED = 0.05F;
}
