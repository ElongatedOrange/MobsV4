package me.orange.mobsv4.mobs;

public class ModelData {
    public static int getModelData(String mob) {
        return switch (mob) {
            default -> 0;
            case "Blaze" -> 1;
            case "Chicken" -> 2;
            case "Creeper" -> 3;
            case "Evoker" -> 4;
            case "Skeleton" -> 5;
            case "Turtle" -> 6;
            case "Warden" -> 7;
            case "IronGolem" -> 8;
            case "Dragon" -> 9;
        };
    }
}