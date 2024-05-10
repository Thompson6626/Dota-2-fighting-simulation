# Dota 2 Hero Battle Simulator

This project simulates battles between two Dota 2 heroes, taking into account their base stats, equipped items (including neutral items), and hero levels.

## Features:

- Simulate battles between two user-chosen heroes.
- Account for hero base stats, equipped items (6 slots + neutral item), and hero levels.
- Process user input to specify the number of battle simulations.
- Fetch hero and item data from the OpenDota API.
- Include basic functionality for passive item effects.
- User interface with menus for hero selection, item selection, level selection, and battle simulation initiation.
- Combat log displays battle progress during simulations. Users can close and reopen the combat log as needed.
- Unit tests (JUnit) to verify the correctness of items' and heroes' values according to the patch of this date (7.35c).

*Note: Currently, the program does not implement passive bonuses from abilities, certain item passives, active skills from items, or hero abilities.*

## Data Source
The program fetches hero and item data from the OpenDota API.
