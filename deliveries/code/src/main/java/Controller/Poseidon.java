package Controller;

public class Poseidon extends God{

    @Override
    public String getName() {
        return "Poseidon";
    }

    @Override
    public String getDescription() {
        return "God of the Sea\n" +
                "End of Your Turn: If your unmoved\n" +
                "Worker is on the ground level,\n" +
                "it may build up to three times in\n" +
                "neighboring spaces.";
    }
}
