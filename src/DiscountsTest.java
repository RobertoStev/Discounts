import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Discounts
 */
public class DiscountsTest {
    public static void main(String[] args) throws IOException {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}

// Vashiot kod ovde
class Product implements Comparable<Product> {
    int discount;
    int price;

    public Product(int discount, int price) {
        this.discount = discount;
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public int getPrice() {
        return price;
    }

    public int totalDiscountForProduct() {
        return getPrice() - getDiscount();
    }

    public int percentDiscountForProduct() {
        int discount = getPrice() - getDiscount();
        return (discount * 100) / getPrice();
    }

    //48% 2579/4985
    @Override
    public String toString() {
        return String.format("%2d%% %d/%d\n", this.percentDiscountForProduct(), this.discount, this.price);
    }

    @Override
    public int compareTo(Product o) {
        return Comparator.comparing(Product::percentDiscountForProduct).thenComparing(Product::getPrice).reversed().compare(this, o);
    }
}

//Levis 6385:9497  9988:19165  7121:11287  1501:2316  2579:4985  6853:8314
class Store implements Comparable<Store> {
    String storeName;
    Set<Product> products;

    public Store() {
        storeName = "";
        products = new TreeSet<>();
    }

    public Store(String storeName, Set<Product> products) {
        this.storeName = storeName;
        this.products = products;
    }

    public String getStoreName() {
        return storeName;
    }

    public int totalDiscount() {
        int sum = 0;
        for (Product p : products) {
            sum += p.totalDiscountForProduct();
        }
        return sum;
    }

    public double averageDiscount() {
        int sum = 0;
        for (Product p : products) {
            sum += p.percentDiscountForProduct(); //48
        }
        return (double) sum / products.size();
    }


    @Override
    public int compareTo(Store o) {
        return Comparator.comparing(Store::averageDiscount)
                .thenComparing(Store::getStoreName).reversed()
                .compare(this, o);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(storeName + "\n");
        sb.append(String.format("Average discount: %.1f%%\n", averageDiscount()));
        sb.append(String.format("Total discount: %d\n", totalDiscount()));
        for (Product p : products) {
            sb.append(p.toString());
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}

class Discounts {
    Map<String, Store> stores;

    public Discounts() {
        stores = new TreeMap<>();
    }

    //Levis 6385:9497  9988:19165  7121:11287  1501:2316  2579:4985  6853:8314
    public int readStores(InputStream inputStream) throws IOException {
        Scanner sc = new Scanner(inputStream);

        while (sc.hasNextLine()) {

            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String storeName = parts[0];

            Set<Product> products = new TreeSet<>();
            for (int i = 1; i < parts.length; i++) {
                String[] tmp = parts[i].split(":");//6385:9497
                Product product = new Product(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
                products.add(product);
            }
            Store store = new Store(storeName, products);
            stores.put(storeName, store);
        }

        return stores.size();
    }

    public List<Store> byAverageDiscount() {

        return stores.values().stream()
                .sorted() //opagjacki redosled
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return stores.values().stream()
                .sorted(Comparator.comparing(Store::totalDiscount)
                        .thenComparing(Store::getStoreName))
                .limit(3)
                .collect(Collectors.toList());
    }
}