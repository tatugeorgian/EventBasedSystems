import java.util.*;

public class GeneratorImpl implements Generator {

    private Random random = new Random();

    private Map<Company, Double> previousStocks = new HashMap<Company, Double>();

    @Override
    public List<Publication> generatePublications(PublicationGenerationParams params) {
        List<Publication> publications = new ArrayList<>();

        Publication.stockMin = params.getMinStockValue();
        Publication.stockMax = params.getMaxStockValue();

        if (params.getPublicationCount() < Company.values().length) {
            throw new RuntimeException("Publication count must be bigger than the number of companies");
        }
        //compute necessary constants
        double stockMean = computeMean(params.getMinStockValue(), params.getMaxStockValue());
        double stockStddev = computeStddev(params.getMinStockValue(), params.getMaxStockValue());

        //add initial stock values
        for (Company company : Company.values()) {
            double initialStock = nextStock(stockMean, stockStddev, params.getMinStockValue(), params.getMaxStockValue());
            publications.add(new Publication(company, initialStock, 0D, params.getStartingDate()));
            previousStocks.put(company, initialStock);
        }

        Date currentDate = params.getStartingDate();
        while (publications.size() < params.getPublicationCount()) {
            currentDate = addDaysToDate(currentDate, params.getDaysBetween());
            for (Company company : Company.values()) {
                double previousStock = previousStocks.get(company);
                double change = nextChange(previousStock, params.getMaxChangePercentage());
                publications.add(new Publication(company, previousStock + change, change, currentDate));
                previousStocks.put(company, previousStock + change);
            }
        }

        return publications;
    }

    @Override
    public List<Subscription> generateSubscriptions(SubscriptionGenerationParams params) {
        List<Subscription> subscriptions = new ArrayList<>();

        while (subscriptions.size() < params.getSubscriptionCount()) {
            Subscription subscription = new Subscription();

            if (random.nextDouble() < params.getCompanyPercentage()) {
                int companyIndex = random.nextInt(Company.values().length);
                Filter companyFilter;
                if (random.nextDouble() < params.getEqualCompanyPercentage()) {
                    companyFilter = new Filter(Operator.EQ, Company.values()[companyIndex], "company");
                } else {
                    int operatorIndex = random.nextInt(Operator.values().length - 1);
                    companyFilter = new Filter(Operator.values()[operatorIndex], Company.values()[companyIndex], "company");
                }
                subscription.getFilters().add(companyFilter);
            }

            if (random.nextDouble() < params.getStockValuePercentage()) {
                double value = nextUniform(params.getPublicationParams().getMinStockValue(), params.getPublicationParams().getMaxStockValue());
                int operatorIndex = random.nextInt(Operator.values().length);
                Filter stockFilter = new Filter(Operator.values()[operatorIndex], value, "stockValue");
                subscription.getFilters().add(stockFilter);
            }

            if (random.nextDouble() < params.getChangePercentage()) {
                double maxChange = Math.abs(params.getPublicationParams().getMaxStockValue() * params.getPublicationParams().getMaxChangePercentage());
                double value = nextUniform(-maxChange, maxChange);
                int operatorIndex = random.nextInt(Operator.values().length);
                Filter changeFilter = new Filter(Operator.values()[operatorIndex], value, "change");
                subscription.getFilters().add(changeFilter);
            }

            if (random.nextDouble() < params.getVariationPercentage()) {
                double value = nextUniform(0D, 1D);
                int operatorIndex = random.nextInt(Operator.values().length);
                Filter variationFilter = new Filter(Operator.values()[operatorIndex], value, "variation");
                subscription.getFilters().add(variationFilter);
            }

            if (random.nextDouble() < params.getDatePercentage()) {
                Date value = new Date(nextUniform(params.getPublicationParams().getStartingDate().getTime(), params.getEndDate().getTime()));
                int operatorIndex = random.nextInt(Operator.values().length);
                Filter dateFilter = new Filter(Operator.values()[operatorIndex], value, "date");
                subscription.getFilters().add(dateFilter);
            }

            subscriptions.add(subscription);
        }

        return subscriptions;
    }

    private double nextChange(double stock, double maxPercentage) {
        double maxChange = Math.abs(stock * maxPercentage);
        return 2 * maxChange * random.nextDouble() - maxChange;
    }

    private double nextUniform(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private long nextUniform(long min, long max) {
        return min + (max - min) * random.nextLong();
    }

    private double nextStock(double mean, double stddev, double min, double max) {
        double value = random.nextGaussian() * stddev + mean;
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        }
        return value;
    }

    private double computeMean(double min, double max) {
        return (max + min) / 2;
    }

    private double computeStddev(double min, double max) {
        return (max - min) / 4;
    }

    private Date addDaysToDate(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DATE, days);

        return calendar.getTime();

    }
}
