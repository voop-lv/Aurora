package com.zenya.aurora.api;

public abstract class AuroraAPI {

  private static AuroraAPI api;

  /**
   * A method to access Aurora's API.
   *
   * @return An instance of AuroraAPI.
   */
  public static AuroraAPI getAPI() {
    return api;
  }

  /**
   * An internal method used by Aurora to initialise the API. External developers should never need to use this method.
   *
   * @param api An implementation of AuroraAPI.
   */
  public static void setAPI(AuroraAPI api) {
    if (api != null) {
      AuroraAPI.api = api;
    }
  }

  /**
   * A method to access ParticleFactory.
   *
   * @return An instance of ParticleFactory.
   */
  public abstract ParticleFactory getParticleFactory();
}
