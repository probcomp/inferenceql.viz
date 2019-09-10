function new_improved_simulation() {
  var simulation = inferdb.search_by_example.api.simulate();
  console.log("Simulation from Multi-mix");
  console.log(simulation);
  var Apogee_km  = simulation["Apogee_km"]
  var Perigee_km = simulation["Perigee_km"]
  console.log("Result from applying Kepler's law");
  console.log(keplersLaw(Apogee_km, Perigee_km))
}
