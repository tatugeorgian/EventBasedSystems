import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Subscription {

    private List<Filter> filters = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");

        for (Filter filter : filters) {
            sb.append(filter.toString());
        }
        sb.append(" }\n");

        return sb.toString();
    }

    public List<Filter> getFilters() {
        return filters;
    }
}
