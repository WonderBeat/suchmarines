package org.wow.java.logics;

import com.epam.starwors.bot.Logic;
import com.epam.starwors.galaxy.Move;
import com.epam.starwors.galaxy.Planet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by WooDmaN on 07.08.2014.
 */
public class UniformAttack implements Logic {
    String userName;

    public UniformAttack(String userName) {
        this.userName = userName;
    }

    @Override
    public Collection<Move> step(Collection<Planet> planets) {
        Collection<Move> moves = new ArrayList<Move>();
        for (Planet planet: planets){
            if(planet.getOwner().equals(userName)){
                List<Planet> neighbours = planet.getNeighbours();
                for (Planet neighbour : neighbours){
                    if(neighbour.getOwner().equals("")){
                        moves.add(new Move(planet,neighbour,planet.getUnits()/4));
                    }
                    if(planet.getUnits()== planet.getType().getLimit() && neighbour.getOwner().equals("WooDmaN") && neighbour.getUnits() < neighbour.getType().getLimit()/2 ){
                        moves.add(new Move(planet,neighbour,planet.getUnits()/4));
                    }
                    if(neighbour.getOwner().indexOf("bot")>-1){
                        moves.add(new Move(planet,neighbour,planet.getUnits()/2));
                    }
                }
            }
        }
        return moves;
    }
}
