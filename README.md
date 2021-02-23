The Resistance: Avalon is a social deduction game for five to ten players published by [Indie Boards & Cards](http://indieboardsandcards.com/index.php/our-games/the-resistance-avalon/). The game starts with a character card being dealt to each player. Each character is either Good or Evil. The number of Good and Evil characters depends on the number of players.

The basic game has three character classes. 

|Class|Alignment|
|-|-|
|Servant|Good|
|Merlin|Good|
|Minion|Evil|

There is only one Merlin character in a game. There are multiple of the other classes. Games typically last about 30 minutes so playing several in a row is common. The Servant class is the least interesting to play. The aim of this project is to bias the character dealing such that the probability of being dealt the same class two games in a row is reduced.

The table below shows character class counts and probabilities. _Ptwice_ is the probability of being the same class for two consecutive games. _Pstwice_ is the probability of being the Servant class for two consecutive games.

|Players|Merlin|Servant|Minion|Pmerlin|Pservant|Pminion|Ptwice|Pstwice|
|-|-|-|-|-|-|-|-|-|
|5|1|2|2|0.20|0.40|0.40|0.36|0.16|
|6|1|3|2|0.17|0.50|0.33|0.39|0.25|
|7|1|3|3|0.14|0.43|0.43|0.39|0.18|
|8|1|4|3|0.13|0.50|0.38|0.41|0.25|
|9|1|5|3|0.11|0.56|0.33|0.43|0.31|
|10|1|5|4|0.10|0.50|0.40|0.42|0.25|

### How To Build & Run

Building and running the application locally requires the following to be installed and configured.

 1. Java SDK
 1. Docker

Clone the repository locally
> git clone https://github.com/DangerousDarlow/round-table.git

Start dependencies in docker containers
> docker-compose up

Build and run using the gradle tool
> gradlew bootRun

### API

The application has an HTTP API and is hosted on the Heroku platform. The API can be browsed using swagger at this address

https://round-table-game.herokuapp.com/swagger-ui.html#/
