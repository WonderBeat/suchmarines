package org.wow.metrics;

import com.epam.starwors.galaxy.Planet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Use this class for get any metrics for World
 */
public class World {
    Collection<Planet> world;

    public World(Collection<Planet> world) {
        this.world = world;
    }

    public int getNumberOfPlanets(){
        return world.size();
    }

    public List<Planet> getUsersPlanets(String user){
        List<Planet> userPlanets = new ArrayList<Planet>();
        for(Planet planet:world){
            if(planet.getOwner().equals(user)){
                userPlanets.add(planet);
            }
        }
        return userPlanets;
    }
}
