package me.cjcrafter.neat.genome;

import me.cjcrafter.neat.Neat;
import me.cjcrafter.neat.util.SortedList;

import java.util.concurrent.ThreadLocalRandom;

public enum Mutation {

    RANDOM_WEIGHT {
        @Override
        public boolean mutate(Genome genome) {
            if (genome.getConnections().isEmpty())
                return false;

            ConnectionGene connection = genome.getConnections().getRandomElement();
            connection.setWeight((ThreadLocalRandom.current().nextDouble(-1.0, +1.0))
                    * genome.getNeat().getProperty("randomWeightStrength"));
            return true;
        }
    },
    WEIGHT_SHIFT {
        @Override
        public boolean mutate(Genome genome) {
            if (genome.getConnections().isEmpty())
                return false;

            ConnectionGene connection = genome.getConnections().getRandomElement();
            connection.setWeight(connection.getWeight() + ThreadLocalRandom.current().nextDouble(-1.0, +1.0)
                    * genome.getNeat().getProperty("shiftWeightStrength"));
            return true;
        }
    },
    WEIGHT_SHIFTS {
        @Override
        public boolean mutate(Genome genome) {
            if (genome.getConnections().isEmpty())
                return false;

            double percentage = ThreadLocalRandom.current().nextDouble(0.8, 1.2) * genome.getNeat().getProperty("shiftWeightsPercentage");
            int num = (int) (percentage * genome.getConnections().size());

            for (int i = 0; i < num; i++) {
                WEIGHT_SHIFT.mutate(genome);
            }
            return true;
        }
    },
    TOGGLE {
        @Override
        public boolean mutate(Genome genome) {
            if (genome.getConnections().isEmpty())
                return false;

            ConnectionGene connection = genome.getConnections().getRandomElement();
            connection.setEnabled(!connection.isEnabled());
            return true;
        }
    },
    ADD_LINK {
        @Override
        public boolean mutate(Genome genome) {

            // Try to mutate a new link 50 times. 50 times is most likely
            // always going to be enough tries to mutate a link.
            for (int i = 0; i < 50; i++) {
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

                // If that connection already exists, try again
                if (genome.getConnections().contains(connection)) {
                    continue;
                }

                // Pull the connection from the neat pool of connections, or
                // add it if it does not yet exist
                connection = genome.getNeat().newConnectionGene(connection.getFrom(), connection.getTo());
                connection.setWeight(ThreadLocalRandom.current().nextDouble(-1, +1)
                        * genome.getNeat().getProperty("randomWeightStrength"));

                genome.getConnections().addSorted(connection);
                return true;
            }
            return false;
        }
    },
    ADD_NODE {
        @Override
        public boolean mutate(Genome genome) {
            if (genome.getConnections().isEmpty())
                return false;

            ConnectionGene connection = genome.getConnections().getRandomElement();

            NodeGene from = connection.getFrom();
            NodeGene to = connection.getTo();
            NodeGene middle;

            int replaceId = genome.getNeat().getReplaceIndex(from, to);
            if (replaceId == 0) {
                middle = genome.getNeat().newNode();
                middle.setX((from.getX() + to.getX()) / 2);
                middle.setY((from.getY() + to.getY()) / 2);
                genome.getNeat().setReplaceIndex(from, to, middle.getId());
            } else {
                middle = genome.getNeat().getNode(replaceId);
            }

            ConnectionGene a = genome.getNeat().newConnectionGene(from, middle);
            ConnectionGene b = genome.getNeat().newConnectionGene(middle, to);

            a.setWeight(1);
            b.setWeight(connection.getWeight());
            b.setEnabled(connection.isEnabled());

            genome.getConnections().remove(connection);
            genome.getConnections().add(a);
            genome.getConnections().add(b);

            genome.getNodes().add(middle);
            return true;
        }
    },
    MUTATE {
        @Override
        public boolean mutate(Genome genome) {
            Neat neat = genome.getNeat();
            boolean isMutated = false;

            if (neat.getProperty("mutateLink") > ThreadLocalRandom.current().nextDouble())
                isMutated = ADD_LINK.mutate(genome);
            if (neat.getProperty("mutateNode") > ThreadLocalRandom.current().nextDouble())
                isMutated |= ADD_NODE.mutate(genome);
            if (neat.getProperty("mutateRandomWeight") > ThreadLocalRandom.current().nextDouble())
                isMutated |= RANDOM_WEIGHT.mutate(genome);
            if (neat.getProperty("mutateShiftWeight") > ThreadLocalRandom.current().nextDouble())
                isMutated |= WEIGHT_SHIFT.mutate(genome);
            if (neat.getProperty("mutateShiftWeights") > ThreadLocalRandom.current().nextDouble())
                isMutated |= WEIGHT_SHIFTS.mutate(genome);
            if (neat.getProperty("mutateToggleLink") > ThreadLocalRandom.current().nextDouble())
                isMutated |= TOGGLE.mutate(genome);

            return isMutated;
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
        this.name = builder.substring(0, builder.length() - 1);
    }

    public abstract boolean mutate(Genome genome);
}
