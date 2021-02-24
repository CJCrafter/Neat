package me.cjcrafter.neat;

import java.util.Comparator;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.TreeMap;

public class ProbabilityMap<E> {

    private final SplittableRandom rand = new SplittableRandom();

    private final Node dummy;
    private final TreeMap<Node, Node> map;
    private Node first;
    private double totalWeight;

    public ProbabilityMap() {
        dummy = new Node();
        map = new TreeMap<>(Comparator.comparingDouble(Node::getOffset));
    }

    public double put(E object, double weight) {
        if (weight <= 0.0) {
            throw new IllegalArgumentException("Invalid weight: " + weight);
        }

        dummy.object = object;
        if (map.containsKey(dummy)) {
            Node node = map.get(dummy);

            double old = node.weight;
            node.setWeight(weight);
            return old;
        } else {
            Node node = new Node(object, weight, totalWeight);
            Node left = map.floorKey(node);
            Node right = map.ceilingKey(node);

            node.left = left;
            node.right = right;
            right.left = node;
            left.right = node;

            map.put(node, node);
            totalWeight += weight;
            return 0.0;
        }
    }

    public int size() {
        return map.size();
    }

    public E random() {
        if (size() == 0) {
            throw new IllegalStateException("Cannot pull an element from an empty set!");
        }

        dummy.offset = rand.nextDouble(totalWeight);
        return Objects.requireNonNull(map.floorKey(dummy)).object;
    }

    public E randomIgnoreWeight() {
        if (size() == 0) {
            throw new IllegalStateException("Cannot pull an element from an empty set!");
        }

        int index = rand.nextInt(size());
        Node node = map.firstKey();
        while (index-- > 0) {
            node = node.right;
        }

        return node.object;
    }


    /**
     * Doubly linked nodes.
     */
    private class Node {

        private E object;
        private double weight;
        private double offset;
        private Node left, right;

        Node() {
            object = null;
            weight = 0.0;
        }

        public Node(E object, double weight, double offset) {
            this.object = object;
            this.weight = weight;
            this.offset = offset;
        }

        public double getOffset() {
            return offset;
        }

        public void setWeight(double weight) {
            double diff = weight - this.weight;
            modifyWeight(diff);
            totalWeight += diff;
        }

        private void modifyWeight(double diff) {
            this.weight += diff;

            if (right != null) {
                right.modifyWeight(diff);
            }
        }

        public void setPosition(Node left, Node right) {
            this.left = left;
            this.right = right;

            right.left = this;
            left.right = this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (getClass() != other.getClass()) {
                return false;
            } else {
                Node node = (Node) other;
                return Objects.equals(object, node.object);
            }
        }
    }
}
