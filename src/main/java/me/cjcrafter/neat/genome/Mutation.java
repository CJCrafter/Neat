package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.util.SortedList;

import java.util.concurrent.ThreadLocalRandom;

public enum Mutation {

    RANDOM_WEIGHT {
        @Override
        public void mutate(Genome genome) {
            ConnectionGene connection = genome.getConnections().getRandomElement();
            if (connection != null) {
                connection.setWeight((ThreadLocalRandom.current().nextDouble() * 2 - 1)
                        * genome.getNeat().getRandomWeightStrength());
            }
        }
    },
    WEIGHT_SHIFT {
        @Override
        public void mutate(Genome genome) {
            ConnectionGene connection = genome.getConnections().getRandomElement();
            if (connection != null) {
                connection.setWeight(connection.getWeight() + (ThreadLocalRandom.current().nextDouble() * 2 - 1)
                        * genome.getNeat().getShiftWeightStrength());
            }
        }
    }, TOGGLE {
        @Override
        public void mutate(Genome genome) {
            ConnectionGene connection = genome.getConnections().getRandomElement();
            if (connection != null) {
                connection.setEnabled(!connection.isEnabled());
            }
        }
    },
    ADD_LINK {
        @Override
        public void mutate(Genome genome) {

            // Try to mutate a new link 100 times. 100 times is most likely
            // always going to be enough tries to mutate a link.
            for (int i = 0; i < 100; i++) {
                SortedList<NodeGene> nodes = genome.getNodes();

                NodeGene a = nodes.getRandomElement();
                NodeGene b = nodes.getRandomElement();

                // Node connections go from left to right
                ConnectionGene connection;
                if (a.getX() == b.getX()) {
                    continue;
                } else if (a.getX() < b.getX()) {
                    connection = new ConnectionGene(a, b);
                } else {
                    connection = new ConnectionGene(b, a);
                }

                genome.getConnections().contains(connection);
            }
        }
    },
    ADD_NODE {
        @Override
        public void mutate(Genome genome) {

        }
    };

    public final String name;

    Mutation() {
        String[] split = name().toLowerCase().split("_");

        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            builder.append(s.substring(0, 1).toUpperCase());
            builder.append(s.substring(1));
            builder.append(" ");
        }
        name = builder.substring(0, builder.length() - 1);
    }

    // todo abstract
    public abstract void mutate(Genome genome);

}
