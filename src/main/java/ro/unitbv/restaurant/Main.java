package ro.unitbv.restaurant;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Iteration 1 ===");

        // Iteration 1 code (consolidated)
        Menu menu1 = new Menu();

        Product p1 = new Food("Pizza Margherita", 45.0, 450, true);
        Product p2 = new Food("Paste Carbonara", 52.5, 400, false);
        Product p3 = new Drink("Limonada", 15.0, 400, false, true);
        Product p4 = new Drink("Apa Plata", 8.0, 500, false, true);

        menu1.addProduct(p1, Category.MAIN_COURSE);
        menu1.addProduct(p2, Category.MAIN_COURSE);
        menu1.addProduct(p3, Category.SOFT_DRINK);
        menu1.addProduct(p4, Category.SOFT_DRINK);

        menu1.printMenuIteration1("La Andrei");

        System.out.println("\n=== Iteration 2 ===");

        // Iteration 2 code (consolidated)
        Food pizza = new Food("Pizza Carbonara", 52.5, 400, false);
        Drink lemonade = new Drink("Limonada", 15.0, 400, false, true);
        Drink wine = new Drink("Vin Rosu", 18.0, 150, true, true);
        Food salad = new Food ("Salata Verde", 20.0, 250, true);
        Food steak = new Food ("Steak de Vita", 80.0, 350, false);


        Order order = new Order();
        order.addProduct(pizza, 2);
        order.addProduct(lemonade, 1);
        order.addProduct(wine, 3);
        order.addProduct(salad, 0);


        System.out.println("Total (with VAT): " + order.calculateTotalWithVAT());

        DiscountRule happyHour = (totalWithVAT, items) -> {

            int hour = java.time.LocalTime.now().getHour();

            // Happy Hour ONLY between 17:00 and 19:00
            if (hour < 17 || hour >= 19) {
                return totalWithVAT; // no discount
            }

            double alcoholicValue = items.entrySet().stream()
                    .filter(e -> e.getKey() instanceof Drink d && d.isAlcoholic())
                    .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                    .sum();

            double discount = alcoholicValue * 0.20 * 1.09;
            return totalWithVAT - discount;
        };

        System.out.println("Total (Happy Hour): " + order.calculateTotalWithDiscount(happyHour));

        System.out.println("\n=== Iteration 3 ===");

        // Iteration 3 code (consolidated)
        Menu menu3 = new Menu();

        Food pizza3 = new Food("Pizza Margherita", 45.0, 450, true);
        Food pasta = new Food("Paste Carbonara", 52.5, 400, false);
        Food tiramisu = new Food("Tiramisu", 20.0, 200, true);
        Food lavacake = new Food("Lava Cake", 25.0, 300, false);

        Drink lemonade3 = new Drink("Limonada", 15.0, 400, false, true);
        Drink wine3 = new Drink("Vin Rosu", 18.0, 150, true, true);

        menu3.addProduct(pizza3, Category.MAIN_COURSE);
        menu3.addProduct(pasta, Category.MAIN_COURSE);
        menu3.addProduct(tiramisu, Category.DESSERT);
        menu3.addProduct(lavacake, Category.DESSERT);
        menu3.addProduct(lemonade3, Category.SOFT_DRINK);
        menu3.addProduct(wine3, Category.ALCOHOLIC_DRINK);

        menu3.printMenu("La Andrei");

        System.out.println("\nVegetarian products:");
        menu3.getVegetarianSorted().forEach(p -> System.out.println(" -> " + p.getName()));

        System.out.println("\nAverage dessert price: " + menu3.getAveragePrice(Category.DESSERT));

        System.out.println("\nExists product > 100 RON? " + menu3.existsProductMoreExpensiveThan(100));

        menu3.findProductByName("tiramisu")
                .ifPresentOrElse(
                        p -> System.out.println("\nFound: " + p),
                        () -> System.out.println("\nNot found")
                );

        Pizza custom = new Pizza.Builder("Pizza Custom", 60.0, "thin", "tomato")
                .addTopping("mozzarella")
                .addTopping("mushrooms")
                .addTopping("bacon")
                .build();

        System.out.println("\nCustom Pizza:");
        System.out.println(custom);

        System.out.println("\n=== Iteration 4 ===");

        // Iteration 4 code (consolidated)
        AppConfig config = ConfigLoader.load("src/main/resources/config.json");

        if (config == null) {
            System.out.println("Aplicatia nu poate continua fără configurare.");
            return;
        }

        System.out.println("Restaurant: " + config.getRestaurantName());
        System.out.println("TVA: " + config.getVat());

        Menu menu4 = new Menu();

        menu4.addProduct(new Food("Pizza Margherita", 45, 450, true), Category.MAIN_COURSE);
        menu4.addProduct(new Drink("Limonada", 15, 400, false, true), Category.SOFT_DRINK);

        menu4.printMenu(config.getRestaurantName());

        MenuExporter.export(menu4, "menu_export.json");
    }
}
