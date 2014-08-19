package org.wow.logic;

import com.epam.starwors.bot.Logic;
import com.epam.starwors.galaxy.Move;
import com.epam.starwors.galaxy.Planet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class UniformAttack implements Logic {
    String userName;

    Random rand = new Random();

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
                        moves.add(new Move(planet,neighbour,planet.getUnits()/(rand.nextInt(4) + 2)));
                    }
                    if(planet.getUnits()== planet.getType().getLimit() && neighbour.getOwner().equals(userName) && neighbour
                            .getUnits() < neighbour.getType().getLimit()/2 ){
                        moves.add(new Move(planet,neighbour,planet.getUnits()/(rand.nextInt(5) + 1)));
                    }
                    if(!neighbour.getOwner().equals(userName)){
                        moves.add(new Move(planet,neighbour,planet.getUnits()/(rand.nextInt(3) + 1)));
                    }
                }
            }
        }
        return moves;
    }
}
