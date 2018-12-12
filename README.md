# Discrete-Final-Project
In order to run this project, run Graph.java after compiling all classes. This will call the main method, which will run Greedy Clique Creation V1, Greedy Clique Creation V2, Greedy Clique Selection, and Clique Coloring, the solutions that are feasibly runnable.

## Team Creation Priorities and Formula


## Used Algorithms

### Greedy Clique Creation V1
The first greedy clique creation starts each new clique by finding the edge with the highest weight in the adjacency matrix. From there, it searches all other nodes to see which is most strongly connected to those already in the clique. This pattern continues until the maximum number of full cliques is formed. If there were overflow persons, they were added to the teams they fit best with by total edge weightings between the current members and the prospective overflow person.

### Greedy Clique Creation V2
For the second greedy clique creation, we innovated on the first greedy clique creation algorithm by realizing if we got unlucky there could be no final team that could be created with the leftover members. What this therefore does is take in additional value indicating the number of top edges that should be skipped when starting with the first two members of a team. If at the conclusion of one round no solution has been found, it then recursively increases the number of top edges skipped by one until a set depth  (the default of which is 10).

### Brute Force Clique Assignments V1 (done recursively)
The brute force clique assignments first took all valid cliques that could be created from the given persons who needed to be placed in teams and then recursively started picking valid combinations of the teams. The goal was to generate all teams, compare them to see which have the strongest edges, and pick groups in descending orders. However, it was determined far too long to run. Looking at this combinatorically and assuming a worst-case scenario of everyone liking each other and there being 0 silver bullets (which wouldn't happen, but for computer science runtime analysis we have to do worst-case scenarios), we can calculate exactly how many combinations of teams there are. Assuming there are the 95 POE students wee were working with and that the goal was to have teams of 4, we could generate (95 Choose 4) or 3183545 different cliques. From these cliques, we have to choose 23 teams, and (3183545 Choose 23) is
14269997543090240956357300053952460562995691697881045897728015592300706590585628050714184284327695008067970348080814055177143300. Put in a readable format, that's ~1.437 x 10^123. As we didn't have a supercomputer on hand, it was determined to take too long to run.

### Brute Force Clique Assignments V2 (done recursively)
Before fully writing out how many computations were going to be necessary for the brute force clique assignments, we decided to try to make the code for brute force clique formation a lot faster. In order to do this, as cliques were selected, possible cliques that contained members of the selected clique were eliminated and not passed forward to future iterations. This allowed for significantly fewer calculations, as ~1/23 of the cliques had each member, so the algorithm had to process significantly fewer inward calculations. It was still way too large, which is why the Greedy Clique Selection algorithm was created.

### Greedy Clique Selection
The greedy clique selection algorithm determined what the mean clique skill score should be and generates a full set of cliques with approximately that score. Any additional members are then added to the team of four where they fit best. This algorithm was all about creating teams with a balanced skill set, and although it excelled in that things such as partner and project preference were not taken into account (although silver bullets were accounted for). Therefore, a second version of the Greedy Clique selection was needed.  

### Colored Cliques
One of our original ideas for creating teams was to generate them based on each team member's major, and try to have a good representation of majors within each team. What we are attempting to create here is a complete set of 'rainbow cliques', teamings of 4-5 nodes with as many different majors represented as possible. This algorithm first traverses the list of profiles created to represent the students and assigns each student a major according to their reported skills. Currently, we are operating with three majors: Electrical and Computer Engineering, Software Engineering, and Mechanical Engineering. After assigning each student to one of these majors, we begin team creation.

## Outcomes

## Conclusion

# Annotated Bibliography

Anderson, R. (n.d.). Windows. Retrieved December 11, 2018, from http://everythingcomputerscience.com/algorithms/Greedy_Algorithm.html

We were looking for a good overview of what a greedy algorithm was, and found this website. It has an overall good overview of it as well as some source code that we could reference outside of our personal experiences. As all team members were familiar at a high level with how to write different greedy algorithms, the heart of the discussion we typically faced was what should we be focusing on while creating cliques.

Angles d’Auriac, J., El Maftouhi, H., Legay, S., Cohen, N., Harutyunyan, A., & Manoussakis, Y. (2016, July 26). Connected Tropical Subgraphs in Vertex-Colored Graphs(Rep.). Retrieved December 11, 2018, from Discrete Mathematics and Theoretical Computer Science website: https://www.dmtcs.org/dmtcs-ojs/index.php/dmtcs/article/download/2765/4764.pdf

We used this source in order to read more about tropical graphs and subgraphs as well as provide insight into how we could best approach this program algorithmically. Sarah had suggested that we review this document, and it was very helpful.

*Graph Coloring Exploration* (Tech.). (n.d.). Retrieved December 11, 2018, from Sarah Spence Adams website: https://drive.google.com/open?id=16IaoO2pte9BbkEVT_uXAsTyPGf3PUMgq

We referenced Annie Kroo’s implementation of POE teaming in an attempt to see another way that the same teaming that we were attempting was accomplished. Although it was useful to look at, we ended up mostly defining our own ways to solve things based on what we researched and were familiar with.

Millner, A. (2018, December 11). [POE Teaming Survey Data]. Unpublished raw data.

We needed some physical metric of how our algorithms were doing, and we decided the best way to do this was to use some data from an Olin teaming class. As Kyle was a NINJA for POE this semester, we were granted access to the anonymized teaming data for this year’s POE teams. We used this data for testing purposes throughout our project.

StackOverflow. (n.d.). Retrieved December 11, 2018, from https://stackoverflow.com/

As is the norm for programmers, as we were implementing our solutions we encountered a variety of issues. The easiest way to deal with these issues was typically to visit StackOverflow. It also occasionally provided hints at ways to streamline our code.
