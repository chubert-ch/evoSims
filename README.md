## What is this?

It's a simple simulator of blobs that move, evolve and hunt for food. You get remarkable speciation from simple rules:

![image](https://github.com/chubert-ch/evoSims/assets/14638014/fb54a594-9a30-4de1-bad6-436c5e6cbcc7)
![image](https://github.com/chubert-ch/evoSims/assets/14638014/2c430e18-acb2-405f-8593-a78ac9b02c36)

### Details

Each blob has a size, speed, sensory radius and innate food production (their colors and sizes are derived from these stats). Blobs will hunt smaller blobs that are slower than them and avoid blobs that are even bigger than they are. There's also randomly spawning food in the map.

Speed and size are expensive in terms of energy consumption (`energyUsage = speed * speed * size * size * size / 1000 + senseRadius / 1000`), so there is a strong incentive to be small and slow, but that makes blobs easy prey.

The "algae" stat allows blobs to produce their own energy, without having to hunt or gather, but makes blobs slow (`actualSpeed = 10 * (speed / (1 + algae))`).

## How do I run it?

It includes a pom.xml, so just open it with your preferred IDE and run the main method in `BlobWindow`. (if you're a vim user you don't need my help getting it working!)

You can adjust the slider to change simulation speed.
