import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Generator generator = new GeneratorImpl();
        List<Publication> publications = generator.generatePublications(new PublicationGenerationParams());
        writePublicationsToFile(publications);
        Date endDate = publications.get(publications.size() - 1).getDate();
        List<Subscription> subscriptions = generator.generateSubscriptions(new SubscriptionGenerationParams
                (new PublicationGenerationParams(), endDate));
        writeSubscriptionsToFile(subscriptions);
    }

    private static void writePublicationsToFile(List<Publication> publications) {
        Path path = Paths.get(System.getProperty("user.dir"), "publications.txt");

        try {
            for (Publication publication : publications) {
                Files.write(path, publication.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeSubscriptionsToFile(List<Subscription> subscriptions) {
        Path path = Paths.get(System.getProperty("user.dir"), "subscriptions.txt");

        for (Subscription subscription : subscriptions) {
            try {
                Files.write(path, subscription.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
