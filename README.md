suchmarines
===========

Internal EPAM competition bot based on machine learning

# Game
Turn based Space strategy.  
Your goal is to capture all planets on the map. This could be done with drones.   
Drones is your primary weapon. They could be used to capture neutral planets and attack enemies.  
Each planet contains drones factories (planet regeneration level).  
![Screenshot](https://raw.githubusercontent.com/WonderBeat/suchmarines/master/game-screenshot.png)

# Idea
Based on [I am a legend: Hacking Hearthstone with machine learning](https://www.defcon.org/html/defcon-22/dc-22-speakers.html)  
Could we make a bot based on machine learning for a game, where is pretty difficult to analyze all
possible combinations?

# Timeline

* 08 aug - Dirty MVP created. Machine learning could play! Yey!
* 19 aug - bot passed semifinals. 
Heh. I don't know why. Was it a small DB or algo issue, but bot decided to defend during all semifinal games. And passed them =)  
Here is a screenshot of a final game (2nd place):  
![much screenshot](https://raw.githubusercontent.com/WonderBeat/suchmarines/master/semifinal-screenshot.png)
* 23 aug - final. Bot still wanted to play a defend game. Heh. But it was so boring. We've implemented slightly different
strategy. So, finally, our bot made first aggressive moves with hardcoded algorithm. And then switched to machine 
learning. We lost that game ;).

# Summary
There was a guess. Can we use machine learning without any experience with it?  
The answer is: "Yes, we can!". And that's great! 

[![Build Status](https://travis-ci.org/WonderBeat/suchmarines.svg?branch=master)](https://travis-ci.org/WonderBeat/suchmarines)

# Authors
Denis Golovachev  
Nikita Derevyanko  
Aleksander Egorov  
