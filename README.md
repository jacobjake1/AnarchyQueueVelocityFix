## AnarchyQueue

[![discord](https://img.shields.io/discord/895546064260718622?logo=discord)](https://discord.0b0t.org)
[![reddit](https://img.shields.io/reddit/subreddit-subscribers/0b0t)](https://old.reddit.com/r/0b0t/)
![last commit](https://img.shields.io/github/last-commit/zeroBzeroT/AnarchyQueue)
![code size](https://img.shields.io/github/languages/code-size/zeroBzeroT/AnarchyQueue)
[![downloads](https://img.shields.io/github/downloads/zeroBzeroT/AnarchyQueue/total)](https://github.com/zeroBzeroT/AnarchyQueue/releases)

---

A simple queue system for BungeeCord (and Velocity) in the style of the 2b2t queue. 

### Commands

- **/maxplayers {count}**   gets or sets the capacity of the main server

### Config

| Value                | Description                                                  |
|----------------------|--------------------------------------------------------------|
| target               | server name from bungee config                               |
| queue                | server name from bungee config                               |
| maxPlayers           | max players of target before players are queued              |
| waitOnKick           | seconds to wait after a kick before reconnect                |
| messagePosition      | player message, that shows his position in the queue         |
| messageConnecting    | player message, when connecting to the target server         |
| messageFullOrOffline | player message, when the target is full or offline           |
| serverName           | target server name displayed to the player                   |
| kickPassthrough      | if false, players will be enqueued again                     |
| kickOnRestart        | if true, players will be kicked when the target restarts     |
| kickOnBusy           | if true, players will be kicked if the target is busy        |
| bStats               | if true, plugin metrics are enabled                          |
| senTitle             | should the position be displayed in the center of the screen |
### Versions

 - 1.* is for BungeeCord
 - 2.* is for Velocity

### Warranty

The Software is provided "as is" and without warranties of any kind, express
or implied, including but not limited to the warranties of merchantability,
fitness for a particular purpose, and non-infringement. In no event shall the
Authors or copyright owners be liable for any claims, damages or other
liability, whether in an action in contract, tort or otherwise, arising from, 
out of or in connection with the Software or the use or other dealings in the 
Software.