# SimpleEvolution
A cellular automaton based on a simple concept of evolution through genetic drift.

##Rules
- Cells can either be alive or dead.
- Living cell:
    - Has a colour that represents its "genome".
    - Tries to move in a random direction.
    - If adjacent cell is dead -> move there.
    - If adjacent cell contains another living cell:
        - Compare genomes.
        - If genomes are compatible -> turn an adjacent dead cell into a living cell (reproduction).
        - The newly spawned cell will have the average of its parents
         genomes, with some random deviation.
    - Must wait a period of time between reproductions.
    - Loses health after every reproduction. Dies at health = 0.