### Sample project to support Apache Ignite crash course presentation

The presentation is
located [at Google Slides here](https://docs.google.com/presentation/d/1VNtYk3kRnYctGb5D3XrtCxRvg2SnGuZolcCPshUrouw/edit?usp=sharing)

The project repo has IntelliJ run/debug configurations. For the e2e demo, run
all three servers (the #3 deploy services), and then the client.

Alternatively, run following commands:

- `./gradlew run --args=1 # server #1`
- `./gradlew run --args=2 # server #2`
- `./gradlew run --args=3 # server #3, this one deploy listening services`
- `./gradlew run # this is the client inserting data`

And/or play with the code which has commented out sections to play with.