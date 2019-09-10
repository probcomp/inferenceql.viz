/**
 * Returns the period of a satellite, given its Apogee_km and Perigee_km.
 * @param {number} Apogee_km The point at which the satellite is furthest from the earth.
 * @param {number} Perigee_km The point at which the satellite is closest to the earth.
 */
function keplersLaw(Apogee_km, Perigee_km) {
    const GM = 398600.4418;
    const earth_radius = 6378;
    const a = 0.5 * (Math.abs(Apogee_km) + Math.abs(Perigee_km)) + earth_radius;
    return 2 * Math.PI * Math.sqrt(Math.pow(a, 3) / GM) / 60;
};

/**
 * Generates Gaussian noise via a Box-Muller transform.
 * @param {number} mu Mean of normal distribution.
 * @param {number} sigma Varance of normal distribution.
 */
function gaussianNoise(mu, sigma) {
    const u1 = Math.random();
    const u2 = Math.random();
    return mu + sigma * Math.sqrt(-2 * Math.log(u1))
        * Math.cos(2 * Math.PI * u2);
};
