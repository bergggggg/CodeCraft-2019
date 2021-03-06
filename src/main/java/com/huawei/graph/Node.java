package com.huawei.graph;

/**
 * The Node class implements a node in a directed graph keyed on a label of type String, with adjacency lists for
 * representing edges.
 * <p>
 * Created by brandonsmock on 5/31/15.
 */

import com.huawei.entity.Car;
import com.huawei.entity.Road;
import com.huawei.graph.util.Path;

import java.util.*;

public class Node {
    protected String label;
    protected HashMap<String, Double> neighbors; // adjacency list, with HashMap for each edge weight
    protected HashMap<String, Road> roads; //相关道路的id

    public Node() {
        neighbors = new HashMap<>();
        roads = new HashMap<String, Road>();
    }

    public Node(String label) {
        this.label = label;
        neighbors = new HashMap<>();
        roads = new HashMap<String, Road>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public HashMap<String, Double> getNeighbors() {
        return neighbors;
    }

    public HashMap<String, Road> getRoads() {
        return roads;
    }

    public void setNeighbors(HashMap<String, Double> neighbors) {
        this.neighbors = neighbors;
    }

    public void setRoads(HashMap<String, Road> roads) {
        this.roads = roads;
    }

    public void addEdge(String toNodeLabel, Double weight) {
        neighbors.put(toNodeLabel, weight);
    }

    public void addEdge(String toNodeLabel, Double weight, Road road) {
        neighbors.put(toNodeLabel, weight);
        roads.put(toNodeLabel, road);
    }

    public double removeEdge(String toNodeLabel) {
        if (neighbors.containsKey(toNodeLabel)) {
            double weight = neighbors.get(toNodeLabel);
            neighbors.remove(toNodeLabel);
            roads.remove(toNodeLabel);
            return weight;
        }

        return Double.MAX_VALUE;
    }

    public Set<String> getAdjacencyList() {
        return neighbors.keySet();
    }

    public void updateWeight(Car car) {
        Iterator it = getAdjacencyList().iterator();
        while (it.hasNext()) {
            String to = (String) it.next();
            Road road = roads.get(to);
            int v = Math.min(car.getSpeed(), road.getSpeed());
            double weight = road.getLength() / 1.0 / v;
            neighbors.put(to, weight);
        }
    }


    public void updateWeight(Car car, List<List<Path>> carPaths, int numOfCars, double WEIGHT_RATIO) {
        for (String roadToId : getAdjacencyList()) {
            int i = 1;
            Road road = roads.get(roadToId);
            int v = Math.min(road.getSpeed(),car.getSpeed());
            double weight = road.getLength() / 1.0 / v;
            if (carPaths.size() > numOfCars) {
                // 每次只考虑前numOfCars的车对路径权重的影响
                int len = carPaths.size();
                for (int j = len - 1; j > 0; j--) {
                    Path path = carPaths.get(j).get(0);
                    if (i >= numOfCars) {
                        break;
                    }
                    for (Edge edge : path.getEdges()) {
                        if (edge.getToNode().equals(roadToId) && edge.getFromNode().equals(road.getFrom())) {
                            double nums = road.getChannel();
                            weight += 1.0/nums;
                        }
                    }
                    i++;
                }
                neighbors.put(roadToId, weight);
            } else {
                neighbors.put(roadToId, weight);
            }
        }
    }


    public LinkedList<Edge> getEdges() {
        LinkedList<Edge> edges = new LinkedList<Edge>();
        for (String toNodeLabel : neighbors.keySet()) {
            edges.add(new Edge(label, toNodeLabel, neighbors.get(toNodeLabel)));
        }

        return edges;
    }

    public String toString() {
        StringBuilder nodeStringB = new StringBuilder();
        nodeStringB.append(label);
        nodeStringB.append(": {");
        Set<String> adjacencyList = this.getAdjacencyList();
        Iterator<String> alIt = adjacencyList.iterator();
        HashMap<String, Double> neighbors = this.getNeighbors();
        while (alIt.hasNext()) {
            String neighborLabel = alIt.next();
            nodeStringB.append(neighborLabel.toString());
            nodeStringB.append(": ");
            nodeStringB.append(neighbors.get(neighborLabel));
            if (alIt.hasNext())
                nodeStringB.append(", ");
        }
        nodeStringB.append("}");
        nodeStringB.append("\n");

        return nodeStringB.toString();
    }
}
