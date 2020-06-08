package Controller;

public class Triton extends God{

    @Override
    public String getName() {
        return "Triton";
    }

    @Override
    public String getDescription() {
        return "God of the Waves\n" +
                "Your Move: Each time your\n" +
                "Worker moves onto a perimeter\n" +
                "space (ground or block), it may\n" +
                "immediately move again.";
    }
}
